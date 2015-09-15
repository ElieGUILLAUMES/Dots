package com.icelandic_courses.elie.myfirstapp.animation;

/**
 * Created by Endos on 14.09.2015.
 */

import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;
import com.icelandic_courses.elie.myfirstapp.trace.TraceHandler;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Constant velocity animation
 * Created by Endos on 07.09.2015.
 */
public class LinearAnimationLogic extends AbstractAnimationLogic {

    private final Timer timer;

    //velocity in unit pixelSize/s
    private float velocity = 1.2f;

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
        }

        @Override
        public void dotCreated(LogicDot logicDot) {
            //create animation dot
            AnimationDot animationDot = createAnimationDotFromLogicDot(logicDot);

            //update current position
            createNewCurrentPosition(animationDot);

            //add the dot to the model
            addDot(animationDot);
        }

        private void createNewCurrentPosition(AnimationDot dot) {
            //set the dot to the top of the column
            float desiredX = dot.getDesiredPosition().getX();
            dot.getCurrentPosition().setX(desiredX);
            float segmentSize = converter.getDescription().getSegmentSize();
            float padding = converter.getDescription().getPadding();
            dot.getCurrentPosition().setY(- segmentSize / 2 + padding);
        }
    };

    public LinearAnimationLogic(ILogic logic, PixelToPitchConverter converter, TraceHandler traceHandler) {
        super(logic, converter, traceHandler);

        //register dot change handler
        logic.registerDotsChangeHandler(dotsChangeHandler);

        //start animation timer
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                animateDots();
            }
        }, 0, PERIOD);
    }

    private void animateDots() {
        //copy dots to prevent concurrent modification exception
        Collection<AnimationDot> animationDots = new ArrayList<>(getAnimationDots());

        //iterate through copied collection
        for(AnimationDot animationDot : animationDots) {
            animateDot(animationDot);
        }
    }

    private void animateDot(AnimationDot animationDot) {

        //get the vertical position
        float currentY = animationDot.getCurrentPosition().getY();
        float desiredY = animationDot.getDesiredPosition().getY();

        //animation finished
        if(currentY == desiredY) {
            return;
        }

        //animate the dot
        float animationDistance = velocity * converter.getDescription().getPixelSize() / FPS;
        currentY = Math.min(desiredY, currentY + animationDistance);
        animationDot.getCurrentPosition().setY(currentY);
    }

    @Override
    public DotsChangeHandler getDotsChangeHandler() {
        return dotsChangeHandler;
    }
}

