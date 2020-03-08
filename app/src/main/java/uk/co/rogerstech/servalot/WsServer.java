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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.enums.HandshakeState;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.HandshakeImpl1Server;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

public class WsServer extends WebSocketServer {

    private Logger logger = null;
    private WsHttpHandler httpHandler = null;

	public WsServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ), new ArrayList<Draft>(Arrays.asList(new Draft_6455(), new Draft_HTTPD())) );
        //super( new InetSocketAddress( port ), new ArrayList<Draft>(Arrays.asList(new Draft_HTTPD())) );
        logger = Logger.getInstance();
        setReuseAddr(true);
	}

	@Override
	public void onStart() {
        logger.info("WebSocket server started!");
		setConnectionLostTimeout(60); // Check every minute.
        
	}

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer( WebSocket conn, Draft draft, ClientHandshake request ) throws InvalidDataException {
       return new HandshakeImpl1Server();
    }

    @Override
	public void onError( WebSocket ws, Exception ex ) {
        logger.error("WS oops!" + ex);
	}

	@Override
	public void onOpen( WebSocket ws, ClientHandshake h ) {
        logger.info("WS open");
		ws.send("Hello!");
	}

    @Override
    public void onClose( WebSocket ws, int code, String reason, boolean remote ) {
	    logger.info("WS close");
    }

	@Override
	public void onMessage( WebSocket ws, String message ) {
		logger.info( "WS msg: " + message );
	}

    public void setHttpHandler(WsHttpHandler handler) {
        httpHandler = handler;
    }

    static class Draft_HTTPD extends Draft_6455 {

        private Logger logger = Logger.getInstance();

        Draft_HTTPD() {
            super();
        }

        @Override
        public Draft copyInstance() {
                return new Draft_HTTPD();
        }

        @Override
        public HandshakeState acceptHandshakeAsServer( ClientHandshake handshakedata ) throws InvalidHandshakeException {
            return HandshakeState.MATCHED;
        }

        @Override
        public List<ByteBuffer> createHandshake( Handshakedata handshakedata, boolean withcontent ) {
            ArrayList<ByteBuffer> ret=new ArrayList<ByteBuffer>();
            byte[] resp=new HttpResponse("<html><head></head><body><h1>Hello world!</h1></body></html>").getBytes();
            ByteBuffer bb=ByteBuffer.allocate(resp.length);
            bb.put(resp);
            bb.flip();
            ret.add(bb);
            return ret;
        }

        @Override
        public ByteBuffer createBinaryFrame( Framedata framedata ) {
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
        public HttpResponse handle(final String[] headers);
    }

    // TODO adapt this to send the websocket client to the browser
    static class DemoHandler implements WsHttpHandler {

        @Override
        public HttpResponse handle(final String[] headers) {
            return new HttpResponse("<html><head></head><body><h1>Hello world!</h1></body></html>");
        }
    }

}

