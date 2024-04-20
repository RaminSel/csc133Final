package com.gamecodeschool.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Wall extends GameObject {
    private int x, y; // Wall's position
    private Bitmap image; // The wall's image
    private Point mSpawnRange; // The range within which the wall can spawn
    private int mBlockSize; // The size of each block in the game

    // Removed the unused constructor for clarity

    public Wall(Context context, Point spawnRange, int blockSize) {
        // Save the passed-in parameters for later use
        // Correctly set the block size
        mBlockSize = blockSize;

        // Save the passed-in parameters for later use
        mSpawnRange = spawnRange;

        // Initialize the position of the wall randomly
        x = (int) (Math.random() * mSpawnRange.x) * mBlockSize;
        y = (int) (Math.random() * mSpawnRange.y) * mBlockSize;


        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.piece);

        image = Bitmap.createScaledBitmap(image, mBlockSize, mBlockSize, false);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        // Draw the image at its position
        if (image != null) {
            canvas.drawBitmap(image, x, y, paint);
        }
    }

    @Override
    public void update() {
        // This can remain empty if the wall doesn't need to change or move
    }
}