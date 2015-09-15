package com.icelandic_courses.elie.myfirstapp.trace;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.List;

/**
 * Created by Endos on 13.09.2015.
 */
public interface TraceChangeHandler {

    public void onTraceChanged(Trace trace);
    public void onLastTrackingPointChanged(Position<Float> lastTrackingPoint);

}
