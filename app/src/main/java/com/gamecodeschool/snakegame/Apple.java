package com.gamecodeschool.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import java.util.Random;

public class Apple extends GameObject implements Updateable {

    private static final int MIN_SIZE = 5;
    private static final int SIZE_DECREMENT = 10;
    private static final int SCORE_THRESHOLD = 5;
    private Point location = new Point();
    private Point mSpawnRange;
    private int mSize;
    private Bitmap mBitmapApple;

    public Apple(Context context, Point sr, int s) {
        // Make a note of the passed in spawn range
        mSpawnRange = sr;
        mSize = s;
        location.x = -10;

        // Load the image to the bitmap
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        // Resize the bitmap to the specified size
        resizeBitmap(mSize);
    }

    // This is called every time an apple is eaten
    public void spawn() {
        Random random = new Random();
        // Choose two random values within the spawn range and place the apple
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    // Overloaded method to spawn the apple based on the current score
    public void spawn(int score) {
        spawn();
        if (score > SCORE_THRESHOLD) {
            int newSize = mSize - SIZE_DECREMENT;
            mSize = Math.max(newSize, MIN_SIZE);
            resizeBitmap(mSize);
        }
    }

    // Method to resize the apple's bitmap
    private void resizeBitmap(int size) {
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, size, size, false);
    }

    public Point getLocation() {
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapApple, location.x * mSize, location.y * mSize, paint);
    }

    @Override
    public void update() {

    }
}
