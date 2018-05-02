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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServiceRecyclerViewAdapter extends RecyclerView.Adapter<ServiceRecyclerViewAdapter.ViewHolder>{

    private ItemClickListener clickListener;
    private String[] serviceList;

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // Client code implements this to receive item clicks
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Constructor
    public ServiceRecyclerViewAdapter(String[] serviceList) {
        this.serviceList = serviceList;
    }

    // Register the listener
    void setClickListener(ItemClickListener l) {
        this.clickListener = l;
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
        holder.tvName.setText(serviceList[position]);
    }

    // Return the number of services
    @Override
    public int getItemCount() {
        return serviceList.length;
    }


}
