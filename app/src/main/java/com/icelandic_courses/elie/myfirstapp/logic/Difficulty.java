package com.icelandic_courses.elie.myfirstapp.logic;

/**
 * Created by Elie on 16.09.2015.
 */
public enum Difficulty {

    EASY ("easy"),
    MIDDLE ("middle"),
    HARD ("hard");

    private final String difficulty;

    private Difficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public boolean equalsName(String otherDifficulty) {
        return (otherDifficulty == null) ? false : difficulty.equals(otherDifficulty);
    }

    public String toString() {
        return this.difficulty;
    }

}
