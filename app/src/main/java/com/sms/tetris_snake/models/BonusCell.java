package com.sms.tetris_snake.models;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BonusCell extends Cell {
    public BonusCell(int column, int row, float w, float h, int color) {
        super(column, row, w, h, color);
    }

    @Override
    public void draw(Canvas canvas) {
        float left = column * w;
        float top = row * h;
        float right = (column + 1) * w;
        float bottom = (row + 1) * h;

        canvas.drawLine(left,top,right,bottom,paint);
        canvas.drawLine(left,bottom,right,top,paint);
    }
}
