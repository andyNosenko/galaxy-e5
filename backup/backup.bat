@echo off
setlocal

set log_file=..\logs\backup_log.txt

rem Убедитесь, что папка для логов существует
if not exist "..\logs" (
    mkdir "..\logs"
)

echo [%DATE% %TIME%] backup started ------------------------------- >> %log_file%
@echo on

rem Проверка наличия подключенного устройства
adb devices | findstr /C:"device$" >nul
if errorlevel 1 (
    echo No devices found. Please connect a device and enable USB debugging. >> %log_file%
    echo No devices found. Please connect a device and enable USB debugging.
    pause
    exit /b
)

adb kill-server && adb start-server
adb wait-for-device
adb root
adb remount
@echo off

for %%i in (
    "/vendor"
    "/product"
    "/odm"
    "/lcfg"
    "/bigdata/EXD_Geely/exd/adapter"
    "/system-ext"
    "/system"
) do (
    echo Moving data from %%i...
    adb pull -a %%i . 2>&1 >> %log_file%
    if errorlevel 1 (
        echo ERROR: Failed to backup %%i >> %log_file%
        echo ERROR: Failed to backup %%i
        echo Skipping...
    ) else (
        echo --done for %%i >> %log_file%
        echo --done for %%i
    )
)

echo [%DATE% %TIME%] backup created ------------------------------- >> %log_file%
@echo off
pause
