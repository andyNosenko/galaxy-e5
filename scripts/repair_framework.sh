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


# === Передача файлов ===
copy_framework_files() {
    log_info "⬆ Копирование services.jar..."
    adb push "$SCRIPT_DIR/../backup/system/framework/services.jar" /system/framework/services.jar || {
        log_error "❌ Ошибка при передаче services.jar";
        return 1
    }

    log_info "⬆ Копирование services.dex..."
    adb push "$SCRIPT_DIR/../backup/system/framework/oat/arm64/services.dex" /system/framework/oat/arm64/services.dex || {
        log_error "❌ Ошибка при передаче services.dex";
        return 1
    }

    log_info "⬆ Копирование services.vdex..."
    adb push "$SCRIPT_DIR/../backup/system/framework/oat/arm64/services.vdex" /system/framework/oat/arm64/services.vdex || {
        log_error "❌ Ошибка при передаче services.vdex";
        return 1
    }
    return 0
}

# === Очистка кэша Dalvik ===
clean_dalvik_cache() {
    log_info "🧹 Очистка кэша Dalvik..."
    adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
    adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex
    return 0
}

# === Основная функция ===
main() {
    log_info "🔧 Начало восстановления services.jar -------------------------------"

    check_device || exit 1
    copy_framework_files || exit 1
    clean_dalvik_cache

    log_info "✅ Восстановление завершено -------------------------------"
    read -rp "Нажмите Enter для выхода..."
}

main
