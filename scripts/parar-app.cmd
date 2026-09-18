@echo off
rem Detiene la tienda QA si esta corriendo.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop-app.ps1"
pause