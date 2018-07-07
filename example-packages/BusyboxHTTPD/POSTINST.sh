# No shebang required

# Assumes Servalot has set the working directory to the files directory of the app.
FILES_DIR="$PWD"
BB="busybox-armv7l"

# Rename to busybox
chmod 755 "bin/$BB"
"./bin/$BB" mv "bin/$BB" "bin/busybox"
BB="busybox"

chmod 755 "bin/wget-armeabi"

busybox_symlinks() {
  local pdir="$PWD"
  cd "$FILES_DIR/bin"
  for app in $(./$BB --list)
  do
    if [ ! -e "$app" ]; then
      ./$BB ln -s $BB $app
    fi
  done

  # Remove the wget link, busybox wget doesn't work on Android.
  rm -f wget

  cd "$pdir"
}

busybox_symlinks

# Set the shebang on the shebang setting script.
#  Assumes Servalot has set the PATH to include the bin directory.
sh bin/mkshexec bin/mkshexec

# Make the CGIs executable
mkshexec www/cgi-bin/cmd.sh
mkshexec www/cgi-bin/cmd1.sh

# Setup the wget link
(cd bin && ln -s wget-armeabi wget)


