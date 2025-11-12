<#
PowerShell helper to produce LLVM IR and object for the runtime.
Usage:
  .\build_runtime.ps1          # generates runtime.ll and runtime.obj (if tools available)
#>

$here = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $here

$runtimeC = Join-Path $here 'runtime.c'
$runtimeLL = Join-Path $here 'runtime.ll'
$runtimeObj = Join-Path $here 'runtime.obj'

function Check-Tool($name) {
    $cmd = Get-Command $name -ErrorAction SilentlyContinue
    return $null -ne $cmd
}

if (-not (Test-Path $runtimeC)) {
    Write-Error "runtime.c not found in $here"
    exit 1
}

if (-not (Check-Tool 'clang')) {
    Write-Warning "clang not found. You can still inspect runtime.c. To build LLVM IR install clang/LLVM."
    exit 0
}

# Generate LLVM IR (.ll)
clang -S -emit-llvm -O0 "$runtimeC" -o "$runtimeLL"
if ($LASTEXITCODE -ne 0) { Write-Error "clang failed to emit runtime.ll"; exit 1 }
Write-Host "Generated: $runtimeLL"

# Try to produce object file via llc if available, otherwise use clang to compile object
if (Check-Tool 'llc') {
    llc -filetype=obj "$runtimeLL" -o "$runtimeObj"
    if ($LASTEXITCODE -ne 0) { Write-Warning "llc failed to produce runtime.obj" }
    else { Write-Host "Generated object: $runtimeObj" }
} else {
    # fallback: ask clang to create object directly
    clang -c "$runtimeC" -o "$runtimeObj"
    if ($LASTEXITCODE -ne 0) { Write-Warning "clang failed to produce runtime.obj" }
    else { Write-Host "Generated object via clang: $runtimeObj" }
}

Write-Host "Done."
