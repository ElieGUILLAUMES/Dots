package com.icelandic_courses.elie.myfirstapp.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Elie on 15.09.2015.
 */
public enum GameMode {

    CLASSIC ("classic"),
    MOVES ("moves");

    private final String mode;

    private GameMode(String mode) {
        this.mode = mode;
    }

    public boolean equalsName(String otherMode) {
        return (otherMode == null) ? false : mode.equals(otherMode);
    }

    public String toString() {
        return this.mode;
    }
}
