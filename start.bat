@echo off
chcp 1251 > nul
setlocal enabledelayedexpansion

:: === Настройки ===
set "LOG=logs\adb_menu.log"
set "ip=192.168.1.5"
set "timestamp=[%DATE% %TIME%]"

:: === ASCII-арт (если есть) ===
if exist art.txt (
    type art.txt
    timeout /t 2 > nul
)

:: === Старт логирования ===
echo Запуск скрипта
echo !timestamp! Запуск скрипта >> "%LOG%"

:: === Получаем IP шлюза ===
for /f "tokens=2 delims={,}" %%a in ('"WMIC NICConfig where IPEnabled="True" get DefaultIPGateway /value | find "I" "') do (
    if not defined ip set "ip=%%~a"
)

:: === Подключение к ADB ===
@REM echo Подключение к ADB по Wi-Fi: %ip%:5555...
@REM echo !timestamp! Подключение к ADB: %ip%:5555 >> "%LOG%"
@REM
@REM cd backup
@REM adb connect %ip%:5555 > nul
@REM if errorlevel 1 (
@REM     echo Ошибка подключения к ADB.
@REM     echo !timestamp! Ошибка подключения к ADB >> "%LOG%"
@REM     pause
@REM     exit /b
@REM )



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

if "%choice%"=="1" (
@REM     adb devices -l | find "device usb" > nul
@REM     if !errorlevel! == 0 (
@REM         echo Устройство по USB подключено.
@REM         echo !timestamp! USB подключение активно >> "%LOG%"
@REM     ) else (
@REM         echo Устройство по USB не обнаружено.
@REM         echo !timestamp! USB не обнаружено >> "%LOG%"
@REM     )
@REM
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
    )
    pause
    goto menu
)

if "%choice%"=="2" (
    call adb_test.bat
    if errorlevel 1  echo Ошибка в adb_test.bat & echo !timestamp! Ошибка в adb_test.bat >> "%LOG%"
    goto menu
)

if "%choice%"=="3" (
    call backupzip.bat
    if errorlevel 1 echo Ошибка в backupzip.bat & echo !timestamp! Ошибка в backupzip.bat >> "%LOG%"
    goto menu
)

if "%choice%"=="4" (
    call restorezip.bat
    if errorlevel 1 echo  Ошибка в restorezip.bat & echo !timestamp! Ошибка в restorezip.bat >> "%LOG%"
    goto menu
)

if "%choice%"=="5" (
    echo  Открытие ADB-консоли
    echo !timestamp! Открытие ADB-консоли >> "%LOG%"
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
            echo Ошибка при установке: %%~nxA
            echo !timestamp! Ошибка установки: %%~nxA >> "%LOG%"
        ) else (
            echo Установлено: %%~nxA
            echo !timestamp! Установлено: %%~nxA >> "%LOG%"
        )
    )
    pause
    goto menu
)

if "%choice%"=="0" (
    echo Выход...
    echo !timestamp! Выход из скрипта >> "%LOG%"
    exit /b
)

echo Неверный выбор. Повторите попытку.
echo !timestamp! Неверный ввод: %choice% >> "%LOG%"
pause
goto menu
