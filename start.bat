@echo off


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
echo 1. Check ADB connection via cable.
echo 2. Run adb_test.bat
echo 3. Run backupzip.bat - Dont work
echo 4. Run backup.bat - Dont work
echo 5. Run restorezip.bat - Dont work
echo 6. Run ADB Console
echo 7. Install base apps pack.
echo 0. Exit
echo ==========================================
set /p choice="choose action (1-7): "

if "%choice%"=="1" (
    adb devices -l | find "device usb" > nul 2>&1
if %errorlevel%==0 (
    echo car connected
    ) else (
    echo check connection to car
    )
pause
cls
goto :menu
)


if "%choice%"=="2" (
    call adb_test.bat
) else if "%choice%"=="3" (
    call backupzip.bat
) else if "%choice%"=="4" (
    call backup.bat
) else if "%choice%"=="5" (
    call restorezip.bat
) else if "%choice%"=="6" (
    cd backup
    cmd
) else if "%choice%"=="7" (
    cd ..\app_to_install
    for %%A in (*.apk) do (
        echo Installing app: %%~nxA
        adb install -g "%%A"
    )
  ) else if "%choice%"=="0" (
    echo Exit from app
    exit /b
) else (
    echo Incorrect choice. Try again.
)

pause
goto menu
