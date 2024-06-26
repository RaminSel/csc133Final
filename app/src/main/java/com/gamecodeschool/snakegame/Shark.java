package com.gamecodeschool.snakegame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

public class Shark extends GameObject implements Movable, Updateable, Drawing{
    private Point mMoveRange;
    private int mSegmentSize;
    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Point location;
    private double sharkSpeed = 1;


    private enum Heading {
        RIGHT, LEFT
    }

    // Start by heading to the right
    private Heading heading = Heading.RIGHT;
    public Shark(Context context, Point mr, int ss){
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
    public void move(){

        // Calculate distance to move based on speed and deltaTime
        //float distanceToMove = (float) (sharkSpeed * deltaTimeInSeconds);

        // Move it appropriately
        switch (heading) {
            case RIGHT:
                location.x += sharkSpeed;
                if (location.x >= mMoveRange.x) {
                    location.y++;
                    heading = Heading.LEFT;
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
                    heading = Heading.RIGHT;
                    if (location.y >= mMoveRange.y) {
                        location.x = 0;
                        location.y = 0;
                    }
                }
                break;
        }
    }
    void move(Heading newHeading) {
        this.heading = newHeading;
        move();
    }

    void reset(int w, int h) {

        // Reset the heading
        heading = Heading.RIGHT;

        // Delete the old contents of the ArrayList
        location.x = 0;
        location.y = 0;
    }
    public Point getLocation(){
        return new Point(location.x, location.y);
    }
    @Override
    public void update() {

    }
}
