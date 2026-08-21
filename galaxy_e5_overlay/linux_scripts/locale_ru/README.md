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
