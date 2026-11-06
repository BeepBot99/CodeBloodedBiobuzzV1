@echo off
setlocal
for /f "tokens=2 delims=:" %%i in ('netsh wlan show interface ^| findstr /C:" SSID"') do (
    set "SSID=%%i"
    goto :done
)
:done
REM Remove leading spaces from the SSID variable
if defined SSID set "SSID=%SSID:~1%"
echo Current Wi-Fi SSID: %SSID%

set ROBOT_SSID=12649-RC

echo ==================================
echo Connecting to robot Wi-Fi...
echo ==================================

call :connectWifi "%ROBOT_SSID%"

echo.
echo Connecting ADB...
adb connect 192.168.43.1:5555

echo.
echo Running Deploy Sloth...
call gradlew.bat deploySloth

echo.
echo Disconnecting ADB...
adb disconnect

echo.
echo ==================================
echo Reconnecting to home Wi-Fi...
echo ==================================

call :connectWifi "%HOME_SSID%"

echo Done.
exit /b


:connectWifi
set SSID=%~1

echo Waiting for "%SSID%" to appear...

:waitWifi
netsh wlan show networks mode=bssid 2>nul | findstr /C:"%SSID%" >nul

if errorlevel 1 (
    timeout /t 1 >nul
    goto waitWifi
)

echo Found "%SSID%". Connecting...

netsh wlan connect name="%SSID%" >nul 2>nul

echo Waiting for connection...

:waitConnection
netsh wlan show interfaces 2>nul | findstr /C:"SSID                   : %SSID%" >nul

if errorlevel 1 (
    timeout /t 1 >nul
    goto waitConnection
)

echo Connected to "%SSID%".
exit /b