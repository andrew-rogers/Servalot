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

import org.json.JSONException;
import org.json.JSONObject;

public class WebViewHelper{

    private Activity activity= null;
    private WebView webView = null;
    private Logger logger = null;
    private WebViewCommandResponseListener crl = null;
    private boolean wvReady = false;

    WebViewHelper(Activity activity){
        this.activity = activity;
        webView = new WebView(activity);
        activity.setContentView(webView);
        logger = new WebViewLogger();
        CommandHandler.getInstance().registerCommand(new CommandReady());
        initWebView();
    }

    public void initWebView()
    {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new WebViewInterface(), "CommandHandler");
        webView.loadUrl("file:///android_asset/index.html");
        crl = new WebViewCommandResponseListener();
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
                webView.evaluateJavascript("CommandHandler.response(\"" + str.replace("\"","\\\"") + "\");", null);
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
            logger.toast("WebView ready. "+args.getString("msg"));
        }
    }

    public class WebViewCommandResponseListener extends CommandHandler.ResponseListener {
        public void onResponse(final JSONObject obj) {
            sendToWebView(obj.toString());
        }
    }

    public class WebViewInterface {

        @JavascriptInterface
        public void command(String cmd) {
            if( cmd.charAt(0) == '{' ) {
                try {
                    JSONObject obj = new JSONObject(cmd);
                    CommandHandler.getInstance().command(obj, crl);
                }
                catch(JSONException e) {
		            // TODO
                }
            } else {
                logger.error("Unkown command: "+cmd);
            }
        }

    }

    public class WebViewLogger extends Logger {

        WebViewLogger() {
            instance = this;
        }

        public void error(final String str) {
            log("E",str);
        }

        public void info(final String str) {
            log("I",str);
        }

        public void toast(final String str) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), str, Toast.LENGTH_LONG)
                         .show();
                }
            });
        }

        private void log(final String type, final String str) {
            if(ready()) {
                JSONObject obj=new JSONObject();
                try {
                    obj.put("cmd","log");
                    obj.put("type",type);
                    obj.put("arg",str);
                    sendToWebView(obj.toString());
                }
                catch(JSONException e) {
		            // TODO
                }
            }
            else {
                toast(type+": "+str);
            }
        }

    }

}

