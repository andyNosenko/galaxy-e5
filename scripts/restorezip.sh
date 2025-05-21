#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(dirname "$0")"
source "$SCRIPT_DIR/config.sh"
source "$SCRIPT_DIR/logger.sh"
source "$SCRIPT_DIR/adb_utils.sh"

# Проверка наличия устройства
check_device() {
    if ! adb devices | grep -q "device$"; then
        log_error "Устройство не найдено"
        return 1
    fi
    return 0
}

# Выбор файла для восстановления
select_backup_file() {
    local backup_files=("$BACKUP_DIR"/*.ab)
    if [ ${#backup_files[@]} -eq 0 ]; then
        log_error "Резервные копии не найдены"
        return 1
    fi
    
    echo "Доступные резервные копии:"
    local i=1
    for file in "${backup_files[@]}"; do
        echo "$i. $(basename "$file")"
        ((i++))
    done
    
    read -rp "Выберите номер копии для восстановления: " choice
    if ! [[ "$choice" =~ ^[0-9]+$ ]] || [ "$choice" -lt 1 ] || [ "$choice" -gt ${#backup_files[@]} ]; then
        log_error "Неверный выбор"
        return 1
    fi
    
    selected_file="${backup_files[$((choice-1))]}"
    log_info "Выбрана копия: $(basename "$selected_file")"
    return 0
}

# Восстановление из копии
restore_backup() {
    local backup_file="$1"
    
    log_info "Начало восстановления из копии: $(basename "$backup_file")"
    
    if adb restore "$backup_file"; then
        log_info "✅ Восстановление завершено успешно"
        return 0
    else
        log_error "❌ Ошибка при восстановлении"
        return 1
    fi
}

main() {
    log_info "Запуск восстановления из резервной копии"
    
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
    
    # Выбор файла для восстановления
    if ! select_backup_file; then
        log_error "Операция прервана"
        exit 1
    fi
    
    # Восстановление из копии
    if ! restore_backup "$selected_file"; then
        log_error "Операция прервана"
        exit 1
    fi
    
    log_info "Восстановление завершено"
}

main 