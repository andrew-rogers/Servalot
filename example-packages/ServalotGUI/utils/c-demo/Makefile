
CC=clang
LLI=lli

# We need to unset the LD_LIBRARY_PATH because /system/bin/sh is used to run the wildbox.
LD_LIBRARY_PATH=

%.bc: %.c
	$(CC) -emit-llvm -c -I $(FILES_DIR)/usr/include $<

run: hello.bc
	$(LLI) $^


