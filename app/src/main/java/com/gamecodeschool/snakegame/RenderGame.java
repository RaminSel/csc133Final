package com.gamecodeschool.snakegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Typeface;

public class RenderGame {
    private Canvas canvas;
    private Paint paint;
    private Paint textPaint;

    // Constants for readability and easier maintenance
    private static final int SCORE_TEXT_SIZE = 120;
    private static final int SCORE_X_POSITION = 20;
    private static final int SCORE_Y_POSITION = 120;

    public RenderGame(Canvas canvas, Paint paint) {
        this.canvas = canvas;
        this.paint = paint;
        this.textPaint = new Paint(paint);
        textPaint.setTypeface(Typeface.create(textPaint.getTypeface(), Typeface.ITALIC));

    }

    public void drawBackground(int color) {
        canvas.drawColor(color);
    }

    public void drawScore(int score) {
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(SCORE_TEXT_SIZE);
        canvas.drawText("Score: " + score, SCORE_X_POSITION, SCORE_Y_POSITION, textPaint);
    }

    public void drawGameObjects(List<GameObject> gameObjects) {
        List<GameObject> copy = new ArrayList<>(gameObjects); // Create a copy of the list
        for(GameObject gameObject : copy) {
            gameObject.draw(canvas, paint);
        }
    }

    public void drawCustomText(String text, int x, int y, int color, int textSize, Paint.Align align) {
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(align);
        canvas.drawText(text, x, y, textPaint);
    }
}
