# Shared helpers for e5_*.sh (sourced, not executed).
# Bundled Windows tools live in galaxy-e5/app_to_install/ (no PATH setup needed on Cygwin).

_E5_SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# linux_scripts -> galaxy_e5_overlay -> galaxy-e5
_E5_REPO_ROOT="$(cd "${_E5_SCRIPTS_DIR}/../.." && pwd)"
_E5_TOOLS="${_E5_REPO_ROOT}/app_to_install"

_e5_uname() {
  uname -s 2>/dev/null || echo unknown
}

_e5_is_windows_shell() {
  case "$(_e5_uname)" in
    CYGWIN*|MINGW*|MSYS*) return 0 ;;
    *) return 1 ;;
  esac
}

to_host_path() {
  local p="$1"
  if command -v cygpath >/dev/null 2>&1; then
    cygpath -w "$p"
  else
    printf '%s\n' "$p"
  fi
}

# --- adb ---
resolve_adb_bin() {
  local c
  if _e5_is_windows_shell; then
    for c in "${_E5_TOOLS}/adb.exe" "${_E5_REPO_ROOT}/backup/adb.exe"; do
      [[ -f "$c" ]] && { printf '%s\n' "$c"; return 0; }
    done
  fi
  if type -P adb >/dev/null 2>&1; then
    type -P adb
    return 0
  fi
  for c in "${_E5_TOOLS}/adb" "${_E5_REPO_ROOT}/backup/adb"; do
    [[ -f "$c" ]] && { printf '%s\n' "$c"; return 0; }
  done
  return 1
}

ADB_BIN="$(resolve_adb_bin 2>/dev/null || true)"

adb() {
  if [[ -z "${ADB_BIN:-}" || ! -f "$ADB_BIN" ]]; then
    ADB_BIN="$(resolve_adb_bin)" || {
      echo "[ERROR] adb not found (expected ${_E5_TOOLS}/adb.exe or PATH)" >&2
      return 127
    }
  fi
  command "$ADB_BIN" "$@"
}

require_adb() {
  if [[ -z "${ADB_BIN:-}" || ! -f "$ADB_BIN" ]]; then
    ADB_BIN="$(resolve_adb_bin)" || {
      echo "[ERROR] Required command not found: adb" >&2
      echo "  looked in: ${_E5_TOOLS}/ , backup/ , PATH" >&2
      exit 1
    }
  fi
}

# --- rg ---
resolve_rg_bin() {
  local c
  if _e5_is_windows_shell; then
    for c in "${_E5_TOOLS}/rg.exe"; do
      [[ -f "$c" ]] && { printf '%s\n' "$c"; return 0; }
    done
  fi
  if type -P rg >/dev/null 2>&1; then
    type -P rg
    return 0
  fi
  [[ -f "${_E5_TOOLS}/rg" ]] && { printf '%s\n' "${_E5_TOOLS}/rg"; return 0; }
  return 1
}

RG_BIN="$(resolve_rg_bin 2>/dev/null || true)"

rg() {
  if [[ -z "${RG_BIN:-}" || ! -f "$RG_BIN" ]]; then
    RG_BIN="$(resolve_rg_bin)" || {
      echo "[ERROR] rg not found (expected ${_E5_TOOLS}/rg.exe or PATH)" >&2
      return 127
    }
  fi
  command "$RG_BIN" "$@"
}

require_rg() {
  if [[ -z "${RG_BIN:-}" || ! -f "$RG_BIN" ]]; then
    RG_BIN="$(resolve_rg_bin)" || {
      echo "[ERROR] Required command not found: rg" >&2
      echo "  looked in: ${_E5_TOOLS}/rg.exe , PATH" >&2
      exit 1
    }
  fi
}

# --- java (for apktool) ---
resolve_java_bin() {
  local c
  if _e5_is_windows_shell; then
    for c in "${_E5_TOOLS}/jre/bin/java.exe" "${_E5_TOOLS}/jdk/bin/java.exe"; do
      [[ -f "$c" ]] && { printf '%s\n' "$c"; return 0; }
    done
  fi
  if type -P java >/dev/null 2>&1; then
    type -P java
    return 0
  fi
  return 1
}

JAVA_BIN="$(resolve_java_bin 2>/dev/null || true)"

java() {
  if [[ -z "${JAVA_BIN:-}" || ! -f "$JAVA_BIN" ]]; then
    JAVA_BIN="$(resolve_java_bin)" || {
      echo "[ERROR] java not found (expected ${_E5_TOOLS}/jre/bin/java.exe or PATH)" >&2
      return 127
    }
  fi
  command "$JAVA_BIN" "$@"
}

