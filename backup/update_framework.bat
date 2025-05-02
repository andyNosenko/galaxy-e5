@echo off
set log_file=update_framework_log.txt
echo [%DATE% %TIME%] update started -------------------------------
echo [%DATE% %TIME%] update started ------------------------------- >> %log_file%
@echo on
adb kill-server && adb start-server
adb wait-for-device
adb root
adb remount

adb push ".\system\framework\services-242961-ru.jar" /system/framework/services.jar
adb shell rm -rf /system/framework/oat/arm64/services.dex
adb shell rm -rf /system/framework/oat/arm64/services.vdex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex

echo [%DATE% %TIME%] update finished -------------------------------
echo [%DATE% %TIME%] update finished ------------------------------- >> %log_file%
pause