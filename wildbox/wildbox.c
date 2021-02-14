/*
    Servalot - An inetd like multi-server
    Copyright (C) 2021  Andrew Rogers

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>

void env_error(char *ev)
{
    fprintf( stderr, "Environment variable %s must be set.\n", ev );
    exit(1);
}

int main( int argc, char *argv[] )
{
    char* fdir=getenv("FILES_DIR");
    if( fdir == NULL )
    {
        env_error( "FILES_DIR" );
    }

    char* helper=getenv("WILDBOX_HELPER");
    if( helper == NULL )
    {
        // WILDBOX_HELPER="$FILES_DIR/wildbox-helper.sh"
        char* wbh = "/wildbox-helper.sh";
        char wh[ strlen(fdir) + strlen(wbh) + 1 ];
        sprintf( wh, "%s%s", fdir, wbh );
        setenv( "WILDBOX_HELPER", wh, 0 );
    }
    helper=getenv("WILDBOX_HELPER");
    if( helper == NULL )
    {
        env_error( "WILDBOX_HELPER" );
    }

    char* slibs=getenv("SERVALOT_LIBS");
    if( slibs == NULL )
    {
        env_error( "SERVALOT_LIBS" );
    }

    // LD_LIBRARY_PATH="$FILES_DIR/lib:$FILES_DIR/usr/lib"
    char* ldp1="/lib";
    char* ldp2="/usr/lib";
    char ldp[ strlen(fdir)*2 + strlen(ldp1) + strlen(ldp2) + 2 ];
    sprintf( ldp, "%s%s:%s%s", fdir, ldp1, fdir, ldp2 );
    setenv( "LD_LIBRARY_PATH", ldp, 0 );

    // LD="$SERVALOT_LIBS/ld-musl-aarch64.so"
    char* ld1="/ld-musl-aarch64.so";
    char ld[ strlen(slibs) + strlen(ld1) + 1 ];
    sprintf( ld, "%s%s", slibs, ld1 );
    setenv( "LD", ld, 0 );

    // BB="$SERVALOT_LIBS/busybox.so"
    char* bb1="/busybox.so";
    char bb[ strlen(slibs) + strlen(bb1) + 1 ];
    sprintf( bb, "%s%s", slibs, bb1 );
    setenv( "BB", bb, 0 );

    // Run the helper with busybox
    // "$LD" "$BB" sh "$WILDBOX_HELPER" "$0" $*
    char* argv1[argc+5];
    argv1[0]=ld;
    argv1[1]=bb;
    argv1[2]="sh";
    argv1[3]=helper;
    for( int i=0; i<=argc; i++)
    {
        argv1[i+4]=argv[i];
    }
    // Use execv() to inherit environment variables from current process.
    int retval=execv( ld, argv1 );
    return retval;
}

