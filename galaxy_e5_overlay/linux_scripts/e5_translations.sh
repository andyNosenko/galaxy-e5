#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-help}"
shift || true
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=e5_common.sh
source "${BASE_DIR}/e5_common.sh"
OUT_ORIGINALS="${BASE_DIR}/original_strings"
OUT_EXPORT="${BASE_DIR}/exported"
LOCALE_RU_DIR="${BASE_DIR}/locale_ru"
TRANSLATIONS_DIR="${BASE_DIR}/translations_ru"
OVERLAYS_DIR="${BASE_DIR}/overlays"
TMP_DIR="${BASE_DIR}/.tmp_translations"
KEYSTORE="${BASE_DIR}/.overlay.keystore"
KEYSTORE_PASS="android"
KEY_ALIAS="overlay"

# short_name : overlay_package : overlay_apk_filename
APPS=(settings energy hvac)

overlay_package() {
  case "$1" in
    settings) echo "com.flyme.auto.settings.ru" ;;
    energy)   echo "com.flyme.auto.energy.ru" ;;
    hvac)     echo "com.flyme.auto.hvac.ru" ;;
  esac
}

target_package() {
  case "$1" in
    settings) echo "com.flyme.auto.settings" ;;
    energy)   echo "com.flyme.auto.energy" ;;
    hvac)     echo "com.flyme.auto.hvac" ;;
  esac
}

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

run_extract_from_car() {
  require_cmd adb
  require_cmd apktool
  require_cmd rg
  log "Extracting originals from HU via e5_overlay_audit.sh..."
  "${BASE_DIR}/e5_overlay_audit.sh" extract
  init_ru_locale_files
}

export_flat_tsv() {
  local app src_dir out_file
  require_cmd python3

  mkdir -p "$OUT_EXPORT"

  for app in "${APPS[@]}"; do
    src_dir="${OUT_ORIGINALS}/${app}/res"
    out_file="${OUT_EXPORT}/${app}_strings.tsv"

    if [[ ! -d "$src_dir" ]]; then
      echo "[WARN] Skip export $app: ${src_dir} not found (run extract first)" >&2
      continue
    fi

    log "Export TSV: $app -> ${out_file}"
    python3 - "$src_dir" "$out_file" "$app" <<'PY'
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

res_dir = Path(sys.argv[1])
out_path = Path(sys.argv[2])
app = sys.argv[3]

def load_strings(path: Path) -> dict:
    if not path.exists():
        return {}
    root = ET.parse(path).getroot()
    out = {}
    for node in root.findall("string"):
        name = node.get("name")
        if not name:
            continue
        text = node.text or ""
        for child in node:
            if child.tail:
                text += child.tail
        out[name] = text
    return out

locales = {}
for values_dir in sorted(res_dir.glob("values*")):
    strings_file = values_dir / "strings.xml"
    if strings_file.exists():
        locales[values_dir.name] = load_strings(strings_file)

if not locales:
    print(f"[WARN] No strings.xml in {res_dir}", file=sys.stderr)
    sys.exit(0)

# Prefer stable column order
columns = ["values", "values-zh-rCN", "values-en-rUS"]
extra = sorted(k for k in locales if k not in columns)
columns = [c for c in columns if c in locales] + extra

all_keys = sorted({k for loc in locales.values() for k in loc})

with out_path.open("w", encoding="utf-8") as f:
    f.write("key\t" + "\t".join(columns) + "\n")
    for key in all_keys:
        row = [key]
        for col in columns:
            row.append(locales.get(col, {}).get(key, ""))
        f.write("\t".join(row) + "\n")

print(f"  keys={len(all_keys)} locales={len(columns)}")
PY
  done

  log "Exported: ${OUT_EXPORT}/"
}

