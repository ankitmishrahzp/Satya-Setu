#!/bin/bash

echo "========================================"
echo "   TruthGuard Fake News Detector"
echo "========================================"
echo

echo "Starting TruthGuard application..."
echo

echo "1. Building and starting with Docker Compose..."
docker-compose up --build -d

echo
echo "2. Waiting for services to start..."
sleep 10

echo
echo "3. Checking service status..."
docker-compose ps

echo
echo "========================================"
echo "   Application is starting up!"
echo "========================================"
echo
echo "Frontend: http://localhost"
echo "Backend:  http://localhost:8080"
echo "H2 Console: http://localhost:8080/h2-console"
echo

# Try to open browser (works on macOS and some Linux distros)
if command -v open >/dev/null 2>&1; then
    echo "Opening application in browser..."
    open http://localhost
elif command -v xdg-open >/dev/null 2>&1; then
    echo "Opening application in browser..."
    xdg-open http://localhost
else
    echo "Please open http://localhost in your browser"
fi

echo
echo "To stop the application, run: docker-compose down"
echo "To view logs, run: docker-compose logs -f"
echo 