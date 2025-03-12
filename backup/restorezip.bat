@echo off
echo [%DATE% %TIME%] restore started -------------------------------
echo [%DATE% %TIME%] restore started ------------------------------- >> restore_log.txt

rem Test folder on android phone
adb push Documents.tar /sdcard/
adb shell "tar -xvf /sdcard/Documents.tar -C /sdcard Documents"
adb shell "rm /sdcard/Documents.tar"

@echo off
echo [%DATE% %TIME%] restore created -------------------------------
echo [%DATE% %TIME%] restore created ------------------------------- >> restore_log.txt
pause
