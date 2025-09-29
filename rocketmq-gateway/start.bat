@echo off
echo 🚀 Starting RocketMQ Gateway...
echo.

REM Check if Go is installed
go version >nul 2>&1
if errorlevel 1 (
    echo ❌ Go is not installed or not in PATH
    echo Please install Go from https://golang.org/
    pause
    exit /b 1
)

REM Check if application.yml exists
if not exist application.yml (
    echo ⚠️  application.yml not found, using default configuration
    echo.
)

REM Build and run
echo 🔧 Building application...
go build -o rocketmq-gateway.exe .

if errorlevel 1 (
    echo ❌ Build failed
    pause
    exit /b 1
)

echo ✅ Build successful
echo.
echo 🌐 Starting RocketMQ Gateway...
echo Press Ctrl+C to stop
echo.

rocketmq-gateway.exe
