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

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommandHandler {

    private static CommandHandler instance = null;
    private HashMap<String, Command> commands = null;
    private Logger logger = null;

    CommandHandler() {
        commands = new HashMap<String, Command>();
        logger = Logger.getInstance();
    }

    static CommandHandler getInstance() {
        if (instance==null) instance = new CommandHandler();
        return instance;
    }

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public void command(final JSONObject obj, ResponseListener l) {
        try {
            String str_cmd = obj.getString("cmd");
            Command cmd = commands.get(str_cmd);
            if (cmd!=null) cmd.execute(obj, l);
            else logger.error("Unknown command: "+str_cmd);
        }
        catch(JSONException e) {
            // TODO
        }
    }

    public void command(final JSONObject obj, Node node) {
        try {
            String str_cmd = obj.getString("cmd");
            Command cmd = commands.get(str_cmd);
            if (cmd!=null) cmd.execute(obj, node);
            else logger.error("Unknown command: "+str_cmd);
        }
        catch(JSONException e) {
            // TODO
        }
    }

    static class CommandArgs {
        private JSONObject obj_cmd = null;
        private JSONObject obj_response = null;
        private ResponseListener response_listener = null;
        private Node response_node = null;

        CommandArgs(final JSONObject cmd, Node node) {
            obj_cmd = cmd;
            response_node = node;
            obj_response=new JSONObject();
        }

        CommandArgs(final JSONObject cmd, ResponseListener l) {
            obj_cmd = cmd;
            response_listener = l;
            obj_response=new JSONObject();
            try {
                obj_response.put("cb_num",cmd.getString("cb_num"));
            }
            catch(JSONException e) {
                // TODO
            }
        }

        public String getString(final String key) {
            String ret = null;
            try {
                ret = obj_cmd.getString(key);
            }
            catch(JSONException e) {
                ret = null;
            }
            return ret;
        }

        public List<String> getStringList(final String key) {
            Vector<String> ret = new Vector<String>();
            try {
                JSONArray arr = obj_cmd.getJSONArray(key);
                for( int i=0; i<arr.length(); i++) {
                    ret.add(arr.getString(i));
                }
            }
            catch(JSONException e) {
                ret = null;
            }
            return ret;
        }

        public boolean put(final String key, final JSONArray value) {
            boolean ret = true;
            try {
                obj_response.put(key,value);
            }
            catch(JSONException e) {
                ret = false;
            }
            return ret;
        }

        public boolean put(final String key, final JSONObject value) {
            boolean ret = true;
            try {
                obj_response.put(key,value);
            }
            catch(JSONException e) {
                ret = false;
            }
            return ret;
        }

        public boolean put(final String key, final String value) {
            boolean ret = true;
            try {
                obj_response.put(key,value);
            }
            catch(JSONException e) {
                ret = false;
            }
            return ret;
        }

        public void respond() {
            if( response_listener != null ) response_listener.sendResponse(obj_response);
            if( response_node != null ) response_node.send(obj_response);
        }
    }

    abstract static class Command {
        private String name;

        abstract void onExecute(CommandHandler.CommandArgs args);

        protected void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void execute(final JSONObject cmd, Node node) {
            onExecute(new CommandHandler.CommandArgs(cmd, node));
        }

        public void execute(final JSONObject cmd, ResponseListener l) {
            onExecute(new CommandHandler.CommandArgs(cmd, l));
        }
    }

    abstract static class ResponseListener {
        abstract void onResponse(final JSONObject obj);

        public void sendResponse(final JSONObject obj) {
            onResponse(obj);
        }
    }

}

