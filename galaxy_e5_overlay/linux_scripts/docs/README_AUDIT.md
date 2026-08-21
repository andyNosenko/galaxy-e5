# E5 Overlay Audit (before/after)

Этот чек-лист фиксирует поведенческий след русификации на ГУ:
- какие overlay включаются;
- какие пакеты появляются;
- где лежат overlay-файлы;
- какие idmap создаются.

## Быстрый старт

```bash
cd _decompile/starshine_1.8.0/e5_audit
bash e5_overlay_audit.sh before
# установить русификацию
bash e5_overlay_audit.sh after
bash e5_overlay_audit.sh diff
```

Или один прогон:

```bash
bash e5_overlay_audit.sh all
```

## Что собирается

- `before/overlay_list.txt` и `after/overlay_list.txt` (`adb shell cmd overlay list`)
- `before/packages_overlay_filtered.txt` и `after/...` (`pm list packages -f` + фильтр)
- `before/vendor_overlay_ls.txt`, `after/vendor_overlay_ls.txt`
- `before/data_resource_cache_ls.txt`, `after/data_resource_cache_ls.txt`
- `before/product_overlay_ls.txt`, `after/product_overlay_ls.txt`
- `before/system_product_overlay_ls.txt`, `after/system_product_overlay_ls.txt`

При наличии root (`su`):
- `idmap_files.txt`
- `overlay_files_full.txt`

## Какие diff формируются

- `diff_overlay_list.patch`
- `diff_packages_overlay.patch`
- `diff_vendor_overlay.patch`
- `diff_resource_cache.patch`
- `diff_product_overlay.patch`
- `diff_system_product_overlay.patch`
- (опционально) `diff_idmap_files.patch`, `diff_overlay_files_full.patch`

## Что искать в результатах

Ожидаемые overlay-пакеты:
- `com.flyme.auto.settings.ru`
- `com.flyme.auto.energy.ru`
- `com.flyme.auto.hvac.ru`

Ожидаемые признаки:
- новые/активированные overlay в `overlay_list.txt`
- появление `.ru` пакетов в `packages_overlay_filtered.txt`
- изменения в `/data/resource-cache` (idmap), возможно в `/vendor/overlay`

## Примечания

- Скрипт **ничего не меняет** на устройстве: только читает состояние через ADB.
- Если `adb shell ls /vendor/overlay` дает permission denied, это фиксируется в выходных файлах как есть.
