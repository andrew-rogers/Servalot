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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.Toast;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RfcommHelper{

    private Activity activity;
    private BluetoothAdapter bluetoothAdapter = null;

    RfcommHelper(Activity activity){
        this.activity = activity;
    }

    public void enableBluetooth(final int requestCode) {
        // Turn on the Bluetooth if not enabled.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if ( bluetoothAdapter==null ) {
            Toast.makeText(activity.getApplicationContext(), "No bluetooth adapter.", Toast.LENGTH_LONG).show();
        } else if ( !bluetoothAdapter.isEnabled() ) {
            Intent bt_on = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(bt_on, requestCode);
        }
    }

    public JSONArray getDevices () {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        JSONArray json = new JSONArray();

        if ( devices.size() > 0 ) {
            for ( BluetoothDevice dev : devices ) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("name",dev.getName().toString());
                    obj.put("address",dev.getAddress().toString());
                    json.put(obj);
                } catch(JSONException e) {
		            // TODO
                }
            }
        } else {
            Toast.makeText(activity.getApplicationContext(), "No paired devices.", Toast.LENGTH_LONG).show();
        }

        return json;
    }

}

