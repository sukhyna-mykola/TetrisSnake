package com.sms.tetris_snake.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.sms.tetris_snake.GameSurface;

public class Cell {

    protected float w, h;
    protected int row, column;

    protected Paint paint;

    public Cell( int column,int row, float w, float h, int color) {

        this.row = row;
        this.column = column;
        this.h = h;
        this.w = w;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
    }

    public Cell(Cell cell) {
        this(cell.column, cell.row, cell.w, cell.h, cell.paint.getColor());
    }


    public void draw(Canvas canvas) {
        float left = column * w;
        float right = (column + 1) * w;
        float top = row * h;
        float bottom = (row + 1) * h;

        RectF shape = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(shape, 5, 5, paint);
    }

    public void update(int newColumn, int newRow) {
        this.column = newColumn;
        this.row = newRow;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }


    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
