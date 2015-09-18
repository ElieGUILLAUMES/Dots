package com.icelandic_courses.elie.myfirstapp.trace;

import android.util.Log;
import android.view.MotionEvent;

import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.util.Position;
import com.icelandic_courses.elie.myfirstapp.util.Segment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Endos on 09.09.2015.
 */
public class TraceHandler {

    private final static double MIN_DISTANCE_FACTOR = 0.5; // has to be between 0.0 and 0.5

    private final ILogic logic;
    private final PixelToPitchConverter converter;
    private final Trace trace;

    private final Collection<TraceChangeHandler> traceChangeHandlers;

    public TraceHandler(ILogic logic, PixelToPitchConverter converter) {
        this.logic = logic;
        this.converter = converter;
        trace = new Trace();
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
        trace.setStartTrackingPoint(trackingPoint);
    }

    protected void track(Position<Float> trackingPoint) {
        //initialize the start of the segment
        Position<Integer> segmentPitchStart;
        Position<Float> segmentStart;
        if(trace.getPositions().isEmpty()) {
            //use the first tracking point as the start of the segment
            Position<Float> start = trace.getStartTrackingPoint();
            segmentPitchStart = converter.transformPixelToPitch(start);
            segmentStart = start;
        }
        else {
            //use the last trace point as the start of the segment
            segmentPitchStart = trace.getLastPosition();
            segmentStart = converter.transformPitchToPixel(segmentPitchStart);
        }

        //list of potential starting points, including the nearest point
        List<Position<Integer>> potentialStartingPoints = getPotentialNeighbors(segmentPitchStart);

        //if the trace is empty, you add the segmentPitchStart to the potential points at the beginning
        if(trace.getPositions().isEmpty()) {
            potentialStartingPoints.add(0, segmentPitchStart);
        }

        //find starting point and other matching points
        captureNextTracePoints(segmentPitchStart, potentialStartingPoints, segmentStart, trackingPoint);

        //set last tracking point
        trace.setLastTrackingPoint(trackingPoint);

        //notify TraceChangeHandler
        notifyLastTrackingPointChanged();
    }

    protected void captureNextTracePoints(
            Position<Integer> referencePoint,
            Collection<Position<Integer>> potentialPoints,
            Position<Float> segmentStart,
            Position<Float> segmentEnd) {

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
            if(trace.getColor() != null && trace.getColor() != logic.getDot(potentialPoint).getColor()) {
                //the dot at the potential position has another color, this point is not usable
                continue;
            }

            Position<Float> pixelPotentialPoint = converter.transformPitchToPixel(potentialPoint);
            double closestDistanceToSegment = pixelPotentialPoint.distanceToSegment(segmentStart, segmentEnd);
            if(closestDistanceToSegment <= minDistance) {

                //check for segment duplicates
                Segment potentialSegment = new Segment(referencePoint, potentialPoint);
                if(trace.getSegments().contains(potentialSegment)) {

                    //check, if the user wants to go back
                    Position<Integer> lastTracePoint = trace.getLastPosition();
                    if(referencePoint.equals(lastTracePoint)) {
                        //go back: remove latest point
                        removeLastTracePoint();
                        break;
                    }
                    else {
                        //the segment is already in use, this point is not usable
                        break;
                    }
                }

                //found the next trace point and update the next potential points
                addTracePoint(potentialPoint);
                Collection<Position<Integer>> nextPotentialPoints = getPotentialNeighbors(potentialPoint);
                nextPotentialPoints.remove(referencePoint);
                captureNextTracePoints(potentialPoint, nextPotentialPoints, segmentStart, segmentEnd);
                break;
            }
        }
    }

    private void removeLastTracePoint() {
        //remove it
        trace.removeLastPosition();

        //notify handlers
        notifyTraceChanged();
    }

    private void addTracePoint(Position<Integer> point) {

        //add point to trace
        trace.addPoint(point, logic.getDot(point).getColor());

        //notify listeners
        notifyTraceChanged();
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
        //use the last point as tracking point, too
        track(trackingPoint);

        //check the trace for at least 2 dots
        if(trace.getPositions().size() >= 2) {
            //pass the trace to the logic component
            logic.traceFinished(trace);
        }

        //restart and clear the trace
        restartTracking();
    }

    protected void restartTracking() {

        //clear the trace
        trace.clear();

        //notify handler
        notifyTraceChanged();
    }

    public Trace getTrace() {
        return trace;
    }


    public void notifyTraceChanged() {
        for(TraceChangeHandler traceChangeHandler : traceChangeHandlers) {
            traceChangeHandler.onTraceChanged(trace);
        }
    }

    public void notifyLastTrackingPointChanged() {
        for(TraceChangeHandler traceChangeHandler : traceChangeHandlers) {
            traceChangeHandler.onLastTrackingPointChanged(trace.getLastTrackingPoint());
        }
    }

    public void registerTraceChangeHandler(TraceChangeHandler traceChangeHandler) {
        traceChangeHandlers.add(traceChangeHandler);
    }

    public void unregisterTraceChangeHandler(TraceChangeHandler traceChangeHandler) {
        traceChangeHandlers.remove(traceChangeHandler);
    }
}
