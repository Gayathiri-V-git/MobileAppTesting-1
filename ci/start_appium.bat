@echo off
REM ----------------------------
REM start_appium.bat
REM Starts Appium (npx appium recommended). Assumes node & npm available in PATH.
REM ----------------------------
SETLOCAL


echo Starting Appium server...
start "" cmd /c "npx appium --log-level error > %TEMP%\appium.log 2>&1"
TIMEOUT /T 5 >nul
echo Appium start attempted. Check %TEMP%\appium.log for details.
ENDLOCAL