#!/bin/bash

set -euo pipefail

LOG_DIR="../logs"
LOG_FILE="$LOG_DIR/install_log.txt"
APK_PATH="../app_to_install/AppDrawer v1.2.apk"

# Создание папки для логов, если не существует
mkdir -p "$LOG_DIR"

echo "🔍 Поиск подключённых устройств..."
adb devices

# Проверка, есть ли хотя бы одно устройство в статусе "device"
if ! adb devices | grep -w "device" | grep -v "List" > /dev/null; then
  echo "❌ Устройства не найдены. Подключите устройство и включите отладку по USB."
  exit 1
fi

# Установка APK
echo "📦 Установка приложения: $(basename "$APK_PATH")..."
if adb install -d -r "$APK_PATH" >> "$LOG_FILE" 2>&1; then
  echo "✅ Установка прошла успешно."
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] ✅ Усп
