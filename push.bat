@echo off
setlocal

set "ROBOT_SSID=12649-RC"
set "WIFI_TIMEOUT=5"

call :getSSID
set "HOME_SSID=%CURRENT_SSID%"

echo Current Wi-Fi SSID: %CURRENT_SSID%

echo ==================================
echo Connecting to robot Wi-Fi...
echo ==================================

call :connectWifi "%ROBOT_SSID%"

if errorlevel 1 (
    echo Deployment aborted.
    if defined HOME_SSID call :connectWifi "%HOME_SSID%"
    exit /b 1
)

echo.
echo Connecting ADB...
adb connect 192.168.43.1:5555

echo.
echo Running Deploy Sloth...
call gradlew.bat deploySloth
set "DEPLOY_RESULT=%ERRORLEVEL%"

echo.
echo Disconnecting ADB...
adb disconnect

if defined HOME_SSID (
    if /i not "%HOME_SSID%"=="%ROBOT_SSID%" (
        echo.
        echo ==================================
        echo Reconnecting to home Wi-Fi...
        echo ==================================
        call :connectWifi "%HOME_SSID%"
        if errorlevel 1 exit /b 1
    )
)

echo Done.
exit /b %DEPLOY_RESULT%


:getSSID
set "CURRENT_SSID="

for /f "tokens=1,* delims=:" %%a in ('netsh wlan show interfaces 2^>nul') do (
    for /f "tokens=1" %%c in ("%%a") do (
        if /i "%%c"=="SSID" (
            for /f "tokens=*" %%d in ("%%b") do (
                set "CURRENT_SSID=%%d"
                exit /b 0
            )
        )
    )
)

exit /b 0


:connectWifi
set "TARGET_SSID=%~1"
set /a ELAPSED=0

call :getSSID

if /i "%CURRENT_SSID%"=="%TARGET_SSID%" (
    echo Already connected to "%TARGET_SSID%".
    exit /b 0
)

echo Connecting to "%TARGET_SSID%"...

netsh wlan connect name="%TARGET_SSID%" >nul 2>nul

:waitConnection
call :getSSID

if /i "%CURRENT_SSID%"=="%TARGET_SSID%" (
    echo Connected to "%TARGET_SSID%".
    exit /b 0
)

if %ELAPSED% GEQ %WIFI_TIMEOUT% (
    echo ERROR: Timed out connecting to "%TARGET_SSID%".
    exit /b 1
)

ping 127.0.0.1 -n 2 >nul
set /a ELAPSED+=1

goto waitConnection