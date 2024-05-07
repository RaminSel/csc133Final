package com.gamecodeschool.snakegame;

import static com.gamecodeschool.snakegame.R.*;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import java.io.IOException;
import android.media.AudioManager;
import android.util.Log;

public class ManageSound {
    private SoundPool soundPool;
    private int eatSoundId = -1;
    private int crashSoundId = -1;

    private MediaPlayer backgroundMusic;

    public ManageSound(Context context) {
        initializeSoundPool(context);
        loadSounds(context);
    }

    private void initializeSoundPool(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
    }

    private void loadSounds(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("get_apple.ogg");
            eatSoundId = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("snake_death.ogg");
            crashSoundId = soundPool.load(descriptor, 0);
        } catch (IOException e) {
            Log.e("ManageSound", "Error loading sound assets", e);
        }
    }



    public void initBackgroundMusic(Context context) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd("music.ogg");
            backgroundMusic = new MediaPlayer();
            backgroundMusic.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            backgroundMusic.prepare(); // Prepare the MediaPlayer asynchronously if this runs on the UI thread
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(1.0f, 1.0f);
        } catch (IOException e) {
            Log.e("ManageSound", "Could not load background music", e);
        }
    }

    public void playEatSound() {
        soundPool.play(eatSoundId, 1, 1, 0, 0, 1);
    }

    public void playCrashSound() {
        soundPool.play(crashSoundId, 1, 1, 0, 0, 1);
    }



    public void playBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause(); // Use pause() to be able to resume
            backgroundMusic.seekTo(0); // Optional: Reset music to start
        }
    }
}


