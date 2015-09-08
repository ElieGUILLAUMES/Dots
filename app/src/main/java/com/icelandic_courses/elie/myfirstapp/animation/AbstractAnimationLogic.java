package com.icelandic_courses.elie.myfirstapp.animation;

import android.util.Log;

import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;
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

    private Collection<AnimationDot> dotsList = new ArrayList<AnimationDot>();
    private Map<Integer, AnimationDot> dotsMap = new HashMap<Integer, AnimationDot>();

    public AbstractAnimationLogic(ILogic logic) {
        this.logic = logic;
    }

    protected AnimationDot createAnimationDotFromLogicDot(LogicDot logicDot) {
        AnimationDot animationDot = new AnimationDot(logicDot.getColor(), logicDot.getId());

        //TODO calc current and desired positions
        animationDot.getDesiredPosition().setX((float) logicDot.getPosition().getX());
        animationDot.getDesiredPosition().setY((float) logicDot.getPosition().getY());

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
