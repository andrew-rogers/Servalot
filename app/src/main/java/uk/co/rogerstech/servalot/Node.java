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

import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONObject;

abstract class Node {

    protected int id=0;

    public Node() {
		id = NodeList.getInstance().registerNode(this);
		Logger.getInstance().info("New node id=" + id);
	}

    abstract public JSONObject getDescription();
    abstract public InputStream getInputStream();
    abstract public OutputStream getOutputStream();
    abstract public StreamConnection getConnection();
    abstract public void setConnection(StreamConnection c);
    abstract public void close();
}

