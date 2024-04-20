package com.gamecodeschool.snakegame;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class GameObject implements Drawing, Updateable {

    @Override
    public abstract void draw(Canvas canvas, Paint paint);

    @Override
    public abstract void update();
}