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

import java.io.File;
import java.util.List;
import java.util.Vector;

public class CommandNodeFactory implements NodeFactory {

    private CommandHandler command_handler = null;

    CommandNodeFactory(CommandHandler handler) {
        command_handler = handler;
    }

    @Override
    public Node createNode() {
        Node ret = new CommandNode(command_handler);
        return ret;
    }

    public static class Builder implements NodeFactory.Builder {

        public CommandNodeFactory build(final List<String> conf) {
            return new CommandNodeFactory(CommandHandler.getInstance());
        }
    }
}

