package com.icelandic_courses.elie.myfirstapp.trace;

import android.os.Trace;
import android.util.Log;
import android.view.MotionEvent;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.util.Orientation;
import com.icelandic_courses.elie.myfirstapp.util.Position;
import com.icelandic_courses.elie.myfirstapp.util.Segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Endos on 09.09.2015.
 */
public class TrackingHandler {

    private final static double MIN_DISTANCE_FACTOR = 0.45; // has to be between 0.0 and 0.5

    private final ILogic logic;
    private final PixelToPitchConverter converter;
    private final Collection<TraceChangeHandler> traceChangeHandlers;

    private DotColor traceColor = null;
    private List<Position<Integer>> trace;
    private Set<Segment> segments;
    private Position<Float> startTrackingPoint = null;

    public TrackingHandler(ILogic logic, PixelToPitchConverter converter) {
        this.logic = logic;
        this.converter = converter;
        trace = new ArrayList<>();
        segments = new HashSet<>();
        traceChangeHandlers = new ArrayList<>();
    }

    public synchronized boolean onTouchEvent(MotionEvent event) {

        Position<Float> position = new Position<Float>(event.getY(), event.getX());

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startTracking(position);
                break;

            case MotionEvent.ACTION_MOVE:
                track(position);
                break;

            case MotionEvent.ACTION_UP:
                finishTracking(position);
                break;

            case MotionEvent.ACTION_CANCEL:
                restartTracking();
                break;
        }

        return true;
    }

    protected void startTracking(Position<Float> trackingPoint) {
        Log.i("StartTrack", trackingPoint.toString());

        //set the starting point
        startTrackingPoint = trackingPoint;
    }

    protected void track(Position<Float> trackingPoint) {
        //Log.i("track", trackingPoint.toString());
        //initialize the start of the segment
        Position<Integer> segmentPitchStart;
        Position<Float> segmentStart;
        if(trace.isEmpty()) {
            Log.i("track", "really looking for the first pos");
            segmentPitchStart = converter.transformPixelToPitch(startTrackingPoint);
            segmentStart = startTrackingPoint;
        }
        else {
            segmentPitchStart = trace.get(trace.size() - 1);
            segmentStart = converter.transformPitchToPixel(segmentPitchStart);
        }


        //list of potential starting points, including the nearest point
        List<Position<Integer>> potentialStartingPoints = getPotentialNeighbors(segmentPitchStart);

        //if the trace is empty, you add the segmentPitchStart to the potential points at the beginning
        if(trace.isEmpty()) {
            potentialStartingPoints.add(0, segmentPitchStart);
        }

        //find starting point and other matching points
        captureNextTracePoints(segmentPitchStart, potentialStartingPoints, segmentStart, trackingPoint);
    }

    protected void captureNextTracePoints(
            Position<Integer> referencePoint,
            Collection<Position<Integer>> potentialPoints,
            Position<Float> segmentStart,
            Position<Float> segmentEnd) {
        //Log.i("captureNextTracePoints", referencePoint.toString());
        //min distance
        double minDistance = MIN_DISTANCE_FACTOR * converter.getDescription().getSegmentSize();

        //check every potential point
        for(Position<Integer> potentialPoint : potentialPoints) {

            //check boundaries
            if(     potentialPoint.getY() < 0 ||
                    potentialPoint.getY() >= logic.getPitchSize() ||
                    potentialPoint.getX() < 0 ||
                    potentialPoint.getX() >= logic.getPitchSize()) {
                continue;
            }

            //check color
            if(traceColor != null && traceColor != logic.getDot(potentialPoint).getColor()) {
                //the dot at the potential position has another color, this point is not usable
                continue;
            }

            //check for segment duplicates
            if(segments.contains(new Segment(referencePoint, potentialPoint))) {
                //the segment is already in use, this point is not usable
                continue;
            }

            Position<Float> pixelPotentialPoint = converter.transformPitchToPixel(potentialPoint);
            double closestDistanceToSegment = pixelPotentialPoint.distanceToSegment(segmentStart, segmentEnd);
            if(closestDistanceToSegment <= minDistance) {
                //found the next trace point and update the next potential points
                addTracePoint(potentialPoint);
                potentialPoints = getPotentialNeighbors(potentialPoint);
                captureNextTracePoints(potentialPoint, potentialPoints, segmentStart, segmentEnd);
                break;
            }
        }
    }

    private void addTracePoint(Position<Integer> point) {
        Log.i("addTracePoint", point.toString());
        //add a segment if there is already a point in the trace
        if(!trace.isEmpty()) {
            Position<Integer> lastPoint = trace.get(trace.size() - 1);
            segments.add(new Segment(lastPoint, point));
        }

        //set trace color
        if(traceColor == null) {
            traceColor = logic.getDot(point).getColor();
        }

        //add to trace
        trace.add(point);

        Log.i("trace", trace.toString());

        //notify listeners
        notifyTraceChangeHandlers();
    }

    private List<Position<Integer>> getPotentialNeighbors(final Position<Integer> referencePoint) {
        return new ArrayList<Position<Integer>>(4) {{
            add(referencePoint.add(1,0));
            add(referencePoint.add(0,1));
            add(referencePoint.add(-1,0));
            add(referencePoint.add(0,-1));
        }};
    }

    protected void finishTracking(Position<Float> trackingPoint)
    {
        Log.i("finishTracking", trackingPoint.toString());
        //use the last point as tracking point, too
        track(trackingPoint);

        //convert list to array and pass it to the logic component
        Position<Integer>[] traceArray = (Position<Integer>[]) trace.toArray(new Position[]{});
        try{
            logic.traceFinished(traceArray);
        }catch(IllegalArgumentException e){
            Log.e("TrackingHandler", e.getMessage());
        }


        //restart and clear the trace
        restartTracking();
    }

    protected void restartTracking() {

        //clear the trace
        trace.clear();

        //clear trace color
        traceColor = null;

        //clear segments
        segments.clear();

        //clear prev tracking point
        startTrackingPoint = null;
    }

    public List<Position<Integer>> getTrace() {
        return trace;
    }


    public void notifyTraceChangeHandlers() {
        for(TraceChangeHandler traceChangeHandler : traceChangeHandlers) {
            traceChangeHandler.onTraceChange(trace);
        }
    }

    public void registerTraceChangeHandler(TraceChangeHandler traceChangeHandler) {
        traceChangeHandlers.add(traceChangeHandler);
    }

    public void unregisterTraceChangeHandler(TraceChangeHandler traceChangeHandler) {
        traceChangeHandlers.remove(traceChangeHandler);
    }
}
