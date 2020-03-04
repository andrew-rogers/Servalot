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

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private static final int GOT_CONTENT = 1;
    private static final int BT_ON = 2;
    private WebViewHelper webViewHelper = null;
    private Logger logger = null;
    private RfcommHelper rfcomm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webViewHelper = new WebViewHelper(this);

        logger = Logger.getInstance();

        packageManager = new PackageManager(getFilesDir());

        Intent i= new Intent(getBaseContext(), BackgroundService.class);
        startService(i);

        rfcomm = new RfcommHelper(this);
        rfcomm.enableBluetooth(BT_ON);

        CommandHandler.getInstance().registerCommand(new CommandInstall());

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
            default:
                // TODO
        }
    }

    public class CommandInstall extends CommandHandler.Command {

        CommandInstall() {
            setName("install");
        }

        public void onExecute(CommandHandler.CommandArgs args) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip");
            startActivityForResult(intent, GOT_CONTENT);
        }
    }

}

