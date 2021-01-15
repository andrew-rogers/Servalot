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

package uk.co.rogerstech.servalot;

public class StreamConnection {

    private Node node_a;
    private Node node_b;

    StreamConnection(Node a, Node b){
        node_a=a;
        node_b=b;
        node_a.setConnection(this);
        node_b.setConnection(this);
        connect();
    }

    public void connect() {
        // Start A to B thread
        StreamConnectorThread a2b = new StreamConnectorThread(this, node_a.getInputStream(), node_b.getOutputStream());
        a2b.start();

        // Start B to A thread.
        StreamConnectorThread b2a = new StreamConnectorThread(this, node_b.getInputStream(), node_a.getOutputStream());
        b2a.start();

    }

    public void close() {
        node_a.close();
        node_b.close();
    }

}
