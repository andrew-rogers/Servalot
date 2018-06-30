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
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class Service extends Thread{

    private String name;
    private File root_dir;
    private List<String> cmd;
    private String address;
    private int port;

    Service(String name, File root_dir, List<String> cmd, String address, int port){
        this.name=name;
        this.root_dir=root_dir;
        this.cmd=cmd;
        this.address=address;
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket = null;
        Process process = null;
        ProcessBuilder processBuilder = null;
        StreamConnectorThread stdout=null;

        try {
            ServerSocket httpServerSocket = new ServerSocket(port);

            while(true){

                // Wait connection then accept
                socket = httpServerSocket.accept();

                // Start the service process
                processBuilder = new ProcessBuilder(cmd);
                Map<String, String> env = processBuilder.environment();
                String path=env.get("PATH");
                path=root_dir.getPath()+"/bin:"+path;
                env.put("PATH", path);
                processBuilder.directory(root_dir);
                process = processBuilder.start();

                // Start process stdout to socket thread
                stdout = new StreamConnectorThread(process.getInputStream(), socket.getOutputStream());
                stdout.start();

                // Start socket to process stdin thread.
                StreamConnectorThread stdin = new StreamConnectorThread(socket.getInputStream(), process.getOutputStream());
                stdin.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getServiceName(){
        return name;
    }

    public List<String> getCommand(){
        return cmd;
    }

    public int getPort(){
        return port;
    }

    public void cleanUp(){

    }
}
