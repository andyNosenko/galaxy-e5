# Starshine 1.8.0 Binary Intelligence Report

## 1) Target apps / resources extracted
- **energy**: package=`com.flyme.auto.energy.ru`; strings(values)=525; locales=res/values-en-rUS/strings.xml, res/values-zh-rCN/strings.xml, res/values/strings.xml
- **hvac**: package=`com.flyme.auto.hvac.ru`; strings(values)=401; locales=res/values-en-rUS/strings.xml, res/values-zh-rCN/strings.xml, res/values/strings.xml
- **settings**: package=`com.flyme.auto.settings.ru`; strings(values)=970; locales=res/values-en-rUS/strings.xml, res/values-zh-rCN/strings.xml, res/values/strings.xml

## 2) Installation pipeline markers (from EXE strings)
- `/data/local/tmp/deployagent dump 3 %s`
- `/data/local/tmp/deployagent apply - -pm %s`
- `/data/local/tmp/deployagent apply - -o %s`
- `/data/local/tmp/deployagent.jar`
- `/data/local/tmp/deployagent`
- `exec app_process $base com.android.fastdeploy.DeployAgent "$@"`

## 3) Package manager command markers
- `pm uninstall -k`
- `cmd package uninstall -k`
- `pm list packages -f`
- `pm install-write -S %d %d -- -`
- `pm install-commit %d -- -`
- `pm list packages -f <packageName>`
- `pm install-create <args...>`
- `pm install-write -S <size> <sessionId> -- -`
- `pm install-commit <sessionId> -- -`

## 4) Overlay / idmap markers
- `installOverlay`
- `uninstallOverlay`
- `BaseOverlayOperation`
- `/vendor/overlay/`
- `.frro`
- `idmap: too small to contain any mapping`
- `idmap: target package ID is invalid (%02x)`
- `idmap: no mappings`
- `idmap: too many mappings. Only 255 are possible but %u are present`
- `idmap: unexpected NULL parameter`
- `idmap: target path exceeds idmap file format limit of 255 chars`
- `idmap: overlay path exceeds idmap file format limit of 255 chars`
- `idmap: invalid overlay package`
- `idmap: invalid target package`
- `idmap: no matching resources`
- `idmap: header is not word aligned`
- `idmap: header too small (%d bytes)`
- `idmap: no magic found in header (is 0x%08x, expected 0x%08x)`
- `idmap: version mismatch in header (is 0x%08x, expected 0x%08x)`
- `idmap: entry header is not word aligned`
- `idmap: entry header is too small (%u bytes)`
- `idmap: invalid type map (%u -> %u)`
- `idmap: too small (%u bytes) for the number of entries (%u)`

## 5) ADB transport markers
- `AdbWinApi.DLL`
- `AdbWinApi.dll`
- `AdbWinUsbApi.DLL`
- `AdbWinUsbApi.dll`
- `/adb/adb.exe`
- `getAdbPath`

## 6) Target package IDs
- `com.flyme.auto.energy.ru`
- `com.flyme.auto.hvac.ru`
- `com.flyme.auto.settings.ru`

## 7) Extracted translations
- `_decompile/starshine_1.8.0/translations_ru/energy_strings.xml`
- `_decompile/starshine_1.8.0/translations_ru/hvac_strings.xml`
- `_decompile/starshine_1.8.0/translations_ru/settings_strings.xml`

## 8) Supporting dumps
- `_decompile/starshine_1.8.0/intel/strings/all_strings.txt`
- `_decompile/starshine_1.8.0/intel/strings/interesting_strings.txt`
- `_decompile/starshine_1.8.0/intel/strings/cyrillic_strings.txt`
- `_decompile/starshine_1.8.0/intel/strings/extracted_markers.json`
- `_decompile/starshine_1.8.0/intel/resources/resource_summary.json`