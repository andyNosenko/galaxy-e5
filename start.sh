#!/bin/bash
set -euo pipefail

source config.sh
source scripts/logger.sh

# === Настройки ===
DEFAULT_IP="192.168.1.5"
APK_DIR="../app_to_install"
LOG_FILE="logs/adb_menu.log"

# === Функция логирования ===
log() {
  echo "[$(date '+%Y-%m-%d %H:%M:%S')] $*" | tee -a "$LOG_FILE"
}

# Проверка зависимостей
check_dependencies() {
    local deps=("adb" "netstat")
    for dep in "${deps[@]}"; do
        if ! command -v "$dep" &> /dev/null; then
            log_error "❌ Зависимость не найдена: $dep"
            exit 1
        fi
    done
}

# Создание необходимых директорий
setup_directories() {
    mkdir -p "$LOG_DIR" "$APK_DIR" "$BACKUP_DIR"
}

# Основная функция
main() {
    check_dependencies
    setup_directories
    
    cat art.txt
    log_info "🔄 Запуск ADB-меню"

    # === Определение IP-адреса шлюза (если возможно) ===
    GATEWAY_IP=$(netstat -rn | grep default | awk '{print $2}' | head -n 1)
    ADB_IP="${GATEWAY_IP:-$DEFAULT_IP}"
    log_debug "Определен IP адрес: $ADB_IP"

    # === Подключение по Wi-Fi ===
    #log "Подключение к ADB по Wi-Fi: $ADB_IP:5555..."
    #if adb connect "$ADB_IP:5555" >/dev/null; then
    #  log "✅ Успешное подключение к ADB по Wi-Fi."
    #else
    #  log "❌ Ошибка подключения к ADB по Wi-Fi."
    #  echo "Проверьте устройство и сеть."
    #  exit 1
    #fi

    # === Меню ===
    while true; do
        echo "=========================================="
        echo "           МЕНЮ ADB-СКРИПТА (Mac)"
        echo "=========================================="
        echo "1. Проверить ADB-соединение по USB"
        echo "2. Запустить adb_test.sh"
        echo "3. Сделать резервную копию (backupzip.sh)"
        echo "4. Восстановить из копии (restorezip.sh)"
        echo "5. Открыть ADB-консоль"
        echo "6. Установить все APK из: $APK_DIR"
        echo "0. Выход"
        echo "=========================================="
        read -rp "Выберите действие (0-6): " choice

        case $choice in
            1)
                log_info "Проверка ADB-соединения по USB..."
                if adb devices | grep -q "device$"; then
                    log_info "✅ Устройство по USB подключено."
                else
                    log_error "❌ Устройство по USB не обнаружено."
                fi
                read -rp "Нажмите Enter для продолжения..."
                ;;
            2)
                log_info "▶ Запуск adb_test.sh"
                ./scripts/adb_test.sh || log_error "❌ Ошибка при запуске adb_test.sh"
                read -rp "Нажмите Enter для продолжения..."
                ;;
            3)
                log_info "💾 Создание резервной копии..."
                ./scripts/backupzip.sh || log_error "❌ Ошибка при запуске backupzip.sh"
                read -rp "Нажмите Enter для продолжения..."
                ;;
            4)
                log_info "♻ Восстановление из резервной копии..."
                ./scripts/restorezip.sh || log_error "❌ Ошибка при запуске restorezip.sh"
                read -rp "Нажмите Enter для продолжения..."
                ;;
            5)
                log_info "🖥 Запуск ADB-консоли"
                bash
                ;;
            6)
                log_info "📦 Установка всех APK из $APK_DIR"
                for apk in "$APK_DIR"/*.apk; do
                    [ -f "$apk" ] || continue
                    apk_name=$(basename "$apk")
                    log_info "➡ Установка: $apk_name"
                    if adb install -g "$apk"; then
                        log_info "✅ Установлен: $apk_name"
                    else
                        log_error "❌ Ошибка установки: $apk_name"
                    fi
                done
                read -rp "Нажмите Enter для продолжения..."
                ;;
            0)
                log_info "🚪 Выход из меню"
                break
                ;;
            *)
                log_error "Неверный выбор: $choice"
                echo "Неверный выбор. Повторите попытку."
                ;;
        esac
    done

    log_info "✅ Скрипт завершён"
}

main
