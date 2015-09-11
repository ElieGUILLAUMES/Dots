package com.icelandic_courses.elie.myfirstapp.animation;

import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.util.Position;

/**
 * Created by Endos on 07.09.2015.
 */
public class NoAnimationLogic extends AbstractAnimationLogic {

    private DotsChangeHandler dotsChangeHandler = new DotsChangeHandler() {
        @Override
        public void dotRemoved(LogicDot logicDot) {
            //remove corresponding animation dot
            removeDot(logicDot.getId());
        }

        @Override
        public void dotMoved(LogicDot logicDot, Position<Integer> prevPosition) {
            //do nothing
        }

        @Override
        public void dotCreated(LogicDot logicDot) {
            //create animation dot
            AnimationDot dot = createAnimationDotFromLogicDot(logicDot);

            //set the current position to the desired position --> no animation
            Position<Float> desiredPosition = dot.getDesiredPosition();
            Position<Float> currentPosition = dot.getCurrentPosition();
            currentPosition.setX(desiredPosition.getX());
            currentPosition.setY(desiredPosition.getY());

            //add the dot to the model
            addDot(dot);
        }
    };

    public NoAnimationLogic(ILogic logic, PixelToPitchConverter converter) {
        super(logic, converter);

        logic.registerDotsChangeHandler(dotsChangeHandler);
    }
}
