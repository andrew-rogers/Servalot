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

import java.lang.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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
        CommandHandler.getInstance().registerCommand(new CommandHttpGet());
    }

    static String readString(File file) throws FileNotFoundException, IOException {
        String ret="";
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int nread;
        while( (nread = fis.read(buffer)) > 0) {
            ret += new String(buffer, 0, nread);
        }
        fis.close();
        return ret;
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

    public void httpGet(CommandHandler.CommandArgs args) {
        final String url = args.getString("url");
        final String filename = args.getString("filename");
        try {
            URL u = new URL(url);

            URLConnection c = u.openConnection();
            c.connect();
            int len = c.getContentLength();

            InputStream is = u.openStream();
            DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
            FileOutputStream fos = new FileOutputStream(new File(root_dir, filename));
            byte[] buf = new byte[4096];
            int cnt;
            int done=0;
            long next = System.currentTimeMillis();
            while ((cnt = dis.read(buf)) > 0) {
                fos.write(buf, 0, cnt);
                done = done + cnt;
                long ct = System.currentTimeMillis();
                if( ct >= next ) {
                    next = next + 1000;
                    args.put("done", ""+done);
                    args.put("len", ""+len);
                    args.send();
                }
            }
            fos.close();
            is.close();
            args.put("done", ""+done);
            args.put("len", ""+len);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

    public class CommandHttpGet extends CommandHandler.Command {

        CommandHttpGet() {
            setName("httpget");
        }

        public void onExecute(CommandHandler.CommandArgs args) {
            httpGet(args);
            args.respond();
        }
    }
}

