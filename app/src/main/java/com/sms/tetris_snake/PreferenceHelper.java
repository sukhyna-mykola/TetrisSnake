package com.sms.tetris_snake;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;


public final class PreferenceHelper {

    private static PreferenceHelper helper;

    private SharedPreferences sPref;

    private static final String PREFERENCE = "preference";

    private static final String MAX_POINTS = "max_points";
    private static final String SOUND = "sound";

    private PreferenceHelper(Context context) {
        sPref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
    }

    public static PreferenceHelper getInstance(Context c) {
        if (helper == null)
            helper = new PreferenceHelper(c);
        return helper;
    }


    public void saveMaxPoints(int points) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(MAX_POINTS, points);
        ed.apply();
    }

    public int loadMaxPoints() {
        return sPref.getInt(MAX_POINTS, 0);

    }

    public void saveSoundPref(boolean isPlay) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(SOUND, isPlay);
        ed.apply();
    }

    public boolean isPlaySound() {
        return sPref.getBoolean(SOUND, true);
    }
}
