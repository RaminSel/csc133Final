package com.gamecodeschool.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;


public class goldenApple extends GameObject implements Updateable{
    private static final int MIN_SIZE = 5;
    private static final int SIZE_DECREMENT = 10;
    private static final int SCORE_THRESHOLD = 5;
    private Point location = new Point();
    private Point mSpawnRange;

    private int mSize;
    private Bitmap mBitmapGold;


    public goldenApple (Context context, Point sr, int s){
        mSpawnRange = sr;
        mSize = s;
        location.x = -40;

        // Load the image to the bitmap
        mBitmapGold = BitmapFactory.decodeResource(context.getResources(), R.drawable.goldapple);

        // Resize the bitmap to the specified size
        resizeBitmap(mSize);

    }
    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapGold, location.x * mSize, location.y * mSize, paint);
    }

    public void spawn() {
        Random random = new Random();
        double randomValue = random.nextDouble(); // Generate a random double between 0.0 and 1.0

        // Check if the random value falls within the spawn chance range (10% chance)
        if (randomValue < 0.3) {
            // Spawn the golden apple
            location.x = random.nextInt(mSpawnRange.x) + 1;
            location.y = random.nextInt(mSpawnRange.y - 1) + 1;
            // Resize the golden apple based on the score

        }
    }

    public void spawn(int score) {
        spawn();
        if (score > SCORE_THRESHOLD) {
            int newSize = mSize - SIZE_DECREMENT;
            mSize = Math.max(newSize, MIN_SIZE);
            resizeBitmap(mSize);
        }
    }


    public Point getLocation() {
        return location;
    }

    private void resizeBitmap(int size) {
        mBitmapGold = Bitmap.createScaledBitmap(mBitmapGold, size, size, false);
    }
    public void despawn() {
        // Reset the location of the golden apple to make it disappear
        location.x = -20;
        location.y = -20;
    }


    @Override
    public void update() {

    }
}
