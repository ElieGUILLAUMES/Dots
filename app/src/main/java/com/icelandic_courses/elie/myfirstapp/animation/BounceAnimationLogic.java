package com.icelandic_courses.elie.myfirstapp.animation;

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
 * Created by Endos on 15.09.2015.
 */
public class BounceAnimationLogic extends AbstractAnimationLogic {

    private final static float GRAVITATION = 2.f; // segmentsize/s²
    private final static float FRICTION = 12.f; // friction / s

    private final Timer timer;

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

    public BounceAnimationLogic(ILogic logic, PixelToPitchConverter converter, TraceHandler traceHandler) {
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

        //cast the dot to a bounced animation dot
        BounceAnimationDot bounceAnimationDot = (BounceAnimationDot) animationDot;

        //get the vertical position
        float currentY = bounceAnimationDot.getCurrentPosition().getY();
        float desiredY = bounceAnimationDot.getDesiredPosition().getY();
        float velocityY = bounceAnimationDot.getVelocity();

        //animation finished
        if(currentY == desiredY) {
            return;
        }

        //adjust velocity
        float segmentSize = converter.getDescription().getSegmentSize();
        velocityY = (1-FRICTION/FPS) * (velocityY + GRAVITATION * segmentSize /FPS);

        //move distance
        float animationDistance = velocityY * segmentSize / FPS;
        currentY = currentY + animationDistance;

        //check bounce
        if(currentY > desiredY) {
            if(velocityY < segmentSize * 0.1f) {
                //stop it bouncing, it's close
                velocityY = 0;
                currentY = desiredY;
            }
            else {
                //invert velocity and bounce at the desired y line
                velocityY *= -1;
                currentY = 2 * desiredY - currentY;
            }
        }

        //update animation dot
        bounceAnimationDot.setVelocity(velocityY);
        bounceAnimationDot.getCurrentPosition().setY(currentY);
    }

    @Override
    protected AnimationDot createAnimationDotFromLogicDot(LogicDot logicDot) {
        AnimationDot animationDot = new BounceAnimationDot(logicDot);
        updateDesiredPosition(animationDot);
        return animationDot;
    }

    @Override
    public DotsChangeHandler getDotsChangeHandler() {
        return dotsChangeHandler;
    }
}
