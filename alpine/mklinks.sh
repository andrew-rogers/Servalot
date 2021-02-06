# This is run by the busybox sh on device

BB="$BIN_DIR/busybox.so"
LD="$BIN_DIR/ld-musl-aarch64.so"

mk_link () {
    "$LD" "$BB" rm -f "$2"
    "$LD" "$BB" ln -s "$1" "$2"
}

so_links () {
    while read line; do
        so=$(echo "$line" | sed 's|.*/||' | sed 's|[.]so.*|.so|')
        mk_link "$BIN_DIR/$so" "$line"
    done <"SO_FILES"
}

exec_links () {
    while read line; do
        so=$(echo "$line.so" | sed 's|.*/||')
        mk_link "$BIN_DIR/$so" "$line"
    done <"EXEC_FILES"
}

so_links
exec_links

