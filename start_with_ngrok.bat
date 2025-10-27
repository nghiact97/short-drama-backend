@echo off
echo Starting Spring Boot Backend with Ngrok tunnel...
echo.
echo Make sure you have:
echo 1. Java installed
echo 2. Maven installed
echo 3. Ngrok installed and configured with authtoken
echo.

REM Check if ngrok is available
where ngrok >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Ngrok not found in PATH!
    echo Please add ngrok to your PATH or update this script
    pause
    exit /b 1
)

REM Start Spring Boot
echo Starting Spring Boot application on port 8101...
start "Spring Boot" cmd /k "mvn spring-boot:run"

REM Wait a bit for Spring Boot to start
timeout /t 10 /nobreak >nul

REM Start ngrok
echo Starting Ngrok tunnel...
ngrok http 8101

echo.
echo Tunnel started! Check the Forwarding URL above.
echo Copy that URL and use it in your Flutter app.
echo.
pause

