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
    if (scanf("%d", &x) == 1) return x;
    return 0;
}

void puteol(void) {
    putchar('\n');
}
