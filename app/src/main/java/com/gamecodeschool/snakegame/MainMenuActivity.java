package com.gamecodeschool.snakegame;

import com.bumptech.glide.Glide;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainMenuActivity extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mainmenu);

        ImageView backgroundImage = findViewById(R.id.main_menu);

        Glide.with(this)
                .asGif()
                .load(R.drawable.mainmenu)
                .into(backgroundImage);



        Button startGameButton = findViewById(R.id.start_game_button);
        Button optionsButton = findViewById(R.id.options_button);

        startGameButton.setOnClickListener(v -> {
            // Start a new game activity or transition to the game screen
            startActivity(new Intent(MainMenuActivity.this, SnakeActivity.class));
        });

        optionsButton.setOnClickListener(v -> {
            // Transition to the options screen
            startActivity(new Intent(MainMenuActivity.this, OptionsActivity.class));
        });


    }





}
