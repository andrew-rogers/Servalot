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

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class WebViewHelper{

    private Activity activity= null;
    private WebView webView = null;
    private Logger logger = null;
    private boolean wvReady = false;
    private HashMap<Integer, WebViewServer> servers;

    WebViewHelper( NodeFactory pf, int port, Activity activity ){
        this.activity = activity;
        webView = new WebView(activity);
        activity.setContentView(webView);
        logger = Logger.getInstance();
        CommandHandler.getInstance().registerCommand(new CommandReady());
        servers = new HashMap<Integer, WebViewServer>();
        servers.put( port, new WebViewServer( pf, port ) );
        initWebView();
    }

    public void initWebView()
    {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new WebViewInterface(), "wvi");
        String index_html = "www/index.html";
        File file_index_html = new File(activity.getFilesDir(),index_html);
        if (file_index_html.exists()) {
            webView.loadUrl("file://" + file_index_html);
        }
        else {
            webView.loadUrl("file:///android_asset/" + index_html);
        }
    }

    public WebView getWebView() {
        return webView;
    }

    public void sendToWebView(final String str)
    {
        // evaluateJavscript can only be run on UI thread.
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("wvi.response(\"" + str.replace("\\","\\\\").replace("\"","\\\"") + "\");", null);
            }
        });
    }

    public boolean ready() {
        return wvReady;
    }

    public class CommandReady extends CommandHandler.Command {

        CommandReady() {
            setName("ready");
        }

        public void onExecute(CommandHandler.CommandArgs args) {
            wvReady = true;
            logger.info("WebView ready. "+args.getString("msg"));
        }
    }

    public class WebViewInterface {

        @JavascriptInterface
        public void command(final String cmd) {
            if( wvReady ) {
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        command1(cmd);
                    }
                } ).start();
            }
            else {
                command1(cmd);
            }
        }

        private void command1(String cmd) {
            if( cmd.charAt(0) == '{' ) {
                try {
                    final JSONObject obj = new JSONObject(cmd);
                    Integer dst = new Integer(0);
                    if( obj.has("dst") ) dst = new Integer(obj.getString("dst"));

                    if( servers.containsKey(dst) ) {
                        final WebViewServer s = servers.get(dst);
                        s.onMessage(obj);
                    }
                }
                catch(JSONException e) {
		            // TODO
                }
            } else {
                logger.error("Unkown command: "+cmd);
            }
        }

    }

    public class WebViewServer {
        private int port = 0;
        private HashMap<Integer, WebViewNode> nodes = null;
        private NodeFactory peer_factory = null;

        public WebViewServer( NodeFactory pf, int p ) {
            peer_factory = pf;
            port = p;
            nodes = new HashMap<Integer, WebViewNode>();
        }

        public void onOpen(Integer src) {

            // If it is already open then close it
            if( nodes.containsKey(src) ) {
                nodes.get(src).close();
                nodes.remove(src);
            }

            // Create the local WebView node
            WebViewNode local = new WebViewNode( this, src );
            nodes.put(src, local);

            // Create the peer node
            Node peer = peer_factory.createNode();

            // Tie them together
            peer.setPeer(local);
            local.setPeer(peer);
        }

        public void onMessage(JSONObject obj) {
            try {
                Integer src = new Integer(0);
                Logger.getInstance().info("Msg: "+obj.toString());
                if( obj.has("src") ) {
                    src = new Integer(obj.getString("src"));
                }
                if( nodes.containsKey(src) == false ) onOpen(src);

                // Look-up node and call its message handler.
                if( nodes.containsKey(src) ) {
                    nodes.get(src).onMessage(obj.getJSONObject("data"));
                }
            }
            catch(JSONException e) {
		        // TODO
            }
        }

        public void onClose(Integer src) {
            JSONObject tcp = new JSONObject();
            try {
                tcp.put( "tcf", "f" );
                tcp.put( "src", ""+port );
                tcp.put( "dst", src.toString() );
            }
            catch(JSONException e) {
		        // TODO
            }
            if( nodes.containsKey(src) ) {
                WebViewNode node = nodes.get(src);
                sendToWebView(tcp.toString());
	            node.close();
	            nodes.remove(src);
	        }
	    }

	    public void send(WebViewNode node, JSONObject obj) {
	        Integer src = node.getPort();
	        try {

	            // Create tansport control packet
	            JSONObject tcp = new JSONObject();
	            tcp.put( "src", ""+port);
	            tcp.put( "dst", src.toString() );
	            tcp.put( "data", obj);
	            sendToWebView(tcp.toString());
	        }
	        catch(JSONException e) {
		        // TODO
            }
	    }
    }
}

