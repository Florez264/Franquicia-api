param(
    [ValidateSet('init', 'validate', 'plan', 'summary', 'adopt')]
    [string]$Action = 'validate'
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$terraformDirectory = Join-Path $projectRoot 'terraform'
# Los certificados auxiliares van al temporal de Windows, fuera del proyecto.
$temporaryDirectory = Join-Path ([IO.Path]::GetTempPath()) 'Franquicia-api-terraform'
$profilePath = Join-Path $env:APPDATA 'clever-cloud/clever-tools.json'

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Instala e inicia Docker Desktop antes de ejecutar este script.'
}
if ($Action -in @('plan', 'adopt') -and -not $env:CC_ORGANISATION) {
    throw 'Define CC_ORGANISATION con el ID user_... u orga_... de Clever Cloud.'
}

New-Item -ItemType Directory -Path $temporaryDirectory -Force | Out-Null
$certificateBundle = Join-Path $temporaryDirectory 'windows-trusted-ca.pem'
$certificateLines = foreach ($certificate in Get-ChildItem Cert:\CurrentUser\Root,Cert:\LocalMachine\Root,Cert:\CurrentUser\CA,Cert:\LocalMachine\CA) {
    '-----BEGIN CERTIFICATE-----'
    [Convert]::ToBase64String($certificate.RawData, [Base64FormattingOptions]::InsertLineBreaks)
    '-----END CERTIFICATE-----'
}
# Solo certificados públicos del almacén de confianza de Windows, nunca claves privadas.
[IO.File]::WriteAllLines($certificateBundle, [string[]]$certificateLines, [Text.Encoding]::ASCII)

$dockerArguments = @(
    'run', '--rm',
    '-e', 'CHECKPOINT_DISABLE=1',
    '-e', 'SSL_CERT_FILE=/certs/windows-ca.pem',
    '-e', 'CC_ORGANISATION',
    '-v', "${terraformDirectory}:/work",
    '-v', "${certificateBundle}:/certs/windows-ca.pem:ro",
    '-w', '/work'
)
if (Test-Path -LiteralPath $profilePath) {
    # Se monta la sesión en modo lectura. No se copian tokens al repositorio ni a argumentos.
    $dockerArguments += @('-v', "${profilePath}:/root/.config/clever-cloud/clever-tools.json:ro")
} elseif ($Action -in @('plan', 'adopt')) {
    throw 'No se encontró la sesión de Clever Tools. Ejecuta clever.cmd login.'
}
$dockerArguments += 'hashicorp/terraform:1.13.0'

switch ($Action) {
    'init' { $terraformArguments = @('init', '-backend=false', '-input=false', '-lockfile=readonly', '-no-color') }
    'validate' { $terraformArguments = @('validate', '-no-color') }
    'plan' { $terraformArguments = @('plan', '-input=false', '-out=adoption.tfplan', '-no-color') }
    'summary' { $terraformArguments = @('show', '-json', 'adoption.tfplan') }
    'adopt' {
        # Solo permite aplicar el plan revisado de una importación sin cambios remotos.
        $savedPlanJson = & docker @dockerArguments show -json adoption.tfplan
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
        $savedPlan = ($savedPlanJson -join "`n") | ConvertFrom-Json
        $imports = @($savedPlan.resource_changes | Where-Object { $_.change.importing })
        $remoteChanges = @($savedPlan.resource_changes | Where-Object { ($_.change.actions -join ',') -ne 'no-op' })
        if ($remoteChanges.Count -ne 0 -or $imports.Count -ne 1 -or
            $imports[0].address -ne 'clevercloud_mysql.franquicia') {
            throw 'El plan no es una importación única sin cambios. Genera y revisa un plan nuevo.'
        }
        Write-Host 'Se registrará la base existente en el estado local. No hay cambios remotos en el plan.'
        $terraformArguments = @('apply', '-input=false', '-no-color', 'adoption.tfplan')
    }
}

if ($Action -eq 'summary') {
    # Leer el JSON en memoria sin imprimir valores de recursos que pueden contener secretos.
    $planJson = & docker @dockerArguments @terraformArguments
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    $plan = ($planJson -join "`n") | ConvertFrom-Json
    foreach ($resource in $plan.resource_changes) {
        [PSCustomObject]@{
            Resource = $resource.address
            Actions  = $resource.change.actions -join ','
            ImportId = $resource.change.importing.id
        }
    }
} else {
    & docker @dockerArguments @terraformArguments
    exit $LASTEXITCODE
}
