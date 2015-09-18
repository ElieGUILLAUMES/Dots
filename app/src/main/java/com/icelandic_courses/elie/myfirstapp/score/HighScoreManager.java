package com.icelandic_courses.elie.myfirstapp.score;

import android.content.SharedPreferences;

import com.icelandic_courses.elie.myfirstapp.logic.Difficulty;
import com.icelandic_courses.elie.myfirstapp.logic.GameMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Endos on 18.09.2015.
 */
public class HighScoreManager {

    public static int getHighScore(SharedPreferences preferences, GameMode gameMode, Difficulty difficulty) {
        List<Integer> scoreList = getList(preferences, gameMode, difficulty);

        if(scoreList == null || scoreList.size() == 0) {
            return 0;
        }

        return scoreList.get(0);
    }

    public static List<Integer> getList(SharedPreferences preferences, GameMode gameMode, Difficulty difficulty) {

        //string to int list
        String key = generateKey(gameMode, difficulty);
        StringTokenizer tokenizer = new StringTokenizer(preferences.getString(key, ""), ",");
        List<Integer> scoreList = new ArrayList<>(10);
        for (int i = 0; i < 10 && tokenizer.hasMoreElements(); i++) {
            scoreList.add(Integer.parseInt(tokenizer.nextToken()));
        }
        return scoreList;
    }

    static void add(int score, SharedPreferences preferences, GameMode gameMode, Difficulty difficulty) {

        //get list of scores and add score to list
        List<Integer> scoreList = getList(preferences, gameMode, difficulty);
        scoreList.add(score);

        //sort the list
        Collections.sort(scoreList, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs == rhs ? 0 : (lhs > rhs ? -1 : 1);
            }
        });

        //store in prefs
        storeScores(scoreList, preferences, gameMode, difficulty);
    }

    private static void storeScores(List<Integer> scoreList, SharedPreferences preferences, GameMode gameMode, Difficulty difficulty) {
        //int array to string
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < scoreList.size() && i < 10; i++) {
            str.append(scoreList.get(i)).append(",");
        }

        //put string
        String key = generateKey(gameMode, difficulty);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, str.toString());
        editor.commit();
    }

    private static String generateKey(GameMode gameMode, Difficulty difficulty) {
        return "highscore" + gameMode.toString() + difficulty.toString();
    }

    public static void resetHighScore(SharedPreferences preferences, GameMode gameMode, Difficulty difficulty) {
        String key = generateKey(gameMode, difficulty);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, "");
        editor.commit();
    }
}