compare_translations() {
  local app orig ru report="${OUT_EXPORT}/compare_report.txt"
  require_cmd python3
  mkdir -p "$OUT_EXPORT"

  {
    echo "Compare report: $(date -Iseconds 2>/dev/null || date)"
    echo ""
  } > "$report"

  for app in "${APPS[@]}"; do
    orig="${OUT_ORIGINALS}/${app}/res/values/strings.xml"
    ru="${TRANSLATIONS_DIR}/${app}_strings.xml"

    log "Compare: $app"
    python3 - "$app" "$orig" "$ru" >> "$report" <<'PY'
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

app, orig_path, ru_path = sys.argv[1:4]

def load(path):
    p = Path(path)
    if not p.exists():
        return None
    root = ET.parse(p).getroot()
    return {n.get("name"): (n.text or "") for n in root.findall("string") if n.get("name")}

orig = load(orig_path)
ru = load(ru_path)

print(f"=== {app} ===")
if orig is None:
    print(f"  original: MISSING ({orig_path})")
    print("")
    sys.exit(0)
if ru is None:
    print(f"  translations_ru: MISSING ({ru_path})")
    print("")
    sys.exit(0)

orig_keys = set(orig)
ru_keys = set(ru)
only_orig = sorted(orig_keys - ru_keys)
only_ru = sorted(ru_keys - orig_keys)
common = sorted(orig_keys & ru_keys)

print(f"  original keys: {len(orig_keys)}")
print(f"  ru keys:       {len(ru_keys)}")
print(f"  only original: {len(only_orig)}")
print(f"  only ru:       {len(only_ru)}")
print(f"  common:        {len(common)}")

if only_orig[:5]:
    print(f"  sample only-original: {only_orig[:5]}")
if only_ru[:5]:
    print(f"  sample only-ru: {only_ru[:5]}")
print("")
PY
  done

  log "Report: $report"
}

init_ru_locale_files() {
  local app orig_dir out_file
  require_cmd python3
  mkdir -p "$LOCALE_RU_DIR"

  for app in "${APPS[@]}"; do
    orig_dir="${OUT_ORIGINALS}/${app}/res"
    out_file="${LOCALE_RU_DIR}/${app}_strings.xml"

    if [[ ! -d "$orig_dir" ]]; then
      echo "[WARN] Skip init-ru $app: ${orig_dir} not found (run extract first)" >&2
      continue
    fi

    log "Init locale_ru: $app -> ${out_file}"
    python3 - "$orig_dir" "$out_file" "${TRANSLATIONS_DIR}/${app}_strings.xml" <<'PY'
import sys
import xml.sax.saxutils as sax
from pathlib import Path
import xml.etree.ElementTree as ET

res_dir = Path(sys.argv[1])
out_path = Path(sys.argv[2])
starshine_path = Path(sys.argv[3])

def load_strings(path: Path) -> dict:
    if not path.exists():
        return {}
    root = ET.parse(path).getroot()
    out = {}
    for node in root.findall("string"):
        name = node.get("name")
        if not name:
            continue
        parts = [node.text or ""]
        for child in node:
            if child.text:
                parts.append(child.text)
            if child.tail:
                parts.append(child.tail)
        out[name] = "".join(parts)
    return out

def load_existing_ru(path: Path) -> dict:
    return load_strings(path)

# All locales from original APK on device
locales = {}
for values_dir in sorted(res_dir.glob("values*")):
    sf = values_dir / "strings.xml"
    if sf.exists():
        locales[values_dir.name] = load_strings(sf)

if not locales:
    print(f"[ERROR] No strings in {res_dir}", file=sys.stderr)
    sys.exit(1)

starshine = load_strings(starshine_path)
existing_ru = load_existing_ru(out_path)

all_keys = sorted({k for loc in locales.values() for k in loc})

locale_order = ["values", "values-zh-rCN", "values-en-rUS"]
extra = sorted(k for k in locales if k not in locale_order)
locale_order = [c for c in locale_order if c in locales] + extra

def comment_safe(text: str) -> str:
    return (text or "").replace("--", "- -")

def original_value(key: str) -> str:
    if "values" in locales and key in locales["values"]:
        return locales["values"][key]
    for loc in locale_order:
        if key in locales.get(loc, {}):
            return locales[loc][key]
    return ""

lines = [
    '<?xml version="1.0" encoding="utf-8"?>',
    "<resources>",
    "    <!--",
    "      Русская локаль для overlay. Редактируйте текст в <string>.",
    "      original[...] — оригинал с авто (справка).",
    "      При первом создании в <string> подставляется оригинал.",
    "    -->",
    "",
]

from_original = 0
preserved = 0
for key in all_keys:
    orig = original_value(key)
    for loc in locale_order:
        val = locales.get(loc, {}).get(key)
        if val is not None and val != "":
            lines.append(f"    <!-- original[{loc}]: {comment_safe(val)} -->")
    if key in starshine and starshine[key]:
        lines.append(f"    <!-- starshine: {comment_safe(starshine[key])} -->")

    if key in existing_ru:
        ru_val = existing_ru[key]
        preserved += 1
    else:
        ru_val = orig
        from_original += 1

    lines.append(f'    <string name="{sax.escape(key)}">{sax.escape(ru_val)}</string>')
    lines.append("")

lines.append("</resources>")
lines.append("")

out_path.write_text("\n".join(lines), encoding="utf-8")
print(f"  keys={len(all_keys)} from_original={from_original} preserved={preserved}")
PY
  done

  cat > "${LOCALE_RU_DIR}/README.md" <<'MD'
# locale_ru — ваши русские переводы

Создаётся автоматически после `extract` / `init-ru`.

## Формат

```xml
<!-- original[values]: 充电区域 -->
<!-- original[values-en-rUS]: Charging area -->
<!-- starshine: Зарядка -->
<string name="ac_charging_time_area">充电区域</string>
```

- **original[...]** — оригинал с вашего ГУ (справка, не редактировать)
- **starshine** — подсказка из готовой русификации
- **`<string>`** — сначала копия оригинала; замените на русский перевод

## Сборка

```bash
./e5_translations.sh build   # берёт строки из locale_ru/ автоматически
```
MD

  log "Edit Russian translations in: ${LOCALE_RU_DIR}/"
}

