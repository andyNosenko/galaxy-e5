@echo off
setlocal enabledelayedexpansion

:: Подключение общих функций
call adb_utils.bat

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