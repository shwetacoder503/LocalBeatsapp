package com.example.localbeats;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    Context context;
    ArrayList<AudioModel> audioList;

    public AudioAdapter(Context context, ArrayList<AudioModel> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioModel currentAudio = audioList.get(position);
        holder.songTitle.setText(currentAudio.getTitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("songPath", currentAudio.getPath());
            intent.putExtra("songTitle", currentAudio.getTitle());
            intent.putExtra("audioList", audioList); // pass full list
            intent.putExtra("songIndex", position);  // pass clicked position
            context.startActivity(intent);


            Log.d("AUDIO_ADAPTER", "Sending songPath: " + currentAudio.getPath());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.tvSongTitle); // Make sure this ID exists in item_audio.xml
        }
    }
}
