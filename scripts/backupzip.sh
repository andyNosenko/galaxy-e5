#!/bin/bash
set -euo pipefail

source ../config.sh
source logger.sh
source adb_utils.sh

# Проверка наличия устройства
check_device() {
    if ! adb devices | grep -q "device$"; then
        log_error "Устройство не найдено"
        return 1
    fi
    return 0
}

# Создание резервной копии
create_backup() {
    local timestamp
    timestamp=$(date +%Y%m%d_%H%M%S)
    local backup_file="$BACKUP_DIR/backup_$timestamp.ab"
    
    log_info "Создание резервной копии: $backup_file"
    
    if adb backup -f "$backup_file" -apk -shared -all; then
        log_info "✅ Резервная копия создана успешно"
        return 0
    else
        log_error "❌ Ошибка при создании резервной копии"
        return 1
    fi
}

# Проверка свободного места
check_disk_space() {
    local required_space=1024 # 1GB в MB
    local available_space
    
    available_space=$(df -m . | awk 'NR==2 {print $4}')
    if [ "$available_space" -lt "$required_space" ]; then
        log_error "Недостаточно места на диске"
        return 1
    fi
    return 0
}

main() {
    log_info "Запуск создания резервной копии"
    
    # Проверка версии ADB
    if ! check_adb_version; then
        log_error "Операция прервана"
        exit 1
    fi
    
    # Проверка соединения
    if ! check_adb_connection; then
        log_error "Операция прервана"
        exit 1
    fi
    
    # Проверка места на устройстве
    if ! check_device_space; then
        log_error "Операция прервана"
        exit 1
    fi
    
    # Проверка места на компьютере
    if ! check_disk_space; then
        log_error "Операция прервана"
        exit 1
    fi
    
    # Создание резервной копии
    if ! create_backup; then
        log_error "Операция прервана"
        exit 1
    fi
    
    log_info "Резервное копирование завершено"
}

main 