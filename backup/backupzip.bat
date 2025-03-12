@echo off
echo [%DATE% %TIME%] backup started -------------------------------
echo [%DATE% %TIME%] backup started ------------------------------- >> backup_log.txt
@echo on


adb exec-out "tar -cvf /documents.tar /documents" 
adb pull /documents.tar .
@echo off
echo [%DATE% %TIME%] backup created -------------------------------
echo [%DATE% %TIME%] backup created ------------------------------- >> backup_log.txt
pause
