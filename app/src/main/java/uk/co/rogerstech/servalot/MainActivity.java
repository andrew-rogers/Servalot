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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements ServiceRecyclerViewAdapter.ItemListener{

    private ServiceRecyclerViewAdapter rvaServices;
    private ServiceManager serviceManager;
    private PackageManager packageManager;
    private static final int GOT_CONTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button buttonInstall = findViewById(R.id.buttonInstall);
        final EditText etName = findViewById(R.id.editName);
        Log.i("MainActivity","onCreate()");

        // Start service manager
        serviceManager = new ServiceManager(new File(getFilesDir(),"services.tsv"));
        serviceManager.startAll();

        // Services view
        RecyclerView rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvaServices = new ServiceRecyclerViewAdapter(serviceManager);
        rvaServices.setListener(this);
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

        etName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                  // Perform action on key press
                  editService(etName.getText().toString());
                  return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(Service service) {
        Toast.makeText(this, "Service: " + service.getServiceName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEdit(Service service) {
        Toast.makeText(this, "Service ed: " + service.getServiceName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDelete(Service service) {
        Toast.makeText(this, "Service delete: " + service.getServiceName(), Toast.LENGTH_SHORT).show();
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
    public void editService(String name)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.configure_service, null));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // TODO: save the settings
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing.
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

}
