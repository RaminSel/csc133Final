package com.gamecodeschool.snakegame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
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
import android.view.KeyEvent;

class SnakeGame extends SurfaceView implements Runnable{

    private Thread mThread = null;
    private long mNextFrameTime;
    // Is the game currently playing and or paused?
    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    private volatile boolean mpauseBtn = false;


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

    private Shark mShark;

    private ManageSound soundManager;

    private Bitmap[] backgroundFrames;
    private int currentFrameIndex = 0;


    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    private int screenWidth = displayMetrics.widthPixels;
    private int screenHeight = displayMetrics.heightPixels;


    private int backgroundWidth = 3120;
    private int backgroundHeight = 1440;

    // Calculate the scaling factors
    private float scaleX = (float) screenWidth / backgroundWidth;
    private float scaleY = (float) screenHeight / backgroundHeight;

    // Choose the larger scaling factor to ensure the entire image covers the screen
    private float scaleFactor = Math.max(scaleX, scaleY);

    // Calculate the scaled dimensions of the background image
    private int scaledWidth = (int) (backgroundWidth * scaleFactor);
    private int scaledHeight = (int) (backgroundHeight * scaleFactor);

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
        loadBackgroundFrames(context);
        soundManager = new ManageSound(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        soundManager.initBackgroundMusic(context); // Initialize background music
    }

    private void initializeGameArea(Point size) {
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;
    }

    private void loadBackgroundFrames(Context context) {
        backgroundFrames = new Bitmap[40];
        for (int i = 0; i < backgroundFrames.length; i++) {
            int resourceId = context.getResources().getIdentifier("frame" + i, "drawable", context.getPackageName());
            backgroundFrames[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resourceId), scaledWidth, scaledHeight, true);
        }
    }

    private void initializeGameObjects(Context context, Point size) {
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        mApple = new Apple(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        mSnake = new Snake(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        mShark = new Shark(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        gameObjects.add(mSnake);
        gameObjects.add(mApple);
        gameObjects.add(mShark);
    }

    private void initializeDrawingObjects() {
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
    }

    public void newGame() {
        soundManager.playBackgroundMusic(); // Ensure the background music starts

        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        // Get the apple ready for dinner
        mApple.spawn();
        //Reset shark location
        mShark.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        // Reset the mScore
        mScore = 0;
        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();

        mHandler.postDelayed(addNewWall, 10000);

        synchronized (gameObjects) {
            gameObjects.removeIf(gameObject -> gameObject instanceof Wall);
        }

        // Reset wall spawning mechanism
        mHandler.removeCallbacks(addNewWall);
        mHandler.postDelayed(addNewWall, 10000);
    }

    @Override
    public void run() {
        while (mPlaying) {
            if(!mPaused && !mpauseBtn) {
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

        final long TARGET_FPS = 10;
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


    //public void update() {
        //updateGameObjects();
        //checkSnakeEatingApple();
      //  checkSnakeDeath();
    //}

    public void update() {
        mSnake.move(); // Move the snake first
        mShark.move();
        checkCollisions();
        checkSnakeDeath();
    }

    private void checkCollisions() {
        for (GameObject object : gameObjects) {
            if (object instanceof Wall && mSnake.checkCollision(((Wall) object).getLocation())
            || object instanceof Shark && mSnake.checkCollision(((Shark) object).getLocation())) {
                // Collision with a wall, play crash sound and stop the game
                playCrashSound();
                soundManager.stopBackgroundMusic();
                mPaused = true; // End the game
                //soundManager.stopBackgroundMusic(); // Stop the background music
                return; // No need to check other objects
            }
            else if (object instanceof Apple && mSnake.checkDinner(((Apple) object).getLocation())) {
                // The snake has eaten an apple
                playEatSound();
                mScore += 1;
                ((Apple)object).spawn(mScore); // Note: spawn now considers score
            } else {
                // No collision, update the game object normally
                object.update();
            }
        }
    }


    private void updateGameObjects() {
        mSnake.move(); // Move the snake first

        // Check for collisions with the wall and the apple
        GameObject collisionObject = null;
        for (GameObject object : gameObjects) {
            if (object instanceof Wall && mSnake.checkCollision(((Wall) object).getLocation())
            ) {
                // Collision with a wall, play crash sound and stop the game
                playCrashSound();
                mPaused = true; // End the game
                break; // No need to check other objects
            } else if (object instanceof Apple && mSnake.checkDinner(((Apple) object).getLocation())) {
                // The snake has eaten an apple
                playEatSound();
                mScore += 1;
                ((Apple)object).spawn();
            } else {
                // No collision, update the game object normally
                object.update();
            }
        }

        // If the game is over, clear all walls (bricks)
        if (mPaused) {
            gameObjects.removeIf(gameObject -> gameObject instanceof Wall);
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
            if(mpauseBtn && !mPaused) {
                drawPauseBtnScreen();
            }
            if(mPaused){
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
        mCanvas.drawBitmap(backgroundFrames[currentFrameIndex], 0, 0, mPaint);
        if (currentFrameIndex == backgroundFrames.length - 1) {
            currentFrameIndex = 0;
        } else {
            currentFrameIndex++;
        }
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
        mShark.draw(mCanvas, mPaint);
        renderer.drawCustomText("Ramin, Parsa, Julian, Tyler", 2900, 120, Color.WHITE, 75, Paint.Align.RIGHT);
    }

    private void drawPauseScreen() {
        mPaint.setTypeface(Typeface.create(mPaint.getTypeface(), Typeface.ITALIC));
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(175);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mCanvas.drawText(getResources().getString(R.string.tap_to_play), screenWidth/2, screenHeight/2, mPaint);
    }

    private void drawPauseBtnScreen(){
        mPaint.setTypeface(Typeface.create(mPaint.getTypeface(), Typeface.ITALIC));
        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setTextSize(175);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mCanvas.drawText("Paused!", screenWidth/2, screenHeight/2, mPaint);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_W:
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!mPaused) {
                    mSnake.move(Snake.Heading.UP);
                }
                return true;
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!mPaused) {
                    mSnake.move(Snake.Heading.DOWN);
                }
                return true;
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!mPaused) {
                    mSnake.move(Snake.Heading.LEFT);
                }
                return true;
            case KeyEvent.KEYCODE_D:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!mPaused) {
                    mSnake.move(Snake.Heading.RIGHT);
                }
                return true;
            case KeyEvent.KEYCODE_SPACE:
                togForPause(); // Pause the Game
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void togForPause() {
        mpauseBtn = !mpauseBtn;

        if (!mpauseBtn) {
            mNextFrameTime = System.currentTimeMillis();
        }

    }

    public boolean ismPaused() {

        return mpauseBtn;
    }

    // Stop the thread
    public void pause() {
        soundManager.stopBackgroundMusic(); // Stop music when the game is paused
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
        soundManager.playBackgroundMusic(); // Play music when game resumes
        mHandler.post(addNewWall);
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }

    public Snake getSnake() {
        return mSnake;
    }

}
