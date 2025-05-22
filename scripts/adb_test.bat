@echo off
setlocal enabledelayedexpansion

:: === Настройки ===
set "SCRIPT_DIR=%~dp0"

:: Подключение общих функций
call "%SCRIPT_DIR%\config.bat"
call "%SCRIPT_DIR%\logger.bat"
call "%SCRIPT_DIR%\adb_utils.bat"

:: Проверка версии ADB
call :check_adb_version
if errorlevel 1 (
    call :log "❌ Тестирование прервано"
    exit /b 1
)

:: Проверка соединения
call :check_adb_connection
if errorlevel 1 (
    call :log "❌ Тестирование прервано"
    exit /b 1
)

:: Получение информации об устройстве
for /f "tokens=*" %%a in ('call :get_device_info') do (
    set "device_info=%%a"
    call :log "Модель устройства: !device_info!"
)

:: Проверка батареи
for /f "tokens=*" %%a in ('call :get_battery_level') do (
    set "battery_level=%%a"
    call :log "Уровень заряда: !battery_level!%%"
)

:: Проверка места на устройстве
call :check_device_space
if errorlevel 1 (
    call :log "❌ Недостаточно места на устройстве"
)

call :log "✅ Тестирование завершено"
exit /b 0

:: Функция проверки состояния устройства
:check_device_state
for /f "tokens=*" %%a in ('adb shell getprop sys.boot_completed 2^>nul') do (
    if "%%a"=="1" (
        call :log "Устройство загружено"
        exit /b 0
    )
)
call :log "❌ Устройство не загружено"
exit /b 1

:: Функция проверки USB соединения
:check_usb_connection
for /f "tokens=*" %%a in ('adb shell getprop sys.usb.state 2^>nul') do (
    if "%%a"=="mtp" (
        call :log "USB соединение активно"
        exit /b 0
    )
)
call :log "❌ USB соединение неактивно"
exit /b 1

:: Функция проверки root прав
:check_root_access
adb shell "su -c 'id'" > nul 2>&1
if errorlevel 1 (
    call :log "❌ Нет root прав"
    exit /b 1
)
call :log "✅ Root права доступны"
exit /b 0 