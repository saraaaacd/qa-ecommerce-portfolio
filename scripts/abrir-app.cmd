@echo off
rem Abre la tienda en el navegador (arrancando la app si no esta corriendo).
powershell -NoProfile -ExecutionPolicy Bypass -Command "try { (Invoke-RestMethod -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 3).status } catch { 'down' }" >"%TEMP%\app_health.txt" 2>&1
set /p HEALTH=<"%TEMP%\app_health.txt"
del "%TEMP%\app_health.txt" >nul 2>&1

if not "%HEALTH%"=="UP" (
    echo Arrancando la tienda QA...
    powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-app.ps1"
    if errorlevel 1 (
        echo.
        echo No se pudo arrancar la app. Revisa el mensaje anterior.
        pause
        exit /b 1
    )
)

start "" http://localhost:8080
echo Listo: la tienda esta en http://localhost:8080