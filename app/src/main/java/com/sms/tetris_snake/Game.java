package com.sms.tetris_snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

import com.sms.tetris_snake.models.TetrisCanvas;

public class Game {

    private static final String TAG = "TAG";

    private TetrisCanvas tetrisCanvas;


    public TetrisCanvas getTetrisCanvas() {
        return tetrisCanvas;
    }

    public void startGame(ViewCallbacks viewCallbacks, int w, int h) {
        tetrisCanvas = new TetrisCanvas(viewCallbacks, w, h);

        newGame();
    }

    public void newGame() {
        tetrisCanvas.newGame();
    }

    public void draw(Canvas canvas) {
        tetrisCanvas.draw(canvas);

    }

    public void update() {
        tetrisCanvas.update();
    }

}
