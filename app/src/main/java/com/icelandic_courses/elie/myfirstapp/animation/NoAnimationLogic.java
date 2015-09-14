package com.icelandic_courses.elie.myfirstapp.animation;

import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;
import com.icelandic_courses.elie.myfirstapp.trace.TrackingHandler;
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
            //get corresponding animation dot
            AnimationDot animationDot = getAnimationDot(logicDot.getId());

            //update desired position
            updateDesiredPosition(logicDot);

            //update current position
            updateCurrentPosition(animationDot);
        }

        @Override
        public void dotCreated(LogicDot logicDot) {
            //create animation dot
            AnimationDot animationDot = createAnimationDotFromLogicDot(logicDot);

            //update current position
            updateCurrentPosition(animationDot);

            //add the dot to the model
            addDot(animationDot);
        }

        private void updateCurrentPosition(AnimationDot dot) {
            //set the current position to the desired position --> no animation
            Position<Float> desiredPosition = dot.getDesiredPosition();
            Position<Float> currentPosition = dot.getCurrentPosition();
            currentPosition.setX(desiredPosition.getX());
            currentPosition.setY(desiredPosition.getY());
        }
    };

    public NoAnimationLogic(ILogic logic, PixelToPitchConverter converter, TrackingHandler trackingHandler) {
        super(logic, converter, trackingHandler);

        logic.registerDotsChangeHandler(dotsChangeHandler);
    }

    @Override
    public DotsChangeHandler getDotsChangeHandler() {
        return dotsChangeHandler;
    }
}
