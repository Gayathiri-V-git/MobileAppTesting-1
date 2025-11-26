@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

REM Resolve SDK
if defined ANDROID_SDK_ROOT ( set "SDK_ROOT=%ANDROID_SDK_ROOT%" ) else if defined ANDROID_HOME ( set "SDK_ROOT=%ANDROID_HOME%" ) else ( set "SDK_ROOT=C:\Android\Sdk" )
set "EMULATOR_EXE=%SDK_ROOT%\emulator\emulator.exe"
set "ADB_EXE=%SDK_ROOT%\platform-tools\adb.exe"

if "%~1"=="" (
  echo No AVD provided, will pick first available.
  set "AVD_NAME="
) else (
  set "AVD_NAME=%~1"
)

REM list avds
for /f "usebackq delims=" %%A in (`"%EMULATOR_EXE%" -list-avds 2^>nul`) do (
  if not defined FIRST_AVD set "FIRST_AVD=%%A"
  if /I "%%A"=="%AVD_NAME%" set "FOUND_AVD=%%A"
)

if defined FOUND_AVD (
  set "USE_AVD=%FOUND_AVD%"
) else if defined AVD_NAME (
  echo Requested AVD "%AVD_NAME%" not found. Falling back to first available AVD: %FIRST_AVD%
  set "USE_AVD=%FIRST_AVD%"
) else (
  set "USE_AVD=%FIRST_AVD%"
)

if not defined USE_AVD (
  echo ERROR: No AVDs found on this machine. Run 'emulator -list-avds' to create one.
  ENDLOCAL
  exit /b 2
)

echo Starting emulator: "%USE_AVD%"

set "EMULOG=%TEMP%\emulator_%USE_AVD%_%RANDOM%.log"
start "" /B "%EMULATOR_EXE%" -avd "%USE_AVD%" -no-window -no-audio -no-snapshot-load -gpu swiftshader_indirect > "%EMULOG%" 2>&1

echo Waiting for emulator to appear in adb...
"%ADB_EXE%" wait-for-device

:boot
"%ADB_EXE%" shell getprop sys.boot_completed > "%TEMP%\boot_%RANDOM%.txt" 2>nul
for /f "delims=" %%B in (%TEMP%\boot_*.txt) do set "BOOTVAL=%%B"
if not "%BOOTVAL%"=="1" (
  timeout /t 3 >nul
  goto boot
)

echo Emulator "%USE_AVD%" is ready. See %EMULOG% for emulator output.
ENDLOCAL
exit /b 0