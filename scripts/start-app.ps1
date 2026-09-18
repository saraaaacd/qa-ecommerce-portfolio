param(
    [int]$Port = 8080
)
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot

function Find-MavenWrapper {
    param([string]$Root)
    $candidate = Join-Path $Root 'mvnw.cmd'
    if (Test-Path $candidate) { return $candidate }
    throw 'No se encontro mvnw.cmd. Revisa que existe el Maven Wrapper en la raiz del proyecto.'
}

$jar = Join-Path $root 'app\target\store-app-1.0.0-SNAPSHOT.jar'
$mvnw = Find-MavenWrapper -Root $root
$pidFile = Join-Path $root 'app\target\store-app.pid'

if (-not $env:JAVA_HOME) {
    Write-Warning 'JAVA_HOME no definido. Se requiere un JDK 17+ (usa IntelliJ o define JAVA_HOME).'
    exit 1
}

# 1. Empaqueta la app si el jar no existe
if (-not (Test-Path $jar)) {
    Write-Host 'Construyendo el jar de la app por primera vez...'
    Push-Location (Join-Path $root 'app')
    try { & $mvnw -q -DskipTests package; if ($LASTEXITCODE -ne 0) { throw 'Fallo al empaquetar la app.' } }
    finally { Pop-Location }
}

# 2. Detiene instancias previas
$existing = Get-CimInstance Win32_Process -Filter "Name='java.exe'" -ErrorAction SilentlyContinue |
    Where-Object { $_.CommandLine -like '*store-app-*-SNAPSHOT.jar*' }
foreach ($proc in $existing) {
    Write-Host "Deteniendo instancia previa (PID $($proc.ProcessId))..."
    Stop-Process -Id $proc.ProcessId -Force -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 2

# 3. Arranca la app
$java = Join-Path $env:JAVA_HOME 'bin\java.exe'
if (-not (Test-Path $java)) { throw "No se encontro $java" }
if (Test-Path (Join-Path $root 'app\target')) { New-Item -ItemType Directory -Force -Path (Join-Path $root 'app\target') | Out-Null }
$log = Join-Path $root 'app\target\store-app.log'

$args = @('-jar', ('"' + $jar + '"'))
$proc = Start-Process -FilePath $java -ArgumentList $args -RedirectStandardOutput $log -RedirectStandardError "$log.err" -PassThru
$proc.Id | Set-Content $pidFile
Write-Host "SUT arrancando (PID $($proc.Id), puerto $Port). Log: $log"

# 4. Espera al health check
$ok = $false
for ($i = 0; $i -lt 60; $i++) {
    Start-Sleep -Seconds 2
    if ($proc.HasExited) {
        Get-Content $log -Tail 30
        throw "La app murio al iniciar (exit $($proc.ExitCode))."
    }
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:$Port/actuator/health" -TimeoutSec 3
        if ($health.status -eq 'UP') { $ok = $true; break }
    } catch { }
}
if (-not $ok) {
    Get-Content $log -Tail 40
    throw 'La app no respondio al health check.'
}

Write-Host "SUT listo: http://localhost:$Port"