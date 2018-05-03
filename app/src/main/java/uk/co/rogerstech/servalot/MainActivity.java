/*
    Servalot - An inetd like multi-server
    Copyright (C) 2018  Andrew Rogers

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements ServiceRecyclerViewAdapter.ItemClickListener{

    private ServiceRecyclerViewAdapter rvaServices;
    private String[] serviceList;
    private ServiceManager serviceManager;
    private PackageManager packageManager;
    private static final int GOT_CONTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button buttonInstall = findViewById(R.id.buttonInstall);
        Log.i("MainActivity","onCreate()");

        // Start a demo service for now
        Service simpleHttpd = new Service("httpd","sh /sdcard/Download/httpd.sh", "localhost", 8081);
        simpleHttpd.start();

        serviceList = new String[4];
        serviceList[0]="httpd";
        serviceList[1]="telnetd";
        serviceList[2]="tftpd";
        serviceList[3]="bootp";

        // Start service manager
        serviceManager = new ServiceManager(new File(getFilesDir().getPath(),"services.tsv"));
        serviceManager.startAll();

        // Services view
        RecyclerView rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvaServices = new ServiceRecyclerViewAdapter(serviceManager);
        rvaServices.setClickListener(this);
        rvServices.setAdapter(rvaServices);

        packageManager = new PackageManager(getFilesDir());

        buttonInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/zip");
                startActivityForResult(intent, GOT_CONTENT);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Service: " + serviceList[position], Toast.LENGTH_SHORT).show();
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


}
