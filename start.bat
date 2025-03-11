@echo off
chcp 65001 > nul
cls


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
echo "   ____           _          ____       _                    _____ ____  ";
echo "  / ___| ___  ___| |_   _   / ___| __ _| | __ ___  ___   _  | ____| ___| ";
echo " | |  _ / _ \/ _ \ | | | | | |  _ / _` | |/ _` \ \/ / | | | |  _| |___ \ ";
echo " | |_| |  __/  __/ | |_| | | |_| | (_| | | (_| |>  <| |_| | | |___ ___) |";
echo "  \____|\___|\___|_|\__, |  \____|\__,_|_|\__,_/_/\_\\__, | |_____|____/ ";
echo "                    |___/                            |___/               ";
echo ==========================================
echo          ADB Script Menu
echo ==========================================
echo Что вы хотите сделать?
echo 1. Проверка подключения к головному устройству по проводу.
echo 2. Run adb_test.bat
echo 3. Run backupzip.bat
echo 4. Run backup.bat
echo 5. Run restorezip.bat
echo 6. Run ADB Console
echo 7. Установка стартового пакета приложений.
echo 0. Выйти
echo ==========================================
set /p choice="Выберите действие для запуска (1-7): "

if "%choice%"=="1" (
    cd backup
    adb devices -l | find "device usb" > nul 2>&1
if %errorlevel%==0 (
    echo Машина подключена
    ) else (
    echo Проверьте подключение к авто
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
    cd ..\apps_to_install
    for %%A in (*.apk) do (
        echo Установка приложений: %%~nxA
        adb install -g "%%A"
    )
    cd ..\backup  rem Возвращаемся в папку backup после установки
  ) else if "%choice%"=="0" (
    echo Выход из программы
    exit /b
) else (
    echo Некорректный выбор. Попробуйте снова.
)

pause
goto menu
