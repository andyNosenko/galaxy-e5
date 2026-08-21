#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-help}"
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=e5_common.sh
source "${BASE_DIR}/e5_common.sh"
OUT_BEFORE="${BASE_DIR}/before"
OUT_AFTER="${BASE_DIR}/after"
OUT_ORIGINALS="${BASE_DIR}/original_strings"
TMP_DIR="${BASE_DIR}/.tmp_extract"

# Target packages for Flyme Auto localization (original, not .ru overlays)
TARGET_PACKAGES=(
  "com.flyme.auto.settings"
  "com.flyme.auto.energy"
  "com.flyme.auto.hvac"
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

adb_connected() {
  require_adb
  adb get-state >/dev/null 2>&1 || {
    echo "[ERROR] No ADB device connected. Run: adb devices" >&2
    exit 1
  }
}

capture_snapshot() {
  local out_dir="$1"
  mkdir -p "$out_dir"

  adb shell cmd overlay list > "${out_dir}/overlay_list.txt" || true
  adb shell pm list packages -f > "${out_dir}/packages_all.txt" || true
  adb shell pm list packages -f | rg -i "flyme|\.ru|overlay" > "${out_dir}/packages_overlay_filtered.txt" || true

  adb shell ls -la /vendor/overlay > "${out_dir}/vendor_overlay_ls.txt" || true
  adb shell ls -la /product/overlay > "${out_dir}/product_overlay_ls.txt" || true
  adb shell ls -la /system/product/overlay > "${out_dir}/system_product_overlay_ls.txt" || true
  adb shell ls -la /data/resource-cache > "${out_dir}/data_resource_cache_ls.txt" || true

  adb shell getprop ro.build.version.release > "${out_dir}/build_android_release.txt" || true
  adb shell getprop ro.build.fingerprint > "${out_dir}/build_fingerprint.txt" || true

  if adb shell "command -v su >/dev/null 2>&1" >/dev/null 2>&1; then
    adb shell su -c "find /data/resource-cache -maxdepth 2 -type f \\( -name '*idmap*' -o -name '*.idmap' \\)" > "${out_dir}/idmap_files.txt" || true
    adb shell su -c "find /vendor/overlay /product/overlay /system/product/overlay -type f" > "${out_dir}/overlay_files_full.txt" || true
  fi
}

make_diff() {
  diff -u "${OUT_BEFORE}/overlay_list.txt" "${OUT_AFTER}/overlay_list.txt" > "${BASE_DIR}/diff_overlay_list.patch" || true
  diff -u "${OUT_BEFORE}/packages_overlay_filtered.txt" "${OUT_AFTER}/packages_overlay_filtered.txt" > "${BASE_DIR}/diff_packages_overlay.patch" || true
  diff -u "${OUT_BEFORE}/vendor_overlay_ls.txt" "${OUT_AFTER}/vendor_overlay_ls.txt" > "${BASE_DIR}/diff_vendor_overlay.patch" || true
  diff -u "${OUT_BEFORE}/product_overlay_ls.txt" "${OUT_AFTER}/product_overlay_ls.txt" > "${BASE_DIR}/diff_product_overlay.patch" || true
  diff -u "${OUT_BEFORE}/system_product_overlay_ls.txt" "${OUT_AFTER}/system_product_overlay_ls.txt" > "${BASE_DIR}/diff_system_product_overlay.patch" || true
  diff -u "${OUT_BEFORE}/data_resource_cache_ls.txt" "${OUT_AFTER}/data_resource_cache_ls.txt" > "${BASE_DIR}/diff_resource_cache.patch" || true

  if [[ -f "${OUT_BEFORE}/idmap_files.txt" && -f "${OUT_AFTER}/idmap_files.txt" ]]; then
    diff -u "${OUT_BEFORE}/idmap_files.txt" "${OUT_AFTER}/idmap_files.txt" > "${BASE_DIR}/diff_idmap_files.patch" || true
  fi

  if [[ -f "${OUT_BEFORE}/overlay_files_full.txt" && -f "${OUT_AFTER}/overlay_files_full.txt" ]]; then
    diff -u "${OUT_BEFORE}/overlay_files_full.txt" "${OUT_AFTER}/overlay_files_full.txt" > "${BASE_DIR}/diff_overlay_files_full.patch" || true
  fi
}

# Resolve all APK paths for a package (base + splits)
get_apk_paths() {
  local pkg="$1"
  adb shell pm path "$pkg" 2>/dev/null | sed 's/^package://' | tr -d '\r'
}

# Short name for output dirs: com.flyme.auto.settings -> settings
pkg_short_name() {
  local pkg="$1"
  echo "${pkg##*.}"
}

pull_package_apks() {
  local pkg="$1"
  local dest_dir="$2"
  local paths pulled=0

  mkdir -p "$dest_dir"

  while IFS= read -r remote_path; do
    [[ -z "$remote_path" ]] && continue
    local base_name
    base_name="$(basename "$remote_path")"
    local host_dest
    host_dest="$(to_host_path "${dest_dir}/${base_name}")"
    echo "  pull: $remote_path"
    if adb pull "$remote_path" "$host_dest"; then
      pulled=$((pulled + 1))
    else
      echo "  [WARN] Failed to pull: $remote_path" >&2
    fi
  done < <(get_apk_paths "$pkg")

  if [[ "$pulled" -eq 0 ]]; then
    echo "[ERROR] No APK pulled for $pkg (package not installed?)" >&2
    return 1
  fi
}

find_base_apk() {
  local apk_dir="$1"
  local base

  base="$(find "$apk_dir" -maxdepth 1 -name 'base.apk' -print -quit)"
  if [[ -n "$base" ]]; then
    echo "$base"
    return 0
  fi

  # Prefer non-split APK
  base="$(find "$apk_dir" -maxdepth 1 -name '*.apk' ! -name 'split_*' -print -quit)"
  if [[ -n "$base" ]]; then
    echo "$base"
    return 0
  fi

  # Fallback: first APK
  base="$(find "$apk_dir" -maxdepth 1 -name '*.apk' | head -n 1)"
  [[ -n "$base" ]] || return 1
  echo "$base"
}

decode_apk_resources() {
  local apk_file="$1"
  local decode_dir="$2"
  # apktool on Windows is usually a .bat/java wrapper — needs Windows paths under Cygwin
  apktool d -f -s "$(to_host_path "$apk_file")" -o "$(to_host_path "$decode_dir")" >/dev/null
}

copy_string_resources() {
  local decode_dir="$1"
  local out_res_dir="$2"
  local locale_dir xml_file locale_name

  mkdir -p "$out_res_dir"

  if [[ ! -d "${decode_dir}/res" ]]; then
    echo "  [WARN] No res/ in decoded APK: $decode_dir" >&2
    return 0
  fi

  while IFS= read -r locale_dir; do
    locale_name="$(basename "$locale_dir")"
    mkdir -p "${out_res_dir}/${locale_name}"

    for xml_file in strings.xml plurals.xml arrays.xml; do
      if [[ -f "${locale_dir}/${xml_file}" ]]; then
        cp "${locale_dir}/${xml_file}" "${out_res_dir}/${locale_name}/${xml_file}"
      fi
    done
  done < <(find "${decode_dir}/res" -maxdepth 1 -type d -name 'values*' | sort)
}

count_string_keys() {
  local xml_file="$1"
  rg -c '<string name=' "$xml_file" 2>/dev/null || echo 0
}

extract_package_strings() {
  local pkg="$1"
  local short_name
  short_name="$(pkg_short_name "$pkg")"
  local pkg_out="${OUT_ORIGINALS}/${short_name}"
  local apk_dir="${pkg_out}/apk"
  local decode_dir="${TMP_DIR}/${short_name}_decoded"
  local res_out="${pkg_out}/res"

  echo "[INFO] Extracting: $pkg"

  rm -rf "$pkg_out" "$decode_dir"
  mkdir -p "$pkg_out"

  if ! pull_package_apks "$pkg" "$apk_dir"; then
    echo "  package: $pkg" >> "${OUT_ORIGINALS}/errors.txt"
    echo "  status: NOT_INSTALLED_OR_PULL_FAILED" >> "${OUT_ORIGINALS}/errors.txt"
    echo "" >> "${OUT_ORIGINALS}/errors.txt"
    return 1
  fi

  local base_apk
  if ! base_apk="$(find_base_apk "$apk_dir")"; then
    echo "[ERROR] No APK file found in $apk_dir" >&2
    return 1
  fi

  echo "  decode: $(basename "$base_apk")"
  decode_apk_resources "$base_apk" "$decode_dir"

  # Decode split APKs that may contain locale-specific resources
  local split_apk
  while IFS= read -r split_apk; do
    [[ "$split_apk" == "$base_apk" ]] && continue
    local split_name split_decode
    split_name="$(basename "$split_apk" .apk)"
    split_decode="${TMP_DIR}/${short_name}_${split_name}"
    echo "  decode split: $(basename "$split_apk")"
    decode_apk_resources "$split_apk" "$split_decode" || true
    copy_string_resources "$split_decode" "${pkg_out}/res_splits/${split_name}"
  done < <(find "$apk_dir" -maxdepth 1 -name '*.apk' | sort)

  copy_string_resources "$decode_dir" "$res_out"

  {
    echo "package: $pkg"
    echo "short_name: $short_name"
    echo "base_apk: $(basename "$base_apk")"
    echo "apk_files:"
    find "$apk_dir" -maxdepth 1 -name '*.apk' -printf '  - %f\n' 2>/dev/null || \
      find "$apk_dir" -maxdepth 1 -name '*.apk' -exec basename {} \; | sed 's/^/  - /'
    echo "locales:"
  } >> "${OUT_ORIGINALS}/extraction_report.txt"

  local locale_dir
  for locale_dir in "$res_out"/values*; do
    [[ -d "$locale_dir" ]] || continue
    local locale_name strings_file key_count
    locale_name="$(basename "$locale_dir")"
    strings_file="${locale_dir}/strings.xml"
    key_count=0
    if [[ -f "$strings_file" ]]; then
      key_count="$(count_string_keys "$strings_file")"
    fi
    echo "  ${locale_name}: strings=${key_count}" >> "${OUT_ORIGINALS}/extraction_report.txt"
  done
  echo "" >> "${OUT_ORIGINALS}/extraction_report.txt"

  echo "[OK] $pkg -> ${pkg_out}/res/"
}

extract_all_originals() {
  local pkg

  adb_connected
  require_cmd apktool

  rm -rf "$TMP_DIR"
  mkdir -p "$OUT_ORIGINALS" "$TMP_DIR"

  {
    echo "Extraction started: $(date -Iseconds 2>/dev/null || date)"
    echo "device: $(adb shell getprop ro.product.model 2>/dev/null | tr -d '\r')"
    echo "fingerprint: $(adb shell getprop ro.build.fingerprint 2>/dev/null | tr -d '\r')"
    echo ""
  } > "${OUT_ORIGINALS}/extraction_report.txt"

  local ok=0 fail=0
  for pkg in "${TARGET_PACKAGES[@]}"; do
    if extract_package_strings "$pkg"; then
      ok=$((ok + 1))
    else
      fail=$((fail + 1))
    fi
  done

  rm -rf "$TMP_DIR"

  {
    echo "Summary: ok=${ok} fail=${fail}"
    echo "Output: ${OUT_ORIGINALS}/"
  } >> "${OUT_ORIGINALS}/extraction_report.txt"

  echo "[OK] Original strings saved: ${OUT_ORIGINALS}/"
  echo "[OK] Report: ${OUT_ORIGINALS}/extraction_report.txt"
  if [[ "$fail" -gt 0 ]]; then
    echo "[WARN] ${fail} package(s) failed — see extraction_report.txt and errors.txt"
    return 1
  fi
}

print_help() {
  cat <<USAGE
Usage:
  bash e5_overlay_audit.sh before
  bash e5_overlay_audit.sh after
  bash e5_overlay_audit.sh diff
  bash e5_overlay_audit.sh all
  bash e5_overlay_audit.sh extract

Modes:
  before   - capture overlay state BEFORE installation
  after    - capture overlay state AFTER installation
  diff     - build diffs between before/after
  all      - before -> pause -> after -> diff
  extract  - pull original APKs and extract all string keys/translations

Extract output (./original_strings/):
  settings/res/values/strings.xml
  settings/res/values-zh-rCN/strings.xml
  energy/res/...
  hvac/res/...
  extraction_report.txt

Target packages:
  com.flyme.auto.settings
  com.flyme.auto.energy
  com.flyme.auto.hvac

Requirements:
  before/after/diff/all: adb, rg, diff
  extract:             adb, apktool, rg
USAGE
}

main() {
  case "$MODE" in
    before)
      require_cmd adb
      require_cmd rg
      adb_connected
      capture_snapshot "$OUT_BEFORE"
      echo "[OK] BEFORE saved: $OUT_BEFORE"
      ;;
    after)
      require_cmd adb
      require_cmd rg
      adb_connected
      capture_snapshot "$OUT_AFTER"
      echo "[OK] AFTER saved: $OUT_AFTER"
      ;;
    diff)
      require_cmd diff
      make_diff
      echo "[OK] Diff files created in: $BASE_DIR"
      ;;
    all)
      require_cmd adb
      require_cmd rg
      require_cmd diff
      adb_connected
      capture_snapshot "$OUT_BEFORE"
      echo "[OK] BEFORE saved."
      echo "[ACTION] Perform install/uninstall now, then press Enter..."
      read -r _
      capture_snapshot "$OUT_AFTER"
      make_diff
      echo "[OK] AFTER + diff ready in: $BASE_DIR"
      ;;
    extract|extract-originals|originals)
      require_cmd adb
      require_cmd rg
      extract_all_originals
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
