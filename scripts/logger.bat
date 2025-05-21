@echo off
setlocal enabledelayedexpansion

:: Константы для уровней логирования
set "LOG_LEVEL_INFO=INFO"
set "LOG_LEVEL_ERROR=ERROR"
set "LOG_LEVEL_DEBUG=DEBUG"
set "MAX_LOG_SIZE=10485760"
set "MAX_LOG_FILES=5"

:: Функция ротации логов
:rotate_logs
if exist "%LOG_FILE%" (
    for /f "tokens=*" %%a in ('dir /a-d /s "%LOG_FILE%" ^| find /c /v ""') do set size=%%a
    if !size! gtr %MAX_LOG_SIZE% (
        for /l %%i in (%MAX_LOG_FILES%,-1,1) do (
            if exist "%LOG_FILE%.%%i" (
                if %%i==%MAX_LOG_FILES% (
                    del "%LOG_FILE%.%%i"
                ) else (
                    move "%LOG_FILE%.%%i" "%LOG_FILE%.%%i+1"
                )
            )
        )
        move "%LOG_FILE%" "%LOG_FILE%.1"
    )
)
goto :eof

:: Функция логирования
:log
set "level=%~1"
set "message=%~2"
for /f "tokens=2 delims==" %%a in ('wmic os get localdatetime /value') do set "dt=%%a"
set "timestamp=!dt:~0,4!-!dt:~4,2!-!dt:~6,2! !dt:~8,2!:!dt:~10,2!:!dt:~12,2!"

call :rotate_logs

echo [!timestamp!][!level!] !message! >> "%LOG_FILE%"
echo [!timestamp!][!level!] !message!
goto :eof

:: Функции для разных уровней логирования
:log_info
call :log "%LOG_LEVEL_INFO%" "%~1"
goto :eof

:log_error
call :log "%LOG_LEVEL_ERROR%" "%~1"
goto :eof

:log_debug
if "%DEBUG%"=="true" (
    call :log "%LOG_LEVEL_DEBUG%" "%~1"
)
goto :eof 