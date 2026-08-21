# Starshine 1.8.0 — Linux toolkit (Geely Galaxy E5 / Flyme Auto)

Самодостаточный набор для русификации через RRO overlay: установка, удаление, извлечение оригиналов, сборка overlay APK.

---

## Быстрый старт

```bash
cd linux_scripts
chmod +x e5_overlay_manage.sh e5_overlay_audit.sh e5_translations.sh
adb devices   # ГУ должен быть виден
```

### Сценарий A — установить готовую русификацию starshine

```bash
./e5_overlay_manage.sh install
./e5_overlay_manage.sh status
# откат:
./e5_overlay_manage.sh uninstall --reboot
```

### Сценарий B — адаптировать перевод под своё авто (E5 / Flyme Auto 1.1.0)

```bash
./e5_translations.sh pipeline      # 1. снять оригиналы + создать locale_ru/
# правка locale_ru/*.xml           # 2. заменить оригинал на русский
./e5_translations.sh build         # 3. собрать overlay APK
./e5_overlay_manage.sh install     # 4. установить
./e5_overlay_manage.sh status      # 5. проверить
```

---

## Структура папки

```
linux_scripts/
├── e5_overlay_manage.sh       # install / uninstall / status
├── e5_overlay_audit.sh        # аудит системы до/после
├── e5_translations.sh         # extract / export / compare / build
│
├── overlays/                  # overlay APK для установки
│   ├── com.flyme.auto.*.ru.apk       # собранные build (или текущие рабочие)
│   └── starshine_original/           # оригиналы из installer-starshine-1.8.0.exe
│
├── translations_ru/           # русские переводы starshine (для правки)
│   ├── settings_strings.xml   # 970 ключей
│   ├── energy_strings.xml     # 525 ключей
│   └── hvac_strings.xml       # 401 ключ
│
├── decoded/                   # декод overlay APK — res/ + manifest (шаблон)
│   ├── settings/  AndroidManifest.xml + res/
│   ├── energy/    ...
│   └── hvac/      ...
│
├── intel/                     # разведка из installer-starshine-1.8.0.exe
│   ├── EXTRACTION_REPORT.md
│   ├── strings/
│   │   ├── all_strings.txt          # все строки из exe (~1.5 MB)
│   │   ├── cyrillic_strings.txt
│   │   ├── extracted_markers.json
│   │   └── interesting_strings.txt
│   └── resources/resource_summary.json
│
├── reference/                 # материалы reverse-engineering (не нужны для E5)
│   ├── jadx_deployagent/      # Java-декомпил DeployAgent (jadx)
│   ├── ghidra_scripts/        # export_uninstall_logic.py
│   ├── extracted/             # сырые архивы из exe
│   └── apktool_decoded/       # полный decode overlay (smali + apktool.yml)
│
├── archive/
│   └── starshine_1.8.0_artifacts.zip  # полный бэкап _decompile (~22 MB)
│
├── docs/
│   ├── ReconstructedDeployAgent.kt
│   └── README_AUDIT.md
│
└── (создаются скриптами)
    ├── original_strings/      # оригиналы с ГУ (extract)
    ├── locale_ru/             # ваш перевод (init-ru), original в <string>
    ├── exported/              # TSV для Excel (export)
    ├── before/ after/         # снимки аудита
    ├── manage_logs/           # логи install/uninstall
    └── .overlay.keystore      # keystore для подписи APK (build)
```

---

## Скрипты

### `e5_translations.sh` — переводы и сборка overlay

| Команда | Действие | Результат |
|---------|----------|-----------|
| `extract` | Снять оригиналы с ГУ | `./original_strings/` + `./locale_ru/` |
| `init-ru` | Создать/обновить файлы перевода | `./locale_ru/` |
| `export` | Экспорт в TSV (key + все локали) | `./exported/*.tsv` |
| `compare` | Сравнить original vs `translations_ru/` | `./exported/compare_report.txt` |
| `build` | Собрать overlay APK (из `locale_ru/`) | `./overlays/*.apk` |
| `pipeline` / `all` | extract + init-ru + export + compare | всё выше |

**`locale_ru/` — ваши файлы для перевода:**

```xml
<!-- original[values]: 充电区域 -->
<!-- original[values-en-rUS]: Charging area -->
<!-- starshine: Зарядка -->
<string name="ac_charging_time_area">充电区域</string>
```

- в `<string>` — **оригинал с авто** (замените на русский)
- комментарии `original[...]` — справка по всем локалям
- повторный `init-ru` не затирает уже изменённые строки

**Сборка из другой папки:**
```bash
TRANSLATIONS_SOURCE=./my_strings ./e5_translations.sh build
```

---

### `e5_overlay_manage.sh` — установка и удаление

| Команда | Действие |
|---------|----------|
| `install` | Установить 3 overlay из `./overlays/` |
| `uninstall` | Полное удаление **без `-k`** + очистка следов |
| `uninstall --reboot` | То же + перезагрузка ГУ |
| `status` | Проверить установленные overlay |

**Uninstall удаляет:**
- overlay-пакеты (`pm uninstall`, не `-k`)
- `/data/local/tmp/deployagent*`
- idmap в `/data/resource-cache` (с root)
- файлы в `/vendor/overlay`, `/product/overlay` (с root)

