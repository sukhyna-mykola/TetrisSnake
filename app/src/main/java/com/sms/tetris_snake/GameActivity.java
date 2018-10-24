package com.sms.tetris_snake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements Updator, ViewCallbacks {


    private Game game;
    private GameView gameView;
    private GameThread gameThread;

    private TextView result, bestResult;
    private View endGameView;

    private PreferenceHelper preferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        endGameView = findViewById(R.id.end_game_allert);

        result = findViewById(R.id.result);
        bestResult = findViewById(R.id.best_result);

        findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameView.setVisibility(View.GONE);
                gameThread.setPause(false);
                game.newGame();
            }
        });
        findViewById(R.id.exit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameThread.setPause(false);
                gameThread.setRun(false);
                finish();
            }
        });

        preferenceHelper = PreferenceHelper.getInstance(this);

        gameThread = new GameThread(this);

        game = new Game();
        gameView = new GameView(this, game);

        gameView.post(new Runnable() {
            @Override
            public void run() {

                game.startGame(GameActivity.this, gameView.getWidth(), gameView.getHeight());

                gameThread.start();
            }
        });

        ViewGroup gameLayout = findViewById(R.id.game_layout);
        gameLayout.addView(gameView);

    }

    @Override
    public void update() {
        game.update();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameView.update();

            }
        });
    }

    @Override
    public void endGame(final int score) {
        gameThread.setPause(true);


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int maxScore = preferenceHelper.loadMaxPoints();
                if (maxScore < score) {
                    preferenceHelper.saveMaxPoints(score);
                    maxScore = score;
                }

                result.setText(getString(R.string.result, score));
                bestResult.setText(getString(R.string.best_result, maxScore));

                endGameView.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        gameThread.setPause(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameThread.setPause(true);
    }
}
