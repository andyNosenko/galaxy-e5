#!/bin/bash

# Общие функции для работы с ADB
source ../config.sh
source logger.sh

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
    local info
    info=$(adb shell getprop ro.product.model 2>/dev/null)
    if [ $? -eq 0 ]; then
        echo "$info"
        return 0
    fi
    return 1
}

# Проверка состояния батареи
get_battery_level() {
    local battery
    battery=$(adb shell dumpsys battery | grep "level" | awk '{print $2}')
    if [ -n "$battery" ]; then
        echo "$battery"
        return 0
    fi
    return 1
}

# Проверка свободного места на устройстве
check_device_space() {
    local required_space=1024 # 1GB в MB
    local available_space
    
    available_space=$(adb shell df /data | awk 'NR==2 {print $4}')
    if [ "$available_space" -lt "$required_space" ]; then
        log_error "Недостаточно места на устройстве"
        return 1
    fi
    return 0
}

# Проверка версии APK
check_apk_version() {
    local apk_file="$1"
    local package_name=$(basename "$apk_file" .apk)
    local installed_version
    local new_version
    
    installed_version=$(adb shell pm dump "$package_name" 2>/dev/null | grep "versionName" | cut -d'=' -f2)
    new_version=$(aapt dump badging "$apk_file" 2>/dev/null | grep "versionName" | cut -d'=' -f2 | tr -d "'")
    
    if [ -n "$installed_version" ] && [ -n "$new_version" ]; then
        if [ "$installed_version" = "$new_version" ]; then
            log_info "ℹ️ Версия $(basename "$apk_file") уже установлена: $new_version"
            return 0
        fi
        log_info "📦 Обновление $(basename "$apk_file") с $installed_version до $new_version"
    else
        log_info "📦 Установка новой версии $(basename "$apk_file")"
    fi
    return 1
}

# Установка APK
install_apk() {
    local apk_file="$1"
    
    if check_apk_version "$apk_file"; then
        log_info "ℹ️ Пропуск: $(basename "$apk_file") (уже установлена последняя версия)"
        return 0
    fi
    
    log_info "Установка: $(basename "$apk_file")"
    if adb install -g -r "$apk_file"; then
        log_info "✅ Установлен: $(basename "$apk_file")"
        return 0
    else
        log_error "❌ Ошибка установки: $(basename "$apk_file")"
        return 1
    fi
}

# Проверка версии ADB
check_adb_version() {
    local version
    version=$(adb version | head -n 1)
    if [ -n "$version" ]; then
        log_info "Версия ADB: $version"
        return 0
    fi
    return 1
}

# Перезагрузка ADB сервера
restart_adb_server() {
    log_info "Перезапуск ADB сервера..."
    adb kill-server
    sleep 2
    if adb start-server; then
        log_info "✅ ADB сервер перезапущен"
        return 0
    else
        log_error "❌ Ошибка перезапуска ADB сервера"
        return 1
    fi
}

# Проверка наличия APK файлов
check_apk_files() {
    local apk_count=0
    for apk in "$APK_DIR"/*.apk; do
        if [ -f "$apk" ]; then
            ((apk_count++))
        fi
    done
    
    if [ $apk_count -eq 0 ]; then
        log_error "❌ APK файлы не найдены в $APK_DIR"
        return 1
    fi
    log_info "📦 Найдено APK файлов: $apk_count"
    return 0
}

# Установка всех APK файлов
install_all_apks() {
    check_apk_files || return 1
    
    log_info "📦 Установка всех APK файлов..."
    for apk in "$APK_DIR"/*.apk; do
        if [ -f "$apk" ]; then
            install_apk "$apk"
        fi
    done
    if [ $? -ne 0 ]; then
        log_error "❌ Ошибка при установке APK файлов"
        return 1
    fi
    log_info "✅ Все APK файлы установлены"
    return 0
}

# Получение списка установленных приложений
get_installed_apps() {
    log_info "📱 Получение списка установленных приложений..."
    if ! adb shell pm list packages -3; then
        log_error "❌ Ошибка получения списка приложений"
        return 1
    fi
    return 0
}

# Удаление одного приложения
uninstall_app() {
    local package_name="$1"
    log_info "🗑️ Удаление приложения: $package_name"
    
    if adb uninstall "$package_name"; then
        log_info "✅ Удалено: $package_name"
        return 0
    else
        log_error "❌ Ошибка удаления: $package_name"
        return 1
    fi
}

# Удаление всех пользовательских приложений
uninstall_all_apps() {
    log_info "🗑️ Удаление всех пользовательских приложений..."
    local error=0
    
    while read -r package; do
        package=$(echo "$package" | cut -d':' -f2)
        if ! uninstall_app "$package"; then
            error=1
        fi
    done < <(adb shell pm list packages -3)
    
    if [ $error -eq 0 ]; then
        log_info "✅ Все приложения удалены"
        return 0
    else
        log_error "❌ Ошибка при удалении приложений"
        return 1
    fi
}

# Удаление приложений из app_to_install
uninstall_installed_apps() {
    log_info "🗑️ Удаление приложений из $APK_DIR..."
    local error=0
    
    for apk in "$APK_DIR"/*.apk; do
        if [ -f "$apk" ]; then
            local apk_name=$(basename "$apk" .apk)
            while read -r package; do
                if [[ "$package" == *"$apk_name"* ]]; then
                    package=$(echo "$package" | cut -d':' -f2)
                    if ! uninstall_app "$package"; then
                        error=1
                    fi
                fi
            done < <(adb shell pm list packages -3)
        fi
    done
    
    if [ $error -eq 0 ]; then
        log_info "✅ Все приложения из $APK_DIR удалены"
        return 0
    else
        log_error "❌ Ошибка при удалении приложений"
        return 1
    fi
} 