@echo off
REM Start Appium in background
setlocal

REM Path to Node.js (if not in PATH)
set "NODE_PATH=C:\Program Files\nodejs\node.exe"

REM Path to Appium (adjust if global npm install)
set "APPIUM_CMD=appium"

echo Starting Appium server...
start "" "%NODE_PATH%" "%APPIUM_CMD%" --port 4723 --log "C:\Windows\TEMP\appium.log" --no-reset

echo Appium server start attempted. Check C:\Windows\TEMP\appium.log for details.
endlocal
exit /b 0