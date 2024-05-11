package com.gamecodeschool.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

public class Hook extends GameObject implements Movable, Updateable, Drawing {

    private Point mMoveRange;
    private int mSegmentSize;
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Point location;
    private double sharkSpeed = .5;

    private enum Heading {
        RIGHT, LEFT
    }

    // Start by heading to the right
    private com.gamecodeschool.snakegame.Hook.Heading heading = com.gamecodeschool.snakegame.Hook.Heading.RIGHT;

    public Hook(Context context, Point mr, int ss) {
        mSegmentSize = ss;
        mMoveRange = mr;
        location = new Point();

        // Create and scale the bitmaps
        mBitmapHeadRight = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.shark);

        // Create opposite direction version of the head for different heading
        mBitmapHeadLeft = BitmapFactory
                .decodeResource(context.getResources(),
                        R.drawable.shark);

        mBitmapHeadRight = Bitmap
                .createScaledBitmap(mBitmapHeadRight,
                        ss, ss, false);

        // A matrix for scaling
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);

        mBitmapHeadLeft = Bitmap
                .createBitmap(mBitmapHeadRight,
                        0, 0, ss, ss, matrix, true);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        switch (heading) {
            case RIGHT:
                canvas.drawBitmap(mBitmapHeadRight,
                        location.x
                                * mSegmentSize,
                        location.y
                                * mSegmentSize, paint);
                break;

            case LEFT:
                canvas.drawBitmap(mBitmapHeadLeft,
                        location.x
                                * mSegmentSize,
                        location.y
                                * mSegmentSize, paint);
                break;
        }
    }

    public void move() {

        // Move it appropriately
        switch (heading) {
            case RIGHT:
                location.x += sharkSpeed;
                if (location.x >= mMoveRange.x) {
                    location.y++;
                    heading = com.gamecodeschool.snakegame.Hook.Heading.LEFT;
                    if (location.y >= mMoveRange.y) {
                        location.x = 0;
                        location.y = 0;
                    }
                }
                break;

            case LEFT:
                location.x -= sharkSpeed;
                if (location.x < 0) {
                    location.y++;
                    heading = com.gamecodeschool.snakegame.Hook.Heading.RIGHT;
                    if (location.y >= mMoveRange.y) {
                        location.x = 0;
                        location.y = 0;
                    }
                }
                break;
        }
    }

    void move(com.gamecodeschool.snakegame.Hook.Heading newHeading) {
        this.heading = newHeading;
        move();
    }

    void reset(int w, int h) {

        // Reset the heading
        heading = com.gamecodeschool.snakegame.Hook.Heading.RIGHT;

        // Delete the old contents of the ArrayList
        location.x = 0;
        location.y = 0;
    }

    public Point getLocation() {
        return new Point(location.x, location.y);
    }

    @Override
    public void update() {

    }
}
