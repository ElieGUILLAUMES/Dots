package com.icelandic_courses.elie.myfirstapp.logic;

import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.List;

/**
 * Created by Endos on 03.09.2015.
 */
public interface ILogic {

    public void start();
    public void traceFinished(Position<Integer>[] trace);
    public void finish();

    public void registerDotsChangeHandler(DotsChangeHandler dotsChangeHandler);
    public void unregisterDotsChangeHandler(DotsChangeHandler dotsChangeHandler);

}
