#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(dirname "$0")"
source "$SCRIPT_DIR/config.sh"
source "$SCRIPT_DIR/logger.sh"

# Проверка наличия устройства
check_device() {
    if ! adb devices | grep -q "device$"; then
        log_error "Устройство не найдено"
        return 1
    fi
    return 0
}


# === Копирование обновлённого JAR ===
update_services_jar() {
    log_info "⬆ Обновление services.jar..."
    adb push "$SCRIPT_DIR/../backup/system/framework/services-242961-ru.jar" /system/framework/services.jar || {
        log_error "❌ Ошибка при передаче services-242961-ru.jar"
        return 1
    }
    return 0
}

# === Очистка старых oat/dex файлов ===
clean_old_files() {
    log_info "🧹 Очистка старых oat/dex файлов..."
    adb shell rm -rf /system/framework/oat/arm64/services.dex
    adb shell rm -rf /system/framework/oat/arm64/services.vdex
    adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
    adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex
    return 0
}

# === Основная функция ===
main() {
    log_info "🔄 Начало обновления services.jar -------------------------------"

    check_device || exit 1
    update_services_jar || exit 1
    clean_old_files

    log_info "✅ Обновление завершено -------------------------------"
    read -rp "Нажмите Enter для выхода..."
}

main
