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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class PackageManager {

    private File root;

    PackageManager(File root){
        this.root = root;
    }

    public String install(InputStream in){
        String ret="";
        unzip(in, root);
        File postinst=new File(root.getPath(),"POSTINST.sh");
        if(postinst.exists()) {
            Vector<String> cmd = new Vector<String>();
            cmd.add("sh");
            cmd.add(postinst.getPath());
            ret = runCommand(cmd);
        }
        return ret;
    }

    public static int unzip(InputStream in, File dstDir)
    {
        BufferedInputStream bis_zip = new BufferedInputStream(in);
        ZipInputStream is_zip = new ZipInputStream(bis_zip);
        try{
            ZipEntry entry;
            byte[] buffer = new byte[32*1024];
            while ((entry = is_zip.getNextEntry()) != null) {

                // Get destination path.
                File file = new File(dstDir, entry.getName());

                // Get path of destination directory.
                File dir = entry.isDirectory() ? file : file.getParentFile();

                // If destination directory does not exist then create it.
                if (!dir.isDirectory()) {
                    // Try to create directory.
                    if (!dir.mkdirs()) {
                        //TODO: handle this
                        //log("Failed to make directory: " + dir.getAbsolutePath());
                    }
                }

                // If entry is a file then write to destination file
                if (!entry.isDirectory()) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file);

                        int nread;
                        while ((nread = is_zip.read(buffer)) != -1) {
                            fos.write(buffer, 0, nread);
                        }
                        fos.close();
                    } catch(FileNotFoundException e) {
                        e.printStackTrace();
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

            is_zip.close();
        }
        catch(IOException ex){
            return -1;
        }
        return 0;
    }

    public String runCommand(List<String> cmd) {
        StringBuffer output = new StringBuffer();
        try {
            // Execute the command.
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Map<String, String> env = processBuilder.environment();
            String path=env.get("PATH");
            path=root.getPath()+"/bin:"+path;
            env.put("PATH", path);
            processBuilder.directory(root);
            Process process = processBuilder.start();

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
        return output.toString();
    }
}
