# Run with sh

CC=usr/bin/clang
LLI=usr/bin/lli

"$CC" -emit-llvm -c -I usr/include hello.c 2>&1
"$LLI" hello.bc 2>&1


