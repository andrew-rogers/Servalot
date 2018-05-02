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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ServiceRecyclerViewAdapter.ItemClickListener{

    private ServiceRecyclerViewAdapter rvaServices;
    private String[] serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity","onCreate()");

        // Start a demo service for now
        Service simpleHttpd = new Service("sh /sdcard/Download/httpd.sh",8081);
        simpleHttpd.start();

        serviceList = new String[4];
        serviceList[0]="httpd";
        serviceList[1]="telnetd";
        serviceList[2]="tftpd";
        serviceList[3]="bootp";

        // Services view
        RecyclerView rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvaServices = new ServiceRecyclerViewAdapter(serviceList);
        rvaServices.setClickListener(this);
        rvServices.setAdapter(rvaServices);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "Service: " + serviceList[position], Toast.LENGTH_SHORT).show();
    }
}
