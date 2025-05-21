@echo off
setlocal enabledelayedexpansion

:: Подключение общих функций
call adb_utils.bat

:: Проверка версии ADB
call :check_adb_version
if errorlevel 1 (
    call :log "❌ Операция прервана"
    exit /b 1
)

:: Проверка соединения
call :check_adb_connection
if errorlevel 1 (
    call :log "❌ Операция прервана"
    exit /b 1
)

:: Проверка места на устройстве
call :check_device_space
if errorlevel 1 (
    call :log "❌ Операция прервана"
    exit /b 1
)

:: Проверка места на компьютере
for /f "tokens=3" %%a in ('dir /-c "%BACKUP_DIR%" ^| findstr "байт свободно"') do (
    set "free_space=%%a"
    set "free_space=!free_space:,=!"
    if !free_space! lss 1048576 (
        call :log "❌ Недостаточно места на диске"
        exit /b 1
    )
)

:: Создание резервной копии
set "timestamp=%date:~6,4%%date:~3,2%%date:~0,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "timestamp=!timestamp: =0!"
set "backup_file=%BACKUP_DIR%\backup_!timestamp!.ab"

call :log "Создание резервной копии: !backup_file!"
adb backup -f "!backup_file!" -apk -shared -all
if errorlevel 1 (
    call :log "❌ Ошибка при создании резервной копии"
    exit /b 1
)

call :log "✅ Резервная копия создана успешно"
exit /b 0 