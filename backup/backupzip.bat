@echo off
chcp 1251 > nul
setlocal enabledelayedexpansion

set "LOG=..\logs\backup_log.log"
set "timestamp=[%DATE% %TIME%]"

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
    pause
)

:: Старт лога
echo Начало резервного копирования -------------------------------
echo !timestamp! Начало резервного копирования ------------------------------- >> "%LOG%"

rem Test folder on android phone
:: Создание архива на устройстве и загрузка
adb exec-out "tar -cvf /sdcard/Documents.tar -C /sdcard Documents"
adb pull /sdcard/Documents.tar .
adb shell "rm /sdcard/Documents.tar"

:: Завершение логаs
echo Резервная копия создана -------------------------------
echo !timestamp! Резервная копия создана ------------------------------- >> "%LOG%"

pause
