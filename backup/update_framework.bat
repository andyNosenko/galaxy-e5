@echo off
chcp 1251 > nul
setlocal enabledelayedexpansion

:: === Настройка лог-файла ===
set "LOG=..\logs\update_framework_log.log"
set "timestamp=[%DATE% %TIME%]"

:: === Начало обновления ===
echo Начало обновления services.jar -------------------------------
echo !timestamp! Начало обновления services.jar ------------------------------- >> "%LOG%"

:: Перезапуск ADB
echo Перезапуск ADB-сервера...
echo !timestamp! Перезапуск ADB-сервера... >> "%LOG%"
adb kill-server && adb start-server
if errorlevel 1 (
    echo Ошибка при перезапуске ADB.
    echo !timestamp! Ошибка при перезапуске ADB. >> "%LOG%"
    pause
)

adb wait-for-device
adb root
adb remount
if errorlevel 1 (
    echo Не удалось получить root-доступ или сделать remount.
    echo !timestamp! Не удалось получить root-доступ или сделать remount. >> "%LOG%"
    pause
)

echo Поиск подключённых устройств...
adb devices

:: Проверка наличия хотя бы одного устройства
set "DEVICE_FOUND="
for /f "skip=1 tokens=1" %%a in ('adb devices') do (
    if not "%%a"=="offline" if not "%%a"=="unauthorized" if not "%%a"=="" (
        set "DEVICE_FOUND=1"
    )
)

if not defined DEVICE_FOUND (
    echo Устройства не найдены. Подключите устройство и включите отладку по USB.
    echo !timestamp! Устройства не найдены. Подключите устройство и включите отладку по USB. >> "%LOG%"
    pause
)

:: === Копирование нового JAR ===
echo Обновление services.jar...
echo !timestamp! Обновление services.jar... >> "%LOG%"
adb push ".\system\framework\services-242961-ru.jar" /system/framework/services.jar || (
    echo Ошибка при передаче services-242961-ru.jar
    echo !timestamp! Ошибка при передаче services-242961-ru.jar >> "%LOG%"
    pause
)

:: === Очистка кеша и старых файлов ===
echo Очистка старых oat/dex файлов...
echo !timestamp! Очистка старых oat/dex файлов... >> "%LOG%"
adb shell rm -rf /system/framework/oat/arm64/services.dex
adb shell rm -rf /system/framework/oat/arm64/services.vdex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex

pause
