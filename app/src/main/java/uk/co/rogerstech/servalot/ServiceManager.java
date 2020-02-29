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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class ServiceManager {

    private Vector<Service> vecServices;
    private Vector<TcpServer> vecTcpServers;
    private File root_dir;
    private File file;
    private File serviceDir;

    ServiceManager(File root_dir, File file){
        this.root_dir=root_dir;
        this.file=file;
        serviceDir=new File(file.getParentFile(),"services");
        vecServices = new Vector<Service>();
        vecTcpServers = new Vector<TcpServer>();
        load();
        exampleHC05("20:16:07:04:79:81",8085);
    }

    void load(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            deleteAll();
            while ((line = reader.readLine()) != null){
                createServiceFromTSV(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            // Don't need to do anything if not found
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void deleteAll(){
        for (int i = 0; i < vecServices.size(); i++) {
            vecServices.get(i).cleanUp();
        }
        vecServices.clear();
    }

    void createServiceFromTSV(String tsv){
        StringTokenizer tokenizer = new StringTokenizer(tsv,"\t");
        Vector<String> vec = new Vector<String>() ;
        while(tokenizer.hasMoreElements()){
            vec.add(tokenizer.nextElement().toString());
        }
        if(vec.size()>=5){
            String type = vec.get(1);
            if(type.equals("sh")) {
                File exec = new File(serviceDir, vec.get(2));
                Vector<String> cmd = new Vector<String>();
                cmd.add("sh");
                cmd.add(exec.getPath());
                Service service = new Service(vec.get(0), root_dir, cmd, vec.get(3), Integer.parseInt(vec.get(4)));
                vecServices.add(service);
            }
            // TODO: Support HC-05
        }
    }

    void exampleHC05(final String bt_address, final int port) {
        RfcommNodeFactory factory = new RfcommNodeFactory(bt_address);
        TcpServer tcp = new TcpServer(factory, "0.0.0.0", port);
        vecTcpServers.add(tcp);
    }

    void startAll(){
        for (int i = 0; i < vecServices.size(); i++) {
            vecServices.get(i).start();
        }
        for (int i = 0; i < vecTcpServers.size(); i++) {
            vecTcpServers.get(i).start();
        }
    }

    Service get(int position){
        return vecServices.get(position);
    }

    int size(){
        return vecServices.size();
    }
}
