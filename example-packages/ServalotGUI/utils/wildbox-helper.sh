# dot sourced by wildbox.so in Servalot libs. This can be placed in a directory without exec permissions. An environment variable, WILDBOX_HELPER, should point to this script.

# TODO: Search PATH for sh script $name.sh and run with busybox.so sh
# TODO: Search PATH for LLVM bitcode $name.bc and run with LLVM lli


# Remove directory prefix. See Parameter Expansion section in bash man page.
name="${1##*/}"
shift

# TODO: Eventually Servalot will be split into multiple APKS, search SERVALOT_PATH $name.so
if [ -x "$SERVALOT_LIBS/$name.so" ]; then
    "$LD" "$SERVALOT_LIBS/$name.so" $*
else
    # otherwise, assume a busybox applet
    "$LD" "$BB" "$name" $*
fi

