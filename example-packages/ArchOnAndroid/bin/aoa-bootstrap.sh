#!/bin/sh

SETUP_URL="https://github.com/andrew-rogers/ArchOnAndroid/raw/master/utils/aoa-setup.sh"

# Assumes the working directory is set to the files directory of the Servalot app.
UTILS="$PWD/ArchOnAndroid/utils"

# If aoa-setup doesn't exist then download it
if [ ! -e "$UTILS/aoa-setup.sh" ]; then
    mkdir -p "$UTILS"
    wget --no-check-certificate --directory-prefix="$UTILS" "$SETUP_URL"
fi

# Run the AoA setup script
. "$UTILS/aoa-setup.sh" 2>&1

