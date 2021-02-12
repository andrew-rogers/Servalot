# This file is to be dot sourced by the terminal

#    Servalot - An inetd like multi-server
#    Copyright (C) 2021  Andrew Rogers
#
#    This program is free software; you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation; either version 2 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License along
#    with this program; if not, write to the Free Software Foundation, Inc.,
#    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

export SERVALOT_LIBS=

wildbox() {

    local cmd="$1"
    local LD="$SERVALOT_LIBS/ld-musl-aarch64.so"
    local BB="$SERVALOT_LIBS/busybox.so"
    local WB="$SERVALOT_LIBS/wildbox.so"
    shift

    case "$cmd" in

        "get_writeable_dir" )
            # If current directory is child of an existing Servalot directory then get Servalot parent directory.
            local here=${PWD%%/Servalot*}
            if [ -w "$here" ]; then
                # We can write to the current dir so install here.
                echo "$here";
            elif [ -w "/data/data/org.connectbot/files" ]; then
                echo "/data/data/org.connectbot/files"
            else
                echo ""
            fi
        ;;

        "mkdir" )
            "$LD" "$BB" mkdir -p "$1"
        ;;

        "mklink" )
            "$LD" "$BB" rm -f "$FILES_DIR/$1"
            "$LD" "$BB" ln -s "$WB" "$FILES_DIR/$1"
        ;;

        "mklinks" )
            wildbox mkdir "$FILES_DIR/bin"
            wildbox mklink "bin/ln"
            wildbox mklink "bin/ifconfig"

    esac
}

cdf() {
    cd "$FILES_DIR/$1"
}

# If running setup in Servalot then FILES_DIR will already be set to a
#  writeable directory. If not set to a writable directory then create a
#  Servalot directory in a writeable location.
if [ ! -w "$FILES_DIR" ]; then
    FILES_DIR="$(wildbox get_writeable_dir)/Servalot"
    wildbox mkdir "$FILES_DIR"
fi

if [ -w "$FILES_DIR" ]; then
    export FILES_DIR
    export PATH="$FILES_DIR/bin:$PATH"
    export WILDBOX_HELPER="$FILES_DIR/wildbox-helper.sh"

    # Install wildbox helper if not already installed
    if [ ! -e "$WILDBOX_HELPER" ]; then
        cat /sdcard/Servalot/wildbox-helper.sh > "$WILDBOX_HELPER"
    fi

    # Setup links if not already done
    if [ ! -e "$FILES_DIR/bin/ln" ]; then
        wildbox mklinks
    fi
else
    echo "Could not create a writeable directory for WildBox links." >&2
fi

