package com.gamecodeschool.snakegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;

import java.util.Iterator;
import java.util.List;

public class RenderGame {
    private Canvas canvas;
    private Paint paint;
    private Paint textPaint;
    private List<GameObject> gameObjects;

    // Constants for readability and easier maintenance
    private static final int SCORE_TEXT_SIZE = 120;
    private static final int SCORE_X_POSITION = 50;
    private static final int SCORE_Y_POSITION = 120;
    private SnakeGame mSnake;

    public RenderGame(Canvas canvas, Paint paint, List<GameObject> gameObjects) {
        this.canvas = canvas;
        this.paint = paint;
        this.gameObjects = gameObjects;

        // Initialize text paint with typeface and other properties
        textPaint = new Paint();
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        textPaint.setTextSize(SCORE_TEXT_SIZE);
        textPaint.setColor(Color.RED);
    }

    public void drawScore(int score) {
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(SCORE_TEXT_SIZE);
        canvas.drawText("Score: " + score, SCORE_X_POSITION, SCORE_Y_POSITION, textPaint);
    }

    public void drawGameObjects() {
        // Draw game objects directly to the main canvas
        synchronized (gameObjects) {
            Iterator<GameObject> iterator = gameObjects.iterator();
            while (iterator.hasNext()) {
                GameObject gameObject = iterator.next();
                gameObject.draw(canvas, paint);
            }
        }
    }

    public void drawPauseScreen() {
        textPaint.setTypeface(Typeface.create(textPaint.getTypeface(), Typeface.ITALIC));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(175);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Tap to Play", canvas.getWidth() / 2, canvas.getHeight() / 2, textPaint);
    }

    public void drawPauseBtnScreen() {
        textPaint.setTypeface(Typeface.create(textPaint.getTypeface(), Typeface.ITALIC));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(175);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Paused!", canvas.getWidth() / 2, canvas.getHeight() / 2, textPaint);
    }

    public void drawGameOver(){
        textPaint.setTypeface(Typeface.create(textPaint.getTypeface(), Typeface.ITALIC));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(175);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game Over! Tap To Play Again", canvas.getWidth() / 2, canvas.getHeight() / 2, textPaint);
    }

    public void drawCustomText(String text, int x, int y, int color, int textSize, Paint.Align align) {
        Paint textPaint = new Paint();
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(align);

        // Draw custom text directly to the main canvas
        canvas.drawText(text, x, y, textPaint);
    }
}



