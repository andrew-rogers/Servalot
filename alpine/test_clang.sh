# Run with sh

CC=usr/bin/clang
LLI=usr/bin/lli

"$CC" -emit-llvm -c -I usr/include hello.c 2>&1
LD_LIBRARY_PATH="$PWD/lib:$PWD/usr/lib" "$LLI" hello.bc 2>&1


