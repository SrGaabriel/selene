#include <stdio.h>
#include <stdarg.h>

// Function to print formatted output
int println(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    int result = vprintf(fmt, args);
    va_end(args);
    return result;
}