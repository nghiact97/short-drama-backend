@echo off
echo ========================================
echo  Short Drama Backend - Local + Ngrok
echo ========================================
echo.

REM Check if Docker is installed
where docker >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Docker not found!
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

REM Check if ngrok is installed
where ngrok >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Ngrok not found!
    echo Please download from: https://ngrok.com/download
    echo Then add ngrok to your PATH
    pause
    exit /b 1
)

echo [1/3] Starting MySQL and Backend with Docker...
docker-compose up -d

echo.
echo Waiting for services to be ready...
timeout /t 15 /nobreak >nul

echo.
echo [2/3] Services are running!
echo MySQL: localhost:3306
echo Backend: http://localhost:8101/api

echo.
echo [3/3] Starting Ngrok tunnel...
echo Please copy the Forwarding URL below and use it in your Flutter app!
echo.
ngrok http 8101

pause

