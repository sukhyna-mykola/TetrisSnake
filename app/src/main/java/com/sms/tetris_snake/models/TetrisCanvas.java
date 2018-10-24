package com.sms.tetris_snake.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;


import com.sms.tetris_snake.Direction;
import com.sms.tetris_snake.ViewCallbacks;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TetrisCanvas {

    private final String TAG = "TetrisCanvas";

    private static final float CELL_SIZE_PERCENT = 0.1f;

    private Paint paint;

    private Snake snake;
    private List<Cell> cells;
    private Cell bonus;
    private int score;

    private float cellSize;
    private int columnCount;
    private int rowCount;


    private float marginTop, marginLeft;

    private ViewCallbacks viewCallbacks;


    public TetrisCanvas(ViewCallbacks viewCallbacks, int w, int h) {
        this.viewCallbacks = viewCallbacks;

        cellSize = w * CELL_SIZE_PERCENT;

        columnCount = (int) (w / cellSize);
        rowCount = (int) (h / cellSize);

        marginLeft = (w - columnCount * cellSize) / 2;
        marginTop = (h - rowCount * cellSize) / 2;

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        paint.setTextSize(90);
        paint.setTextAlign(Paint.Align.LEFT);

        newGame();
    }

    public void newGame() {

        cells = new ArrayList<>();
        snake = new Snake(rowCount, columnCount, cellSize);

        generateNewBonus();

        score = 0;
    }

    public void setSnakeDirection(Direction direction) {
        snake.changeDirection(direction);
    }

    public void update() {

        if (snake.isDead()) {
            viewCallbacks.endGame(score);
            return;
        }

        snake.update();

        boolean intersect = checkIntersectSnakeWithBonus();

        if (intersect) {
            snake.isFailDown = true;
            snake.direction = Direction.DOWN;
        }


        boolean intersectWithTetris = checkIntersectionsWithTetrisElements();

        if (intersectWithTetris) {
            if (!snake.isFailDown) {
                snake.dead = true;
            }
            addSnakeToTetrisCells();
            snake.regenerateSnake();

            generateNewBonus();
        }


        checkTetrisLines();

    }

    private void addSnakeToTetrisCells() {
        for (Cell c : snake.getCells()) {
            c.setColor(Color.BLUE);
            cells.add(new TetrisCell(c));
            score++;
        }
    }

    private boolean checkIntersectionsWithTetrisElements() {

        for (Cell snCell : snake.getCells()) {
            if (snCell.getRow() == rowCount - 1 && snake.isFailDown) {
                return true;
            }

            for (Cell c : cells) {
                if (c == null) continue;

                if (c.getRow() - snCell.getRow() == 1 && snCell.getColumn() == c.getColumn()) {
                    return true;
                }


            }
        }
        return false;
    }


    private boolean checkIntersectSnakeWithBonus() {
        if (snake.isFailDown)
            return false;

        for (Cell c : snake.getCells()) {
            if (c.getColumn() == bonus.getColumn() && c.getRow() == bonus.getRow()) {
                return true;
            }
        }
        return false;
    }

    private void checkTetrisLines() {
        for (int i = 0; i < rowCount; i++) {
            Log.d(TAG, "checkLine(" + i + ");");
            checkLine(i);
        }
    }

    private void checkLine(int i) {
        int countNotNul = 0;

        for (Cell c : cells) {
            if (c.getRow() == i)
                countNotNul++;
        }

        Log.d(TAG, "countNotNul = " + countNotNul);
        if (countNotNul < columnCount)
            return;

        //remove fill line
        Iterator<Cell> cellIterator = cells.iterator();
        while (cellIterator.hasNext()) {
            Cell c = cellIterator.next();
            if (c.getRow() == i)
                cellIterator.remove();

        }

        //tear down all tetris cells that is above fill line
        for (Cell c : cells) {
            if (c.getRow() < i) {
                c.update(c.column, c.row + 1);
            }
        }
    }

    private void generateNewBonus() {
        List<Integer> free = findFreeCells();
        if (free.isEmpty())
            return;

        int newCellPosition = new Random().nextInt(free.size());
        int newCellValue = free.get(newCellPosition);

        int row = newCellValue / columnCount;
        int column = newCellValue % columnCount;

        bonus = new BonusCell(column, row, cellSize, cellSize, Color.GREEN);
    }


    public void draw(Canvas canvas) {
        canvas.translate(marginLeft, marginTop);

        canvas.drawColor(Color.WHITE);
        canvas.drawRect(-3, -3, columnCount * cellSize + 3, rowCount * cellSize + 3, paint);

        for (Cell c : cells) {
            c.draw(canvas);
        }

        snake.draw(canvas);

        if (snake.isFailDown) {
            drawControlers(canvas);
        } else {
            bonus.draw(canvas);
        }

        paint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(score), 0, 80, paint);
        paint.setColor(Color.BLACK);

        canvas.translate(-marginLeft, -marginTop);

    }

    private void drawControlers(Canvas canvas) {

        paint.setColor(Color.WHITE);
        canvas.drawLine(columnCount * cellSize / 2, 0, columnCount * cellSize / 2, rowCount * cellSize, paint);
    }


    public void moveTetrisElement(Direction d) {
        if (snake.isFailDown) {

            int left = Integer.MAX_VALUE;
            int right = Integer.MIN_VALUE;

            for (Cell c : snake.getCells()) {
                left = Math.min(c.getColumn(), left);
                right = Math.max(c.getColumn(), right);
            }

            if (d == Direction.RIGHT) {
                if (right == columnCount - 1)
                    return;

                for (Cell c : snake.getCells()) {
                    for (Cell tc : cells) {
                        if (c.column + 1 == tc.column && c.row == tc.row)
                            return;
                    }
                }

                for (Cell c : snake.getCells()) {
                    c.update(c.getColumn() + 1, c.getRow());
                }
            } else if (d == Direction.LEFT) {
                if (left == 0)
                    return;

                for (Cell c : snake.getCells()) {
                    for (Cell tc : cells) {
                        if (c.column - 1 == tc.column && c.row == tc.row)
                            return;
                    }
                }

                for (Cell c : snake.getCells()) {
                    c.update(c.getColumn() - 1, c.getRow());
                }
            }

        }
    }


    private List<Integer> findFreeCells() {
        Set<Integer> used = new HashSet<>();
        List<Integer> free = new ArrayList<>();

        for (Cell c : snake.getCells()) {
            used.add(c.getRow() * columnCount + c.getColumn());
        }

        for (Cell c : cells) {
            used.add(c.getRow() * columnCount + c.getColumn());
        }

        for (int i = rowCount / 3; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                used.add(i * columnCount + j);
            }
        }


        for (int i = 0; i < columnCount * rowCount; i++) {
            if (!used.contains(i)) {
                free.add(i);
            }
        }
        return free;
    }

}
