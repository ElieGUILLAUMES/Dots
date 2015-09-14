package com.icelandic_courses.elie.myfirstapp.logic;

import android.util.Log;

import com.icelandic_courses.elie.myfirstapp.score.ScoreManager;
import com.icelandic_courses.elie.myfirstapp.trace.TraceChecker;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Endos on 03.09.2015.
 */
public abstract class AbstractLogic implements ILogic {

    private final Collection<DotsChangeHandler> dotsChangeHandlers;
    private final Collection<GameStateChangeHandler> gameStateChangeHandlers;


    private final ScoreManager scoreManager;
    private GameState gameState = GameState.NOT_STARTED;
    private final int pitchSize;
    private final int numberDotColors;
    private LogicDot pitch[][];

    /**
     * Abstract class for different implementations of game types.
     * @param pitchSize size of the pitch in horizontal and vertical orientation
     * @param numberDotColors number of different colors a dot can have
     */
    public AbstractLogic(int pitchSize, int numberDotColors) {
        this.pitchSize = pitchSize;
        this.numberDotColors = numberDotColors;
        pitch = new LogicDot[pitchSize][pitchSize];
        scoreManager = new ScoreManager();
        dotsChangeHandlers = new ArrayList<DotsChangeHandler>();
        gameStateChangeHandlers = new ArrayList<GameStateChangeHandler>();

    }

    @Override
    public void start() {
        //create dots
        for(int column = 0; column < pitchSize; column++) {
            createDots(column);
        }

        //change game state
        setGameState(GameState.RUNNING);
    }

    @Override
    public synchronized void traceFinished(Position<Integer>[] trace) throws IllegalArgumentException {
        if (gameState != GameState.RUNNING) {
            return; //do nothing, if the game is not running
        }

        TraceChecker.check(trace, this);
        int[] affectedColumns = removeDots(trace);
        shiftDown(affectedColumns);
        createDots(affectedColumns);
        scoreManager.addTrace(trace);

        Log.i("Pitch", toString());
    }

    @Override
    public void finish() {
        setGameState(GameState.FINISHED);
    }

    /**
     * Remove all dots in the trace from the pitch
     * @param trace
     * @return affected columns to be able to accelerate
     */
    protected int[] removeDots(Position<Integer>[] trace) {
        Set<Integer> affectedColumnsSet = new HashSet<Integer>();

        for(Position<Integer> dot : trace) {
            removeDot(dot);
            affectedColumnsSet.add(dot.getX());
        }

        //convert Integer set to int array and return it
        int[] affectedColumns = new int[affectedColumnsSet.size()];
        int i = 0;
        for(Integer column : affectedColumnsSet) {
            affectedColumns[i++] = column;
        }

        return affectedColumns;
    }

    /**
     * Remove one dot from the pitch and notify
     * @param position
     */
    protected void removeDot(Position<Integer> position) {
        //cache dot to notify listeners
        LogicDot dot = getDot(position);

        //dot could be null, if the trace contain it twice
        if(dot == null)
            return;

        //set dot to null
        setDot(null, position);

        //notify handlers
        for(DotsChangeHandler dotsChangeHandler : dotsChangeHandlers) {
            dotsChangeHandler.dotRemoved(dot);
        }
    }

    /**
     * Shift down all dots as far as possible for all the given columns
     * @param affectedColumns
     */
    protected void shiftDown(int[] affectedColumns) {
        for(int column : affectedColumns) {
            shiftDownColumn(column);
        }
    }

    /**
     * Shift down all dots as far as possible in the given column
     * @param column
     */
    protected void shiftDownColumn(int column) {
        for(int row = pitchSize-1; row >= 0; row--) {

            LogicDot dot = getDot(row, column);
            if(dot != null) {
                //calculate row with the farest shift down
                int shiftToRow = row;
                while(shiftToRow+1 < pitchSize && getDot(shiftToRow+1, column) == null) {
                    shiftToRow++;
                }

                //do the shift
                moveDownDot(dot, shiftToRow);
            }
        }
    }

