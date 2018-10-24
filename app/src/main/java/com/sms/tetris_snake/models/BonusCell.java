package com.sms.tetris_snake.models;

import android.graphics.Paint;

public class BonusCell extends Cell {
    public BonusCell(int column, int row, float w, float h, int color) {
        super(column, row, w, h, color);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

}
