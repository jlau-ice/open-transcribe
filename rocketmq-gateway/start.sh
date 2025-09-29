#!/bin/bash

echo "🚀 Starting RocketMQ Gateway..."
echo

# Check if Go is installed
if ! command -v go &> /dev/null; then
    echo "❌ Go is not installed or not in PATH"
    echo "Please install Go from https://golang.org/"
    exit 1
fi

# Check if application.yml exists
if [ ! -f "application.yml" ]; then
    echo "⚠️  application.yml not found, using default configuration"
    echo
fi

# Build and run
echo "🔧 Building application..."
go build -o rocketmq-gateway .

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo "✅ Build successful"
echo
echo "🌐 Starting RocketMQ Gateway..."
echo "Press Ctrl+C to stop"
echo

./rocketmq-gateway
