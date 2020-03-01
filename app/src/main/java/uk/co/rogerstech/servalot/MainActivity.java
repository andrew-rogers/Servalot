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

import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private static final int GOT_CONTENT = 1;
    private static final int BT_ON = 2;
    private WebView webView = null;
    private WebViewLogger logger = null;
    private RfcommHelper rfcomm = null;

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
                        msg("Can't open file.");
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

    public void install()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/zip");
        startActivityForResult(intent, GOT_CONTENT);
    }

    public void msg(String str)
    {
        Toast.makeText(this, str, Toast.LENGTH_LONG)
             .show();
        logger.info(str);
    }

    public class WebViewInterface {

        @JavascriptInterface
        public void command(String cmd) {
            switch(cmd)
            {
                case "install":
                    install();
                    break;
                case "listBT":
                    sendToWebView(rfcomm.getDevices().toString());
                    break;
                default:
                    msg("Unkown command: "+cmd);
            }
        }

    }

    private void sendToWebView(final String str)
    {
        // evaluateJavscript can only be run on UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript("response(\"" + str.replace("\"","\\\"") + "\");", null);
            }
        });
    }

    public class WebViewLogger extends Logger {

        WebViewLogger() {
            instance = this;
        }

        public void error(final String str) {
            log("error",str);
        }

        public void info(final String str) {
            log("info",str);
        }

        public void toast(final String str) {
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG)
             .show();
        }

        private void log(final String type, final String str) {
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
    }

}

