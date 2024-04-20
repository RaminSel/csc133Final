package com.gamecodeschool.snakegame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import android.graphics.Typeface;


class SnakeGame extends SurfaceView implements Runnable{

    private Thread mThread = null;
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;


    List<GameObject> gameObjects = new ArrayList<>();
    private final int NUM_BLOCKS_WIDE = 40;
    private int mNumBlocksHigh;
    private int mScore;

    private Handler mHandler = new Handler();

    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    private Snake mSnake;
    private Apple mApple;

    private ManageSound soundManager;

    public void playEatSound() {
        soundManager.playEatSound();
    }

    public void playCrashSound() {
        soundManager.playCrashSound();

    }

    public SnakeGame(Context context, Point size) {
        super(context);

        initializeGameArea(size);
        initializeGameObjects(context, size);
        initializeDrawingObjects();
        soundManager = new ManageSound(context);
    }

    private void initializeGameArea(Point size) {
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;
    }

    private void initializeGameObjects(Context context, Point size) {
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mApple = new Apple(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        mSnake = new Snake(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        gameObjects.add(mSnake);
        gameObjects.add(mApple);
    }

    private void initializeDrawingObjects() {
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
    }

    public void newGame() {
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        // Get the apple ready for dinner
        mApple.spawn();
        // Reset the mScore
        mScore = 0;
        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();

        mHandler.postDelayed(addNewWall, 10000);
    }

    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused) {
                // Update 10 times a second
                if (updateRequired()) {
                    update();
                }
            }
            draw();
        }
    }

    // Check to see if it is time for an update
    public boolean updateRequired() {

        final long TARGET_FPS = 8;
        final long MILLIS_PER_SECOND = 1000;

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            return true;
        }

        return false;
    }


    public void update() {
        updateGameObjects();
        checkSnakeEatingApple();
        checkSnakeDeath();
    }




    private void updateGameObjects() {
        mSnake.move();

        synchronized (gameObjects) {
            for (GameObject object : gameObjects) {
                if (object != null) {
                    object.update();
                }
            }
        }
    }

    private void checkSnakeEatingApple() {
        if (mSnake.checkDinner(mApple.getLocation())) {
            mApple.spawn();
            mScore += 1;
            playEatSound();
        }
    }

    private void checkSnakeDeath() {
        if (mSnake.detectDeath()) {
            playCrashSound();
            mPaused = true;
        }
    }

    // Do all the drawing
    public void draw() {
        if (prepareCanvas()) {
            drawGameElements();
            if(mPaused) {
                drawPauseScreen();
            }
            finalizeCanvas();
        }
    }


    private boolean prepareCanvas() {
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            return true;
        }
        return false;
    }

    private void drawGameElements() {
        RenderGame renderer = new RenderGame(mCanvas, mPaint);
        renderer.drawBackground(Color.argb(255, 110, 225, 120));
        renderer.drawScore(mScore);

        synchronized (gameObjects) {
            for(GameObject gameObject : gameObjects) {
                gameObject.draw(mCanvas, mPaint);
            }
        }
        renderer.drawGameObjects(gameObjects);
        for(GameObject gameObject : gameObjects) {
            gameObject.draw(mCanvas, mPaint);
        }

        mApple.draw(mCanvas, mPaint);
        mSnake.draw(mCanvas, mPaint);
        renderer.drawCustomText("Ramin, Parsa, Julian, Tyler", 2900, 120, Color.BLACK, 75, Paint.Align.RIGHT);
    }

    private void drawPauseScreen() {
        mPaint.setTypeface(Typeface.create(mPaint.getTypeface(), Typeface.ITALIC));
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(175);
        mCanvas.drawText(getResources().getString(R.string.tap_to_play), 200, 700, mPaint);
    }

    private void finalizeCanvas() {

        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mPaused) {
                    mPaused = false;
                    newGame();
                    return true;
                }

                // Let the Snake class handle the input
                mSnake.switchHeading(motionEvent);
                break;

            default:
                break;
        }
        return true;
    }

    // Inside your SnakeGame class
    private Runnable addNewWall = new Runnable() {
        @Override
        public void run() {
            if(!mPaused) {

                int blockSize = getWidth() / NUM_BLOCKS_WIDE;
                Wall wall = new Wall(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
                synchronized (gameObjects) {
                    gameObjects.add(wall);
                }
                // Reschedule the runnable after 10 seconds
                mHandler.postDelayed(this, 10000);
            }
        }
    };

    public void togForPause() {
        mPaused = !mPaused;

        if (!mPaused) {
            mNextFrameTime = System.currentTimeMillis();
        }

    }

    public boolean ismPaused() {

        return mPaused;
    }

    // Stop the thread
    public void pause() {
        mHandler.removeCallbacks(addNewWall);
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    // Start the thread
    public void resume() {
        mHandler.post(addNewWall);
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }

}
