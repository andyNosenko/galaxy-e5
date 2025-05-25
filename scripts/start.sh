#!/bin/bash

# === Настройки ===
SCRIPT_DIR="$(dirname "$0")"
LOG_DIR="$SCRIPT_DIR/logs"
LOG_FILE="$LOG_DIR/adb_menu.log"
APK_DIR="$SCRIPT_DIR/../apps_to_install"
BACKUP_DIR="$SCRIPT_DIR/backup"
MAX_LOG_FILES=5

# Подключение общих функций
source "$SCRIPT_DIR/config.sh"
source "$SCRIPT_DIR/logger.sh"
source "$SCRIPT_DIR/adb_utils.sh"

if [ -f "$SCRIPT_DIR/art.txt" ]; then
    cat "$SCRIPT_DIR/art.txt"
    sleep 2
fi

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
    local apk_dir="$APK_DIR"
    local temp_file
    temp_file=$(mktemp)

    # Получаем список установленных пакетов (user 0)
    if ! adb shell pm list packages -3 --user 0 | sed 's/^package://g' > "$temp_file"; then
        log "❌ Ошибка получения списка приложений"
        rm "$temp_file"
        return 1
    fi

    local apk_path apk_name cleaned_apk_name installed_pkg cleaned_pkg
    declare -a matched_apps=()

    # Функция для очистки имени: в нижний регистр, удалить пробелы, цифры, знаки пунктуации
    clean_name() {
        echo "$1" | tr '[:upper:]' '[:lower:]' | tr -d ' .-_v0123456789'
    }

    echo "🔍 Поиск установленных приложений, соответствующих APK в '$apk_dir'..."

    while IFS= read -r installed_pkg; do
        cleaned_pkg=$(clean_name "$installed_pkg")

        for apk_path in "$apk_dir"/*.apk; do
            [ -e "$apk_path" ] || continue

            apk_name=$(basename "$apk_path" .apk)
            cleaned_apk_name=$(clean_name "$apk_name")

            # Проверяем частичное совпадение: либо apk_name в пакете, либо пакет в apk_name
            if [[ "$cleaned_pkg" == *"$cleaned_apk_name"* ]] || [[ "$cleaned_apk_name" == *"$cleaned_pkg"* ]]; then
                # Добавляем если ещё нет
                if [[ ! " ${matched_apps[*]} " =~ " $installed_pkg " ]]; then
                    matched_apps+=("$installed_pkg")
                fi
                break
            fi
        done
    done < "$temp_file"

    if [ ${#matched_apps[@]} -eq 0 ]; then
        log "❌ Подходящие установленные приложения не найдены"
        rm "$temp_file"
        return 1
    fi

    echo "📱 Найденные установленные приложения:"
    for i in "${!matched_apps[@]}"; do
        echo "$((i+1)). ${matched_apps[$i]}"
    done
    echo "0. Отмена"

    read -rp "Выберите номер приложения для удаления: " choice

    if [[ -z "$choice" || "$choice" == "0" ]]; then
        log "🚫 Удаление отменено"
        rm "$temp_file"
        return 0
    fi

    if ! [[ "$choice" =~ ^[0-9]+$ ]] || [ "$choice" -lt 1 ] || [ "$choice" -gt ${#matched_apps[@]} ]; then
        log "❌ Неверный выбор"
        rm "$temp_file"
        return 1
    fi

    uninstall_app "${matched_apps[$((choice-1))]}"

    rm "$temp_file"
}




# Функция удаления приложений из apps_to_install
uninstall_installed_apps() {
    check_apk_files
    if [ $? -ne 0 ]; then
        return 1
    fi

    local temp_file=$(mktemp)

    # Получаем список всех установленных пакетов (user 0)
    if ! adb shell pm list packages -3 --user 0 | sed 's/^package://g' > "$temp_file"; then
        log "❌ Ошибка получения списка приложений"
        rm "$temp_file"
        return 1
    fi

    clean_name() {
        echo "$1" | tr '[:upper:]' '[:lower:]' | tr -d ' .-_v0123456789'
    }

    local apk_name cleaned_apk_name installed_pkg cleaned_pkg
    declare -a matched_pkgs=()

    # Собираем список подходящих пакетов
    for apk in "$APK_DIR"/*.apk; do
        [ -e "$apk" ] || continue
        apk_name=$(basename "$apk" .apk)
        cleaned_apk_name=$(clean_name "$apk_name")

        while IFS= read -r installed_pkg; do
            cleaned_pkg=$(clean_name "$installed_pkg")

            if [[ "$cleaned_pkg" == *"$cleaned_apk_name"* ]] || [[ "$cleaned_apk_name" == *"$cleaned_pkg"* ]]; then
                # Добавляем в список, если ещё нет
                if [[ ! " ${matched_pkgs[*]} " =~ " $installed_pkg " ]]; then
                    matched_pkgs+=("$installed_pkg")
                fi
            fi
        done < "$temp_file"
    done

    rm "$temp_file"

    if [ ${#matched_pkgs[@]} -eq 0 ]; then
        log "ℹ️ Нет установленных приложений из $APK_DIR"
        return 0
    fi

    echo "📱 Найдены установленные приложения, соответствующие APK:"
    for pkg in "${matched_pkgs[@]}"; do
        echo "- $pkg"
    done

    read -rp "Подтвердите удаление всех перечисленных приложений? (y/N): " confirm
    if [[ "$confirm" != [Yy] ]]; then
        log "🚫 Удаление отменено"
        return 0
    fi

    local uninstalled_count=0
    for pkg in "${matched_pkgs[@]}"; do
        uninstall_app "$pkg" && ((uninstalled_count++))
    done

    log "✅ Удалено приложений: $uninstalled_count"
    return 0
}


# Функция проверки зависимостей
check_dependencies() {
    if ! command -v adb &> /dev/null; then
        log "❌ ADB не найден в системе"
        return 1
    fi
    return 0
}

# Функция проверки состояния устройства
check_device_state() {
    if [ "$(adb shell getprop sys.boot_completed 2>/dev/null)" = "1" ]; then
        log "Устройство загружено"
        return 0
    fi
    log "❌ Устройство не загружено"
    return 1
}

# Функция проверки USB соединения
check_usb_connection() {
    if [ "$(adb shell getprop sys.usb.state 2>/dev/null)" = "mtp" ]; then
        log "USB соединение активно"
        return 0
    fi
    log "❌ USB соединение неактивно"
    return 1
}

# Функция проверки root прав
check_root_access() {
    if adb shell "su -c 'id'" &>/dev/null; then
        log "✅ Root права доступны"
        return 0
    fi
    log "❌ Нет root прав"
    return 1
}

# Функция проверки наличия резервных копий
check_backup_files() {
    local backup_count=$(ls -1 "$BACKUP_DIR"/*.ab 2>/dev/null | wc -l)
    if [ "$backup_count" -eq 0 ]; then
        log "❌ Резервные копии не найдены в $BACKUP_DIR"
        return 1
    fi
    log "📦 Найдено резервных копий: $backup_count"
    return 0
}

# Функция проверки целостности резервной копии
verify_backup() {
    local backup_file="$1"
    if [ ! -f "$backup_file" ]; then
        log "❌ Файл резервной копии не найден"
        return 1
    fi

    local file_size=$(stat -f%z "$backup_file" 2>/dev/null || stat -c%s "$backup_file" 2>/dev/null)
    if [ "$file_size" -lt 1024 ]; then
        log "❌ Файл резервной копии поврежден"
        return 1
    fi

    log "✅ Резервная копия проверена"
    return 0
}

# Функция очистки старых резервных копий
cleanup_old_backups() {
    local max_backups=5
    local backup_files=("$BACKUP_DIR"/*.ab)
    local backup_count=${#backup_files[@]}

    if [ "$backup_count" -gt "$max_backups" ]; then
        local to_delete=$((backup_count - max_backups))
        for ((i=0; i<to_delete; i++)); do
            rm "${backup_files[$i]}"
            log "Удалена старая резервная копия: $(basename "${backup_files[$i]}")"
        done
    fi
    return 0
}

# Функция получения списка установленных приложений
get_installed_apps() {
    local temp_file=$(mktemp)
    adb shell pm list packages -3 > "$temp_file"
    if [ $? -ne 0 ]; then
        log "❌ Ошибка получения списка приложений"
        rm "$temp_file"
        return 1
    fi

    local found_apps=0
    echo "Установленные приложения из apps_to_install:"
    for apk in "$APK_DIR"/*.apk; do
        local apk_name=$(basename "$apk" .apk)
        while IFS=: read -r _ package; do
            if [ "$apk_name" = "$package" ]; then
                echo "- $package"
                ((found_apps++))
            fi
        done < "$temp_file"
    done

    rm "$temp_file"
    if [ $found_apps -eq 0 ]; then
        log "ℹ️ Нет установленных приложений из apps_to_install"
    else
        log "📦 Найдено установленных приложений: $found_apps"
    fi
    return 0
}

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
    echo "11. Запустить ADB консоль"
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
        11)
            log "Запуск ADB-консоли"
            cd ../adb
            bash
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
