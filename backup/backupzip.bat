@echo off
echo [%DATE% %TIME%] backup started -------------------------------
echo [%DATE% %TIME%] backup started ------------------------------- >> backup_log.txt
@echo on

rem Test folder on android phone
adb exec-out "tar -cvf /sdcard/Documents.tar -C /sdcard Documents"
adb pull /sdcard/Documents.tar .
adb shell "rm /sdcard/Documents.tar"

@echo off
echo [%DATE% %TIME%] backup created -------------------------------
echo [%DATE% %TIME%] backup created ------------------------------- >> backup_log.txt
pause