require_java() {
  if [[ -z "${JAVA_BIN:-}" || ! -f "$JAVA_BIN" ]]; then
    JAVA_BIN="$(resolve_java_bin)" || {
      echo "[ERROR] Required command not found: java" >&2
      echo "  looked in: ${_E5_TOOLS}/jre/bin/java.exe , PATH" >&2
      exit 1
    }
  fi
}

# --- apktool ---
resolve_apktool_jar() {
  local c="${_E5_TOOLS}/apktool.jar"
  [[ -f "$c" ]] && { printf '%s\n' "$c"; return 0; }
  return 1
}

APKTOOL_JAR="$(resolve_apktool_jar 2>/dev/null || true)"

apktool() {
  # Windows/Cygwin: always prefer bundled jar + JRE
  if _e5_is_windows_shell; then
    if [[ -z "${APKTOOL_JAR:-}" || ! -f "$APKTOOL_JAR" ]]; then
      APKTOOL_JAR="$(resolve_apktool_jar)" || true
    fi
    if [[ -n "${APKTOOL_JAR:-}" && -f "$APKTOOL_JAR" ]]; then
      require_java
      command "$JAVA_BIN" -jar "$(to_host_path "$APKTOOL_JAR")" "$@"
      return $?
    fi
  fi

  # macOS/Linux: prefer real apktool from PATH (Homebrew etc.)
  if type -P apktool >/dev/null 2>&1; then
    command "$(type -P apktool)" "$@"
    return $?
  fi

  # Fallback: bundled jar + any java
  if [[ -z "${APKTOOL_JAR:-}" || ! -f "$APKTOOL_JAR" ]]; then
    APKTOOL_JAR="$(resolve_apktool_jar)" || true
  fi
  if [[ -n "${APKTOOL_JAR:-}" && -f "$APKTOOL_JAR" ]]; then
    require_java
    command "$JAVA_BIN" -jar "$(to_host_path "$APKTOOL_JAR")" "$@"
    return $?
  fi

  echo "[ERROR] apktool not found (expected ${_E5_TOOLS}/apktool.jar + jre, or apktool in PATH)" >&2
  return 127
}

require_apktool() {
  if _e5_is_windows_shell; then
    if [[ -z "${APKTOOL_JAR:-}" || ! -f "$APKTOOL_JAR" ]]; then
      APKTOOL_JAR="$(resolve_apktool_jar)" || true
    fi
    if [[ -n "${APKTOOL_JAR:-}" && -f "$APKTOOL_JAR" ]]; then
      require_java
      return 0
    fi
  fi
  if type -P apktool >/dev/null 2>&1; then
    return 0
  fi
  if [[ -z "${APKTOOL_JAR:-}" || ! -f "$APKTOOL_JAR" ]]; then
    APKTOOL_JAR="$(resolve_apktool_jar)" || true
  fi
  if [[ -n "${APKTOOL_JAR:-}" && -f "$APKTOOL_JAR" ]]; then
    require_java
    return 0
  fi
  echo "[ERROR] Required command not found: apktool" >&2
  echo "  looked in: ${_E5_TOOLS}/apktool.jar , PATH" >&2
  exit 1
}

# --- python3 ---
resolve_python3_bin() {
  local c
  if _e5_is_windows_shell; then
    for c in "${_E5_TOOLS}/python/python.exe" "${_E5_TOOLS}/python/python3.exe"; do
      [[ -f "$c" ]] && { printf '%s\n' "$c"; return 0; }
    done
  fi
  if type -P python3 >/dev/null 2>&1; then
    type -P python3
    return 0
  fi
  if type -P python >/dev/null 2>&1; then
    type -P python
    return 0
  fi
  return 1
}

PYTHON3_BIN="$(resolve_python3_bin 2>/dev/null || true)"

python3() {
  if [[ -z "${PYTHON3_BIN:-}" || ! -f "$PYTHON3_BIN" ]]; then
    PYTHON3_BIN="$(resolve_python3_bin)" || {
      echo "[ERROR] python3 not found (expected ${_E5_TOOLS}/python/python.exe or PATH)" >&2
      return 127
    }
  fi
  command "$PYTHON3_BIN" "$@"
}

require_python3() {
  if [[ -z "${PYTHON3_BIN:-}" || ! -f "$PYTHON3_BIN" ]]; then
    PYTHON3_BIN="$(resolve_python3_bin)" || {
      echo "[ERROR] Required command not found: python3" >&2
      echo "  looked in: ${_E5_TOOLS}/python/python.exe , PATH" >&2
      exit 1
    }
  fi
}
