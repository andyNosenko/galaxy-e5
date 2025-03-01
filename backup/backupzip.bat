@echo off
setlocal

set BACKUP_FILES=(
    "adapter /bigdata/EXD_Geely/exd/"
    "lcfg /lcfg"
    "odm /odm"
    "product /product"
    "vendor /vendor"
    "system_ext /system_ext"
    "system /system"
)

set log_file=..\logs\backup_log.txt

rem Убедитесь, что папка для логов существует
if not exist "..\logs" (
    mkdir "..\logs"
)

echo [%DATE% %TIME%] backup started -------------------------------
echo [%DATE% %TIME%] backup started ------------------------------- >> %log_file%
@echo on

adb kill-server && adb start-server
adb wait-for-device
adb root
adb remount

for %%F in %BACKUP_FILES% do (
    set "NAME=%%~F"
    set "PATH=%%~G"

    echo Backing up %%~F...
    adb exec-out "tar -cvf /sdcard/%%~F.tar %%~G"
    if errorlevel 1 (
        echo Failed to create backup for %%~F >> %log_file%
        goto end
    )

    adb pull /sdcard/%%~F.tar .
    if errorlevel 1 (
        echo Failed to pull %%~F.tar >> %log_file%
        goto end
    )
)

:end
@echo off
echo [%DATE% %TIME%] backup created -------------------------------
echo [%DATE% %TIME%] backup created ------------------------------- >> %log_file%
pause
