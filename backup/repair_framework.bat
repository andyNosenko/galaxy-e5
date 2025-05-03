@echo off
chcp 1251 > nul
setlocal enabledelayedexpansion

:: === Настройка лог-файла ===
set "LOG=..\logs\repair_framework_log.log"
set "timestamp=[%DATE% %TIME%]"

:: === Начало ремонта ===
echo Начало восстановления services.jar -------------------------------
echo !timestamp! Начало восстановления services.jar ------------------------------- >> "%LOG%"

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
    exit /b
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
    exit /b
)

:: === Передача файлов ===
echo Копирование services.jar...
echo !timestamp! Копирование services.jar... >> "%LOG%"
adb push ".\system\framework\services.jar" /system/framework/services.jar || (
    echo Ошибка при передаче services.jar
    echo !timestamp! Ошибка при передаче services.jar >> "%LOG%"
    pause
    exit /b
)

echo Копирование services.dex...
echo !timestamp! Копирование services.dex... >> "%LOG%"
adb push ".\system\framework\oat\arm64\services.dex" /system/framework/oat/arm64/services.dex || (
    echo Ошибка при передаче services.dex
    echo !timestamp! Ошибка при передаче services.dex >> "%LOG%"
    pause
    exit /b
)

echo Копирование services.vdex...
echo !timestamp! Копирование services.vdex... >> "%LOG%"
adb push ".\system\framework\oat\arm64\services.vdex" /system/framework/oat/arm64/services.vdex || (
    echo Ошибка при передаче services.vdex
    echo !timestamp! Ошибка при передаче services.vdex >> "%LOG%"
    pause
    exit /b
)

:: === Очистка кеша Dalvik ===
echo Очистка кэша Dalvik...
echo !timestamp! Очистка кэша Dalvik... >> "%LOG%"
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex

:: === Завершение ===
echo Восстановление завершено -------------------------------
echo !timestamp! Восстановление завершено ------------------------------- >> "%LOG%"
pause
