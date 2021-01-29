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
import java.util.HashMap;

public class NodeList {

	private int next=0;
	private final int max=1000;
	private ArrayDeque<Integer> available;
	private HashMap<Integer, Node> map;

	// Private constructor prevents instantiation.
	private NodeList(){
		map = new HashMap<Integer, Node>();
		available = new ArrayDeque<Integer>(); 
	}

	// Using the Bill Pugh Singleton pattern.
	private static class BillPughInner {
		private static NodeList instance = new NodeList();
	}

	public static NodeList getInstance() {
		return BillPughInner.instance;
	}

	public int registerNode(Node node) {
		int id=-1;
		do{
			if( available.size()>0 ) {
				id = available.poll();
			} else {
				id = next;
				next ++;
			}
		}while( id<max && map.containsKey(id) );
		
		if( id<max ) map.put(id, node);
		else id = -1;
		return id;
	}

	public void remove(int id) {
		if( map.containsKey(id) ) {
			map.remove(id);
			available.add(id);
		}
	}
}

