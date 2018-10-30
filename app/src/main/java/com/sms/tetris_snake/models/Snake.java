package com.sms.tetris_snake.models;

import android.graphics.Canvas;
import android.graphics.Color;

import com.sms.tetris_snake.Direction;
import com.sms.tetris_snake.Utils;

import java.util.List;
import java.util.Random;

public class Snake {


    private static final int MAX_TAKT_BEFORE_END_GAME = 100;

    private Cell[] cells;
    private Direction direction;

    private boolean isFailDown;

    private int rowCount, columnCount;
    private float cellSize;
    private boolean dead;

    private int superSnakeTakts;

    private int endGameIfNotCatchCellAfterTakts;

    public Snake(int rowCount, int columnCount, float cellSize) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.cellSize = cellSize;

        regenerateSnake();
    }


    public void update() {
        if (isFailDown) {
            for (Cell c : cells) {
                c.update(c.getColumn(), c.getRow() + 1);
            }
        } else {
            Cell[] newcells = new Cell[cells.length];

            System.arraycopy(cells, 0, newcells, 1, cells.length - 1);

            newcells[0] = new Cell(cells[0]);
            cells = newcells;

            if (direction == Direction.UP) {
                cells[0].update(cells[1].getColumn(), cells[1].getRow() - 1);
                if (cells[0].getRow() < 0) {
                    cells[0].setRow(rowCount - 1);
                }
            } else if (direction == Direction.DOWN) {
                cells[0].update(cells[1].getColumn(), cells[1].getRow() + 1);
                if (cells[0].getRow() >= rowCount) {
                    cells[0].setRow(0);
                }
            } else if (direction == Direction.RIGHT) {
                cells[0].update(cells[1].getColumn() + 1, cells[1].getRow());
                if (cells[0].getColumn() >= columnCount) {
                    cells[0].setColumn(0);
                }
            } else if (direction == Direction.LEFT) {
                cells[0].update(cells[1].getColumn() - 1, cells[1].getRow());
                if (cells[0].getColumn() < 0) {
                    cells[0].setColumn(columnCount - 1);
                }
            }
        }

        if (superSnakeTakts >= 0) {
            superSnakeTakts--;
        } else {
            endGameIfNotCatchCellAfterTakts--;
        }

    }

    public void draw(Canvas canvas) {
        for (Cell c : cells) {
            if (superSnakeTakts > 0) {
                c.setColor(Color.RED);
            } else c.setColor(Color.WHITE);

            c.draw(canvas);

        }
    }

    public void changeDirection(Direction direction) {
        if (this.direction == Direction.UP) {
            if (direction == Direction.DOWN) {
                return;
            }
        }
        if (this.direction == Direction.DOWN) {
            if (direction == Direction.UP) {
                return;
            }
        }
        if (this.direction == Direction.RIGHT) {
            if (direction == Direction.LEFT) {
                return;
            }
        }
        if (this.direction == Direction.LEFT) {
            if (direction == Direction.RIGHT) {
                return;
            }
        }
        this.direction = direction;
    }


    void regenerateSnake() {

        cells = new Cell[new Random().nextInt(columnCount / 2) + 2];

        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell(cells.length - i, 0, cellSize, cellSize, Color.WHITE);
        }

        direction = Direction.RIGHT;
        isFailDown = false;
        superSnakeTakts = -1;
        endGameIfNotCatchCellAfterTakts = MAX_TAKT_BEFORE_END_GAME;
    }


    public boolean isDead() {
        return dead || endGameIfNotCatchCellAfterTakts <= 0;
    }

    public Cell[] getCells() {
        return cells;
    }

    public void onSuperMode() {
        superSnakeTakts = 100;
    }


    public void offSuperMode() {
        superSnakeTakts = 0;
    }

    public boolean isSuperMode() {
        return superSnakeTakts > 0;
    }

    public Cell getHead() {
        return cells[0];
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isFailDown() {
        return isFailDown;
    }

    public void setFailDown(boolean failDown) {
        isFailDown = failDown;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getEndGameIfNotCatchCellAfterTakts() {
        return endGameIfNotCatchCellAfterTakts;
    }

    public int getSuperSnakeTakts() {
        return superSnakeTakts;
    }
}
