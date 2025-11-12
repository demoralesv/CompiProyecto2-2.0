Runtime for LLVM-generated Triangle programs

Files:
- runtime.c             : C implementation of putint(int), getint(void), puteol(void).
- build_runtime.ps1     : PowerShell helper to generate runtime.ll and runtime.obj (uses clang/llc if available).
- compile_program_native.ps1 : PowerShell helper to compile a program.ll together with runtime.c into a native executable (uses clang, opt/llc optional).

How to use (PowerShell):
1) Build the runtime IR/object (optional):
   cd ide-triangle-v1.1.src\runtime
   .\build_runtime.ps1

2) Compile a generated program (example):
   Suppose your compiler produced `ide-triangle-v1.1.src/test/prueba.ll`.

   cd ide-triangle-v1.1.src\runtime
   .\compile_program_native.ps1 -ProgramLl ..\test\prueba.ll -OutExe prueba.exe

This script will call `clang` and (optionally) `opt`. If you don't have clang installed, install LLVM (for Windows use Chocolatey or Scoop) and ensure `clang`, `llc`, `opt` are in PATH.

Notes:
- On Windows, using clang from LLVM is the easiest option. If you prefer other toolchains (MSVC), adapt the linking commands.
- If you want a fully static linking or custom runtime behavior (locale, flush, formatting), modify `runtime.c` accordingly.
