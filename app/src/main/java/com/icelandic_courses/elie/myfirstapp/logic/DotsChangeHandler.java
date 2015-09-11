package com.icelandic_courses.elie.myfirstapp.logic;

import com.icelandic_courses.elie.myfirstapp.util.Position;

/**
 * Created by Endos on 03.09.2015.
 */
public interface DotsChangeHandler {

    public void dotRemoved(LogicDot logicDot);
    public void dotMoved(LogicDot logicDot, Position<Integer> prevPosition);
    public void dotCreated(LogicDot logicDot);
}
