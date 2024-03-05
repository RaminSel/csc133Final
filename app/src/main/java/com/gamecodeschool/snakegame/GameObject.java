package com.gamecodeschool.snakegame;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface GameObject {
    // draw apple
     void draw(Canvas canvas, Paint paint);

    void update();
}


