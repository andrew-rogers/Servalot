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
import java.util.List;
import java.util.Vector;

public class ProcessNodeFactory implements NodeFactory {

    private static File dir_files;
    private static File dir_services;
    private List<String> cmd;
    private Logger logger = null;

    ProcessNodeFactory(List<String> list_cmd) {
        Vector<String> cmd = new Vector<String>();
        String type = list_cmd.get(0);
        switch(type)
        {
            case "sh":
                cmd.add("sh"); // Use sh in PATH

                // Get full path of script
                File script = new File(dir_services, list_cmd.get(1));
                cmd.add(script.getPath());
                break;
            case "bin":
                // TODO: support binary.
                logger.error("Unsupported process type: "+type);
                break;
            default:
                logger.error("Unkown process type: "+type);
        }
        this.cmd = cmd;
        logger = Logger.getInstance();
    }

    @Override
    public Node createNode() {
        // TODO: Support option of multiplexing to one process
        Node ret = new ProcessNode(dir_files, cmd);
        return ret;
    }

    public static class Builder implements NodeFactory.Builder {

        Builder(File dir_files, File dir_services){
            ProcessNodeFactory.dir_files=dir_files;
            ProcessNodeFactory.dir_services=dir_services;
        }

        public ProcessNodeFactory build(final List<String> conf) {
            return new ProcessNodeFactory(conf);
        }
    }
}

