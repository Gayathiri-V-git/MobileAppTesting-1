@echo off
REM ===== Start Emulator Script =====
SET AVD_NAME=%1

IF "%AVD_NAME%"=="" (
    echo "Error: AVD name not provided!"
    exit /b 1
)

echo Starting Android Emulator: %AVD_NAME%

REM Start the emulator in background with no window
start "" "C:\Android\sdk\emulator\emulator.exe" -avd %AVD_NAME% -no-snapshot-load -no-audio -no-boot-anim -gpu swiftshader_indirect

REM Wait until emulator is fully booted
echo Waiting for emulator to boot...
:WAIT_BOOT
adb devices | findstr "emulator" >nul
IF %ERRORLEVEL% NEQ 0 (
    timeout /t 5
    goto WAIT_BOOT
)

:BOOTED_CHECK
adb shell getprop sys.boot_completed | findstr "1" >nul
IF %ERRORLEVEL% NEQ 0 (
    timeout /t 5
    goto BOOTED_CHECK
)

echo Emulator %AVD_NAME% is ready!