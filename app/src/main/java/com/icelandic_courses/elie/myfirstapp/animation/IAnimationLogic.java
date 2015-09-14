package com.icelandic_courses.elie.myfirstapp.animation;

import java.util.Collection;

/**
 * Created by Endos on 03.09.2015.
 */
public interface IAnimationLogic {
    public Collection<AnimationDot> getDots();
    public void invalidate();
}
