package com.sms.tetris_snake.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;


import com.sms.tetris_snake.Direction;
import com.sms.tetris_snake.Utils;
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
    private List<Part> parts;
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
        paint.setStrokeCap(Paint.Cap.ROUND);

        newGame();
    }

    public void newGame() {

        cells = new ArrayList<>();
        parts = new ArrayList<>();
        snake = new Snake(rowCount, columnCount, cellSize);

        generateNewBonus();

        score = 0;
    }

    public void setSnakeDirection(Direction direction) {
        snake.changeDirection(direction);
    }

    public void update(long interval) {

        if (snake.isDead()) {
            viewCallbacks.endGame(score);
            return;
        }


        boolean intersect = checkIntersectSnakeWithBonus();

        if (intersect) {
            snake.isFailDown = true;
            snake.direction = Direction.DOWN;
            parts.addAll(generateParts(bonus));
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

        snake.update();
    }

    private List<Part> generateParts(Cell bonus) {
        int n = Utils.RANDOM.nextInt(10) + 10;
        List<Part> parts = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            parts.add(new Part(bonus.column * cellSize + bonus.w / 2,
                    bonus.row * cellSize + bonus.h / 2,
                    cellSize / 10, cellSize / 10,
                    Color.WHITE));
        }

        return parts;
    }

    private void addSnakeToTetrisCells() {
        for (Cell c : snake.getCells()) {
            c.setColor(Color.WHITE);
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

                if (snake.direction == Direction.DOWN)
                    if (c.getRow() - snCell.getRow() == 1 && snCell.getColumn() == c.getColumn()) {
                        return true;
                    }

                if (snake.direction == Direction.UP)
                    if (c.getRow() - snCell.getRow() == -1 && snCell.getColumn() == c.getColumn()) {
                        return true;
                    }

                if (snake.direction == Direction.RIGHT)
                    if (c.getRow() == snCell.getRow() && snCell.getColumn() - c.getColumn() == -1) {
                        return true;
                    }

                if (snake.direction == Direction.LEFT)
                    if (c.getRow() == snCell.getRow() && snCell.getColumn() - c.getColumn() == 1) {
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
            if (c.getRow() == i) {
                cellIterator.remove();
                parts.addAll(generateParts(c));
            }

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

        bonus = new BonusCell(column, row, cellSize, cellSize, Color.WHITE);
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

        //draw parts
        drawParts(canvas);
        //draw score
        paint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(score), 0, 80, paint);
        paint.setColor(Color.BLACK);


        canvas.translate(-marginLeft, -marginTop);

    }

    private void drawParts(Canvas canvas) {
        for (Part p : parts) {
            p.draw(canvas);
        }
    }

    private void drawControlers(Canvas canvas) {


        paint.setColor(Color.WHITE);
        /*Path path = new Path();
        path.reset();


        float y2 = rowCount * cellSize / 2;
        float y1 = y2 - cellSize * 1.5f;
        float y3 = y2 + cellSize * 1.5f;

        float left = cellSize * 1;
        float right = cellSize * (columnCount - 1);

        // left
        path.moveTo(left, y1);
        path.lineTo(left - cellSize / 2, y2);
        path.lineTo(left, y3);
        path.close();

        // right
        path.moveTo(right, y1);
        path.lineTo(right + cellSize / 2, y2);
        path.lineTo(right, y3);
        path.close();

        canvas.drawPath(path, paint);*/

        canvas.drawLine(columnCount * cellSize / 2, cellSize * 1, columnCount * cellSize / 2, cellSize * (rowCount - 1), paint);
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

        used.add(0);

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

    public boolean isFailDown() {
        return snake.isFailDown;
    }

    public void updateAnimations(long interval) {
        for (Part p : parts) {
            p.update(interval);
        }

        Iterator<Part> iterator = parts.iterator();
        while (iterator.hasNext()) {
            Part p = iterator.next();
            if (p.x < 0 || p.x > columnCount * cellSize || p.y < 0 || p.y > rowCount * cellSize || p.h <= 0 || p.w <= 0) {
                iterator.remove();
                continue;
            }


        }


    }
}
