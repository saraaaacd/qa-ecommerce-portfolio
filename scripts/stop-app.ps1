$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$pidFile = Join-Path $root 'app\target\store-app.pid'

if (Test-Path $pidFile) {
    $pidToKill = Get-Content $pidFile
    Stop-Process -Id $pidToKill -Force -ErrorAction SilentlyContinue
    Remove-Item $pidFile -Force -ErrorAction SilentlyContinue
    Write-Host "SUT detenido (PID $pidToKill)."
} else {
    Write-Host 'No hay PID registrado. Buscando instancias de store-app...'
    Get-CimInstance Win32_Process -Filter "Name='java.exe'" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -like '*store-app-*-SNAPSHOT.jar*' } |
        ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue; Write-Host "Detenido PID $($_.ProcessId)" }
}