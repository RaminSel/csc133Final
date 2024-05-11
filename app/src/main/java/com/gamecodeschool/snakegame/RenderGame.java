package com.gamecodeschool.snakegame;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import android.graphics.PorterDuff;
import android.graphics.Typeface;


public class RenderGame {
    private Canvas canvas;
    private Paint paint;
    private Paint textPaint;

    // Constants for readability and easier maintenance
    private static final int SCORE_TEXT_SIZE = 120;
    private static final int SCORE_X_POSITION = 250;
    private static final int SCORE_Y_POSITION = 120;

    // Create a reusable off-screen buffer for double buffering
    private Bitmap offscreenBitmap;
    private Canvas offscreenCanvas;

    public RenderGame(Canvas canvas, Paint paint) {
        this.canvas = canvas;
        this.paint = paint;

        // Create the off-screen buffer with the same dimensions as the canvas
        offscreenBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        offscreenCanvas = new Canvas(offscreenBitmap);

        // Initialize text paint with typeface and other properties
        textPaint = new Paint();
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        textPaint.setTextSize(SCORE_TEXT_SIZE);
        textPaint.setColor(Color.RED);
    }

    public void drawScore(int score) {
        // Draw score to the off-screen buffer
        offscreenCanvas.drawText("Score: " + score, SCORE_X_POSITION, SCORE_Y_POSITION, textPaint);
    }

    public void drawGameObjects(List<GameObject> gameObjects) {
        // Clear the off-screen buffer
        offscreenCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // Draw game objects to the off-screen buffer
        for(GameObject gameObject : gameObjects) {
            gameObject.draw(offscreenCanvas, paint);
        }

        // Draw the off-screen buffer to the main canvas
        canvas.drawBitmap(offscreenBitmap, 0, 0, null);
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



