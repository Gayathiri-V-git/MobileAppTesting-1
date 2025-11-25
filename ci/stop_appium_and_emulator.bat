@echo off
REM ----------------------------
REM stop_appium_and_emulator.bat
REM Kills node.exe (Appium) and asks emulator to stop.
REM ----------------------------
SETLOCAL


echo Stopping Appium (killing node processes if any)...
tasklist /FI "IMAGENAME eq node.exe" | find "node.exe" >nul
IF %ERRORLEVEL%==0 (
for /f "tokens=2 delims=," %%p in ('tasklist /FI "IMAGENAME eq node.exe" /FO CSV /NH') do (
taskkill /PID %%p /F >nul 2>&1
)
) ELSE (
echo No node.exe found.
)


echo Attempting to stop emulator...
adb emu kill >nul 2>&1 || echo No emulator to kill.


ENDLOCAL