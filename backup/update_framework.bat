@echo off
setlocal enabledelayedexpansion

:: === Настройка лог-файла ===
set "log_file=..\logs\update_framework_log.txt"

:: === Начало обновления ===
echo [%DATE% %TIME%] 🔄 Начало обновления services.jar ------------------------------- | tee -a %log_file%

:: Перезапуск ADB
echo Перезапуск ADB-сервера... | tee -a %log_file%
adb kill-server && adb start-server
if errorlevel 1 (
    echo ❌ Ошибка при перезапуске ADB. | tee -a %log_file%
    pause
    exit /b
)

adb wait-for-device
adb root
adb remount
if errorlevel 1 (
    echo ❌ Не удалось получить root-доступ или сделать remount. | tee -a %log_file%
    pause
    exit /b
)

:: === Копирование нового JAR ===
echo ⬆ Обновление services.jar... | tee -a %log_file%
adb push ".\system\framework\services-242961-ru.jar" /system/framework/services.jar || (
    echo ❌ Ошибка при передаче services-242961-ru.jar | tee -a %log_file%
    pause
    exit /b
)

:: === Очистка кеша и старых файлов ===
echo 🧹 Очистка старых oat/dex файлов... | tee -a %log_file%
adb shell rm -rf /system/framework/oat/arm64/services.dex
adb shell rm -rf /system/framework/oat/arm64/services.vdex
adb
