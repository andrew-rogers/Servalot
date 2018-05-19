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

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ServiceRecyclerViewAdapter extends RecyclerView.Adapter<ServiceRecyclerViewAdapter.ViewHolder>{

    private ItemListener listener;
    private ServiceManager services;

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        TextView tvName;
        TextView tvExecutable;
        TextView tvPort;

        Service service = null;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvExecutable = itemView.findViewById(R.id.tvExecutable);
            tvPort = itemView.findViewById(R.id.tvPort);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        void setService(Service service) {
            this.service = service;
            tvName.setText(service.getServiceName());
            List<String> list_cmd = service.getCommand();
            String cmd="";
            if(list_cmd.size() > 0) {
                cmd=list_cmd.get(list_cmd.size()-1);
                File file=new File(cmd);
                cmd=file.getName();
            }
            tvExecutable.setText(cmd);
            String port=""+ service.getPort();
            tvPort.setText(port);
        }

        @Override
        public void onClick(View view) {
            if(listener != null) listener.onClick(service);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(service.getServiceName());
            MenuItem miEdit = menu.add(0, view.getId(), 0, "Edit");
            MenuItem miDelete = menu.add(0, view.getId(), 1, "Delete");
            miEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(listener != null) listener.onEdit(service);
                    return true;
                }
            });
            miDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(listener != null) listener.onDelete(service);
                    return true;
                }
            });
        }
    }

    // Client code implements this to receive service UI events
    public interface ItemListener {
        void onClick(Service service);
        void onEdit(Service service);
        void onDelete(Service service);
    }

    // Constructor
    public ServiceRecyclerViewAdapter(ServiceManager services) {
        this.services = services;
    }

    // Register the listener
    void setListener(ItemListener l) {
        this.listener = l;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.service, parent, false);
        return new ViewHolder(view);
    }


    // Configure elements of view with service details
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Service service = services.get(position);
        holder.setService(service);
    }

    // Return the number of services
    @Override
    public int getItemCount() {
        return services.size();
    }


}
