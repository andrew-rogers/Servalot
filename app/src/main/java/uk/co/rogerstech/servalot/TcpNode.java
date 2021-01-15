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

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONObject;

public class TcpNode implements Node {

    private InputStream istream = null;
    private OutputStream ostream = null;
    private JSONObject description = null;
    private StreamConnection connection = null;

    TcpNode(Socket socket) {
        try {
            istream = socket.getInputStream();
            ostream = socket.getOutputStream();
        } catch (IOException e) {
            // TODO
        }
    }

    public JSONObject getDescription() { return description; }
    public InputStream getInputStream() { return istream; }
    public OutputStream getOutputStream() { return ostream; }
    public StreamConnection getConnection() { return connection; }
    public void setConnection(StreamConnection c) { connection = c; }
    public void close() {
        try {
            ostream.close();
            istream.close();
        } catch (IOException e) {
            // TODO
        }
    }
}

