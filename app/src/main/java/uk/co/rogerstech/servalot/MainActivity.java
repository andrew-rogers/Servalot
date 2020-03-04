/*
    Servalot - An inetd like multi-server
    Copyright (C) 2018, 2020  Andrew Rogers

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

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private static final int GOT_CONTENT = 1;
    private static final int BT_ON = 2;
    private WebView webView = null;
    private WebViewLogger logger = null;
    private RfcommHelper rfcomm = null;
    private boolean wvReady = false;
    private WebViewCommandResponseListener crl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        initWebView(webView);

        logger = new WebViewLogger();

        packageManager = new PackageManager(getFilesDir());

        Intent i= new Intent(getBaseContext(), BackgroundService.class);
        startService(i);

        rfcomm = new RfcommHelper(this);
        rfcomm.enableBluetooth(BT_ON);

        initCommandHandler(CommandHandler.getInstance());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case GOT_CONTENT:
                if(resultCode==RESULT_OK){
                    Uri uri = data.getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        packageManager.install(inputStream);

                    }
                    catch(FileNotFoundException ex) {
                        logger.error("Can't open file.");
                    }


                }
            break;

        }
    }

    public void initWebView(WebView wv)
    {
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);

        wv.addJavascriptInterface(new WebViewInterface(), "CommandHandler");
        wv.loadUrl("file:///android_asset/index.html");
    }

    private void initCommandHandler(CommandHandler h) {
        h.registerCommand(new CommandInstall());
        h.registerCommand(new CommandGetBTDevs());
        h.registerCommand(new CommandReady());
        crl = new WebViewCommandResponseListener();
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

    private void sendToWebView(final String str)
    {
        // evaluateJavscript can only be run on UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("CommandHandler.response(\"" + str.replace("\"","\\\"") + "\");", null);
            }
        });
    }

    public class CommandInstall extends CommandHandler.Command {

        CommandInstall() {
            setName("install");
        }

        public void onExecute(final JSONObject cmd, CommandHandler.ResponseListener l) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip");
            startActivityForResult(intent, GOT_CONTENT);
        }
    }

    public class CommandGetBTDevs extends CommandHandler.Command {

        CommandGetBTDevs() {
            setName("get bluetooth devices");
        }

        public void onExecute(final JSONObject cmd, CommandHandler.ResponseListener l) {
            JSONObject obj=new JSONObject();
            try {
                obj.put("cb_num",cmd.getString("cb_num"));
                obj.put("response","get bluetooth devices");
                obj.put("devs",rfcomm.getDevices());
                l.sendResponse(obj);
            }
            catch(JSONException e) {
                // TODO
            }
        }
    }

    public class CommandReady extends CommandHandler.Command {

        CommandReady() {
            setName("ready");
        }

        public void onExecute(final JSONObject cmd, CommandHandler.ResponseListener l) {
            try{
                wvReady = true;
                logger.toast("WebView ready. "+cmd.getString("msg"));
            }
            catch(JSONException e) {
                // TODO
            }
        }
    }

    public class WebViewCommandResponseListener extends CommandHandler.ResponseListener {
        public void onResponse(final JSONObject obj) {
            sendToWebView(obj.toString());
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
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG)
             .show();
        }

        private void log(final String type, final String str) {
            if(wvReady) {
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

