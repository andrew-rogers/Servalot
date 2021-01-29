/*
    Servalot - An inetd like multi-server
    Copyright (C) 2021  Andrew Rogers

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

import java.util.ArrayDeque;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Logger {

	private ArrayDeque<JSONObject> queue;
	private int max=100;

	// Private constructor prevents instantiation.
	private Logger(){
		queue = new ArrayDeque<JSONObject>(max);
		CommandHandler.getInstance().registerCommand(new CommandReadLogs());
	}

	// Using the Bill Pugh Singleton pattern.
	private static class BillPughInner {
		private static Logger instance = new Logger();
	}

	public static Logger getInstance() {
		return BillPughInner.instance;
	}

	private void put(final String type, final String msg) {
		try {
			JSONObject obj = new JSONObject();
			obj.put("type", type);
			obj.put("msg", msg);
			while( queue.size() >= max) queue.poll();
			queue.add(obj);
		}
		catch(JSONException e) {
			// TODO
		}
	}

	public void error(final String str) {
		put("E", str);
	}

	public void info(final String str) {
		put("I", str);
	}

	public class CommandReadLogs extends CommandHandler.Command {

        CommandReadLogs() {
            setName("readlogs");
        }

        public void onExecute(CommandHandler.CommandArgs args) {
			try {
				JSONObject objs[] = queue.toArray(new JSONObject[0]);
				JSONArray arr = new JSONArray(objs);
				args.put("logs", arr);
			}
			catch(JSONException e) {
				// TODO
			}
            args.respond();
        }
    }
}

