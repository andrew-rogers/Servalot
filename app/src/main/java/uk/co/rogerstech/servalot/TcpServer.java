/*
    Servalot - An inetd like multi-server
    Copyright (C) 2020  Andrew Rogers

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

    TcpServer(NodeFactory peerFactory, String address, int port){
        this.peerFactory = peerFactory;
        this.address=address;
        this.port = port;
    }

    @Override
    public void run() {

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            // Wait connection then accept
            Socket socket = serverSocket.accept();

            // Create the peer node
            Node peer = peerFactory.createNode();

            // Start peer to socket thread
            StreamConnectorThread peer2socket = new StreamConnectorThread(peer.getInputStream(), socket.getOutputStream());
            peer2socket.start();

            // Start socket to peer thread.
            StreamConnectorThread socket2peer = new StreamConnectorThread(socket.getInputStream(), peer.getOutputStream());
            socket2peer.start();

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
