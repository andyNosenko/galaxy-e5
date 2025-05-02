@echo off
setlocal enabledelayedexpansion

:: === Настройка лог-файла ===
set "log_file=..\logs\repair_framework_log.txt"

:: === Начало ремонта ===
echo [%DATE% %TIME%] 🔧 Начало восстановления services.jar ------------------------------- | tee -a %log_file%

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

:: === Передача файлов ===
echo ⬆ Копирование services.jar... | tee -a %log_file%
adb push ".\system\framework\services.jar" /system/framework/services.jar || (
    echo ❌ Ошибка при передаче services.jar | tee -a %log_file%
    pause
    exit /b
)

echo ⬆ Копирование services.dex... | tee -a %log_file%
adb push ".\system\framework\oat\arm64\services.dex" /system/framework/oat/arm64/services.dex || (
    echo ❌ Ошибка при передаче services.dex | tee -a %log_file%
    pause
    exit /b
)

echo ⬆ Копирование services.vdex... | tee -a %log_file%
adb push ".\system\framework\oat\arm64\services.vdex" /system/framework/oat/arm64/services.vdex || (
    echo ❌ Ошибка при передаче services.vdex | tee -a %log_file%
    pause
    exit /b
)

:: === Очистка кеша Dalvik ===
echo 🧹 Очистка кэша Dalvik... | tee -a %log_file%
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex

:: === Завершение ===
echo [%DATE% %TIME%] ✅ Восстановление завершено ------------------------------- | tee -a %log_file%
pause
