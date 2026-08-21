@echo off
REM Windows helper: run bundled apktool.jar with bundled JRE
set "TOOLS=%~dp0"
"%TOOLS%jre\bin\java.exe" -jar "%TOOLS%apktool.jar" %*
