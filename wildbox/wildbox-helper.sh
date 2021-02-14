# dot sourced by wildbox.so in Servalot libs. This can be placed in a directory without exec permissions. An environment variable, WILDBOX_HELPER, should point to this script.

# TODO: Search PATH for sh script $name.sh and run with busybox.so sh
# TODO: Search PATH for LLVM bitcode $name.bc and run with LLVM lli


# Remove directory prefix. See Parameter Expansion section in bash man page.
name="${1##*/}"
name1=$("$LD" "$BB" which "$1")
shift

# Follow any symbolic link chain to get applet name
i=0
while [ -L "$name1" ]; do
    name="${name1##*/}"
    dir1="${name1%/*}"
    name1="$("$LD" "$BB" readlink "$name1")"

    # If link target is not absolute path then prefix with directory of link
    if [ "${name1:0:1}" != "/" ]; then
        name1="$dir1/$name1"
    fi

    # Iterate no more than 20 times
    i=$((i+1))
    if [ $i -gt 20 ]; then
        break
    fi
done

# TODO: Eventually Servalot will be split into multiple APKS, search SERVALOT_PATH $name.so
if [ -x "$SERVALOT_LIBS/$name.so" ]; then
    "$LD" "$SERVALOT_LIBS/$name.so" $*
else
    # otherwise, assume a busybox applet
    "$LD" "$BB" "$name" $*
fi

