#include <stdio.h>
#include <stdlib.h>

// Minimal runtime for Triangle LLVM output
// Provides: putint, getint, puteol

void putint(int x) {
    // print integer without newline
    printf("%d", x);
}

int getint(void) {
    int x = 0;
    if (scanf_s("%d", &x) == 1) return x;
    return 0;
}

void puteol(void) {
    putchar('\n');
}

// Character I/O wrappers for LLVM emitter
void putchar_llvm(int c) {
    putchar(c);
}

int getchar_llvm(void) {
    int c = getchar();
    if (c == EOF) return -1;
    return c;
}