ensure_keystore() {
  if [[ -f "$KEYSTORE" ]]; then
    return 0
  fi
  require_cmd keytool
  log "Creating debug keystore: $KEYSTORE"
  keytool -genkeypair -v \
    -keystore "$KEYSTORE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA -keysize 2048 -validity 10000 \
    -storepass "$KEYSTORE_PASS" -keypass "$KEYSTORE_PASS" \
    -dname "CN=Overlay,O=Local,C=RU" >/dev/null 2>&1
}

sign_apk() {
  local apk="$1"
  if command -v apksigner >/dev/null 2>&1; then
    apksigner sign --ks "$KEYSTORE" --ks-pass "pass:${KEYSTORE_PASS}" \
      --key-pass "pass:${KEYSTORE_PASS}" --ks-key-alias "$KEY_ALIAS" "$apk"
    return 0
  fi
  if command -v jarsigner >/dev/null 2>&1; then
    jarsigner -keystore "$KEYSTORE" -storepass "$KEYSTORE_PASS" \
      -keypass "$KEYSTORE_PASS" "$apk" "$KEY_ALIAS"
    return 0
  fi
  echo "[ERROR] Need apksigner or jarsigner to sign APK" >&2
  return 1
}

resolve_strings_source() {
  local app="$1"
  local custom_dir="${TRANSLATIONS_SOURCE:-}"

  if [[ -z "$custom_dir" ]]; then
    if [[ -f "${LOCALE_RU_DIR}/${app}_strings.xml" ]]; then
      echo "${LOCALE_RU_DIR}/${app}_strings.xml"
      return 0
    fi
    custom_dir="$TRANSLATIONS_DIR"
  fi

  if [[ -f "${custom_dir}/${app}_strings.xml" ]]; then
    echo "${custom_dir}/${app}_strings.xml"
    return 0
  fi
  if [[ -f "${custom_dir}/${app}/res/values/strings.xml" ]]; then
    echo "${custom_dir}/${app}/res/values/strings.xml"
    return 0
  fi
  return 1
}

