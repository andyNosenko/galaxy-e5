#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-help}"
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=e5_common.sh
source "${BASE_DIR}/e5_common.sh"
APK_DIR="${BASE_DIR}/overlays"
LOG_DIR="${BASE_DIR}/manage_logs"

OVERLAY_PACKAGES=(
  "com.flyme.auto.settings.ru"
  "com.flyme.auto.energy.ru"
  "com.flyme.auto.hvac.ru"
)

target_for_overlay() {
  case "$1" in
    com.flyme.auto.settings.ru) echo "com.flyme.auto.settings" ;;
    com.flyme.auto.energy.ru)   echo "com.flyme.auto.energy" ;;
    com.flyme.auto.hvac.ru)     echo "com.flyme.auto.hvac" ;;
    *) echo "" ;;
  esac
}

OVERLAY_DIRS=(
  "/vendor/overlay"
  "/product/overlay"
  "/system/product/overlay"
  "/system/vendor/overlay"
)

TMP_ARTIFACTS=(
  "/data/local/tmp/deployagent"
  "/data/local/tmp/deployagent.jar"
)

require_cmd() {
  case "$1" in
    adb) require_adb; return 0 ;;
    rg) require_rg; return 0 ;;
    apktool) require_apktool; return 0 ;;
    python3) require_python3; return 0 ;;
  esac
  command -v "$1" >/dev/null 2>&1 || {
    echo "[ERROR] Required command not found: $1" >&2
    exit 1
  }
}

log() {
  echo "[$(date '+%H:%M:%S')] $*"
}

adb_connected() {
  require_adb
  adb get-state >/dev/null 2>&1 || {
    echo "[ERROR] No ADB device connected. Run: adb devices" >&2
    exit 1
  }
}

has_root() {
  adb shell "command -v su >/dev/null 2>&1" >/dev/null 2>&1
}

adb_shell() {
  adb shell "$@" | tr -d '\r'
}

package_installed() {
  local pkg="$1"
  adb_shell pm list packages "$pkg" | rg -q "^package:${pkg}$"
}

overlay_apk_path() {
  local pkg="$1"
  local path="${APK_DIR}/${pkg}.apk"
  [[ -f "$path" ]] || return 1
  echo "$path"
}

install_overlay() {
  local pkg="$1"
  local apk_path
  apk_path="$(overlay_apk_path "$pkg")" || {
    echo "[ERROR] APK not found: ${APK_DIR}/${pkg}.apk" >&2
    return 1
  }

  local remote="/data/local/tmp/${pkg}.apk"
  local host_apk
  host_apk="$(to_host_path "$apk_path")"
  log "Install: $pkg"

  adb push "$host_apk" "$remote"
  if adb_shell pm install -r -g --user 0 "$remote" | rg -q "Success"; then
    log "  pm install: OK"
  elif adb install -r -g --user 0 "$host_apk" 2>&1 | rg -q "Success"; then
    log "  adb install: OK"
  else
    echo "[ERROR] Failed to install $pkg" >&2
    adb_shell rm -f "$remote" || true
    return 1
  fi

  adb_shell rm -f "$remote" || true

  # Enable static overlay explicitly (safe if already enabled)
  adb_shell cmd overlay enable --user 0 "$pkg" >/dev/null 2>&1 || true

  if package_installed "$pkg"; then
    log "  package present: OK"
  else
    echo "[ERROR] Package not found after install: $pkg" >&2
    return 1
  fi
}

disable_overlay() {
  local pkg="$1"
  log "Disable overlay: $pkg"
  adb_shell cmd overlay disable --user 0 "$pkg" >/dev/null 2>&1 || true
}

uninstall_overlay_clean() {
  local pkg="$1"
  log "Uninstall (no -k): $pkg"

  disable_overlay "$pkg"

  if package_installed "$pkg"; then
    if adb_shell pm uninstall --user 0 "$pkg" | rg -q "Success"; then
      log "  pm uninstall: OK"
    elif adb_shell cmd package uninstall --user 0 "$pkg" | rg -q "Success"; then
      log "  cmd package uninstall: OK"
    else
      echo "[ERROR] Failed to uninstall $pkg" >&2
      return 1
    fi
  else
    log "  already absent"
  fi

  if package_installed "$pkg"; then
    echo "[ERROR] Package still present after uninstall: $pkg" >&2
    return 1
  fi
}

