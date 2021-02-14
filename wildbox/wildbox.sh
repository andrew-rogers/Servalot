#!/system/bin/sh

# WIth LD, run busyBOX - wildbox
# Symbolic links should be made that target this script. Argument 0 is then used to determine the name of the executable, script or busybox applet as managed by wildbox.

# TODO: Future versions of this will be a static PIE exec to avoid dependence
#       on /system/bin/sh. When this is recursively invoked from applets, the
#       /system/bin/sh will fail to run as LD_LIBRARY_PATH is set and it can't
#       find the correct libc.so

export LD="$SERVALOT_LIBS/ld-musl-aarch64.so"
export BB="$SERVALOT_LIBS/busybox.so"

if [ ! -f "$WILDBOX_HELPER" ]; then
    export WILDBOX_HELPER="$FILES_DIR/wildbox-helper.sh"
fi

if [ -f "$WILDBOX_HELPER" ]; then
    
    export LD_LIBRARY_PATH="$FILES_DIR/lib:$FILES_DIR/usr/lib"

    # Run the helper with busybox
    "$LD" "$BB" sh "$WILDBOX_HELPER" "$0" $*
else
    echo "Can't find WildBox helper." >&2
fi

