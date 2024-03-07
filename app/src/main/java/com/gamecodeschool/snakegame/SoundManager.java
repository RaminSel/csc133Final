package com.gamecodeschool.snakegame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import java.io.IOException;
import android.media.AudioManager;

public class SoundManager {
    private SoundPool soundPool;
    private int eatSoundId = -1;
    private int crashSoundId = -1;

    public SoundManager(Context context) {
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
            // Handle error
        }
    }

    public void playEatSound() {
        soundPool.play(eatSoundId, 1, 1, 0, 0, 1);
    }

    public void playCrashSound() {
        soundPool.play(crashSoundId, 1, 1, 0, 0, 1);
    }
}
