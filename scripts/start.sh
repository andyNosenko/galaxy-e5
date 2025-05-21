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

# Основное меню
show_menu() {
    clear
    echo "=========================================="
    echo "         МЕНЮ ADB-СКРИПТА (Linux)"
    echo "=========================================="
    echo "1. Проверить ADB-соединение по USB"
    echo "2. Запустить adb_test.sh"
    echo "3. Сделать резервную копию (backupzip.sh)"
    echo "4. Восстановить из копии (restorezip.sh)"
    echo "5. Открыть ADB-консоль"
    echo "6. Установить все APK из: $APK_DIR"
    echo "7. Очистить логи"
    echo "8. Перезапустить ADB сервер"
    echo "9. Список установленных приложений"
    echo "10. Удалить приложение"
    echo "11. Удалить все пользовательские приложения"
    echo "12. Удалить приложения из $APK_DIR"
    echo "0. Выход"
    echo "=========================================="
    read -p "Выберите действие (0-12): " choice

    case $choice in
        1)
            log_info "Проверка ADB соединения..."
            check_adb_connection
            ;;
        2)
            log_info "▶ Запуск adb_test.sh"
            "$SCRIPT_DIR/adb_test.sh"
            ;;
        3)
            log_info "💾 Создание резервной копии..."
            "$SCRIPT_DIR/backupzip.sh"
            ;;
        4)
            log_info "♻ Восстановление из резервной копии..."
            "$SCRIPT_DIR/restorezip.sh"
            ;;
        5)
            log_info "🖥 Запуск ADB-консоли"
            bash
            ;;
        6)
            log_info "📦 Установка всех APK из $APK_DIR"
            install_all_apks
            ;;
        7)
            log_info "🗑️ Очистка логов..."
            cleanup_logs
            ;;
        8)
            log_info "🔄 Перезапуск ADB сервера..."
            restart_adb_server
            ;;
        9)
            log_info "📱 Получение списка приложений..."
            get_installed_apps
            ;;
        10)
            log_info "📱 Получение списка приложений..."
            get_installed_apps
            read -p "Введите имя пакета для удаления: " package
            uninstall_app "$package"
            ;;
        11)
            read -p "Вы уверены, что хотите удалить ВСЕ пользовательские приложения? (y/n): " confirm
            if [[ $confirm == [yY] ]]; then
                uninstall_all_apps
            else
                log_info "❌ Операция отменена"
            fi
            ;;
        12)
            read -p "Вы уверены, что хотите удалить приложения из $APK_DIR? (y/n): " confirm
            if [[ $confirm == [yY] ]]; then
                uninstall_installed_apps
            else
                log_info "❌ Операция отменена"
            fi
            ;;
        0)
            log_info "🚪 Выход из меню"
            exit 0
            ;;
        *)
            log_error "Неверный выбор: $choice"
            echo "Неверный выбор. Повторите попытку."
            ;;
    esac
    
    read -p "Нажмите Enter для продолжения..."
    show_menu
}

# Запуск основного меню
show_menu 