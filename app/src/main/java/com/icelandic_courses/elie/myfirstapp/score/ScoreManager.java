package com.icelandic_courses.elie.myfirstapp.score;

import android.content.SharedPreferences;
import android.util.Log;

import com.icelandic_courses.elie.myfirstapp.logic.Difficulty;
import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.GameMode;
import com.icelandic_courses.elie.myfirstapp.logic.GameState;
import com.icelandic_courses.elie.myfirstapp.logic.GameStateChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;
import com.icelandic_courses.elie.myfirstapp.logic.TraceFinishHandler;
import com.icelandic_courses.elie.myfirstapp.trace.Trace;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Endos on 08.09.2015.
 */
public class ScoreManager {

    private final ILogic logic;
    private final Collection<ScoreChangeHandler> scoreChangeHandlers;
    private final SharedPreferences preferences;

    private AtomicInteger score = new AtomicInteger(0);


    public ScoreManager(ILogic logic, final SharedPreferences preferences) {
        this.logic = logic;
        this.preferences = preferences;
        scoreChangeHandlers = new ArrayList<ScoreChangeHandler>();

        //add trace finish handler
        logic.registerTraceFinishHandler(new TraceFinishHandler() {
            @Override
            public void onTraceFinished(Trace trace) {
                addTrace(trace);
            }
        });

        //game state changed
        logic.registerGameStateChangeHandler(new GameStateChangeHandler() {
            @Override
            public void gameStateChanged(GameState gameState, ILogic logic) {
                if(gameState == GameState.FINISHED) {
                    GameMode gameMode = logic.getMode();
                    Difficulty difficulty = Difficulty.get(preferences);
                    HighScoreManager.add(score.get(), preferences, gameMode, difficulty);

                    Log.i("HighScores", HighScoreManager.getList(preferences, gameMode, difficulty).toString());
                }
            }
        });
    }

    private void addTrace(Trace trace) {
        int additionalScore = getAdditionalScore(trace);
        int total = score.addAndGet(additionalScore);
        notifyScoreChangeHandlers(total, additionalScore);
    }

    private void notifyScoreChangeHandlers(int total, int additional) {
        for(ScoreChangeHandler scoreChangeHandler : scoreChangeHandlers) {
            scoreChangeHandler.scoreChanged(total, additional);
        }
    }

    public int getScore() {
        return score.get();
    }

    public void registerScoreChangeHandler(ScoreChangeHandler scoreChangeHandler) {
        scoreChangeHandlers.add(scoreChangeHandler);
    }

    public void unregisterScoreChangeHandler(ScoreChangeHandler scoreChangeHandler) {
        scoreChangeHandlers.remove(scoreChangeHandler);
    }

    public static int getAdditionalScore(Trace trace) {
        int traceLength = trace.getPositions().size();
        int additionalScore = traceLength * (1 + trace.getNumberCircles() / 2);
        return additionalScore;
    }
}
