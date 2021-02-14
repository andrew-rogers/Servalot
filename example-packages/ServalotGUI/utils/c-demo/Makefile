
CC=clang
LLI=lli

%.bc: %.c
	$(CC) -emit-llvm -c -I $(FILES_DIR)/usr/include $<

run: hello.bc
	$(LLI) $^

