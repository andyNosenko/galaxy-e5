#!/bin/bash

set -euo pipefail

LOG_FILE="../logs/restore_log.txt"
DATE_NOW=$(date '+%Y-%m-%d %H:%M:%S')

# Старт лога
echo "[$DATE_NOW] 🔄 Начало восстановления из резервной копии -------------------------------" | tee -a "$LOG_FILE"

# Загрузка архива на устройство
adb push Documents.tar /sdcard/

# Распаковка архива в папку Documents
adb shell "tar -xvf /sdcard/Documents.tar -C /sdcard Documents"

# Удаление архива после восстановления
adb shell "rm /sdcard/Documents.tar"

# Завершение лога
DATE_NOW=$(date '+%Y-%m-%d %H:%M:%S')
echo "[$DATE_NOW] ✅ Восстановление выполнено успешно -------------------------------" | tee -a "$LOG_FILE"
