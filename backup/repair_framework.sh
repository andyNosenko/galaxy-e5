#!/bin/bash

set -euo pipefail

# === Настройка лог-файла ===
log_file="../logs/repair_framework_log.txt"
mkdir -p "$(dirname "$log_file")"

log() {
  local msg="[$(date '+%Y-%m-%d %H:%M:%S')] $1"
  echo "$msg" | tee -a "$log_file"
}

log "🔧 Начало восстановления services.jar -------------------------------"

# === Перезапуск ADB ===
log "Перезапуск ADB-сервера..."
adb kill-server && adb start-server

# === Подключение к устройству и получение root ===
adb wait-for-device
adb root || { log "❌ Не удалось получить root-доступ."; exit 1; }
adb remount || { log "❌ Не удалось выполнить remount."; exit 1; }

# === Передача файлов ===
log "⬆ Копирование services.jar..."
adb push "./system/framework/services.jar" /system/framework/services.jar || { log "❌ Ошибка при передаче services.jar"; exit 1; }

log "⬆ Копирование services.dex..."
adb push "./system/framework/oat/arm64/services.dex" /system/framework/oat/arm64/services.dex || { log "❌ Ошибка при передаче services.dex"; exit 1; }

log "⬆ Копирование services.vdex..."
adb push "./system/framework/oat/arm64/services.vdex" /system/framework/oat/arm64/services.vdex || { log "❌ Ошибка при передаче services.vdex"; exit 1; }

# === Очистка кэша Dalvik ===
log "🧹 Очистка кэша Dalvik..."
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex

log "✅ Восстановление завершено -------------------------------"
read -rp "Нажмите Enter для выхода..."
