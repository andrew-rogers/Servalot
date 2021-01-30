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

import org.json.JSONObject;

public class WebViewNode extends Node {

	private int port = 0;
	private WebViewHelper.WebViewServer server = null;

    WebViewNode(WebViewHelper.WebViewServer s, Integer p) {
        server = s;
        port = p;
    }

    public Integer getPort() {
        return port;
    }

	public void send(JSONObject obj){
		server.send( this, obj );
	}

    @Override
    public void onClose() {
        server.onClose(port);
	}
}

