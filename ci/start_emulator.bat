@echo off
REM Robust start_emulator.bat for Windows Jenkins CI
REM Usage: start_emulator.bat <AVD_NAME>
SETLOCAL ENABLEDELAYEDEXPANSION

REM --------- Resolve SDK paths (prefer env vars) ---------
if defined ANDROID_SDK_ROOT (
  set "SDK_ROOT=%ANDROID_SDK_ROOT%"
) else if defined ANDROID_HOME (
  set "SDK_ROOT=%ANDROID_HOME%"
) else (
  rem try common default
  set "SDK_ROOT=C:\Android\Sdk"
)

set "EMULATOR_EXE=%SDK_ROOT%\emulator\emulator.exe"
set "ADB_EXE=%SDK_ROOT%\platform-tools\adb.exe"

REM --------- Validate inputs & binaries ---------
if "%~1"=="" (
  echo ERROR: No AVD name provided. Usage: start_emulator.bat ^<AVD_NAME^>
  ENDLOCAL
  exit /b 1
)
set "AVD_NAME=%~1"

if not exist "%EMULATOR_EXE%" (
  echo ERROR: emulator.exe not found at "%EMULATOR_EXE%".
  ENDLOCAL
  exit /b 2
)
if not exist "%ADB_EXE%" (
  echo ERROR: adb.exe not found at "%ADB_EXE%".
  ENDLOCAL
  exit /b 3
)

echo Starting emulator: "!AVD_NAME!"

REM --------- Launch emulator in background (no window) ---------
start "" /B "%EMULATOR_EXE%" -avd "!AVD_NAME!" -no-window -no-audio -no-snapshot-load -no-boot-anim -gpu swiftshader_indirect

REM --------- Wait for adb device list to include emulator ---------
echo Waiting for emulator to appear in adb...
set "TMP_DEVICES=%TEMP%\adb_devices_%RANDOM%.txt"

:wait_for_device
"%ADB_EXE%" devices > "%TMP_DEVICES%" 2>nul
findstr /R /C:"emulator" "%TMP_DEVICES%" >nul
if errorlevel 1 (
  timeout /t 3 >nul
  goto wait_for_device
)

REM --------- Wait for boot completion property ---------
echo Waiting for emulator to finish booting...
set "TMP_BOOT=%TEMP%\adb_boot_%RANDOM%.txt"
set "BOOT=0"

:wait_for_boot
"%ADB_EXE%" shell getprop sys.boot_completed > "%TMP_BOOT%" 2>nul
for /f "usebackq delims=" %%B in ("%TMP_BOOT%") do set "BOOTVAL=%%B"
if "%BOOTVAL%"=="1" (
  echo Emulator "!AVD_NAME!" is ready!
  del "%TMP_DEVICES%" >nul 2>&1
  del "%TMP_BOOT%" >nul 2>&1
  ENDLOCAL
  exit /b 0
)
timeout /t 3 >nul
goto wait_for_boot