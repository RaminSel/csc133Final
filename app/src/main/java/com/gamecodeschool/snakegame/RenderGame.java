package com.gamecodeschool.snakegame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.List;

public class RenderGame {
    private Canvas canvas;
    private Paint paint;

    public RenderGame(Canvas canvas, Paint paint) {
        this.canvas = canvas;
        this.paint = paint;
    }

    public void drawBackground(int color) {
        canvas.drawColor(color);
    }

    public void drawScore(int score) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(120);
        canvas.drawText("Score: " + score, 20, 120, paint);
    }

    public void drawGameObjects(List<GameObject> gameObjects) {
        for (GameObject gameObject : gameObjects) {
            gameObject.draw(canvas, paint);
        }
    }

    public void drawCustomText(String text, int x, int y, int color, int textSize, Paint.Align align) {
        Paint textPaint = new Paint();
        textPaint.setColor(color);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(align);
        canvas.drawText(text, x, y, textPaint);
    }
}