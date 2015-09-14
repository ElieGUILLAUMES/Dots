package com.icelandic_courses.elie.myfirstapp.animation;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.Collection;
import java.util.List;

/**
 * Created by Endos on 03.09.2015.
 */
public interface IAnimationLogic {

    public final static int FPS = 60;
    public final static int PERIOD = 1000 / FPS;

    public Collection<AnimationDot> getAnimationDots();
    public List<Position<Float>> getAnimatedTracePositions();
    public DotColor getAnimatedTraceColor();

    public void invalidate();
}
