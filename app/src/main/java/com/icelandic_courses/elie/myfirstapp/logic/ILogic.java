package com.icelandic_courses.elie.myfirstapp.logic;

import com.icelandic_courses.elie.myfirstapp.util.Position;
import com.icelandic_courses.elie.myfirstapp.trace.Trace;

/**
 * Created by Endos on 03.09.2015.
 */
public interface ILogic {

    //mode
    public String getMode();

    //game flow
    public void start();
    public void traceFinished(Trace trace) throws IllegalArgumentException;
    public void finish();

    //Getter
    public LogicDot getDot(Position<Integer> position);
    public int getPitchSize();

    //Handler
    public void registerDotsChangeHandler(DotsChangeHandler dotsChangeHandler);
    public void unregisterDotsChangeHandler(DotsChangeHandler dotsChangeHandler);

    public void registerGameStateChangeHandler(GameStateChangeHandler gameStateChangeHandler);
    public void unregisterGameStateChangeHandler(GameStateChangeHandler gameStateChangeHandler);

    public void registerTraceFinishHandler(TraceFinishHandler traceFinishHandler);
    public void unregisterTraceFinishHandler(TraceFinishHandler traceFinishHandler);

}
