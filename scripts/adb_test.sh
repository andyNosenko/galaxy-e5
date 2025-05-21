#!/bin/bash
set -euo pipefail

source ../config.sh
source logger.sh
source adb_utils.sh

# Проверка ADB соединения
check_adb_connection() {
    log_info "Проверка ADB соединения..."
    if ! adb devices | grep -q "device$"; then
        log_error "ADB устройство не найдено"
        return 1
    fi
    log_info "ADB устройство найдено"
    return 0
}

# Получение информации об устройстве
get_device_info() {
    log_info "Получение информации об устройстве..."
    local info
    info=$(adb shell getprop ro.product.model 2>/dev/null)
    if [ $? -eq 0 ]; then
        log_info "Модель устройства: $info"
    else
        log_error "Не удалось получить информацию об устройстве"
    fi
}

# Проверка состояния батареи
check_battery() {
    log_info "Проверка состояния батареи..."
    local battery
    battery=$(adb shell dumpsys battery | grep "level" | awk '{print $2}')
    if [ -n "$battery" ]; then
        log_info "Уровень заряда: $battery%"
    else
        log_error "Не удалось получить информацию о батарее"
    fi
}

main() {
    log_info "Запуск тестирования ADB"
    
    # Проверка версии ADB
    if ! check_adb_version; then
        log_error "Тестирование прервано"
        exit 1
    fi
    
    # Проверка соединения
    if ! check_adb_connection; then
        log_error "Тестирование прервано"
        exit 1
    fi
    
    # Получение информации об устройстве
    local device_info
    device_info=$(get_device_info)
    if [ $? -eq 0 ]; then
        log_info "Модель устройства: $device_info"
    else
        log_error "Не удалось получить информацию об устройстве"
    fi
    
    # Проверка батареи
    local battery_level
    battery_level=$(get_battery_level)
    if [ $? -eq 0 ]; then
        log_info "Уровень заряда: $battery_level%"
    else
        log_error "Не удалось получить информацию о батарее"
    fi
    
    # Проверка места на устройстве
    if ! check_device_space; then
        log_error "Недостаточно места на устройстве"
    fi
    
    log_info "Тестирование завершено"
}

main 