@echo off
REM Enable delayed variable expansion for IF and loops
setlocal enabledelayedexpansion

REM Check if emulator name is passed
if "%~1"=="" (
    echo ERROR: No AVD name provided.
    exit /b 1
)
set "EMULATOR_NAME=%~1"

REM Full path to emulator (adjust if your path is different)
set "EMULATOR_PATH=C:\Android\Sdk\emulator\emulator.exe"

REM Start emulator in a new window and without snapshots
echo Starting emulator: !EMULATOR_NAME!
start "" "!EMULATOR_PATH!" -avd "!EMULATOR_NAME!" -no-snapshot-load -no-audio -no-boot-anim

REM Wait for emulator to be detected by adb
echo Waiting for emulator to appear in adb...
:wait_for_device
adb devices | findstr /R /C:"emulator" > nul
if errorlevel 1 (
    timeout /t 5 > nul
    goto wait_for_device
)

REM Wait for boot completion
echo Waiting for emulator to finish booting...
:wait_for_boot
set "BOOT_COMPLETE="
for /f "tokens=*" %%i in ('adb shell getprop sys.boot_completed 2^>nul') do set "BOOT_COMPLETE=%%i"
if not "!BOOT_COMPLETE!"=="1" (
    timeout /t 5 > nul
    goto wait_for_boot
)

echo Emulator !EMULATOR_NAME! is ready!
endlocal
exit /b 0