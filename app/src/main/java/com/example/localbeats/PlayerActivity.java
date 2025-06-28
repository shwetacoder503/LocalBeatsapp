package com.example.localbeats;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    private TextView titleTextView, currentTimeTextView, durationTextView;
    private SeekBar seekBar;
    private ImageButton playPauseButton,prevButton,nextButton,backButton;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private boolean isPlaying = true;
    private int currentSongIndex;
    private ArrayList<AudioModel> allSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        titleTextView = findViewById(R.id.titleTextView);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        durationTextView = findViewById(R.id.durationTextView);
        seekBar = findViewById(R.id.seekBar);
        playPauseButton = findViewById(R.id.playPauseButton);
        nextButton = findViewById(R.id.nextButton);
        prevButton = findViewById(R.id.prevButton);
        backButton=findViewById(R.id.backButton);

        backButton.setOnClickListener(view -> {
            Intent intent =new Intent(PlayerActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        // Get data from Intent
        String songPath = getIntent().getStringExtra("songPath");
        String songTitle = getIntent().getStringExtra("songTitle");

        allSongs = (ArrayList<AudioModel>) getIntent().getSerializableExtra("audioList");
        currentSongIndex = getIntent().getIntExtra("songIndex", 0);


        Log.d("PLAYER_ACTIVITY", "Received songPath: " + songPath);
        Log.d("PLAYER_ACTIVITY", "Received songTitle: " + songTitle);

        if (songPath == null || songPath.isEmpty()) {
            Toast.makeText(this, "Error: Song path is missing", Toast.LENGTH_LONG).show();
            finish(); // close the activity safely
            return;
        }

        titleTextView.setText(songTitle != null ? songTitle : "Unknown Title");

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(songPath));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to play song", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        seekBar.setMax(mediaPlayer.getDuration());
        durationTextView.setText(formatTime(mediaPlayer.getDuration()));

        playPauseButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentSongIndex < allSongs.size() - 1) {
                currentSongIndex++;
                playSong(allSongs.get(currentSongIndex));
            }
        });

        prevButton.setOnClickListener(v -> {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                playSong(allSongs.get(currentSongIndex));
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateSeekBar();
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        currentTimeTextView.setText(formatTime(mediaPlayer.getCurrentPosition()));
        if (mediaPlayer.isPlaying()) {
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private String formatTime(int milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
    private void playSong(AudioModel song) {
        titleTextView.setText(song.getTitle());

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(this, Uri.parse(song.getPath()));
                mediaPlayer.prepare();
                mediaPlayer.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                seekBar.setMax(mediaPlayer.getDuration());
                durationTextView.setText(formatTime(mediaPlayer.getDuration()));
                updateSeekBar();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Can't play song", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
