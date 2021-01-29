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

import org.java_websocket.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class WsNode extends Node {

	private String cb_num = null;
	private WebSocket websocket = null;

    WsNode(WebSocket ws) {
        websocket = ws;
    }

	public void setCBNum(String cbnum) {
		cb_num=cbnum;
	}

	public void send(JSONObject obj){
		try {
			if( cb_num != null ) obj.put("cb_num",cb_num);
		}
		catch(JSONException e) {
            // TODO
        }
        websocket.send(obj.toString());
	}

    public void close() {
		// TODO: Close streams and release bt socket
		NodeList.getInstance().remove(id);
	}
}

