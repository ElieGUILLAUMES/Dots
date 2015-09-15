package com.icelandic_courses.elie.myfirstapp.animation;

import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;

/**
 * Created by Endos on 15.09.2015.
 */
public class BounceAnimationDot extends AnimationDot {

    //segmentSize/s
    private float velocity = 0;

    public BounceAnimationDot(LogicDot logicDot) {
        super(logicDot);
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
}
