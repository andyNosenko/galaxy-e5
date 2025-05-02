@echo off
setlocal

set "LOG=..\logs\restore_log.txt"

:: Старт лога
echo [%DATE% %TIME%] 🔄 Начало восстанрвления из резервной копии ------------------------------- | tee -a %LOG%

rem Test folder on android phone
:: Восстановление из резервной копии с загрузкой на устройство
adb push Documents.tar /sdcard/
adb shell "tar -xvf /sdcard/Documents.tar -C /sdcard Documents"
adb shell "rm /sdcard/Documents.tar"

:: Завершение лога
echo [%DATE% %TIME%] ✅ Восстановление выполнено успешно ------------------------------- | tee -a %LOG%

pause
