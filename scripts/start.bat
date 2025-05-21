@echo off
chcp 1251 > nul
setlocal enabledelayedexpansion

:: === Настройки ===
set "SCRIPT_DIR=%~dp0"
set "LOG_DIR=logs"
set "LOG_FILE=%LOG_DIR%\adb_menu.log"
set "APK_DIR=..\apps_to_install"
set "BACKUP_DIR=backup"
set "timestamp=[%DATE% %TIME%]"

:: Создание необходимых директорий
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%APK_DIR%" mkdir "%APK_DIR%"
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

:: Подключение общих функций
call "%SCRIPT_DIR%config.bat"
call "%SCRIPT_DIR%logger.bat"
call "%SCRIPT_DIR%adb_utils.bat"

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
echo 1. Проверить ADB-соединение по USB
echo 2. Запустить adb_test.bat
echo 3. Сделать резервную копию (backupzip.bat)
echo 4. Восстановить из копии (restorezip.bat)
echo 5. Открыть ADB-консоль
echo 6. Установить все APK из: %APK_DIR%
echo 7. Очистить логи
echo 8. Перезапустить ADB сервер
echo 9. Список установленных приложений
echo 10. Удалить приложение
echo 11. Удалить все пользовательские приложения
echo 12. Удалить приложения из %APK_DIR%
echo 0. Выход
echo ==========================================
set /p choice="Выберите действие (0-12): "

if "%choice%"=="1" (
    call :check_adb_connection
    if errorlevel 1 (
        call :log "❌ Ошибка проверки соединения"
    ) else (
        call :check_device_state
    )
    pause
    goto menu
)

if "%choice%"=="2" (
    call :log "▶ Запуск adb_test.bat"
    call adb_test.bat
    if errorlevel 1 call :log "❌ Ошибка при запуске adb_test.bat"
    pause
    goto menu
)

if "%choice%"=="3" (
    call :log "💾 Создание резервной копии..."
    call backupzip.bat
    if errorlevel 1 call :log "❌ Ошибка при запуске backupzip.bat"
    pause
    goto menu
)

if "%choice%"=="4" (
    call :log "♻ Восстановление из резервной копии..."
    call restorezip.bat
    if errorlevel 1 call :log "❌ Ошибка при запуске restorezip.bat"
    pause
    goto menu
)

if "%choice%"=="5" (
    call :log "🖥 Запуск ADB-консоли"
    cmd
    goto menu
)

if "%choice%"=="6" (
    call :log "📦 Установка всех APK из %APK_DIR%"
    for %%A in ("%APK_DIR%\*.apk") do (
        call :install_apk "%%A"
    )
    pause
    goto menu
)

if "%choice%"=="7" (
    call :cleanup_logs
    pause
    goto menu
)

if "%choice%"=="8" (
    call :restart_adb_server
    pause
    goto menu
)

if "%choice%"=="9" (
    call :log "📱 Получение списка приложений..."
    call :get_installed_apps
    pause
    goto menu
)

if "%choice%"=="10" (
    call :log "📱 Получение списка приложений..."
    call :get_installed_apps
    set /p package="Введите имя пакета для удаления: "
    call :uninstall_app "%package%"
    pause
    goto menu
)

if "%choice%"=="11" (
    set /p confirm="Вы уверены, что хотите удалить ВСЕ пользовательские приложения? (y/n): "
    if /i "%confirm%"=="y" (
        call :uninstall_all_apps
    ) else (
        call :log "❌ Операция отменена"
    )
    pause
    goto menu
)

if "%choice%"=="12" (
    set /p confirm="Вы уверены, что хотите удалить приложения из %APK_DIR%? (y/n): "
    if /i "%confirm%"=="y" (
        call :uninstall_installed_apps
    ) else (
        call :log "❌ Операция отменена"
    )
    pause
    goto menu
)

if "%choice%"=="0" (
    call :log "🚪 Выход из меню"
    exit /b 0
)

call :log "Неверный выбор: %choice%"
echo Неверный выбор. Повторите попытку.
pause
goto menu

:: Запуск основного меню
call :check_dependencies
if errorlevel 1 (
    pause
    exit /b 1
)

if exist art.txt type art.txt
call :log "🔄 Запуск ADB-меню"
goto menu 