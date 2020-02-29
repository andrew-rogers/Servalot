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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

public class RfcommNodeFactory implements NodeFactory {

    private String address = null;
    private Node node = null;
    private BluetoothSocket btSocket = null;
    private Logger logger = null;

    RfcommNodeFactory(final String address) {
        this.address = address;
        logger = Logger.getInstance();

        new ConnectRfcommTask().execute();
    }

    public Node createNode() {

        // Only allow one node to be created
        Node ret = null;
        if(node == null) {
            node = new RfcommNode(btSocket);
            ret = node;
        }
        return ret;
    }

    public void msg(String str)
    {
        logger.info(str);
    }

    private class ConnectRfcommTask extends AsyncTask<Void, Void, Void> {
        private boolean failed = false;
        private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        @Override
        protected  void onPreExecute () {
            msg("Connecting to "+address+"...");
        }

        @Override
        protected Void doInBackground (Void... voids) {
            try {
                if ( btSocket==null ) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
                    bluetoothAdapter.cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                failed = true;
                btSocket = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (failed) {
                msg("Failed to connect");
            } else {
                msg("Connected");
            }
        }
    }
}

