@echo off
chcp 1251 > nul
setlocal enabledelayedexpansion

set "LOG=..\logs\install_log.log"
set "apk_path=..\app_to_install\AppDrawer v1.2.apk"
set "timestamp=[%DATE% %TIME%]"

:: Создание папки для логов, если не существует
if not exist "..\logs" (
    mkdir "..\logs"
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

:: Установка APK
echo Установка приложения: AppDrawer v1.2.apk ...
echo !timestamp! Установка приложения: AppDrawer v1.2.apk ... >> "%LOG%"
adb install -d -r "%apk_path%" >> "%log_file%" 2>&1

if errorlevel 1 (
    echo Ошибка установки. Подробнее в "%LOG%".
    echo !timestamp!  Установка не удалась. APK: %apk_path% >> "%LOG%"
) else (
    echo Установка прошла успешно.
    echo !timestamp! Успешная установка. APK: %apk_path% >> "%LOG%"
)

pause
