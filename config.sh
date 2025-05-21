#!/bin/bash

# === Основные настройки ===
DEFAULT_IP="192.168.1.5"
APK_DIR="../app_to_install"
LOG_DIR="logs"
LOG_FILE="$LOG_DIR/adb_menu.log"
MAX_LOG_SIZE=10485760  # 10MB
MAX_LOG_FILES=5

# === Уровни логирования ===
LOG_LEVEL_INFO="INFO"
LOG_LEVEL_ERROR="ERROR"
LOG_LEVEL_DEBUG="DEBUG"

# === Пути к скриптам ===
SCRIPTS_DIR="scripts"
ADB_DIR="adb"
BACKUP_DIR="backup" 