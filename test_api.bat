@echo off
echo ========================================
echo Testing Railway API Connection
echo ========================================
echo.

set RAILWAY_URL=https://outstanding-gentleness-production-0c78.up.railway.app
set API_URL=%RAILWAY_URL%/api

echo [1/5] Testing base URL...
curl -v %RAILWAY_URL%
echo.
echo.

echo [2/5] Testing /api/drama/list...
curl -v "%API_URL%/drama/list?current=1&pageSize=10"
echo.
echo.

echo [3/5] Testing Health Check...
curl -v %RAILWAY_URL%/actuator/health
echo.
echo.

echo [4/5] Testing full API response...
curl "%API_URL%/drama/list"
echo.
echo.

echo [5/5] Testing with Headers...
curl -H "Content-Type: application/json" "%API_URL%/drama/list"
echo.
echo.

echo ========================================
echo Test Complete!
echo ========================================
pause

