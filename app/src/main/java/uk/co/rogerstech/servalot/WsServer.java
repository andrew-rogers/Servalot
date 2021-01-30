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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.HandshakeState;
import org.java_websocket.enums.Opcode;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.server.WebSocketServer;

import org.json.JSONException;
import org.json.JSONObject;

public class WsServer extends WebSocketServer {

    private Logger logger = null;
    private WsHttpHandler httpHandler = null;
    private HashMap<WebSocket, WsNode> nodes;
    private NodeFactory peer_factory = null;

	public WsServer( NodeFactory pf, int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ), new ArrayList<Draft>(Arrays.asList(new Draft_6455(), new Draft_HTTPD())) );
        logger = Logger.getInstance();
        setReuseAddr(true);
        nodes = new HashMap<WebSocket, WsNode>();
        peer_factory = pf;
	}

	@Override
	public void onStart() {
        logger.info("WebSocket server started!");
		setConnectionLostTimeout(60); // Check every minute.
	}

    @Override
	public void onError( WebSocket ws, Exception ex ) {
        logger.error("WS oops!" + ex);
	}

	@Override
	public void onOpen( WebSocket ws, ClientHandshake h ) {
        //dumpHeaderFields(h);
        if( h.getFieldValue("Upgrade").equals("websocket") ) {
            logger.info("WS open ");
            ws.send("Hello!");

            // Create the local WebSocket node
            WsNode local = new WsNode(ws);
            nodes.put(ws, local);

            // Create the peer node
            Node peer = peer_factory.createNode();

            // Tie them together
            peer.setPeer(local);
            local.setPeer(peer);
        }
        else {
            // Send the HTML and close
            ws.send(httpHandler.handle(h.getResourceDescriptor(), null).getBytes());
            ws.close();
        }
	}

    @Override
    public void onClose( WebSocket ws, int code, String reason, boolean remote ) {
	    logger.info("WS close");
	    if( nodes.containsKey(ws) ) {
	        nodes.get(ws).close();
	        nodes.remove(ws);
	    }
    }

	@Override
	public void onMessage( WebSocket ws, String message ) {
		logger.info( "WS msg: " + message );
        String cmd = null;
        try {
            JSONObject obj = new JSONObject(message);
            cmd = obj.getString("cmd");
            if( cmd != null ) {
                WsNode node = nodes.get(ws);
                if( obj.has("cb_num") ) {
                    node.setCBNum(obj.getString("cb_num"));
                }
                node.onMessage(obj);
            }
        }
        catch(JSONException e) {
            // TODO
        }

        // Only broadcast if the message is not a command.
        if (cmd==null) broadcast( message );
	}

    public void setHttpHandler(WsHttpHandler handler) {
        httpHandler = handler;
    }

    public void dumpHeaderFields(Handshakedata h) {
        Iterator<String> it=h.iterateHttpFields();
        while(it.hasNext()) {
            String field = it.next();
            logger.info("" + field + " : " + h.getFieldValue(field));
        }
    }

    static class Draft_HTTPD extends Draft_6455 {

        private Logger logger = Logger.getInstance();

        Draft_HTTPD() {
            super();
        }

        @Override
        public Draft copyInstance() {
                // New Draft instance for each socket.
                return new Draft_HTTPD();
        }

        @Override
        public HandshakeState acceptHandshakeAsServer( ClientHandshake handshakedata ) throws InvalidHandshakeException {
            return HandshakeState.MATCHED;
        }

        @Override
        public List<ByteBuffer> createHandshake( Handshakedata handshakedata, boolean withcontent ) {
            // Return empty list
            ArrayList<ByteBuffer> ret=new ArrayList<ByteBuffer>();
            return ret;
        }

        @Override
        public ByteBuffer createBinaryFrame( Framedata framedata ) {
            byte[] data=framedata.getPayloadData().array();

            // Ignore control frames, only send the HTTP response.
            if(framedata.getOpcode() == Opcode.BINARY) return ByteBuffer.wrap(data);
            return ByteBuffer.allocate(0);
        }

    }

    static class HttpResponse {
        private int code;
        private byte[] response;
        final static int HTTP_OK=200;

        HttpResponse( int code, final String response) {
            this.code = code;
            this.response = response.getBytes(Charset.forName("UTF-8"));
        }

        HttpResponse( final String response) {
            this.code = HTTP_OK;
            this.response = response.getBytes(Charset.forName("UTF-8"));
        }

        public byte[] getBytes() {
            String header = "HTTP/1.1 200 OK \r\nContent-Type: text/html\r\nContent-Length: " + response.length + "\r\n\r\n";
            byte[] hb = header.getBytes(Charset.forName("UTF-8"));
            ByteBuffer bb = ByteBuffer.allocate(hb.length + response.length);
            bb.put(hb);
            bb.put(response);
            return bb.array();
        }

    }

    interface WsHttpHandler {
        public HttpResponse handle(final String resource, final String[] headers);
    }

    static class DemoHandler implements WsHttpHandler {
        File root_dir = null;

        DemoHandler( File root_dir ) {
            this.root_dir = root_dir;
        }

        @Override
        public HttpResponse handle(final String resource, final String[] headers) {

            // The HTML and JavaScript for the WebSocket client
            String html = "";

            String loc = resource;
            if (loc.equals("/")) loc = "/index.html";
            loc = "www/" + loc;

            try {
                html = FileCommands.readString(new File(root_dir, loc));
            }
            catch (FileNotFoundException e) {
                // TODO: send 404.
            }
            catch (IOException e) {
                // TODO: send internal server error
            }

            return new HttpResponse(html);
        }
    }
}

