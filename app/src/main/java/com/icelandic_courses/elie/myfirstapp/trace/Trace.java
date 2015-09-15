package com.icelandic_courses.elie.myfirstapp.trace;

import android.util.Log;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.util.Position;
import com.icelandic_courses.elie.myfirstapp.util.Segment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Endos on 15.09.2015.
 */
public class Trace {
    private DotColor color;

    private final List<Position<Integer>> positions;
    private final Set<Position<Integer>> positionSet;
    private final Set<Segment> segments;

    private Position<Float> startTrackingPoint;
    private Position<Float> lastTrackingPoint;

    private int numberCircles;
    private int numberAffectedDots;

    public Trace() {

        positions = new ArrayList<>();
        positionSet = new HashSet<>();
        segments = new HashSet<>();

        clear();
    }

    public void clear() {
        positions.clear();
        positionSet.clear();
        segments.clear();

        setColor(null);
        setStartTrackingPoint(null);
        setLastTrackingPoint(null);
        numberCircles = 0;
        numberAffectedDots = 0;
    }

    public void removeLastPosition() {

        //remove position
        if(positions.size() == 0) {
            return;
        }
        Position<Integer> lastPosition = getLastPosition();
        positions.remove(lastPosition);

        //remove segment
        if(positions.size() == 0) {
            return;
        }
        Position<Integer> previousPosition = getLastPosition();
        Segment lastSegment = new Segment(previousPosition, lastPosition);
        segments.remove(lastSegment);
    }

    public void addPoint(Position<Integer> point, DotColor pointColor) {

        //add a segment if there is already a point in the trace
        if(!positions.isEmpty()) {
            Position<Integer> lastPoint = getLastPosition();
            segments.add(new Segment(lastPoint, point));
        }

        //set trace color
        if(color == null) {
            setColor(pointColor);
        }

        //add to trace
        positions.add(point);

        //add point to the set to check for duplicates and circles
        if(!positionSet.add(point)) {
            numberCircles++;
        }
    }

    public DotColor getColor() {
        return color;
    }

    public List<Position<Integer>> getPositions() {
        return positions;
    }

    public Position<Integer> getLastPosition() {
        return positions.get(positions.size() - 1);
    }

    public Set<Segment> getSegments() {
        return segments;
    }

    public Position<Float> getStartTrackingPoint() {
        return startTrackingPoint;
    }

    public Position<Float> getLastTrackingPoint() {
        return lastTrackingPoint;
    }

    public int getNumberCircles() {
        return numberCircles;
    }

    public int getNumberAffectedDots() {
        return numberAffectedDots;
    }

    public void setColor(DotColor color) {
        this.color = color;
    }

    public void setStartTrackingPoint(Position<Float> startTrackingPoint) {
        this.startTrackingPoint = startTrackingPoint;
    }

    public void setLastTrackingPoint(Position<Float> lastTrackingPoint) {
        this.lastTrackingPoint = lastTrackingPoint;
    }

    public void setNumberAffectedDots(int numberAffectedDots) {
        this.numberAffectedDots = numberAffectedDots;
    }
}
