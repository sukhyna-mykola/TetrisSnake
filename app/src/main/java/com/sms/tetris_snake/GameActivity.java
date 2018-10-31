package com.sms.tetris_snake;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.sms.tetris_snake.models.TetrisCanvas;

public class GameActivity extends AppCompatActivity implements Updator, ViewCallbacks {

    private final String TAG = getClass().getSimpleName();

    private Game game;
    private GameSurface gameSurface;
    private GameThread gameThread;

    private TextView result, bestResult;
    private View endGameView, settingsView;

    private PreferenceHelper preferenceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        endGameView = findViewById(R.id.end_game_allert);
        settingsView = findViewById(R.id.settings_view);

        result = findViewById(R.id.result);
        bestResult = findViewById(R.id.best_result);

        findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGameView.setVisibility(View.GONE);
                gameThread.setPause(false);

                game.newGame(preferenceHelper.loadGameLevel(), preferenceHelper.loadGameSpeed());
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
        initSettingsView();


        gameThread = new GameThread(this);

        game = new Game();

        gameSurface = findViewById(R.id.game_surface);
        gameSurface.setGame(game);
        gameSurface.post(new Runnable() {
            @Override
            public void run() {

                game.startGame(GameActivity.this, gameSurface, preferenceHelper.loadGameLevel(), preferenceHelper.loadGameSpeed());
                gameThread.start();
            }
        });

    }

    private void initSettingsView() {
        findViewById(R.id.settings_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setVisibility(View.VISIBLE);
                endGameView.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.settings_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsView.setVisibility(View.GONE);
                endGameView.setVisibility(View.VISIBLE);
            }
        });


        String[] gameLevels = {"Easy", "Medium", "Hard"};
        String[] gameSpeeds = {"Quickly", "Medium", "Slow"};

        ArrayAdapter<String> adapterLevel = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gameLevels);
        adapterLevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterSpeed = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gameSpeeds);
        adapterLevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinnerLevel = findViewById(R.id.level_spinner);
        spinnerLevel.setAdapter(adapterLevel);

        Spinner spinnerSpeed = findViewById(R.id.speed_spinner);
        spinnerSpeed.setAdapter(adapterSpeed);


        spinnerLevel.setSelection(preferenceHelper.loadGameLevel());
        // устанавливаем обработчик нажатия
        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                preferenceHelper.saveGameLevel(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        spinnerSpeed.setSelection(preferenceHelper.loadGameSpeed());
        // устанавливаем обработчик нажатия
        spinnerSpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                preferenceHelper.saveGameSpeed(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        SwitchCompat switchCompat = findViewById(R.id.sound_switch);
        switchCompat.setChecked(preferenceHelper.isPlaySound());
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferenceHelper.saveSoundPref(isChecked);
            }
        });
    }

    @Override
    public void update(long interval) {
        game.update(interval);
        gameSurface.update();

    }

    @Override
    public void endGame(final int score) {
        gameThread.setPause(true);
        Log.d(TAG, "endGAme(" + score + ")");


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
