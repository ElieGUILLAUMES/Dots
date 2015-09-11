package com.icelandic_courses.elie.myfirstapp.trace;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.util.Orientation;
import com.icelandic_courses.elie.myfirstapp.util.Position;
import com.icelandic_courses.elie.myfirstapp.util.Segment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Endos on 09.09.2015.
 */
public class TraceChecker {

    /**
     * Check if the trace is legal.
     *
     * In detail it checks:
     * - length >= 2
     * - boundaries inside the pitch
     * - unicolored
     * - manhatten distances == 1
     * - duplicate segments
     *
     * @param trace
     * @throws IllegalArgumentException
     */
    public static void check(Position<Integer>[] trace, ILogic logic) throws IllegalArgumentException {

        // trace has to be at least 2 dots
        if(trace == null || trace.length < 2) {
            String errorMessage = "The trace is not legal, because it has less than 2 dots (no segment).";
            throw new IllegalArgumentException(errorMessage);
        }

        //initial color
        DotColor traceColor = logic.getDot(trace[0]).getColor();
        //previous position to check the manhattan distance and define the segment
        Position<Integer> prevPosition = null;
        //set of segments to find duplicates
        Set<Position<Integer>> segments = new HashSet<Position<Integer>>(trace.length);

        /*
            go through all positions in the trace and check for each:
            - boundaries
            - color
            - manhatten distance to previous neighbor == 1
            - duplicate segments
        */
        for(Position<Integer> position : trace) {

            //check index out of bounds
            if(     position.getX() < 0 ||
                    position.getX() >= logic.getPitchSize() ||
                    position.getY() < 0 ||
                    position.getY() >= logic.getPitchSize()) {
                String errorMessage = "The trace is not legal, because at least one  position "+
                        position.toString() + " is out of bounds.";
                throw new IllegalArgumentException(errorMessage);
            }

            //check color
            if(logic.getDot(position).getColor() != traceColor) {
                String errorMessage = "The trace is not legal, because it's not unicolored!";
                throw new IllegalArgumentException(errorMessage);
            }

            //check neighbor
            if(prevPosition != null) { //no previous neighbor
                if(position.manhattenDistance(prevPosition) != 1) {
                    String errorMessage = "The trace is not legal, because at least one manhatten " +
                            "distance isn't equal to 1";
                    throw new IllegalArgumentException(errorMessage);
                }
            }

            //duplicate segments
            if(prevPosition != null) {
                Segment segment = new Segment(
                        Math.min(prevPosition.getY(), position.getY()),
                        Math.min(prevPosition.getX(), position.getX()),
                        prevPosition.getY() == position.getY() ? Orientation.Horizontal : Orientation.Veritcal);
                if(!segments.add(segment)) {
                    String errorMessage = "The trace is not legal, because at least one segment " +
                            "is duplicate!";
                    throw new IllegalArgumentException(errorMessage);
                }
            }

            prevPosition = position;
        }
    }

}
