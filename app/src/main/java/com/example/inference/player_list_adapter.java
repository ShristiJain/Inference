package com.example.inference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;

public class player_list_adapter extends RecyclerView.Adapter<player_list_adapter.WordViewHolder> {

    private ArrayList<String> mplayers;
    private LayoutInflater mInflater;

    public player_list_adapter(Context context, ArrayList<String> players) {
        mInflater = LayoutInflater.from(context);
        this.mplayers = players;
    }

    @NonNull
    @Override
    public player_list_adapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView= mInflater.inflate(R.layout.recycler_player_list,parent,false);
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull player_list_adapter.WordViewHolder holder, int position) {
       String name= mplayers.get(position);
       holder.playerName.setText(name);
    }

    @Override
    public int getItemCount() {
        return mplayers.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {

        TextView playerName;
        player_list_adapter adap;
        ImageView image;
        WordViewHolder(@NonNull View itemView, player_list_adapter Adapter) {
            super(itemView);
            playerName= itemView.findViewById(R.id.player_name_recycler);
            this.adap= Adapter;
        }
    }
}
