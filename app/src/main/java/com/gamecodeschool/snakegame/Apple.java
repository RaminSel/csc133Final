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
    private int x, y;
    private Point location = new Point();
    private Point mSpawnRange;
    private int mBlockSize;
    private Bitmap mBitmapApple;

    public Apple(Context context, Point spawnRange, int blockSize) {
        // Save the passed-in parameters for later use
        // Correctly set the block size
        mBlockSize = blockSize;

        // Save the passed-in parameters for later use
        mSpawnRange = spawnRange;

        // Initialize the position of the wall randomly
        x = (int) (Math.random() * mSpawnRange.x) * mBlockSize;
        y = (int) (Math.random() * mSpawnRange.y) * mBlockSize;
        x -= 40;
        y -= 40;


        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, mBlockSize, mBlockSize, false);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        // Draw the image at its position
        if (mBitmapApple != null) {
            canvas.drawBitmap(mBitmapApple, x, y, paint);
        }

    }

    public Point getLocation() {
        return new Point(x / mBlockSize, y / mBlockSize); // Ensure coordinates align with game logic
    }

    @Override
    public void update() {


    }
}
