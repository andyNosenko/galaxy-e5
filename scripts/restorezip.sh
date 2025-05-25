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
    local backup_files=("$BACKUP_DIR"/*.tar)
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

# Функция восстановления из выбранного архива
restore_backup() {
    local backup_file="$1"
    local folder_name
    folder_name=$(basename "$backup_file" | sed -E 's/_[0-9]{8}_[0-9]{6}\.tar$//')

    log_info "Начало восстановления из копии: $(basename "$backup_file")"

    # Загружаем архив на устройство
    if adb push "$backup_file" "/sdcard/${folder_name}.tar"; then
        log_info "Архив загружен на устройство: /sdcard/${folder_name}.tar"
    else
        log_error "Ошибка загрузки архива на устройство"
        return 1
    fi

    # Распаковываем архив в соответствующую папку на устройстве
    if adb shell "tar -xf /sdcard/${folder_name}.tar -C /sdcard $folder_name"; then
        log_info "Архив распакован в /sdcard/$folder_name"
    else
        log_error "Ошибка распаковки архива на устройстве"
        return 1
    fi

    # Удаляем архив с устройства
    adb shell "rm -f /sdcard/${folder_name}.tar"
    log_info "Удалён временный архив с устройства"

    log_info "✅ Восстановление завершено успешно"
    return 0
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
