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

:: Выбор файла для восстановления
call :check_backup_files
if errorlevel 1 exit /b 1

set "backup_count=0"
for %%A in ("%BACKUP_DIR%\*.ab") do (
    set /a "backup_count+=1"
    set "backup_file[!backup_count!]=%%A"
)

echo Доступные резервные копии:
for /l %%i in (1,1,!backup_count!) do (
    echo %%i. %%~nxbackup_file[%%i]
)

set /p choice="Выберите номер копии для восстановления: "
if !choice! lss 1 (
    call :log "❌ Неверный выбор"
    exit /b 1
)
if !choice! gtr !backup_count! (
    call :log "❌ Неверный выбор"
    exit /b 1
)

set "selected_file=!backup_file[%choice%]!"
call :log "Выбрана копия: %%~nxselected_file"

:: Восстановление из копии
call :log "Начало восстановления из копии: %%~nxselected_file"
adb restore "!selected_file!"
if errorlevel 1 (
    call :log "❌ Ошибка при восстановлении"
    exit /b 1
)

call :log "✅ Восстановление завершено успешно"
exit /b 0

:: Функция проверки наличия резервных копий
:check_backup_files
set "backup_count=0"
for %%A in ("%BACKUP_DIR%\*.ab") do (
    set /a "backup_count+=1"
)
if %backup_count% equ 0 (
    call :log "❌ Резервные копии не найдены в %BACKUP_DIR%"
    exit /b 1
)
call :log "📦 Найдено резервных копий: %backup_count%"
exit /b 0

:: Функция проверки целостности резервной копии
:verify_backup
set "backup_file=%~1"
if not exist "!backup_file!" (
    call :log "❌ Файл резервной копии не найден"
    exit /b 1
)

set "file_size=0"
for %%A in ("!backup_file!") do set "file_size=%%~zA"
if !file_size! lss 1024 (
    call :log "❌ Файл резервной копии поврежден"
    exit /b 1
)

call :log "✅ Резервная копия проверена"
exit /b 0 