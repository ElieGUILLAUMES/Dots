package com.icelandic_courses.elie.myfirstapp.score;

import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Endos on 08.09.2015.
 */
public class ScoreManager {

    private final Collection<ScoreChangeHandler> scoreChangeHandlers;
    private AtomicInteger score = new AtomicInteger(0);

    public ScoreManager() {
        scoreChangeHandlers = new ArrayList<ScoreChangeHandler>();
    }

    public void addTrace(Position<Integer>[] trace) {
        int additionalScore = trace.length;
        int total = score.addAndGet(additionalScore);
        notifyScoreChangeHandlers(total, additionalScore);
        ScoreManager.addScore(additionalScore);
    }

    private void notifyScoreChangeHandlers(int total, int additional) {
        for(ScoreChangeHandler scoreChangeHandler : scoreChangeHandlers) {
            scoreChangeHandler.scoreChanged(total, additional);
        }
    }

    public void registerScoreChangeHandler(ScoreChangeHandler scoreChangeHandler) {
        scoreChangeHandlers.add(scoreChangeHandler);
    }

    public void unregisterScoreChangeHandler(ScoreChangeHandler scoreChangeHandler) {
        scoreChangeHandlers.remove(scoreChangeHandler);
    }

    private static int totalScore = 0;

    private static void addScore(int score) {
        totalScore += score;
    }
}
