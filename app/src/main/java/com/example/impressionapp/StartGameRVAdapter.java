package com.example.impressionapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class StartGameRVAdapter extends RecyclerView.Adapter<StartGameRVAdapter.ViewHolder> {

    Context c;
    List<PlayerClass> players;

    public StartGameRVAdapter(Context c, List<PlayerClass> players) {
        this.c = c;
        this.players = players;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_div,null);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StartGameRVAdapter.ViewHolder holder, int position) {
        final PlayerClass player = players.get(position);
        Glide.with(c).asBitmap().load(player.getIconURL()).into(holder.userIconImage);
        holder.userName.setText(player.getUserName());
    }


    @Override
    public int getItemCount() {
        // this is done as players is initialized as an empty list and we are waiting for the firebase call to return the players in the gameRoom
        if (players != null) {
            return players.size();
        } else return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userIconImage;
        TextView userName;
        ViewHolder(View itemView) {
            super(itemView);
            userIconImage = itemView.findViewById(R.id.userIconImg);
            userName = itemView.findViewById(R.id.userNameTxt);
        }
    }
}
