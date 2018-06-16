#!/bin/sh

PREVDIR="$PWD"
cd "../../../AndrewWIDE"
AWDIR="$PWD"
cd "$PREVDIR"

mkdir -p www/cgi-bin
cd www
ln -s "$AWDIR/www/aw-sh.js"
ln -s "$AWDIR/www/edit.css"
ln -s "$AWDIR/www/edit.js"
ln -s "$AWDIR/www/edit.html"
ln -s "$AWDIR/www/file.js"
ln -s "$AWDIR/www/fileselector.js"
ln -s "$AWDIR/www/menu.js"
ln -s "$AWDIR/www/not_codemirror.js"

cd cgi-bin
ln -s "$AWDIR/www/cgi-bin/aw.sh"

cd "$PREVDIR"
zip AndrewWIDE.zip -r www POSTINST.sh
rm -rf www


