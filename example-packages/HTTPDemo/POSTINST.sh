# No shebang required

# Assumes Servalot has set the working directory to the files directory of the app.
EXAMPLE=/sdcard/httpd_example.sh
if [ ! -e "$EXAMPLE" ]; then
  cat services/httpd.sh > "$EXAMPLE"
fi

