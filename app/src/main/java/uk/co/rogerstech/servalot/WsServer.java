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
import java.util.List;

import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.HandshakeImpl1Server;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.WebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

public class WsServer extends WebSocketServer {

    private Logger logger = null;
    private WsHttpHandler httpHandler = null;

	public WsServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
        logger = Logger.getInstance();
        setReuseAddr(true);
	}

	@Override
	public void onStart() {
        logger.info("WebSocket server started!");
		setConnectionLostTimeout(60); // Check every minute.
        setWebSocketFactory(new WsFactory());
        
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
		ws.send("Hello!");
        logger.info("WS open");
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

    public class WsChannel implements ByteChannel {

        private SocketChannel socketChannel;
        final static int HEADER = 0;
        final static int WS = 1;
        private int state = HEADER;
        private String header="";
        private String[] headers = null;

        WsChannel( SocketChannel channel ) {
            socketChannel = channel;
        }

        public void processHeaderData(byte[] data, int start, int end) {
            // TODO this may be clearer with a byte-by-byte state machine
            header += new String(data, start, end-start).replace("\r","");
            if( header.contains("\n\n") ){ // Empty line signals end of headers
                headers = header.split("[\\n]+");
                int hl=headers.length;
                for( int i=0; i<hl; i++) {
                    String h=headers[i];
                    if(h.startsWith("Upgrade:") && h.contains("websocket")) state=WS;
                }
            }
        }

        public int read(ByteBuffer dst) throws IOException {
            int nr=0;
            if( state == WS ) {
                nr=socketChannel.read(dst);
            }
            else {
                /* Even if it's not a websocket request, send to websocket handler
                   code anyway. The 404 response is intercepted in write() */
                int p0=dst.position();
                nr=socketChannel.read(dst);
                int p1=dst.position();
                processHeaderData(dst.array(),p0,p1);
            }
            return nr;
        }

        public int write( ByteBuffer src ) throws IOException {
            int nw=0;
            if( state == WS ) {
                nw=socketChannel.write(src);
            }
            else {
                // Intercept 404 response and generate our HTTP response

                // Pretend to write the data by getting it from the ByteBuffer.
                nw=src.limit()-src.position();
                while( src.position() < src.limit() ) src.get();

                // Create our own ByteBuffer with a web page.
                byte[] rb=httpHandler.handle(headers).getBytes();
                ByteBuffer buf=ByteBuffer.allocate(rb.length);
                buf.put(rb);
                buf.flip();
                socketChannel.write(buf);
                
            }
            return nw;
        }

        public void close() throws IOException {
            socketChannel.close();
        }

        public boolean isOpen() {
            return socketChannel.isOpen();
        }

    }

    public class WsFactory implements WebSocketServerFactory {

        @Override
        public WebSocketImpl createWebSocket( WebSocketAdapter a, Draft d) {
            return new WebSocketImpl( a, d );
        }

        @Override
        public WebSocketImpl createWebSocket( WebSocketAdapter a, List<Draft> d) {
            return new WebSocketImpl( a, d );
        }

        @Override
        public ByteChannel wrapChannel( SocketChannel channel, SelectionKey key ) {
            return new WsChannel(channel);
        }

        @Override
        public void close() {
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