build_overlay_apk() {
  local app="$1"
  local pkg src_apk build_dir strings_src out_apk unsigned_apk
  pkg="$(overlay_package "$app")"
  src_apk="${OVERLAYS_DIR}/${pkg}.apk"
  build_dir="${TMP_DIR}/build_${app}"
  out_apk="${OVERLAYS_DIR}/${pkg}.apk"
  unsigned_apk="${TMP_DIR}/${pkg}.unsigned.apk"

  strings_src="$(resolve_strings_source "$app")" || {
    echo "[ERROR] No strings source for $app (check locale_ru/ or translations_ru/)" >&2
    return 1
  }

  if [[ ! -f "$src_apk" ]]; then
    echo "[ERROR] Template APK missing: $src_apk" >&2
    return 1
  fi

  log "Build overlay: $app"
  log "  strings: $strings_src"
  rm -rf "$build_dir"
  apktool d -f "$src_apk" -o "$build_dir" >/dev/null

  mkdir -p "${build_dir}/res/values"
  cp "$strings_src" "${build_dir}/res/values/strings.xml"

  apktool b "$build_dir" -o "$unsigned_apk" >/dev/null
  cp "$unsigned_apk" "$out_apk"
  sign_apk "$out_apk"
  log "  -> $out_apk"
}

build_all_overlays() {
  require_cmd apktool
  ensure_keystore
  rm -rf "$TMP_DIR"
  mkdir -p "$TMP_DIR" "$OVERLAYS_DIR"

  local app failures=0
  for app in "${APPS[@]}"; do
    if ! build_overlay_apk "$app"; then
      failures=$((failures + 1))
    fi
  done

  rm -rf "$TMP_DIR"

  if [[ "$failures" -gt 0 ]]; then
    echo "[ERROR] Build failed for ${failures} app(s)" >&2
    return 1
  fi
  log "All overlays built in: ${OVERLAYS_DIR}/"
}

cmd_pipeline() {
  run_extract_from_car
  export_flat_tsv
  compare_translations
  log "Done. Edit locale_ru/*.xml (original in <string>, replace with Russian), then: $0 build"
}

print_help() {
  cat <<USAGE
Usage:
  bash e5_translations.sh extract              # оригиналы с ГУ -> original_strings/ + locale_ru/
  bash e5_translations.sh init-ru               # создать/обновить locale_ru/ из original_strings/
  bash e5_translations.sh export               # original_strings/ -> exported/*.tsv
  bash e5_translations.sh compare              # original vs translations_ru/
  bash e5_translations.sh build                # locale_ru/ -> overlays/*.apk
  bash e5_translations.sh pipeline             # extract + init-ru + export + compare
  bash e5_translations.sh all                  # alias for pipeline

locale_ru/ — файлы для ручного перевода.
В <string> сначала стоит оригинал с авто; замените на русский.
Комментарии original[...] и starshine — справка.

Options (build):
  TRANSLATIONS_SOURCE=/path/to/dir $0 build
    По умолчанию build берёт строки из locale_ru/

Examples:
  ./e5_translations.sh pipeline
  # правка locale_ru/settings_strings.xml
  ./e5_translations.sh build
  ./e5_overlay_manage.sh install

Requirements:
  extract/pipeline/init-ru: adb, apktool, rg, python3
  export/compare:           python3
  build:                    apktool, keytool, apksigner or jarsigner
USAGE
}

main() {
  case "$MODE" in
    extract)
      run_extract_from_car
      ;;
    init-ru|init_ru|locale-ru)
      init_ru_locale_files
      ;;
    export|export-tsv)
      export_flat_tsv
      ;;
    compare|diff)
      compare_translations
      ;;
    build)
      build_all_overlays
      ;;
    pipeline|all)
      cmd_pipeline
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
