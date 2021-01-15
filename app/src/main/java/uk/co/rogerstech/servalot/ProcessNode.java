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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class ProcessNode implements Node {

    private Process process = null;
    private JSONObject description = null;
    private StreamConnection connection = null;

    ProcessNode(File dir_files, List<String> cmd) {
        try {
            // Start the service process
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Map<String, String> env = processBuilder.environment();
            String path=env.get("PATH");
            path=dir_files.getPath()+"/bin:"+path;
            env.put("PATH", path);
            processBuilder.directory(dir_files);
            process = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getDescription() { return description; }
    public InputStream getInputStream() { return process.getInputStream(); }
    public OutputStream getOutputStream() { return process.getOutputStream(); }
    public StreamConnection getConnection() { return connection; }
    public void setConnection(StreamConnection c) { connection = c; }
    public void close() {
        try {
            process.getOutputStream().close();
            process.getInputStream().close();
        } catch (IOException e) {
            // TODO
        }
    }
}

