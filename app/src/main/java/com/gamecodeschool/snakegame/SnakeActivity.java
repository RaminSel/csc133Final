package com.gamecodeschool.snakegame;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

public class SnakeActivity extends Activity {
    
    SnakeGame mSnakeGame;

    Button btnPause;

    // Set the game up
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);




        btnPause = findViewById(R.id.btnPause);

        // be able to pause and resume the game
        btnPause.setOnClickListener(v -> {
            mSnakeGame.togForPause();
            btnPause.setText(mSnakeGame.ismPaused() ? "Resume" : "Pause");
        });

        initializeGame();
    }

    private void initializeGame() {
        // Get the pixel dimensions of the screen
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        mSnakeGame = new SnakeGame(this, size);

        FrameLayout gameContainer = findViewById(R.id.gameContainer);
        gameContainer.addView(mSnakeGame);
    }


    // Start the thread in snakeEngine
    @Override
    protected void onResume() {
        super.onResume();
        mSnakeGame.resume();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        mSnakeGame.pause();
    }
}
