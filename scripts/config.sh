#!/bin/bash

# === Настройки ===
SCRIPT_DIR="$(dirname "$0")"
LOG_DIR="$SCRIPT_DIR/logs"
LOG_FILE="$LOG_DIR/adb_menu.log"
APK_DIR="$SCRIPT_DIR/../apps_to_install"
BACKUP_DIR="$SCRIPT_DIR/backup"

# Создание необходимых директорий
mkdir -p "$LOG_DIR"
mkdir -p "$APK_DIR"
mkdir -p "$BACKUP_DIR" 