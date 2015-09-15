package com.icelandic_courses.elie.myfirstapp.logic.moves;

import android.util.Log;

import com.icelandic_courses.elie.myfirstapp.logic.AbstractLogic;
import com.icelandic_courses.elie.myfirstapp.logic.GameMode;
import com.icelandic_courses.elie.myfirstapp.logic.GameState;
import com.icelandic_courses.elie.myfirstapp.score.ScoreChangeHandler;
import com.icelandic_courses.elie.myfirstapp.trace.Trace;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Endos on 09.09.2015.
 */
public class LimitedMovesLogic extends AbstractLogic {

    private final int totalMoves;
    private int remainingMoves;

    private final Collection<RemainingMovesHandler> remainingMovesHandlers;
    private final Collection<ScoreChangeHandler> scoreChangeHandlers;

    public LimitedMovesLogic(int moves, int pitchSize, int numberDotColors) {
        super(pitchSize, numberDotColors);
        totalMoves = moves;
        remainingMoves = totalMoves;
        remainingMovesHandlers = new ArrayList<RemainingMovesHandler>();
        scoreChangeHandlers = new ArrayList<ScoreChangeHandler>();
    }

    @Override
    public void traceFinished(Trace trace) throws IllegalArgumentException {

        //if the game is not running, do nothing
        if(getGameState() != GameState.RUNNING) {
            return;
        }

        super.traceFinished(trace);

        //move was successful: decrease moves
        remainingMoves--;

        //notify handlers
        notifyRemainingMovesChanged();

        //finish the game if there is no move left
        if(remainingMoves <= 0) {
            finish();
        }
    }

    private void notifyRemainingMovesChanged() {
        for(RemainingMovesHandler remainingMovesHandler : remainingMovesHandlers) {
            remainingMovesHandler.remainingMovesChanged(remainingMoves);
        }
    }

    public void registerRemainingMoveHandler(RemainingMovesHandler remainingMovesHandler) {
        remainingMovesHandlers.add(remainingMovesHandler);
    }

    public void unregisterRemainingMoveHandler(RemainingMovesHandler remainingMovesHandler) {
        remainingMovesHandlers.remove(remainingMovesHandler);
    }

    @Override
    public String getMode() {
        return GameMode.MOVES.toString();
    }

}
