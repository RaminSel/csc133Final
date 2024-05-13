package com.gamecodeschool.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import android.view.KeyEvent;

public class SnakeGame extends SurfaceView implements Runnable{

    private Thread mThread = null;
    private boolean mRunning = true;
    private boolean mPaused = true;
    private boolean mPauseBtn = false;
    private long mNextFrameTime;
    private volatile boolean mPlaying = false;
    private volatile boolean mpauseBtn = false;
    private List<GameObject> gameObjects = new ArrayList<>();
    private int mScore;
    private final int NUM_BLOCKS_WIDE = 25;
    private int mNumBlocksHigh;
    private Handler mHandler = new Handler();
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Snake mSnake;
    private Shark mShark;
    private ManageSound soundManager;
    private Bitmap[] backgroundFrames;
    private int currentFrameIndex = 0;
    private DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    private int screenWidth = displayMetrics.widthPixels;
    private int screenHeight = displayMetrics.heightPixels;
    private int backgroundWidth = 3120;
    private int backgroundHeight = 1440;
    private float scaleX = (float) screenWidth / backgroundWidth;
    private float scaleY = (float) screenHeight / backgroundHeight;
    private float scaleFactor = Math.max(scaleX, scaleY);
    private int scaledWidth = (int) (backgroundWidth * scaleFactor);
    private int scaledHeight = (int) (backgroundHeight * scaleFactor);
    private goldenApple mGold;

    public static int getBlockSize() {
        return blockSize;
    }

    private static int blockSize;

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
        soundManager.initBackgroundMusic(context);
    }

    private void initializeGameArea(Point size) {
        blockSize = size.x / NUM_BLOCKS_WIDE;
        mNumBlocksHigh = size.y / blockSize;
    }

    private void loadBackgroundFrames(Context context) {
        backgroundFrames = new Bitmap[40];
        for (int i = 0; i < backgroundFrames.length; i++) {
            backgroundFrames[i] = loadFrame(context, i);
        }
    }

    private Bitmap loadFrame(Context context, int frameIndex) {
        int resourceId = context.getResources().getIdentifier("frame" + frameIndex, "drawable", context.getPackageName());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateInSampleSize(options, scaledWidth, scaledHeight);
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), resourceId, options), scaledWidth, scaledHeight, true);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return inSampleSize;
    }

    private void initializeGameObjects(Context context, Point size) {
        blockSize = size.x / NUM_BLOCKS_WIDE;
        mGold = new goldenApple(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        mSnake = new Snake(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        mShark = new Shark(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);

        synchronized (gameObjects) {
            gameObjects.add(mSnake);
            gameObjects.add(mGold);
            gameObjects.add(mShark);
        }
    }

    private void initializeDrawingObjects() {
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
    }

    public void newGame() {
        soundManager.playBackgroundMusic();
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mGold.spawn();
        mShark.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);
        mScore = 0;
        mNextFrameTime = System.currentTimeMillis();
        mHandler.postDelayed(addNewWall, 10000);
        mHandler.post(addApple);
        synchronized (gameObjects) {
            gameObjects.removeIf(gameObject -> gameObject instanceof Wall);
            gameObjects.removeIf(gameObject -> gameObject instanceof Apple);
        }
        mHandler.removeCallbacks(addNewWall);
        mHandler.postDelayed(addNewWall, 10000);
        mHandler.removeCallbacks(addApple);
        mHandler.post(addApple);
    }

    @Override
    public void run() {
        long lastFrameTime = System.currentTimeMillis();
        while (mPlaying) {
            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastFrameTime;
            lastFrameTime = currentTime;

            if (!mPaused && !mpauseBtn) {
                if (updateRequired()) {
                    update();
                }
            }
            draw();

            long sleepTime = 1000 / 10 - deltaTime;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean updateRequired() {
        final long TARGET_FPS = 10;
        final long MILLIS_PER_SECOND = 1000;
        if(mNextFrameTime <= System.currentTimeMillis()){
            mNextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / TARGET_FPS;
            return true;
        }
        return false;
    }

    private void update() {
        //float deltaTimeInSeconds = deltaTime / 1000.0f; // Convert milliseconds to seconds

        mSnake.move();
        mShark.move();
        checkCollisions();
        checkSnakeDeath();
    }

    private void checkCollisions() {

        boolean goldenAppleSpawned = false;
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
                synchronized (gameObjects) {
                    gameObjects.remove(object);
                }
                mHandler.removeCallbacks(addApple);
                mHandler.post(addApple);// Note: spawn now considers score
            }else if (object instanceof goldenApple && mSnake.checkDinner(((goldenApple) object).getLocation())) {
                // The snake has eaten a golden apple
                playEatSound();
                mScore +=3;
                // Despawn the golden apple
                ((goldenApple) object).despawn();
            }
            else if (object instanceof goldenApple) {
                goldenAppleSpawned = true;
            }
            else {
                // No collision, update the game object normally
                object.update();
            }

            if (!goldenAppleSpawned && Math.random() < 0.1) { // 10% chance
                mGold.spawn();
            }

        }
    }


    private void checkSnakeDeath() {
        if (mSnake.detectDeath()) {
            playCrashSound();
            mPaused = true;
        }
    }

    public void draw() {
        if (prepareCanvas()) {
            RenderGame renderer = new RenderGame(mCanvas, mPaint);
            mCanvas.drawBitmap(backgroundFrames[0], 0, 0, mPaint);
            if (currentFrameIndex == backgroundFrames.length - 1) {
                currentFrameIndex = 0;
            } else {
                currentFrameIndex++;
            }
            renderer.drawScore(mScore);
            renderer.drawGameObjects(gameObjects);
            renderer.drawCustomText("Ramin, Parsa, Julian, Tyler", 2900, 120, Color.WHITE, 75, Paint.Align.RIGHT);
            if(mpauseBtn && !mPaused) {
                renderer.drawPauseBtnScreen();
            }
            if(mPaused){
                renderer.drawPauseScreen();
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
                mSnake.switchHeading(motionEvent);
                break;

            default:
                break;
        }
        return true;
    }

    private Runnable addApple = new Runnable() {
        @Override
        public void run() {
            if (!mPaused) {
                int blockSize = getWidth() / NUM_BLOCKS_WIDE;
                Apple apple = new Apple(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
                synchronized (gameObjects) {
                    gameObjects.add(apple);
                }
            }
            // Post this Runnable again after a certain delay to add apples at regular intervals
            //mHandler.postDelayed(this, 10000); // Change the delay as needed
        }
    };

    private Runnable addNewWall = new Runnable() {
        @Override
        public void run() {
            if(!mPaused) {
                int blockSize = getWidth() / NUM_BLOCKS_WIDE;
                Wall wall = new Wall(getContext(), new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
                synchronized (gameObjects) {
                    gameObjects.add(wall);
                }
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
                togForPause();
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

    public void pause() {
        soundManager.stopBackgroundMusic();
        mHandler.removeCallbacks(addNewWall);
        mHandler.removeCallbacks(addApple);
        mPlaying = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        soundManager.playBackgroundMusic();
        mHandler.post(addNewWall);
        mHandler.post(addApple);
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();
    }

    public Snake getSnake() {
        return mSnake;
    }
}
