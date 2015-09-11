package com.icelandic_courses.elie.myfirstapp.logic;

import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.List;

/**
 * Created by Endos on 03.09.2015.
 */
public interface ILogic {

    //game flow
    public void start();
    public void traceFinished(Position<Integer>[] trace) throws IllegalArgumentException;
    public void finish();

    //Getter
    public LogicDot getDot(Position<Integer> position);
    public int getPitchSize();

    //Handler
    public void registerDotsChangeHandler(DotsChangeHandler dotsChangeHandler);
    public void unregisterDotsChangeHandler(DotsChangeHandler dotsChangeHandler);

    public void registerGameStateChangeHandler(GameStateChangeHandler gameStateChangeHandler);
    public void unregisterGameStateChangeHandler(GameStateChangeHandler gameStateChangeHandler);

}
