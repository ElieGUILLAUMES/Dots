package com.icelandic_courses.elie.myfirstapp.animation;

import android.util.Log;

import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Endos on 03.09.2015.
 */
public abstract class AbstractAnimationLogic implements IAnimationLogic {

    protected ILogic logic;
    protected PixelToPitchConverter converter;

    private Collection<AnimationDot> dotsList = new ArrayList<AnimationDot>();
    private Map<Integer, AnimationDot> dotsMap = new HashMap<Integer, AnimationDot>();

    public AbstractAnimationLogic(ILogic logic, PixelToPitchConverter converter) {
        this.logic = logic;
        this.converter = converter;
    }

    /**
     * Create an animation dot from a logic dot.
     * The desired position is the transformed pitch position.
     * The current position is not set within this method.
     * @param logicDot representing logic dot
     * @return animation dot with id, color and desired position
     */
    protected AnimationDot createAnimationDotFromLogicDot(LogicDot logicDot) {
        AnimationDot animationDot = new AnimationDot(logicDot.getColor(), logicDot.getId());

        //transform pitch to pixels
        Position<Float> pixelPosition = converter.transformPitchToPixel(logicDot.getPosition());

        //set the position as the desired position
        animationDot.getDesiredPosition().setY(pixelPosition.getY());
        animationDot.getDesiredPosition().setX(pixelPosition.getX());

        return animationDot;
    }

    protected void addDot(AnimationDot dot) {
        dotsList.add(dot);
        dotsMap.put(dot.getId(), dot);
    }

    protected void removeDot(int id) {
        removeDot(dotsMap.get(id));
    }

    protected void removeDot(AnimationDot dot) {
        dotsList.remove(dot);
        dotsMap.remove(dot.getId());
    }

    protected AnimationDot getDot(int id) {
        return dotsMap.get(id);
    }

    public Collection<AnimationDot> getDots() {
        return dotsList;
    }
}
