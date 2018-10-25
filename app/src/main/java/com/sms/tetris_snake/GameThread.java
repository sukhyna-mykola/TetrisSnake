package com.sms.tetris_snake;

import android.util.Log;

public class GameThread extends Thread {

    private static final int FPS = 5;
    private long ticksPS;

    private static final long PAUSE_SLEEP_TIME = 10;

    public GameThread(Updator updator) {
        this.updator = updator;
        run = true;
        pause = false;

        ticksPS = 1000 / FPS;
    }


    private Updator updator;
    private boolean run, pause;

    @Override
    public void run() {
        /*long startTime, sleepTime;
        while (run) {

            while (pause) {
                try {
                    Thread.sleep(PAUSE_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            startTime = System.currentTimeMillis();

            updator.update();

            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
            } catch (Exception e) {
                e.printStackTrace();
            }*/


        long previousFrameTime = System.currentTimeMillis();
        long beforePauseTime;
        while (run) {

            while (pause) {
                beforePauseTime = System.currentTimeMillis();
                try {
                    Thread.sleep(PAUSE_SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                previousFrameTime += (System.currentTimeMillis() - beforePauseTime);
            }

            long currentFrameTime = System.currentTimeMillis();

            long elapsedTimeMS = currentFrameTime - previousFrameTime;

            updator.update(elapsedTimeMS);


            previousFrameTime = currentFrameTime;
        }


    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
}