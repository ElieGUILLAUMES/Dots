package com.icelandic_courses.elie.myfirstapp.animation;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.util.Position;

/**
 * Created by Endos on 03.09.2015.
 */
public class AnimationDot {

    private final int id;
    private final DotColor color;

    private Position<Float> currentPosition = new Position<Float>(0.f, 0.f);
    private Position<Float> desiredPosition = new Position<Float>(0.f, 0.f);

    public AnimationDot(DotColor color, int id) {
        this.color = color;
        this.id = id;
    }

    public Position<Float> getCurrentPosition() {
        return currentPosition;
    }

    public Position<Float> getDesiredPosition() {
        return desiredPosition;
    }

    public int getId() {
        return id;
    }

    public DotColor getColor() {
        return color;
    }

}
