<#
PowerShell helper that compiles a Triangle-generated program.ll together with runtime.c
into a native executable using LLVM tools (opt/llc/clang). Usage:

  .\compile_program_native.ps1 -ProgramLl ..\test\program.ll -OutExe program.exe

If opt/llc are available it will optionally run opt -O2 on the IR before codegen.
#>
param(
    [string]$ProgramLl = "..\test\program.ll",
    [string]$RuntimeC = "runtime.c",
    [string]$OutExe = "program.exe",
    [switch]$Optimize
)

$here = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $here

function Check-Tool($name) {
    $cmd = Get-Command $name -ErrorAction SilentlyContinue
    return $null -ne $cmd
}

if (-not (Test-Path $ProgramLl)) { Write-Error "Program LLVM file not found: $ProgramLl"; exit 1 }
if (-not (Test-Path $RuntimeC)) { Write-Error "Runtime C not found: $RuntimeC"; exit 1 }

# Try to locate clang: prefer in PATH, otherwise look in common install locations (Scoop, Program Files)
$clangPath = $null
$clangCmd = Get-Command clang -ErrorAction SilentlyContinue
if ($clangCmd) { $clangPath = $clangCmd.Path }
else {
    $candidates = @(
        "$env:USERPROFILE\scoop\apps\llvm\current\bin\clang.exe",
        "$env:ProgramFiles\LLVM\bin\clang.exe",
        "$env:ProgramFiles(x86)\LLVM\bin\clang.exe"
    )
    foreach ($p in $candidates) { if (Test-Path $p) { $clangPath = $p; break } }
}
if (-not $clangPath) { Write-Error "clang not found in PATH or common locations. Install LLVM/clang or add it to PATH."; exit 1 }

# Optional optimize
$workLl = $ProgramLl
if ($Optimize) {
    if (Check-Tool 'opt') {
        $optLl = [System.IO.Path]::ChangeExtension($ProgramLl, ".opt.ll")
        opt -O2 "$ProgramLl" -S -o "$optLl"
        if ($LASTEXITCODE -eq 0) { $workLl = $optLl; Write-Host "Optimized IR: $optLl" } else { Write-Warning "opt failed, continuing with original IR." }
    } else {
        Write-Warning "opt not found; skipping IR optimization.";
    }
}

# Use clang to link the program IR and runtime C (clang accepts .ll and .c)
& "$clangPath" "$workLl" "$RuntimeC" -o "$OutExe"
if ($LASTEXITCODE -ne 0) { Write-Error "clang linking failed"; exit 1 }
Write-Host "Generated native executable: $OutExe"
