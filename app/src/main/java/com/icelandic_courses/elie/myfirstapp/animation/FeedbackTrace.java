package com.icelandic_courses.elie.myfirstapp.animation;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Endos on 15.09.2015.
 */
public class FeedbackTrace {

    private DotColor color;
    private final List<Position<Float>> positions;
    private Position<Float> lastTrackingPoint;
    public FeedbackTrace() {
        color = null;
        positions = new ArrayList<>();
    }

    public DotColor getColor() {
        return color;
    }

    public List<Position<Float>> getPositions() {
        return positions;
    }

    public Position<Float> getLastTrackingPoint() {
        return lastTrackingPoint;
    }

    public void setColor(DotColor color) {
        this.color = color;
    }

    public void setLastTrackingPoint(Position<Float> lastTrackingPoint) {
        this.lastTrackingPoint = lastTrackingPoint;
    }
}
