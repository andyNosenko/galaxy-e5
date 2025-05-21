@echo off
setlocal enabledelayedexpansion

:: === Настройки ===
set "LOG_DIR=logs"
set "LOG_FILE=%LOG_DIR%\adb_menu.log"
set "MAX_LOG_SIZE=10485760"
set "MAX_LOG_FILES=5"
set "timestamp=[%DATE% %TIME%]"

:: Функция ротации логов
:rotate_logs
if exist "%LOG_FILE%" (
    for /f "tokens=*" %%a in ('dir /b /a-d "%LOG_FILE%" 2^>nul') do (
        set "size=%%~za"
        if !size! gtr %MAX_LOG_SIZE% (
            for /l %%i in (%MAX_LOG_FILES%,-1,1) do (
                if exist "%LOG_FILE%.%%i" (
                    if %%i equ %MAX_LOG_FILES% (
                        del "%LOG_FILE%.%%i"
                    ) else (
                        move "%LOG_FILE%.%%i" "%LOG_FILE%.%%i+1"
                    )
                )
            )
            move "%LOG_FILE%" "%LOG_FILE%.1"
        )
    )
)
goto :eof

:: Функция логирования
:log
call :rotate_logs
echo %timestamp% %~1 >> "%LOG_FILE%"
echo %~1
goto :eof

:: Проверка ADB соединения
:check_adb_connection
call :log "Проверка ADB соединения..."
adb devices | findstr /r /c:"device$" > nul
if errorlevel 1 (
    call :log "ADB устройство не найдено"
    call :restart_adb_server
    adb devices | findstr /r /c:"device$" > nul
    if errorlevel 1 (
        call :log "❌ Повторная попытка подключения не удалась"
        exit /b 1
    )
)
call :log "ADB устройство найдено"
exit /b 0

:: Получение информации об устройстве
:get_device_info
for /f "tokens=*" %%a in ('adb shell getprop ro.product.model 2^>nul') do (
    set "info=%%a"
    echo !info!
    exit /b 0
)
exit /b 1

:: Проверка состояния батареи
:get_battery_level
for /f "tokens=2" %%a in ('adb shell dumpsys battery ^| findstr "level"') do (
    echo %%a
    exit /b 0
)
exit /b 1

:: Проверка свободного места на устройстве
:check_device_space
set "required_space=1024"
for /f "tokens=4" %%a in ('adb shell df /data ^| findstr /r /c:"^[0-9]"') do (
    if %%a lss %required_space% (
        call :log "Недостаточно места на устройстве (требуется: %required_space%MB, доступно: %%aMB)"
        exit /b 1
    )
)
exit /b 0

:: Проверка версии APK
:check_apk_version
set "apk_file=%~1"
for /f "tokens=2" %%a in ('adb shell pm dump "%~n1" 2^>nul ^| findstr "versionName"') do (
    set "installed_version=%%a"
    goto :check_version
)
:check_version
for /f "tokens=2" %%a in ('aapt dump badging "!apk_file!" 2^>nul ^| findstr "versionName"') do (
    set "new_version=%%a"
    if "!installed_version!"=="!new_version!" (
        call :log "ℹ️ Версия %~nx1 уже установлена: !new_version!"
        exit /b 0
    )
    call :log "📦 Обновление %~nx1 с !installed_version! до !new_version!"
    exit /b 1
)
call :log "📦 Установка новой версии %~nx1"
exit /b 1

:: Установка APK
:install_apk
set "apk_file=%~1"
call :check_apk_version "!apk_file!"
if errorlevel 1 (
    call :log "Установка: %~nx1"
    adb install -g -r "!apk_file!"
    if errorlevel 1 (
        call :log "❌ Ошибка установки: %~nx1"
        exit /b 1
    )
    call :log "✅ Установлен: %~nx1"
) else (
    call :log "ℹ️ Пропуск: %~nx1 (уже установлена последняя версия)"
)
exit /b 0

