package com.icelandic_courses.elie.myfirstapp.logic;

import android.content.SharedPreferences;

/**
 * Created by Elie on 16.09.2015.
 */
public enum Difficulty {

    EASY,
    MIDDLE,
    HARD;

    public static Difficulty get(SharedPreferences preferences) {
        return Difficulty.valueOf(preferences.getString("difficulty", Difficulty.MIDDLE.toString()));
    }

    public int getPitchSize() {
        switch (this) {
            case EASY:
                return 7;
            case HARD:
                return 5;
            case MIDDLE:
            default:
                return 6;
        }
    }

    public int getNumberColors() {
        switch (this) {
            case EASY:
                return 3;
            case HARD:
                return 5;
            case MIDDLE:
            default:
                return 4;
        }
    }
}
