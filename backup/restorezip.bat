@echo off
setlocal

set BACKUP_FILES=(
    "adapter.tar /bigdata/EXD_Geely/exd/"
    "lcfg.tar /"
    "odm.tar /"
    "product.tar /"
    "vendor.tar /"
    "system_ext.tar /"
    "system.tar /"
)

set log_file=..\logs\restore_log.txt

rem Убедитесь, что папка для логов существует
if not exist "..\logs" (
    mkdir "..\logs"
)

echo [%DATE% %TIME%] restore started -------------------------------
echo [%DATE% %TIME%] restore started ------------------------------- >> %log_file%
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

for %%F in %BACKUP_FILES% do (
    set "ARCHIVE=%%~F"
    set "DEST=%%~G"

    echo Restoring %%~F to %%~G...
    adb push %%~F /sdcard/
    if errorlevel 1 (
        echo Failed to push %%~F >> %log_file%
        goto end
    )

    adb exec-out "tar -xvf /sdcard/%%~F -C %%~G"
    if errorlevel 1 (
        echo Failed to extract %%~F >> %log_file%
        goto end
    )

    adb exec-out "rm /sdcard/%%~F"
    if errorlevel 1 (
        echo Failed to remove %%~F >> %log_file%
    )
)

:end
@echo off
echo [%DATE% %TIME%] restore completed -------------------------------
echo [%DATE% %TIME%] restore completed ------------------------------- >> %log_file%
pause