> Оригинальный `installer-starshine-1.8.0.exe --uninstall` использует `pm uninstall -k` (оставляет data). Этот скрипт удаляет полностью.

---

### `e5_overlay_audit.sh` — аудит системы

| Команда | Действие |
|---------|----------|
| `before` | Снимок системы до изменений |
| `after` | Снимок после изменений |
| `diff` | Diff between before/after |
| `all` | before → пауза → after → diff |
| `extract` | То же что `e5_translations.sh extract` |

**Пример с аудитом:**
```bash
./e5_overlay_audit.sh before
./e5_overlay_manage.sh install
./e5_overlay_audit.sh after
./e5_overlay_audit.sh diff     # → diff_*.patch
```

---

## Overlay-пакеты

| APK | Package | Target app | Ключей |
|-----|---------|------------|--------|
| `com.flyme.auto.settings.ru.apk` | settings.ru | com.flyme.auto.settings | 970 |
| `com.flyme.auto.energy.ru.apk` | energy.ru | com.flyme.auto.energy | 525 |
| `com.flyme.auto.hvac.ru.apk` | hvac.ru | com.flyme.auto.hvac | 401 |

Механизм: **RRO (Runtime Resource Overlay)** — штатный Android. Оригинальные APK в `/system` не модифицируются.

Каждый overlay APK содержит:
- `AndroidManifest.xml` — `targetPackage`, `isStatic=true`, `priority=1`
- `res/values/strings.xml` — переводы (те же `name=` что у оригинала)
- `res/values/public.xml` — те же resource ID

---

## Переводы

### `translations_ru/` — русский из starshine (для правки)

```
<string name="ac_charging_time_area">Зарядка</string>
```

### `original_strings/` — оригиналы с твоего ГУ (после extract)

```
original_strings/
├── settings/res/values/strings.xml        # дефолт (CN)
├── settings/res/values-zh-rCN/strings.xml
├── settings/res/values-en-rUS/strings.xml
├── energy/res/...
├── hvac/res/...
└── extraction_report.txt
```

> Запускай `extract` **до** установки русификации.

### `exported/` — TSV для редактирования в Excel/LibreOffice

```
key    values    values-zh-rCN    values-en-rUS
ac_charging_time_area    充电区域    ...
```

---

## Что нужно установить

### Минимум (install/uninstall/status)

```
adb + rg
```

### Extract оригиналов с авто

| Инструмент | Зачем |
|------------|-------|
| `adb` | `pm path`, `adb pull` |
| `apktool` | декод APK → strings.xml |
| `rg` | подсчёт ключей |

### Export / compare

| Инструмент | Зачем |
|------------|-------|
| `python3` | парсинг XML → TSV |

### Build overlay APK

| Инструмент | Зачем |
|------------|-------|
| `apktool b` | сборка APK |
| `keytool` | keystore (создаётся автоматически в `.overlay.keystore`) |
| `apksigner` или `jarsigner` | подпись APK |

### Опционально

- **root (`su`)** на ГУ — глубокая очистка idmap при uninstall
- **diff** — для аудита before/after

---

## Полные сценарии

### 1. Только установить starshine как есть

```bash
./e5_overlay_manage.sh install
./e5_overlay_manage.sh status
adb reboot   # если UI не обновился
```

### 2. Адаптировать перевод под E5

```bash
./e5_translations.sh pipeline
# правим locale_ru/*.xml — в <string> заменяем оригинал на русский
./e5_translations.sh build
./e5_overlay_manage.sh install
```

### 3. Полный цикл с аудитом

```bash
./e5_overlay_audit.sh before
./e5_translations.sh pipeline
./e5_translations.sh build
./e5_overlay_manage.sh install
./e5_overlay_audit.sh after
./e5_overlay_audit.sh diff
```

### 4. Откат без следов

```bash
./e5_overlay_manage.sh uninstall --reboot
./e5_overlay_manage.sh status   # все .ru должны отсутствовать
```

---

## Справочные материалы

### `decoded/` — декод overlay APK

- `AndroidManifest.xml` — overlay target, isStatic, priority
- `res/values/strings.xml` — русский (дефолт starshine)
- `res/values-zh-rCN/`, `res/values-en-rUS/` — другие локали
- `res/values/public.xml` — таблица resource ID

### `intel/` — из бинарника installer-starshine-1.8.0.exe

- `EXTRACTION_REPORT.md` — команды pm/adb, overlay markers
- `extracted_markers.json` — deployagent, pm, overlay, adb markers
- `resource_summary.json` — статистика строк
- `interesting_strings.txt` — uninstall, overlay, remount строки

### `docs/ReconstructedDeployAgent.kt`

Реконструкция Android-агента установки (`pm install-create/write/commit`).

---

## Правила сборки overlay

1. **Имена ключей** (`name=`) — строго 1:1 с оригинальным APK
2. **Resource ID** в `public.xml` — 1:1 с target APK
3. **`targetPackage`** в manifest — оригинал без `.ru`
4. **`package`** в manifest — overlay с суффиксом (например `.ru`)
5. **`isStatic=true`** — overlay включается автоматически после install

Скрипт `build` берёт шаблон из текущего `./overlays/*.apk`, подставляет `strings.xml` из `translations_ru/` и пересобирает.
