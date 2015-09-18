package com.icelandic_courses.elie.myfirstapp.animation;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.logic.DotsChangeHandler;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.logic.LogicDot;
import com.icelandic_courses.elie.myfirstapp.score.ScoreManager;
import com.icelandic_courses.elie.myfirstapp.trace.Trace;
import com.icelandic_courses.elie.myfirstapp.trace.TraceChangeHandler;
import com.icelandic_courses.elie.myfirstapp.trace.TraceHandler;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Endos on 03.09.2015.
 */
public abstract class AbstractAnimationLogic implements IAnimationLogic {

    protected final ILogic logic;
    protected final PixelToPitchConverter converter;
    protected final TraceHandler traceHandler;

    private final FeedbackTrace feedbackTrace;

    private Collection<AnimationDot> dotsList = new ArrayList<AnimationDot>();
    private Map<Integer, AnimationDot> dotsMap = new HashMap<Integer, AnimationDot>();

    public AbstractAnimationLogic(ILogic logic, PixelToPitchConverter converter, TraceHandler traceHandler) {
        this.logic = logic;
        this.converter = converter;
        this.traceHandler = traceHandler;

        //trace feedback
        feedbackTrace = new FeedbackTrace();
        traceHandler.registerTraceChangeHandler(new TraceChangeHandler() {
            @Override
            public void onTraceChanged(Trace trace) {
                updateAnimatedTrace(trace);
            }

            @Override
            public void onLastTrackingPointChanged(Position<Float> lastTrackingPoint) {
                feedbackTrace.setLastTrackingPoint(lastTrackingPoint);
            }
        });
    }

    private void updateAnimatedTrace(Trace trace) {
        feedbackTrace.getPositions().clear();
        feedbackTrace.setColor(trace.getColor());

        for(Position<Integer> pitchPosition : trace.getPositions()) {
            feedbackTrace.getPositions().add(converter.transformPitchToPixel(pitchPosition));
        }
    }

    @Override
    public FeedbackTrace getFeedbackTrace() {
        return feedbackTrace;
    }

    /**
     * Create an animation dot from a logic dot.
     * The desired position is the transformed pitch position.
     * The current position is not set within this method.
     * @param logicDot representing logic dot
     * @return animation dot with id, color and desired position
     */
    protected AnimationDot createAnimationDotFromLogicDot(LogicDot logicDot) {
        AnimationDot animationDot = new AnimationDot(logicDot);

        updateDesiredPosition(animationDot);

        return animationDot;
    }

    /**
     * Updates the desired position of the corresponding animation dot
     * @param logicDot
     */
    protected void updateDesiredPosition(LogicDot logicDot) {
        AnimationDot animationDot = getAnimationDot(logicDot.getId());
        updateDesiredPosition(animationDot);
    }

    /**
     * Updates the desired position of the animation dot
     * @param animationDot
     */
    protected void updateDesiredPosition(AnimationDot animationDot) {

        //transform pitch to pixels
        Position<Integer> logicDotPosition = animationDot.getLogicDot().getPosition();
        Position<Float> pixelPosition = converter.transformPitchToPixel(logicDotPosition);

        //set the position as the desired position
        animationDot.getDesiredPosition().setY(pixelPosition.getY());
        animationDot.getDesiredPosition().setX(pixelPosition.getX());
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

    protected AnimationDot getAnimationDot(int id) {
        return dotsMap.get(id);
    }

    public Collection<AnimationDot> getAnimationDots() {
        return dotsList;
    }

    /**
     * Call this method, if pixel sizes changed!
     */
    public void invalidate() {

        //cache logic dots
        Collection<LogicDot> logicDots = new ArrayList<>();
        for(AnimationDot animationDot : dotsList) {
            logicDots.add(animationDot.getLogicDot());
        }

        //clear everything
        dotsList.clear();
        dotsMap.clear();

        //create every animation dot again
        for(LogicDot logicDot : logicDots) {
            getDotsChangeHandler().dotCreated(logicDot);
        }
    }

    protected abstract DotsChangeHandler getDotsChangeHandler();
}
