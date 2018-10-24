package com.sms.tetris_snake.models;

import android.graphics.Canvas;

public class TetrisCell extends Cell {

    public TetrisCell(int column, int row, float w, float h, int color) {
        super(column, row, w, h, color);
    }

    public TetrisCell(Cell cell) {
        super(cell);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        float left = column * w;
        float top = row * h;
        float right = (column + 1) * w;
        float bottom = (row + 1) * h;

        canvas.drawLine(left,top,right,bottom,paint);
        canvas.drawLine(left,bottom,right,top,paint);
    }
}
