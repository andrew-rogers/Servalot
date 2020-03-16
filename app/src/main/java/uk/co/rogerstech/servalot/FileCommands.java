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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.java_websocket.util.Base64;

public class FileCommands {

    private HashMap<String, NodeFactory.Builder> builders;
    private HashMap<String, Vector<String> > servers;
    private Logger logger = null;
    private File root_dir;

    FileCommands(File root_dir){
        this.logger = Logger.getInstance();
        this.root_dir=root_dir;
        CommandHandler.getInstance().registerCommand(new CommandExec());
    }

    // Returns base64 encoded stdout as a String.
    public String exec(List<String> cmd, String b64_stdin) {
        StringBuffer output = new StringBuffer();
        try {
            // Execute the command.
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Map<String, String> env = processBuilder.environment();
            String path=env.get("PATH");
            path=root_dir.getPath()+"/bin:"+path;
            env.put("PATH", path);
            processBuilder.directory(root_dir);
            Process process = processBuilder.start();

            // Write the stdin.
            if( b64_stdin != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(b64_stdin.getBytes());
                Base64.OutputStream b64os = new Base64.OutputStream(process.getOutputStream(), Base64.NO_OPTIONS);

                int nread;
                byte[] buffer = new byte[4096];

                while ((nread = bis.read(buffer)) > 0) {
                    b64os.write(buffer, 0, nread);
                }
                b64os.close();
            }

            // Read stdout.
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int nread;
            char[] buffer = new char[4096];

            while ((nread = reader.read(buffer)) > 0) {
                output.append(buffer, 0, nread);
            }
            reader.close();

            // Wait for process to terminate.
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Base64.encodeBytes(output.toString().getBytes());
    }

    public class CommandExec extends CommandHandler.Command {

        CommandExec() {
            setName("exec");
        }

        public void onExecute(CommandHandler.CommandArgs args) {
            String output = exec(args.getStringList("args"), args.getString("stdin"));
            args.put("stdout", output);
            args.respond();
        }
    }
}

