# No shebang required

# Assumes Servalot has set the working directory to the files directory of the app.

# Make the scripts executable
mkshexec bin/aoa-bootstrap.sh
mkshexec bin/make

# Download the AoA setup scripts
aoa-bootstrap.sh

