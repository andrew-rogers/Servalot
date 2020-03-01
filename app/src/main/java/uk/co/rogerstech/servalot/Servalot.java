/*
    Servalot - An inetd like multi-server
    Copyright (C) 2018  Andrew Rogers

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

package uk.co.rogerstech.servalot;

import java.io.File;

public class Servalot {

    public static void main(String[] args){

        // Set filesDir to ~/.servalot/files
        File filesDir = new File(System.getProperty("user.home"),".servalot");
        filesDir = new File(filesDir,"files");

        System.out.println("Files Directory: "+filesDir.getAbsolutePath());

        // Start service manager
        ServiceManager serviceManager = new ServiceManager(filesDir, new File(filesDir,"services.tsv"));
        // TODO: register NodeFactoryBuilders
        serviceManager.load();

        // Endless loop (service threads should be running)
        int cnt=0;
        while(true) {
            try {
                // Sleep for 10 seconds
                Thread.sleep(10000);
                cnt+=10;
                System.out.println(""+cnt);
	            
            } catch(InterruptedException e) {
            }
        }
    }
}

