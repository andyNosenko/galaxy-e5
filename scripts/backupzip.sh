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

# Создание резервной копии
create_backup() {
    local timestamp
    timestamp=$(date +%Y%m%d_%H%M%S)
    local folders=("Documents" "Download")  # Папки для бэкапа

    for folder in "${folders[@]}"; do
        local remote_path="/sdcard/$folder"
        local local_path="$BACKUP_DIR/${folder}_${timestamp}.tar"

        log_info "📦 Архивирование папки $folder и сохранение в $local_path..."

        if adb shell "[ -d \"$remote_path\" ]"; then
            if adb exec-out "cd /sdcard && tar -cf - \"$folder\"" > "$local_path"; then
                log_info "✅ Архив $folder сохранён в $local_path"
            else
                log_error "❌ Ошибка при сохранении архива $folder"
                rm -f "$local_path"
            fi
        else
            log_error "❌ Папка $remote_path не найдена на устройстве"
        fi
    done

    return 0
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
