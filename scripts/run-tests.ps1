param(
    [string]$Groups = '',
    [string]$Browser = 'chrome',
    [switch]$Headless,
    [switch]$ForceStart
)
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$mvnw = Join-Path $root 'mvnw.cmd'
if (-not (Test-Path $mvnw)) { throw 'No se encontro mvnw.cmd en la raiz del proyecto.' }
if (-not $env:JAVA_HOME) {
    Write-Warning 'JAVA_HOME no definido. Se requiere un JDK 17+ (usa IntelliJ o define JAVA_HOME).'
    exit 1
}

# Asegura que el SUT este levantado
try {
    $health = Invoke-RestMethod -Uri 'http://localhost:8080/actuator/health' -TimeoutSec 3
    if ($health.status -ne 'UP') { throw 'SUT no UP' }
} catch {
    Write-Host 'SUT no esta corriendo. Arrancandolo...'
    & (Join-Path $PSScriptRoot 'start-app.ps1')
    if ($LASTEXITCODE -ne 0) { exit 1 }
}

$groupsArgs = if ($Groups) { @('-Dtest.groups=' + $Groups) } else { @() }
$browserArgs = @('-Dbrowser=' + $Browser)
$headlessArgs = if ($Headless) { @('-Dheadless=true') } else { @() }

Push-Location (Join-Path $root 'qa')
try {
    & $mvnw @('test') @groupsArgs @browserArgs @headlessArgs
    exit $LASTEXITCODE
}
finally {
    Pop-Location
}