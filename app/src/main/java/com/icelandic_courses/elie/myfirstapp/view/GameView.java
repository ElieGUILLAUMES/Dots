package com.icelandic_courses.elie.myfirstapp.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.animation.AnimationDot;

import com.icelandic_courses.elie.myfirstapp.animation.BounceAnimationLogic;
import com.icelandic_courses.elie.myfirstapp.animation.FeedbackTrace;
import com.icelandic_courses.elie.myfirstapp.animation.IAnimationLogic;
import com.icelandic_courses.elie.myfirstapp.animation.LinearAnimationLogic;
import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.trace.Trace;
import com.icelandic_courses.elie.myfirstapp.trace.TraceChangeHandler;
import com.icelandic_courses.elie.myfirstapp.trace.TraceHandler;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverterDescription;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Endos on 10.09.2015.
 */
public class GameView extends View {

    private final static String TAG = GameView.class.getSimpleName();

    private final Timer timer;
    private ToneGenerator toneGenerator;

    private SharedPreferences prefs;

    private ILogic logic;
    private IAnimationLogic animationLogic;
    private TraceHandler traceHandler;
    private PixelToPitchConverterDescription converterDescription;

    private RectF m_circle = new RectF();
    private Paint m_paintCircle = new Paint();
    private Paint m_paintLine = new Paint();

    private int m_cellWidth;
    private int m_cellHeight;

    private int boardWidth;
    private int boardHeight;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_paintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        m_paintCircle.setAntiAlias(true);

        m_paintLine.setStyle(Paint.Style.STROKE);
        m_paintLine.setStrokeJoin(Paint.Join.ROUND);
        m_paintLine.setAntiAlias(true);

        prefs =  PreferenceManager.getDefaultSharedPreferences(MyActivity.getContext());

        //tone generator
        try {
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        }
        catch (RuntimeException e) {
            //if the initialization of the tone generator fails, silence the app.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("silence", true);
            editor.commit();
        }

        //auto invalidate every 30 milliseconds
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        }, 0, IAnimationLogic.PERIOD);
    }

    public void initLogic(ILogic logic) {
        this.logic = logic;

        //settings
        int padding = 20;
        int pixelSize = Math.min(getWidth(), getHeight());

        // pixel <--> pitch converter
        converterDescription = new PixelToPitchConverterDescription(
                logic.getPitchSize(),
                pixelSize,
                padding
        );
        PixelToPitchConverter converter = new PixelToPitchConverter(converterDescription);

        //tracking handler
        traceHandler = new TraceHandler(logic, converter);
        traceHandler.registerTraceChangeHandler(new TraceChangeHandler() {
            @Override
            public void onTraceChanged(Trace trace) {
                doTraceFeedback(trace);
            }

            @Override
            public void onLastTrackingPointChanged(Position<Float> lastTrackingPoint) {

            }
        });

        //animation logic
        animationLogic = new BounceAnimationLogic(logic, converter, traceHandler);

        //set cell size, because the pitch size wasn't clear before
        setCellSize();
    }

    private void doTraceFeedback(Trace trace) {
        //TODO visualization

        //play tone
        if(!prefs.getBoolean("silence", false)){
            toneGenerator.startTone(trace.getPositions().size(), 100);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int m_screenWidth  = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int m_screenHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int size = Math.min(m_screenWidth, m_screenHeight);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(),
                size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged( int xNew, int yNew, int xOld, int yOld ) {
        boardWidth = (xNew - getPaddingLeft() - getPaddingRight());
        boardHeight = (yNew - getPaddingTop() - getPaddingBottom());

        setCellSize();

        //update converter description
        int pixelSize = Math.min(xNew, yNew);
        converterDescription.setPixelSize(pixelSize);
        animationLogic.invalidate();
    }

    private void setCellSize() {
        if(logic != null) {
            m_cellWidth = Math.min(boardWidth / logic.getPitchSize(), boardHeight / logic.getPitchSize());
            m_cellHeight = Math.min(boardWidth / logic.getPitchSize(), boardHeight / logic.getPitchSize());

            m_circle.set(0, 0, m_cellWidth, m_cellHeight);
            m_circle.inset(m_cellWidth * 0.1f, m_cellHeight * 0.1f);
            m_circle.offset(getPaddingLeft(), getPaddingTop());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTrace(canvas);
        drawDots(canvas);
    }

    private void drawTrace(Canvas canvas) {

        //get feedback trace
        final FeedbackTrace trace = animationLogic.getFeedbackTrace();

        //set stroke width
        float strokeWidth = converterDescription.getSegmentSize() * 0.1f;
        m_paintLine.setStrokeWidth(strokeWidth);

        //trace color
        int traceColor = getColor(trace.getColor());
        int traceAlpha = 127;
        int traceColorAlpha = Color.argb(traceAlpha, Color.red(traceColor), Color.green(traceColor), Color.blue(traceColor));

        //set color
        m_paintCircle.setColor(traceColorAlpha);
        m_paintLine.setColor(traceColor);

        //draw trace
        Position<Float> previousPosition = null;
        for(Position<Float> position : trace.getPositions()) {

            //draw dots
            drawCircle(canvas, position, 0.8f);

            //draw lines
            if(previousPosition != null) {
                drawLine(canvas, previousPosition, position);
            }
            previousPosition = position;
        }

        //draw line to last tracking point
        m_paintLine.setColor(traceColorAlpha);
        drawLine(canvas, previousPosition, trace.getLastTrackingPoint());
    }

    private void drawLine(Canvas canvas, Position<Float> previousPosition, Position<Float> position) {
        if(previousPosition == null || position == null) {
            return;
        }

        canvas.drawLine(
                previousPosition.getX(),
                previousPosition.getY(),
                position.getX(),
                position.getY(),
                m_paintLine
        );
    }

    private void drawDots(Canvas canvas){

        //not initialized yet
        if(animationLogic == null) {
            return;
        }

        //draw dots
        for(AnimationDot animationDot : animationLogic.getAnimationDots()) {
            setPaintColor(m_paintCircle, animationDot.getColor());
            drawCircle(canvas, animationDot.getCurrentPosition(), 0.7f);
        }
    }

    private void drawCircle(Canvas canvas, Position<Float> position, float percentageWidth) {
        //calculate ovals boundaries
        float x = position.getX();
        float y = position.getY();
        m_circle.set(x - m_cellWidth / 2f, y - m_cellHeight / 2f, x + m_cellWidth / 2f, y + m_cellHeight / 2f);
        m_circle.inset(m_cellWidth * (1-percentageWidth), m_cellHeight * (1-percentageWidth));

        canvas.drawOval(m_circle, m_paintCircle);
    }

    private void setPaintColor(Paint paint, DotColor dotColor){
        paint.setColor(getColor(dotColor));
    }

    private int getColor(DotColor dotColor) {

        if(dotColor == null)
            dotColor = DotColor.YELLOW;

        switch (dotColor){
            case BLUE:
                return getResources().getColor(R.color.blue);
            case RED:
                return getResources().getColor(R.color.red);
            case GREEN:
                return getResources().getColor(R.color.green);
            case VIOLET:
                return getResources().getColor(R.color.violet);
            case YELLOW:
            default:
                return getResources().getColor(R.color.yellow);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(traceHandler != null){
            return traceHandler.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    public TraceHandler getTraceHandler() {
        return traceHandler;
    }
}
