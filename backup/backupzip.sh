#!/bin/bash

set -euo pipefail

LOG_FILE="../logs/backup_log.txt"
DATE_NOW=$(date '+%Y-%m-%d %H:%M:%S')

# Старт лога
echo "[$DATE_NOW] 🔄 Начало резервного копирования -------------------------------" | tee -a "$LOG_FILE"

# Создание архива на Android-устройстве
adb exec-out "tar -cvf /sdcard/Documents.tar -C /sdcard Documents"

# Скачивание архива на компьютер
adb pull /sdcard/Documents.tar .

# Удаление архива с устройства
adb shell "rm /sdcard/Documents.tar"

# Завершение лога
DATE_NOW=$(date '+%Y-%m-%d %H:%M:%S')
echo "[$DATE_NOW] ✅ Резервная копия создана -------------------------------" | tee -a "$LOG_FILE"
