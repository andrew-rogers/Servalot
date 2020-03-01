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

import org.json.JSONException;
import org.json.JSONObject;

public class CommandHandler {

    private static CommandHandler instance = null;
    private ServiceManager serviceManager = null;
    private Logger logger = Logger.getInstance();

    static CommandHandler getInstance() {
        if (instance==null) instance = new CommandHandler();
        return instance;
    }

    public void registerServiceManager(ServiceManager sm) {
        serviceManager = sm;
    }

    public void command(final JSONObject obj) {
        try {
            String cmd = obj.getString("cmd");
            switch(cmd)
            {
                case "add service":
                    addService(obj);
                    break;
                default:
                    logger.error("Unkown command: "+cmd);
            }
        }
        catch(JSONException e) {
            // TODO
        }
    }

    private void addService(final JSONObject obj) {
        try {
            String name = obj.getString("name");
            String type = obj.getString("type");
            String address = obj.getString("address");
            String bind = obj.getString("bind");
            String port = obj.getString("port");
            serviceManager.createServiceFromTSV(name + "\t" + type + "\t" + address + "\t" + bind + "\t" + port);
        }
        catch(JSONException e) {
            // TODO
        }
    }

}

