/*
    Servalot - An inetd like multi-server
    Copyright (C) 2020,2021  Andrew Rogers

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

public class TcpServer extends Thread {

    private NodeFactory peerFactory;
    private String address;
    private int port;
    private ServerSocket serverSocket;

    TcpServer(NodeFactory peerFactory, String address, int port){
        this.peerFactory = peerFactory;
        this.address=address;
        this.port = port;
    }

    public Node createNode() {
        Node node = null;
        try {
            // Wait for a connection then accept
            Socket socket = serverSocket.accept();
            node = new TcpNode(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(port);

            while(true) {
                // Will wait for connection then accept and create a node
                Node local = createNode();

                // Create the peer node
                Node peer = peerFactory.createNode();

                // Start the connection
                StreamConnection c = new StreamConnection(local, peer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getPort(){
        return port;
    }

    public void cleanUp(){

    }
}