:: Проверка версии ADB
:check_adb_version
for /f "tokens=*" %%a in ('adb version ^| findstr /r /c:"^Android Debug Bridge"') do (
    call :log "Версия ADB: %%a"
    exit /b 0
)
exit /b 1

:: Перезагрузка ADB сервера
:restart_adb_server
call :log "Перезапуск ADB сервера..."
adb kill-server
timeout /t 2 /nobreak > nul
adb start-server
if errorlevel 1 (
    call :log "❌ Ошибка перезапуска ADB сервера"
    exit /b 1
)
call :log "✅ ADB сервер перезапущен"
exit /b 0

:: Очистка старых логов
:cleanup_logs
call :log "Очистка старых логов..."
for /l %%i in (%MAX_LOG_FILES%,-1,1) do (
    if exist "%LOG_FILE%.%%i" del "%LOG_FILE%.%%i"
)
if exist "%LOG_FILE%" del "%LOG_FILE%"
call :log "✅ Логи очищены"
exit /b 0

:: Проверка состояния устройства
:check_device_state
for /f "tokens=*" %%a in ('adb shell getprop sys.boot_completed 2^>nul') do (
    if "%%a"=="1" (
        call :log "Устройство загружено"
        exit /b 0
    )
)
call :log "❌ Устройство не загружено"
exit /b 1

:: Проверка наличия APK файлов
:check_apk_files
set "apk_count=0"
for %%A in ("%APK_DIR%\*.apk") do (
    set /a "apk_count+=1"
)
if %apk_count% equ 0 (
    call :log "❌ APK файлы не найдены в %APK_DIR%"
    exit /b 1
)
call :log "📦 Найдено APK файлов: %apk_count%"
exit /b 0

:: Установка всех APK файлов
:install_all_apks
call :check_apk_files
if errorlevel 1 exit /b 1

call :log "📦 Установка всех APK файлов..."
for %%A in ("%APK_DIR%\*.apk") do (
    call :install_apk "%%A"
)
if errorlevel 1 (
    call :log "❌ Ошибка при установке APK файлов"
    exit /b 1
)
call :log "✅ Все APK файлы установлены"
exit /b 0

:: Получение списка установленных приложений
:get_installed_apps
set "temp_file=%TEMP%\installed_apps.txt"
adb shell pm list packages -3 > "!temp_file!"
if errorlevel 1 (
    call :log "❌ Ошибка получения списка приложений"
    exit /b 1
)
type "!temp_file!"
del "!temp_file!"
exit /b 0

:: Удаление одного приложения
:uninstall_app
set "package_name=%~1"
call :log "🗑️ Удаление приложения: %package_name%"
adb uninstall "%package_name%"
if errorlevel 1 (
    call :log "❌ Ошибка удаления: %package_name%"
    exit /b 1
)
call :log "✅ Удалено: %package_name%"
exit /b 0

:: Удаление всех пользовательских приложений
:uninstall_all_apps
call :log "🗑️ Удаление всех пользовательских приложений..."
for /f "tokens=2 delims=:" %%a in ('adb shell pm list packages -3') do (
    call :uninstall_app "%%a"
)
if errorlevel 1 (
    call :log "❌ Ошибка при удалении приложений"
    exit /b 1
)
call :log "✅ Все приложения удалены"
exit /b 0

:: Удаление приложений из app_to_install
:uninstall_installed_apps
call :log "🗑️ Удаление приложений из %APK_DIR%..."
set "error=0"

for %%A in ("%APK_DIR%\*.apk") do (
    set "apk_name=%%~nA"
    for /f "tokens=2 delims=:" %%a in ('adb shell pm list packages -3 ^| findstr /i "!apk_name!"') do (
        call :uninstall_app "%%a"
        if errorlevel 1 set "error=1"
    )
)

if %error% equ 1 (
    call :log "❌ Ошибка при удалении приложений"
    exit /b 1
)
call :log "✅ Все приложения из %APK_DIR% удалены"
exit /b 0 