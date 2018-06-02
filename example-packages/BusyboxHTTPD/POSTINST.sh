# No shebang required

# Assumes Servalot has set the working directory to the files directory of the app.
export FILES_DIR="$PWD"
export BB="busybox-armv7l"
chmod 755 "bin/$BB"
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
cd bin
rm -f wget
ln -s wget-armeabi wget
cd ..

