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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Service extends Thread{

    private int port;
    private String cmd;

    Service(String cmd, int port){
        this.cmd=cmd;
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket = null;
        Process process = null;
        StreamConnectorThread stdout=null;

        try {
            ServerSocket httpServerSocket = new ServerSocket(port);

            while(true){

                // Wait connection then accept
                socket = httpServerSocket.accept();

                // Start the service process
                process = Runtime.getRuntime().exec(cmd);

                // Start process stdout to socket thread
                stdout = new StreamConnectorThread(process.getInputStream(), socket.getOutputStream());
                stdout.start();

                // Start process stderr to socket thread
                StreamConnectorThread stderr = new StreamConnectorThread(process.getErrorStream(), socket.getOutputStream());
                stderr.start();

                // Start socket to process stdin thread.
                StreamConnectorThread stdin = new StreamConnectorThread(socket.getInputStream(), process.getOutputStream());
                stdin.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
