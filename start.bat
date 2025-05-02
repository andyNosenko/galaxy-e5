@echo off
setlocal enabledelayedexpansion

:: === Настройки ===
set "LOG=logs/adb_menu.txt"
set "ip=192.168.1.5"

:: === Показываем ASCII-арт ===
if exist art.txt (
    type art.txt
    timeout /t 2 > nul
)

:: === Лог-функция ===
set "timestamp=[%DATE% %TIME%]"
echo !timestamp! 🔄 Запуск скрипта >> %LOG%

:: === Получаем IP-адрес шлюза, если доступен ===
for /f "tokens=2 delims={,}" %%a in ('"WMIC NICConfig where IPEnabled="True" get DefaultIPGateway /value | find "I" "') do (
    if not defined ip set "ip=%%~a"
)

:: === Подключение к ADB ===
echo Подключение к ADB по Wi-Fi: %ip%:5555...
echo !timestamp! 🌐 Подключение к ADB: %ip%:5555 >> %LOG%

cd backup
adb connect %ip%:5555 > nul
if errorlevel 1 (
    echo ❌ Ошибка подключения к ADB.
    echo !timestamp! ❌ Ошибка подключения к ADB >> %LOG%
    pause
    exit /b
)

:: === Главное меню ===
:menu
cls
echo ==========================================
echo         МЕНЮ ADB-СКРИПТА
echo ==========================================
echo 1. Проверить ADB-соединение по кабелю
echo 2. Запустить adb_test.bat
echo 3. Сделать резервную копию (backupzip.bat)
echo 4. Восстановить из копии (restorezip.bat)
echo 5. Открыть ADB-консоль
echo 6. Установить базовые приложения (*.apk)
echo 0. Выход
echo ==========================================
set /p choice="Выберите действие (0-6): "

:: === Обработка выбора ===

if "%choice%"=="1" (
    adb devices -l | find "device usb" > nul
    if !errorlevel! == 0 (
        echo ✅ Устройство по USB подключено.
        echo !timestamp! ✅ USB подключение активно >> %LOG%
    ) else (
        echo ❌ Устройство по USB не обнаружено.
        echo !timestamp! ❌ USB не обнаружено >> %LOG%
    )
    pause
    goto menu
)

if "%choice%"=="2" (
    call adb_test.bat
    if errorlevel 1 echo !timestamp! ❌ Ошибка в adb_test.bat >> %LOG%
    goto menu
)

if "%choice%"=="3" (
    call backupzip.bat
    if errorlevel 1 echo !timestamp! ❌ Ошибка в backupzip.bat >> %LOG%
    goto menu
)

if "%choice%"=="4" (
    call restorezip.bat
    if errorlevel 1 echo !timestamp! ❌ Ошибка в restorezip.bat >> %LOG%
    goto menu
)

if "%choice%"=="5" (
    echo !timestamp! 🛠 Открытие ADB-консоли >> %LOG%
    cd backup
    cmd
    goto menu
)

if "%choice%"=="6" (
    cd ..\app_to_install
    for %%A in (*.apk) do (
        echo Установка: %%~nxA
        adb install -g "%%A"
        if errorlevel 1 (
            echo ❌ Ошибка при установке: %%~nxA
            echo !timestamp! ❌ Ошибка установки: %%~nxA >> %LOG%
        ) else (
            echo ✅ Установлено: %%~nxA
            echo !timestamp! ✅ Установлено: %%~nxA >> %LOG%
        )
    )
    pause
    goto menu
)

if "%choice%"=="0" (
    echo Выход...
    echo !timestamp! 🚪 Выход из скрипта >> %LOG%
    exit /b
)

echo Неверный выбор. Повторите попытку.
echo !timestamp! ⚠️ Неверный ввод: %choice% >> %LOG%
pause
goto menu
