@echo off
setlocal

set log_file=..\logs\install_log.txt

rem Убедитесь, что папка для логов существует
if not exist "..\logs" (
    mkdir "..\logs"
)

echo Checking for connected devices...
adb devices

rem Проверяем, есть ли хотя бы одно подключенное устройство
for /f "skip=1" %%a in ('adb devices') do (
    set "DEVICE=%%a"
)
if "%DEVICE%"=="" (
    echo No devices found. Please connect a device and enable USB debugging.
    pause
    exit /b
)


echo Installing AppDrawer v1.2.apk...
adb install -d -r "..\app_to_install\AppDrawer v1.2.apk"
if errorlevel 1 (
    echo Installation failed. Check if the APK file is valid or if the device is compatible. >> "%log_file%
    echo Installation failed. Check install_log.txt for more details in the logs folder.
) else (
    echo Installation successful.
)

pause