force_stop_targets() {
  local pkg target
  for pkg in "${OVERLAY_PACKAGES[@]}"; do
    target="$(target_for_overlay "$pkg")"
    [[ -n "$target" ]] || continue
    adb_shell am force-stop "$target" >/dev/null 2>&1 || true
  done
}

cleanup_tmp_artifacts() {
  local path
  for path in "${TMP_ARTIFACTS[@]}"; do
    adb_shell rm -f "$path" >/dev/null 2>&1 || true
  done
}

cleanup_idmap_and_overlay_files() {
  local pkg pattern dir

  if ! has_root; then
    log "[WARN] No root (su) — skip idmap/overlay file cleanup"
    return 0
  fi

  for pkg in "${OVERLAY_PACKAGES[@]}"; do
    pattern="${pkg//./\\.}"

    log "Cleanup idmap for: $pkg"
    adb shell su -c "find /data/resource-cache -type f 2>/dev/null | rg -i '${pattern}|idmap'" \
      | while IFS= read -r file; do
          [[ -z "$file" ]] && continue
          if echo "$file" | rg -qi "${pattern}|${pkg}"; then
            log "  rm idmap: $file"
            adb shell su -c "rm -f '$file'" >/dev/null 2>&1 || true
          fi
        done

    for dir in "${OVERLAY_DIRS[@]}"; do
      adb shell su -c "find '$dir' -type f 2>/dev/null | rg -i '${pattern}|flyme.*\\.ru'" \
        | while IFS= read -r file; do
            [[ -z "$file" ]] && continue
            log "  rm overlay file: $file"
            adb shell su -c "rm -f '$file'" >/dev/null 2>&1 || true
          done
    done
  done
}

verify_clean() {
  local pkg failures=0

  echo ""
  log "=== Verification ==="

  for pkg in "${OVERLAY_PACKAGES[@]}"; do
    if package_installed "$pkg"; then
      echo "[FAIL] Package still installed: $pkg"
      failures=$((failures + 1))
    else
      echo "[OK]   Package absent: $pkg"
    fi
  done

  local overlay_hits
  overlay_hits="$(adb_shell cmd overlay list 2>/dev/null | rg -i 'flyme|\.ru' || true)"
  if [[ -n "$overlay_hits" ]]; then
    echo "[FAIL] Overlay list still contains .ru entries:"
    echo "$overlay_hits"
    failures=$((failures + 1))
  else
    echo "[OK]   Overlay list: no .ru entries"
  fi

  local pkg_hits
  pkg_hits="$(adb_shell pm list packages 2>/dev/null | rg -i 'com\.flyme\.auto\.(settings|energy|hvac)\.ru' || true)"
  if [[ -n "$pkg_hits" ]]; then
    echo "[FAIL] pm list still shows .ru packages:"
    echo "$pkg_hits"
    failures=$((failures + 1))
  else
    echo "[OK]   pm list: no .ru packages"
  fi

  if has_root; then
    local residue
    residue="$(adb shell su -c "find /data/resource-cache -type f 2>/dev/null" | rg -i 'flyme|\.ru|idmap' || true)"
    if [[ -n "$residue" ]]; then
      echo "[WARN] Possible idmap residue (check manually):"
      echo "$residue" | head -n 10
    else
      echo "[OK]   resource-cache: no obvious .ru residue"
    fi
  fi

  return "$failures"
}

verify_installed() {
  local pkg failures=0

  echo ""
  log "=== Verification ==="

  for pkg in "${OVERLAY_PACKAGES[@]}"; do
    if package_installed "$pkg"; then
      echo "[OK]   Installed: $pkg"
    else
      echo "[FAIL] Not installed: $pkg"
      failures=$((failures + 1))
    fi
  done

  local overlay_hits
  overlay_hits="$(adb_shell cmd overlay list 2>/dev/null | rg -i 'flyme.*\.ru|\.ru.*flyme' || true)"
  if [[ -n "$overlay_hits" ]]; then
    echo "[OK]   Overlay entries:"
    echo "$overlay_hits"
  else
    echo "[WARN] No .ru overlays in overlay list (may need reboot)"
  fi

  return "$failures"
}