    /**
     * Moves down one dot to the given row
     * @param dot
     * @param row move to this row
     */
    protected void moveDownDot(LogicDot dot, int row) {
        Position<Integer> position = dot.getPosition();
        Position<Integer> prevPosition = position.clone();

        //remove old dot
        setDot(null, position);

        //set new dot with updated position
        position.setY(row);
        setDot(dot, position);

        //nofity handlers
        for(DotsChangeHandler dotsChangeHandler : dotsChangeHandlers) {
            dotsChangeHandler.dotMoved(dot, prevPosition);
        }
    }

    /**
     * Create as many dots as needed for all given columns
     * @param affectedColumns
     */
    protected void createDots(int[] affectedColumns) {
        for(int column : affectedColumns) {
            createDots(column);
        }
    }

    /**
     * Create as many dots as needed for the given column
     * @param column
     */
    protected void createDots(int column) {
        int row = 0;
        while(row < pitchSize && getDot(row, column) == null) {
            createDot(row, column);
            row++;
        }
    }

    /**
     * Create a dot, set it to the pitch and notify handlers
     * @param row
     * @param column
     */
    protected void createDot(int row, int column) {
        LogicDot dot = createRandomLogicDot(row, column);
        setDot(dot, dot.getPosition());

        //nofity handlers
        for(DotsChangeHandler dotsChangeHandler : dotsChangeHandlers) {
            dotsChangeHandler.dotCreated(dot);
        }
    }

    /**
     * Creates a random LogicDot at the given position
     * @param row row of the pitch
     * @param column column of the pitch
     * @return
     */
    protected LogicDot createRandomLogicDot(int row, int column) {
        DotColor color = DotColor.randomColor(numberDotColors);
        Position<Integer> position = new Position<>(row, column);

        return new LogicDot(color, position);
    }

    /**
     * Check if there is no move left, and shuffle the points in this situation,
     * until there is a possible move.
     */
    protected void checkAndShuffle() {
        //TODO
    }

    public void registerDotsChangeHandler(DotsChangeHandler dotsChangeHandler) {
        dotsChangeHandlers.add(dotsChangeHandler);
    }

    public void unregisterDotsChangeHandler(DotsChangeHandler dotsChangeHandler) {
        dotsChangeHandlers.remove(dotsChangeHandler);
    }

    public void registerGameStateChangeHandler(GameStateChangeHandler gameStateChangeHandler) {
        gameStateChangeHandlers.add(gameStateChangeHandler);
    }

    public void unregisterGameStateChangeHandler(GameStateChangeHandler gameStateChangeHandler) {
        gameStateChangeHandlers.remove(gameStateChangeHandler);
    }


    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        GameState oldState = this.gameState;
        this.gameState = gameState;

        //notify listeners of changes
        if(oldState != gameState) {
            for(GameStateChangeHandler gameStateChangeHandler : gameStateChangeHandlers) {
                gameStateChangeHandler.gameStateChanged(gameState);
            }
        }
    }

    public int getPitchSize() {
        return pitchSize;
    }

    protected LogicDot getDot(int row, int column) {
        return pitch[row][column];
    }

    public LogicDot getDot(Position<Integer> position) {
        return getDot(position.getY(), position.getX());
    }

    protected void setDot(LogicDot dot, int row, int column) {
        pitch[row][column] = dot;
    }

    protected void setDot(LogicDot dot, Position<Integer> position) {
        setDot(dot, position.getY(), position.getX());
    }

    public String toString() {
        String str = "Pitch: \n";

        for(int row = 0; row<pitchSize; row++) {
            str += "\n";
            for(int column = 0; column<pitchSize; column++) {
                LogicDot dot = getDot(row, column);
                str += dot==null? "_" : dot.getColor().toString().charAt(0);
            }
        }

        return str;
    }
}
