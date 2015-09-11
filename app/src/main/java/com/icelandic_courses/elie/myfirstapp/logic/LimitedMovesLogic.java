package com.icelandic_courses.elie.myfirstapp.logic;

import android.util.Log;

import com.icelandic_courses.elie.myfirstapp.util.Position;

/**
 * Created by Endos on 09.09.2015.
 */
public class LimitedMovesLogic extends AbstractLogic {

    private final int totalMoves;
    private int remainingMoves;

    public LimitedMovesLogic(int moves, int pitchSize, int numberDotColors) {
        super(pitchSize, numberDotColors);

        totalMoves = moves;
        remainingMoves = totalMoves;
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
            finish();
        }
        Log.i("Remaining moves", remainingMoves+"");
    }
}
