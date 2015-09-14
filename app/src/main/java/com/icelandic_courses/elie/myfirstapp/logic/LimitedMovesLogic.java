package com.icelandic_courses.elie.myfirstapp.logic;

import android.os.Vibrator;
import android.util.Log;

import com.icelandic_courses.elie.myfirstapp.score.ScoreChangeHandler;
import com.icelandic_courses.elie.myfirstapp.util.Position;
import com.icelandic_courses.elie.myfirstapp.view.MyActivity;

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
        setRemainingMovesLeft(remainingMoves);
    }

    @Override
    public void traceFinished(Position<Integer>[] trace) throws IllegalArgumentException {

        //if the game is not running, do nothing
        if(getGameState() != GameState.RUNNING) {
            return;
        }

        super.traceFinished(trace);

        //move was successful: decrease moves and check if the game is finished
        if(--remainingMoves <= 0) {
            setRemainingMovesLeft(remainingMoves);
            finish();
        }
        Log.i("Remaining moves", remainingMoves + "");
    }

    public void registerRemainingMoveHandler(RemainingMovesHandler remainingMovesHandler) {
        remainingMovesHandlers.add(remainingMovesHandler);
    }

    public void unregisterRemainingMoveHandler(RemainingMovesHandler remainingMovesHandler) {
        remainingMovesHandlers.remove(remainingMovesHandler);
    }

    public int getRemainingMoves() {
        return remainingMoves;
    }

    public void setRemainingMovesLeft(int remainingMoves){
        for(RemainingMovesHandler remainingMovesHandler : remainingMovesHandlers) {
            remainingMovesHandler.remainingMovesChanged(remainingMoves);
        }
    }

}
