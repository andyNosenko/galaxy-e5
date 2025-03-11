@echo off
setlocal


set "ip=192.168.1.5"
for /f "tokens=2,3 delims={,}" %%a in ('"WMIC NICConfig where IPEnabled="True" get DefaultIPGateway /value | find "I" "') do if not defined ip set ip=%%~a

rem Подключаемся по Wi-Fi
echo Connecting to ADB over Wi-Fi at %ip%:5555...
cd backup
adb connect %ip%:5555

if errorlevel 1 (
    echo Failed to connect to ADB. Please check your device and network settings.
    pause
    exit /b
)

:menu
cls
echo ==========================================
echo          ADB Script Menu
echo ==========================================
echo 1. Run adb_test.bat
echo 2. Run backupzip.bat
echo 3. Run backup.bat
echo 4. Run restorezip.bat
echo 5. Exit
echo ==========================================
set /p choice="Please select an option (1-5): "

if "%choice%"=="1" (
    call adb_test.bat
) else if "%choice%"=="2" (
    call backupzip.bat
) else if "%choice%"=="3" (
    call backup.bat
) else if "%choice%"=="4" (
    call restorezip.bat
) else if "%choice%"=="5" (
    echo Exiting...
    exit /b
) else (
    echo Invalid choice. Please select a valid option.
)

pause
goto menu
