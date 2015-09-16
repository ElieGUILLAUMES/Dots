package com.icelandic_courses.elie.myfirstapp.score;

/**
 * Created by Elie on 15/09/2015.
 */
public class HighScore {

    private String gameMode;
    private int highScore;

    public HighScore(String gameMode, int highScore){
        this.gameMode = gameMode;
        this.highScore = highScore;
    }

    public String getGameMode(){
        return gameMode;
    }

    public int getHighScore(){
        return highScore;
    }
}
