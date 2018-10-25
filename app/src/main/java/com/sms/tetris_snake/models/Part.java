package com.sms.tetris_snake.models;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sms.tetris_snake.Utils;


public class Part {
    private static final float PART_MIN_VELOCITY = 0.1f;
    private static final float PART_D_SIZE_INTERVAL_PERCENT = 1 / 200f;

    protected float x, y, w, h;
    private float dx, dy;
    private static Paint paint = new Paint();


    public Part(float x, float y, float width, float height, int color) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;

        paint.setColor(color);


        dx = Utils.RANDOM.nextFloat() + PART_MIN_VELOCITY;
        if (Utils.RANDOM.nextBoolean())
            dx = -dx;

        dy = Utils.RANDOM.nextFloat() + PART_MIN_VELOCITY;
        if (Utils.RANDOM.nextBoolean())
            dy = -dy;

    }

    public void draw(Canvas c) {
        RectF rectF = new RectF(x, y, x + w, y + h);
        c.drawOval(rectF, paint);
    }

    public void update(long interval) {
        x += dx;
        y += dy;

        w -= interval * PART_D_SIZE_INTERVAL_PERCENT;
        h -= interval * PART_D_SIZE_INTERVAL_PERCENT;
    }


}
