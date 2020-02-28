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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private static final int GOT_CONTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WebView webView = new WebView(this);
        setContentView(webView);

        initWebView(webView);

        packageManager = new PackageManager(getFilesDir());

        Intent i= new Intent(getBaseContext(), BackgroundService.class);
        startService(i);

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
                        Toast.makeText(this, "Can't open file.", Toast.LENGTH_LONG).show();
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

        String html = "<html><body>\n"
                    + "<input type=\"button\" value=\"Install package...\" onClick=\"command('install')\" />\n"
                    + "<input type=\"button\" value=\"Thing\" onClick=\"command('thing')\" />\n"
                    + "<script type=\"text/javascript\">\n"
                    + "function command(cmd) {\n"
                    + "    CommandHandler.command(cmd);\n"
                    + "}\n"
                    + "</script>\n"
                    + "</body></html>\n";

        wv.loadData(html, "text/html", null);
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
        Toast.makeText(this, str, Toast.LENGTH_SHORT)
             .show();
    }

    public class WebViewInterface {

        @JavascriptInterface
        public void command(String cmd) {
            switch(cmd)
            {
                case "install":
                    install();
                    break;
                default:
                    msg("Unkown command: "+cmd);
            }
        }
    }


}
