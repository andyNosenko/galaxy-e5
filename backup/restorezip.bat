@echo off
chcp 1251 > nul
setlocal enabledelayedexpansion

set "LOG=..\logs\restore_log.log"
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
echo Начало восстанрвления из резервной копии -------------------------------
echo !timestamp! Начало восстанрвления из резервной копии ------------------------------- >> "%LOG%"

rem Test folder on android phone
:: Восстановление из резервной копии с загрузкой на устройство
adb push Documents.tar /sdcard/
adb shell "tar -xvf /sdcard/Documents.tar -C /sdcard Documents"
adb shell "rm /sdcard/Documents.tar"

:: Завершение логаs
echo Восстановление выполнено успешно -------------------------------
echo !timestamp!  Восстановление выполнено успешно ------------------------------- >> "%LOG%"

pause
