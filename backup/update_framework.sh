#!/bin/bash

set -euo pipefail

# === Настройка лог-файла ===
log_file="../logs/update_framework_log.txt"
mkdir -p "$(dirname "$log_file")"

log() {
  local msg="[$(date '+%Y-%m-%d %H:%M:%S')] $1"
  echo "$msg" | tee -a "$log_file"
}

log "🔄 Начало обновления services.jar -------------------------------"

# === Перезапуск ADB ===
log "Перезапуск ADB-сервера..."
adb kill-server && adb start-server

# === Подключение и подготовка устройства ===
adb wait-for-device
adb root || { log "❌ Не удалось получить root-доступ."; exit 1; }
adb remount || { log "❌ Не удалось выполнить remount."; exit 1; }

# === Копирование обновлённого JAR ===
log "⬆ Обновление services.jar..."
adb push "./system/framework/services-242961-ru.jar" /system/framework/services.jar || {
  log "❌ Ошибка при передаче services-242961-ru.jar"
  exit 1
}

# === Очистка старых oat/dex файлов ===
log "🧹 Очистка старых oat/dex файлов..."
adb shell rm -rf /system/framework/oat/arm64/services.dex
adb shell rm -rf /system/framework/oat/arm64/services.vdex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex

# === Завершение ===
log "✅ Обновление завершено -------------------------------"
read -rp "Нажмите Enter для выхода..."
