@echo off
echo ========================================
echo    TruthGuard Fake News Detector
echo ========================================
echo.

echo Starting TruthGuard application...
echo.

echo 1. Building and starting with Docker Compose...
docker-compose up --build -d

echo.
echo 2. Waiting for services to start...
timeout /t 10 /nobreak > nul

echo.
echo 3. Checking service status...
docker-compose ps

echo.
echo ========================================
echo    Application is starting up!
echo ========================================
echo.
echo Frontend: http://localhost
echo Backend:  http://localhost:8080
echo H2 Console: http://localhost:8080/h2-console
echo.
echo Press any key to view logs...
pause > nul

echo.
echo Opening application in browser...
start http://localhost

echo.
echo To stop the application, run: docker-compose down
echo To view logs, run: docker-compose logs -f
echo. 