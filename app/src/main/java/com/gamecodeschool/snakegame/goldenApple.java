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

    private int x,y;

    private int mBlockSize;

    private int mSize;
    private Bitmap mBitmapGold;


    public goldenApple(Context context, Point spawnRange, int blockSize) {
        // Save the passed-in parameters for later use
        // Correctly set the block size
        mBlockSize = blockSize;

        // Save the passed-in parameters for later use
        mSpawnRange = spawnRange;

        // Initialize the position of the wall randomly
        x = (int) (Math.random() * mSpawnRange.x) * mBlockSize;
        y = (int) (Math.random() * mSpawnRange.y) * mBlockSize;
        x -= 50;
        y -= 50;


        mBitmapGold = BitmapFactory.decodeResource(context.getResources(), R.drawable.goldapple);

        mBitmapGold= Bitmap.createScaledBitmap(mBitmapGold, mBlockSize, mBlockSize, false);
    }
    @Override
    public void draw(Canvas canvas, Paint paint) {
        // Draw the image at its position
        if (mBitmapGold != null) {
            canvas.drawBitmap(mBitmapGold, x, y, paint);
        }

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



    public Point getLocation() {
        return new Point(x / mBlockSize, y / mBlockSize); // Ensure coordinates align with game logic
    }

    public void despawn() {
        // Reset the location of the golden apple to make it disappear
        location.x = -60;
        location.y = -60;
    }


    @Override
    public void update() {

    }
}
