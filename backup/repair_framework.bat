@echo off
set log_file=repair_framework_log.txt
echo [%DATE% %TIME%] repair started -------------------------------
echo [%DATE% %TIME%] repair started ------------------------------- >> %log_file%
@echo on
adb kill-server && adb start-server
adb wait-for-device
adb root
adb remount

adb push ".\system\framework\services.jar" /system/framework/services.jar
adb push ".\system\framework\oat\arm64\services.dex" /system/framework/oat/arm64/services.dex
adb push ".\system\framework\oat\arm64\services.vdex" /system/framework/oat/arm64/services.vdex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.dex
adb shell rm -rf /data/dalvik-cache/arm/system@framework@services.jar@classes.vdex

echo [%DATE% %TIME%] repair finished -------------------------------
echo [%DATE% %TIME%] repair finished ------------------------------- >> %log_file%
pause