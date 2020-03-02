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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class ServiceManager {

    private HashMap<String, NodeFactory.Builder> builders;
    private HashMap<String, Vector<String> > servers;
    private Logger logger = null;
    private File root_dir;
    private File file;
    private File serviceDir;

    ServiceManager(File root_dir, File file){
        this.logger = Logger.getInstance();
        this.root_dir=root_dir;
        this.file=file;
        CommandHandler.getInstance().registerServiceManager(this);
        serviceDir=new File(file.getParentFile(),"services");
        builders = new HashMap<String, NodeFactory.Builder>();
        servers = new HashMap<String, Vector<String> >();
    }

    void load(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
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

    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (Map.Entry entry : servers.entrySet()) {
                String key = (String)entry.getKey();
                Vector<String> vec = (Vector<String>)entry.getValue();
                String line="";
                for (String str : vec) line+=str+'\t';
                writer.write(line.substring(0, line.length() - 1));
                writer.newLine();
            }

            writer.close();
        } catch (FileNotFoundException e) {
            // Don't need to do anything if not found
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    void createServiceFromTSV(String tsv){
        StringTokenizer tokenizer = new StringTokenizer(tsv,"\t");
        Vector<String> vec = new Vector<String>() ;

        while(tokenizer.hasMoreElements()){
            vec.add(tokenizer.nextElement().toString());
        }

        createTcpServer(vec);
    }

    void createTcpServer(final Vector<String> vec) {
        if(vec.size()>=4) {
            String name = vec.get(0);
            String type = vec.get(1);
            List<String> serviceArgs = new ArrayList<String>();
            serviceArgs = vec.subList(1, vec.size()-2);
            String bind = vec.get(vec.size()-2);
            String port = vec.get(vec.size()-1);

            NodeFactory.Builder builder = builders.get(type);
            if(builder!=null) {
                NodeFactory factory = builder.build(serviceArgs);
                TcpServer tcp = new TcpServer(factory, bind, Integer.parseInt(port));
                servers.put(port, vec);
                tcp.start();
            }
        }
    }

    public void registerNodeFactoryBuilder(final String name, NodeFactory.Builder builder) {
        builders.put(name, builder);
    }
}
