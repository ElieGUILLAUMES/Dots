package com.icelandic_courses.elie.myfirstapp.trace;

import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.List;

/**
 * Created by Endos on 13.09.2015.
 */
public interface TraceChangeHandler {

    public void onTraceChange(List<Position<Integer>> trace);
}
