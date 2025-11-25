@echo off
REM ----------------------------
REM start_emulator.bat
REM Expects: ANDROID_SDK_ROOT and ANDROID_SDK_HOME set as system vars
REM ----------------------------
SETLOCAL


IF "%%AVD_NAME%%"=="" SET AVD_NAME=CI_Test_AVD
echo Starting emulator: %%AVD_NAME%%


REM start emulator detached
start "" "%%ANDROID_SDK_ROOT%%\emulator\emulator.exe" -avd %%AVD_NAME%% -no-audio -no-boot-anim -no-window


echo Waiting for emulator to appear in adb...
REM Wait for device connection & system boot
SET BOOTED=0
FOR /L %%i IN (1,1,60) DO (
echo Checking boot status (%%i)...
adb wait-for-device
adb shell getprop sys.boot_completed 2>nul | find "1" >nul
IF NOT ERRORLEVEL 1 (
SET BOOTED=1
GOTO :BOOTED
)
TIMEOUT /T 5 >nul
)
:BOOTED
IF "%%BOOTED%%"=="1" (
echo Emulator booted successfully.
exit /b 0
) ELSE (
echo Emulator failed to boot within timeout.
exit /b 1
)
ENDLOCAL