cmd_install() {
  local pkg failures=0

  adb_connected
  require_cmd adb
  require_cmd rg

  mkdir -p "$LOG_DIR"
  log "=== Install overlays ===" | tee "${LOG_DIR}/install.log"

  for pkg in "${OVERLAY_PACKAGES[@]}"; do
    if ! install_overlay "$pkg" 2>&1 | tee -a "${LOG_DIR}/install.log"; then
      failures=$((failures + 1))
    fi
  done

  force_stop_targets
  if ! verify_installed; then
    failures=$((failures + 1))
  fi

  if [[ "$failures" -gt 0 ]]; then
    echo ""
    echo "[ERROR] Install completed with ${failures} error(s)" >&2
    exit 1
  fi

  echo ""
  echo "[OK] All overlays installed. Reboot HU if UI did not update."
}

cmd_uninstall() {
  local pkg failures=0 reboot="${2:-}"

  adb_connected
  require_cmd adb
  require_cmd rg

  mkdir -p "$LOG_DIR"
  log "=== Clean uninstall (no -k) ===" | tee "${LOG_DIR}/uninstall.log"

  # Reverse order of install
  for (( i=${#OVERLAY_PACKAGES[@]}-1; i>=0; i-- )); do
    pkg="${OVERLAY_PACKAGES[$i]}"
    if ! uninstall_overlay_clean "$pkg" 2>&1 | tee -a "${LOG_DIR}/uninstall.log"; then
      failures=$((failures + 1))
    fi
  done

  cleanup_tmp_artifacts
  cleanup_idmap_and_overlay_files
  force_stop_targets

  if ! verify_clean; then
    failures=$((failures + 1))
  fi

  if [[ "$failures" -gt 0 ]]; then
    echo ""
    echo "[ERROR] Uninstall completed with ${failures} issue(s)" >&2
    echo "Try: adb reboot  then run: $0 status" >&2
    exit 1
  fi

  echo ""
  echo "[OK] System restored — no .ru overlay packages remain."

  if [[ "$reboot" == "--reboot" ]]; then
    log "Rebooting device..."
    adb reboot
  else
    echo "Tip: run '$0 uninstall --reboot' or 'adb reboot' if UI still shows Russian."
  fi
}

cmd_status() {
  adb_connected
  require_cmd rg

  echo "=== Overlay packages ==="
  for pkg in "${OVERLAY_PACKAGES[@]}"; do
    if package_installed "$pkg"; then
      echo "  [installed] $pkg"
    else
      echo "  [absent]    $pkg"
    fi
  done

  echo ""
  echo "=== cmd overlay list (flyme/.ru) ==="
  adb_shell cmd overlay list 2>/dev/null | rg -i 'flyme|\.ru' || echo "  (none)"

  echo ""
  echo "=== pm list packages (flyme/.ru) ==="
  adb_shell pm list packages 2>/dev/null | rg -i 'flyme.*\.ru|\.ru.*flyme' || echo "  (none)"
}

print_help() {
  cat <<USAGE
Usage:
  bash e5_overlay_manage.sh install
  bash e5_overlay_manage.sh uninstall [--reboot]
  bash e5_overlay_manage.sh status

Commands:
  install             Install 3 RRO overlay APKs from ./overlays/
  uninstall           Remove overlays WITHOUT -k (full cleanup, no data kept)
  uninstall --reboot  Same + reboot HU after cleanup
  status              Show installed overlay packages and overlay list

Overlay packages:
  com.flyme.auto.settings.ru  ->  com.flyme.auto.settings
  com.flyme.auto.energy.ru    ->  com.flyme.auto.energy
  com.flyme.auto.hvac.ru      ->  com.flyme.auto.hvac

APK source: ${APK_DIR}/

Uninstall removes:
  - overlay packages (pm uninstall, NOT pm uninstall -k)
  - /data/local/tmp/deployagent*
  - idmap files in /data/resource-cache (requires root)
  - leftover files in /vendor/overlay, /product/overlay (requires root)

Requirements: adb, rg
Optional: root (su) for deep file cleanup
USAGE
}

main() {
  case "$MODE" in
    install)
      cmd_install
      ;;
    uninstall)
      cmd_uninstall "$@"
      ;;
    status)
      cmd_status
      ;;
    help|--help|-h|"")
      print_help
      ;;
    *)
      echo "[ERROR] Unknown mode: $MODE" >&2
      print_help
      exit 1
      ;;
  esac
}

main "$@"
