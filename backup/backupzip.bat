@echo off
setlocal

set "LOG=..\logs\backup_log.txt"

:: Старт лога
echo [%DATE% %TIME%] 🔄 Начало резервного копирования ------------------------------- | tee -a %LOG%

rem Test folder on android phone
:: Создание архива на устройстве и загрузка
adb exec-out "tar -cvf /sdcard/Documents.tar -C /sdcard Documents"
adb pull /sdcard/Documents.tar .
adb shell "rm /sdcard/Documents.tar"

:: Завершение лога
echo [%DATE% %TIME%] ✅ Резервная копия создана ------------------------------- | tee -a %LOG%

pause
