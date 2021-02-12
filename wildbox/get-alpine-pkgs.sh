#!/bin/sh

ARCH=aarch64
REPO=http://uk.alpinelinux.org/alpine/v3.13/main/$ARCH
BASE_DIR=$(cd $(git rev-parse --git-dir)/.. && pwd)
WILDBOX_DIR="$BASE_DIR/wildbox"
PKG_DIR="$WILDBOX_DIR/alpine-packages"
ROOT_DIR="$WILDBOX_DIR/alpine-root"
JNI_DIR="$BASE_DIR/app/src/main/jniLibs/arm64-v8a"

if [ -z "$BASE_DIR" ]; then
    echo "Could not locate base of working directory." 1>&2
    exit 1
fi

mkdir -p "$PKG_DIR"
mkdir -p "$ROOT_DIR"
mkdir -p "$JNI_DIR"

get_pkg () {

    local pkg=$1

    # Only download it if we don't already have it.
    if [ ! -f "$PKG_DIR/$pkg" ]; then
        (cd "$PKG_DIR" && wget "$REPO/$pkg")
    fi
}

get_index () {
    get_pkg APKINDEX.tar.gz
    (cd "$PKG_DIR" && tar -zxvf APKINDEX.tar.gz)
}

get_pkg_info () {
    local name=$1
    local ret=""
    local match=""
    while read line; do
        ret="$ret
$line"
        if [ "$line" = "P:$name" ]; then
            match="true"
        fi
        
        # If empty line
        if [ -z "$line" ]; then
            if [ -n "$match" ]; then
                break
            else
                ret=""
            fi
        fi
    done <"$PKG_DIR/APKINDEX"
    echo "$ret"
}

get_pkg_filename () {
    local inf="$(get_pkg_info "$1")"
    local p=$(echo "$inf" | sed -n 's|P:||p')
    local v=$(echo "$inf" | sed -n 's|V:||p')
    echo "$p-$v.apk"
}

expand_pkg () {
    (cd "$ROOT_DIR" && tar -zxvf "$PKG_DIR/$1")
}

pkg_add () {
    local name=$1
    local fn=$(get_pkg_filename $name)
    get_pkg "$fn"
    expand_pkg "$fn"
}

move_exec () {
    find "$ROOT_DIR" | xargs file | grep ELF | grep "pie executable" | sed "s|$ROOT_DIR/||" | sed 's|:.*||' > "$ROOT_DIR/EXEC_FILES"
    while read line; do
        so=$(echo "$line.so" | sed 's|.*/||')
        mv "$ROOT_DIR/$line" "$JNI_DIR/$so"
    done <"$ROOT_DIR/EXEC_FILES"
}

move_so () {
    find "$ROOT_DIR" | xargs file | grep ELF | grep "shared object" | sed "s|$ROOT_DIR/||" | sed 's|:.*||' > "$ROOT_DIR/SO_FILES"
    while read line; do
        so=$(echo "$line" | sed 's|.*/||' | sed 's|[.]so.*|.so|')
        mv "$ROOT_DIR/$line" "$JNI_DIR/$so"
    done <"$ROOT_DIR/SO_FILES"
}

make_tgz () {
    (cd "$ROOT_DIR" && tar --owner=0 --group=0 -zcvf ../wildbox.tgz *)
}

group_llvm () {
    pkg_add clang
    pkg_add clang-libs
    pkg_add libgcc
    pkg_add libstdc++
    pkg_add libxml2
    pkg_add llvm10-libs
    pkg_add libffi
    pkg_add xz-libs
    pkg_add musl-dev
    pkg_add llvm10
    pkg_add binutils
    pkg_add make
}

get_index

pkg_add musl
pkg_add busybox
pkg_add zlib
pkg_add dropbear

if [ "$1" = "llvm" ]; then
    group_llvm
fi

move_exec
move_so

cp "$WILDBOX_DIR/wildbox" "$JNI_DIR/wildbox.so"
cp "$WILDBOX_DIR/mklinks.sh" "$ROOT_DIR/mklinks.sh"

make_tgz

