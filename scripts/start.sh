#!/bin/bash

# === Настройки ===
SCRIPT_DIR="$(dirname "$0")"
LOG_DIR="$SCRIPT_DIR/logs"
LOG_FILE="$LOG_DIR/adb_menu.log"
APK_DIR="$SCRIPT_DIR/../apps_to_install"
BACKUP_DIR="$SCRIPT_DIR/backup"

# Подключение общих функций
source "$SCRIPT_DIR/config.sh"
source "$SCRIPT_DIR/logger.sh"
source "$SCRIPT_DIR/adb_utils.sh"

# Проверка наличия ADB
if ! command -v adb &> /dev/null; then
    echo "❌ ADB не найден в системе"
    exit 1
fi

# Основное меню
while true; do
    clear
    echo "=========================================="
    echo "         МЕНЮ ADB-СКРИПТА (Linux)"
    echo "=========================================="
    echo "1. Проверить ADB соединение"
    echo "2. Запустить тест"
    echo "3. Создать резервную копию"
    echo "4. Восстановить из копии"
    echo "5. Установить все APK"
    echo "6. Установить один APK"
    echo "7. Список установленных приложений"
    echo "8. Удалить приложение"
    echo "9. Удалить приложения из apps_to_install"
    echo "10. Очистить логи"
    echo "0. Выход"
    echo "=========================================="

    read -p "Выберите действие (0-10): " choice

    case $choice in
        1)
            check_adb_connection
            read -p "Нажмите Enter для продолжения..."
            ;;
        2)
            "$SCRIPT_DIR/adb_test.sh"
            read -p "Нажмите Enter для продолжения..."
            ;;
        3)
            "$SCRIPT_DIR/backupzip.sh"
            read -p "Нажмите Enter для продолжения..."
            ;;
        4)
            "$SCRIPT_DIR/restorezip.sh"
            read -p "Нажмите Enter для продолжения..."
            ;;
        5)
            install_all_apks
            read -p "Нажмите Enter для продолжения..."
            ;;
        6)
            install_single_apk
            read -p "Нажмите Enter для продолжения..."
            ;;
        7)
            get_installed_apps
            read -p "Нажмите Enter для продолжения..."
            ;;
        8)
            uninstall_single_app
            read -p "Нажмите Enter для продолжения..."
            ;;
        9)
            uninstall_installed_apps
            read -p "Нажмите Enter для продолжения..."
            ;;
        10)
            cleanup_logs
            read -p "Нажмите Enter для продолжения..."
            ;;
        0)
            exit 0
            ;;
        *)
            echo "Неверный выбор"
            read -p "Нажмите Enter для продолжения..."
            ;;
    esac
done

# Функция установки одного APK
install_single_apk() {
    local apk_files=("$APK_DIR"/*.apk)
    if [ ${#apk_files[@]} -eq 0 ]; then
        log "❌ APK файлы не найдены"
        return 1
    fi

    echo "Доступные APK файлы:"
    for i in "${!apk_files[@]}"; do
        echo "$((i+1)). $(basename "${apk_files[$i]}")"
    done

    read -p "Выберите номер APK для установки: " choice
    if ! [[ "$choice" =~ ^[0-9]+$ ]] || [ "$choice" -lt 1 ] || [ "$choice" -gt ${#apk_files[@]} ]; then
        log "❌ Неверный выбор"
        return 1
    fi

    install_apk "${apk_files[$((choice-1))]}"
}

# Функция удаления одного приложения
uninstall_single_app() {
    local temp_file=$(mktemp)
    adb shell pm list packages -3 > "$temp_file"
    if [ $? -ne 0 ]; then
        log "❌ Ошибка получения списка приложений"
        rm "$temp_file"
        return 1
    fi

    local app_count=0
    local app_names=()
    while IFS=: read -r _ package; do
        app_names+=("$package")
        ((app_count++))
    done < "$temp_file"

    if [ $app_count -eq 0 ]; then
        log "❌ Пользовательские приложения не найдены"
        rm "$temp_file"
        return 1
    fi

    echo "Установленные приложения:"
    for i in "${!app_names[@]}"; do
        echo "$((i+1)). ${app_names[$i]}"
    done

    read -p "Выберите номер приложения для удаления: " choice
    if ! [[ "$choice" =~ ^[0-9]+$ ]] || [ "$choice" -lt 1 ] || [ "$choice" -gt $app_count ]; then
        log "❌ Неверный выбор"
        rm "$temp_file"
        return 1
    fi

    uninstall_app "${app_names[$((choice-1))]}"
    rm "$temp_file"
} 