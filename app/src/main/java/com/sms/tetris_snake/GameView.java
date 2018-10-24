package com.sms.tetris_snake;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sms.tetris_snake.models.Snake;


public class GameView extends View {
    private final String TAG = getClass().getSimpleName();
    private Game game;

    public GameView(Context context, final Game game) {
        super(context);
        this.game = game;

        setOnTouchListener(new OnTouchListener() {
            float initialX, initialY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();

                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        initialX = event.getX();
                        initialY = event.getY();

                        Log.d("TAG", "x=" + initialX);
                        Log.d("TAG", "w=" + getWidth());

                        if (initialX < getWidth() / 2) {
                            game.getTetrisCanvas().moveTetrisElement(Direction.LEFT);
                        } else {
                            game.getTetrisCanvas().moveTetrisElement(Direction.RIGHT);
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        float finalX = event.getX();
                        float finalY = event.getY();


                        float dx = initialX - finalX;
                        float dy = initialY - finalY;

                        if (Math.abs(dx) > Math.abs(dy)) {
                            if (initialX > finalX) {
                                game.getTetrisCanvas().setSnakeDirection(Direction.LEFT);
                            } else {
                                game.getTetrisCanvas().setSnakeDirection(Direction.RIGHT);
                            }
                        } else {
                            if (initialY > finalY) {
                                game.getTetrisCanvas().setSnakeDirection(Direction.UP);
                            } else {
                                game.getTetrisCanvas().setSnakeDirection(Direction.DOWN);
                            }
                        }

                        break;

                }

                return true;
            }
        });

    }

    public void update() {
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        game.draw(canvas);
    }
}
