package com.sms.tetris_snake;

import android.graphics.Canvas;
import android.util.Log;

import com.sms.tetris_snake.models.TetrisCanvas;

public class Game {

    private static final String TAG = "TAG";

    private TetrisCanvas tetrisCanvas;

    private long time, unitTime;

    public final long UNIT_TIMES[] = {100, 150, 200};


    public TetrisCanvas getTetrisCanvas() {
        return tetrisCanvas;
    }

    public void startGame(ViewCallbacks viewCallbacks, GameSurface gameSurface, int gameLevel, int gameSpeed) {

        tetrisCanvas = new TetrisCanvas(viewCallbacks, gameSurface, gameLevel);

        newGame(gameLevel, gameSpeed);
    }

    public void newGame(int gameLevel, int gameSpeed) {
        time = 0;
        unitTime = UNIT_TIMES[gameSpeed];
        tetrisCanvas.newGame(gameLevel);
    }

    public void draw(Canvas canvas) {
        tetrisCanvas.draw(canvas);
    }

    public void update(long interval) {
        Log.d(TAG, "update, interval = " + interval);
        time += interval;
        if (time > unitTime) {
            time = time - unitTime;
            tetrisCanvas.update(interval);
        }

        tetrisCanvas.updateAnimations(interval);
    }

}
