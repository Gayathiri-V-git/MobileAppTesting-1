@echo off
REM ----------------------------
REM start_emulator.bat
REM ----------------------------
SETLOCAL ENABLEDELAYEDEXPANSION

SET AVD_NAME=%1
IF "%AVD_NAME%"=="" SET AVD_NAME=CI_Test_AVD
echo Starting emulator: %AVD_NAME%

REM start emulator detached
start "" "%ANDROID_SDK_ROOT%\emulator\emulator.exe" -avd %AVD_NAME% -no-audio -no-boot-anim -no-window

echo Waiting for emulator to appear in adb...
SET BOOTED=0
FOR /L %%i IN (1,1,60) DO (
    echo Checking boot status (%%i)...
    adb wait-for-device
    FOR /F "usebackq tokens=*" %%b IN (`adb shell getprop sys.boot_completed 2^>nul`) DO (
        IF "%%b"=="1" (
            SET BOOTED=1
            GOTO :BOOTED
        )
    )
    TIMEOUT /T 5 >nul
)

:BOOTED
IF "!BOOTED!"=="1" (
    echo Emulator booted successfully.
    ENDLOCAL
    exit /b 0
) ELSE (
    echo Emulator failed to boot within timeout.
    ENDLOCAL
    exit /b 1
)