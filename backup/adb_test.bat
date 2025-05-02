@echo off
setlocal

set "log_file=..\logs\install_log.txt"
set "apk_path=..\app_to_install\AppDrawer v1.2.apk"

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
    echo ❌ Устройства не найдены. Подключите устройство и включите отладку по USB.
    pause
    exit /b
)

:: Установка APK
echo Установка приложения: AppDrawer v1.2.apk ...
adb install -d -r "%apk_path%" >> "%log_file%" 2>&1

if errorlevel 1 (
    echo ❌ Ошибка установки. Подробнее в "%log_file%".
    echo [%DATE% %TIME%] ❌ Установка не удалась. APK: %apk_path% >> "%log_file%"
) else (
    echo ✅ Установка прошла успешно.
    echo [%DATE% %TIME%] ✅ Успешная установка. APK: %apk_path% >> "%log_file%"
)

pause
