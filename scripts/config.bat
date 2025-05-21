@echo off
setlocal enabledelayedexpansion

:: === Настройки ===
set "SCRIPT_DIR=%~dp0"
set "LOG_DIR=%SCRIPT_DIR%logs"
set "LOG_FILE=%LOG_DIR%\adb_menu.log"
set "APK_DIR=%SCRIPT_DIR%..\apps_to_install"
set "BACKUP_DIR=%SCRIPT_DIR%backup"

:: Создание необходимых директорий
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%APK_DIR%" mkdir "%APK_DIR%"
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%" 