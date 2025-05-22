@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

:: === Настройки ===
set "SCRIPT_DIR=%~dp0"
set "LOG_DIR=%SCRIPT_DIR%\logs"
set "LOG_FILE=%LOG_DIR%\adb_menu.log"
set "APK_DIR=%SCRIPT_DIR%\..\apps_to_install"
set "BACKUP_DIR=%SCRIPT_DIR%\backup"
set "timestamp=[%DATE% %TIME%]"

:: Создание необходимых директорий
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%APK_DIR%" mkdir "%APK_DIR%"
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

:: Подключение общих функций
call "%SCRIPT_DIR%\config.bat"
call "%SCRIPT_DIR%\logger.bat"
call "%SCRIPT_DIR%\adb_utils.bat"

:: Проверка зависимостей
:check_dependencies
where adb >nul 2>nul
if errorlevel 1 (
    call :log "❌ ADB не найден в системе"
    exit /b 1
)
exit /b 0

:: Основное меню
:menu
cls
echo ==========================================
echo         МЕНЮ ADB-СКРИПТА (Windows)
echo ==========================================
echo 1. Проверить ADB соединение
echo 2. Запустить тест
echo 3. Создать резервную копию
echo 4. Восстановить из копии
echo 5. Установить все APK
echo 6. Установить один APK
echo 7. Список установленных приложений
echo 8. Удалить приложение
echo 9. Удалить приложения из apps_to_install
echo 10. Очистить логи
echo 0. Выход
echo ==========================================
set /p choice="Выберите действие (0-10): "

if "%choice%"=="1" (
    call :check_adb_connection
    pause
    goto menu
)

if "%choice%"=="2" (
    call "%SCRIPT_DIR%\adb_test.bat"
    pause
    goto menu
)

if "%choice%"=="3" (
    call "%SCRIPT_DIR%\backupzip.bat"
    pause
    goto menu
)

if "%choice%"=="4" (
    call "%SCRIPT_DIR%\restorezip.bat"
    pause
    goto menu
)

if "%choice%"=="5" (
    call :install_all_apks
    pause
    goto menu
)

if "%choice%"=="6" (
    call :install_single_apk
    pause
    goto menu
)

if "%choice%"=="7" (
    call :get_installed_apps
    pause
    goto menu
)

if "%choice%"=="8" (
    call :uninstall_single_app
    pause
    goto menu
)

if "%choice%"=="9" (
    call :uninstall_installed_apps
    pause
    goto menu
)

if "%choice%"=="10" (
    call :cleanup_logs
    pause
    goto menu
)

if "%choice%"=="0" (
    exit /b 0
)

echo Неверный выбор
pause
goto menu

:: Запуск основного меню
call :check_dependencies
if errorlevel 1 (
    pause
    exit /b 1
)

if exist "%SCRIPT_DIR%\art.txt" type "%SCRIPT_DIR%\art.txt"
call :log "🔄 Запуск ADB-меню"
goto menu

:: Функция установки одного APK
:install_single_apk
set "apk_count=0"
for %%A in ("%APK_DIR%\*.apk") do (
    set /a "apk_count+=1"
    set "apk_file[!apk_count!]=%%A"
)

if !apk_count! equ 0 (
    call :log "❌ APK файлы не найдены"
    exit /b 1
)

echo Доступные APK файлы:
for /l %%i in (1,1,!apk_count!) do (
    echo %%i. %%~nxapk_file[%%i]
)

set /p choice="Выберите номер APK для установки: "
if !choice! lss 1 (
    call :log "❌ Неверный выбор"
    exit /b 1
)
if !choice! gtr !apk_count! (
    call :log "❌ Неверный выбор"
    exit /b 1
)

call :install_apk "!apk_file[%choice%]!"
exit /b 0

:: Функция удаления одного приложения
:uninstall_single_app
set "temp_file=%TEMP%\installed_apps.txt"
adb shell pm list packages -3 > "!temp_file!"
if errorlevel 1 (
    call :log "❌ Ошибка получения списка приложений"
    exit /b 1
)

set "app_count=0"
for /f "tokens=2 delims=:" %%a in ('type "!temp_file!"') do (
    set /a "app_count+=1"
    set "app_name[!app_count!]=%%a"
)

if !app_count! equ 0 (
    call :log "❌ Пользовательские приложения не найдены"
    del "!temp_file!"
    exit /b 1
)

echo Установленные приложения:
for /l %%i in (1,1,!app_count!) do (
    echo %%i. !app_name[%%i]!
)

set /p choice="Выберите номер приложения для удаления: "
if !choice! lss 1 (
    call :log "❌ Неверный выбор"
    del "!temp_file!"
    exit /b 1
)
if !choice! gtr !app_count! (
    call :log "❌ Неверный выбор"
    del "!temp_file!"
    exit /b 1
)

call :uninstall_app "!app_name[%choice%]!"
del "!temp_file!"
exit /b 0 