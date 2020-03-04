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
            else logger.error("Unknown command: "+cmd);
        }
        catch(JSONException e) {
            // TODO
        }
    }

    abstract static class Command {
        private String name;

        abstract void onExecute(final JSONObject cmd, ResponseListener l);

        protected void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void execute(final JSONObject cmd, ResponseListener l) {
            onExecute(cmd, l);
        }
    }

    abstract static class ResponseListener {
        abstract void onResponse(final JSONObject obj);

        public void sendResponse(final JSONObject obj) {
            onResponse(obj);
        }
    }

}

