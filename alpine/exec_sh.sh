#!/data/data/uk.co.rogerstech.servalot/files/bin/sh

FILES_DIR="/data/data/uk.co.rogerstech.servalot/files"

script="${0##*/}"

LD_LIBRARY_PATH="$FILES_DIR/lib:$FILES_DIR/usr/lib" "$FILES_DIR/bin/sh" "$FILES_DIR/scripts/$script.sh" $* 
