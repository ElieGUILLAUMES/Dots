package com.icelandic_courses.elie.myfirstapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.icelandic_courses.elie.myfirstapp.R;
import com.icelandic_courses.elie.myfirstapp.animation.AnimationDot;

import com.icelandic_courses.elie.myfirstapp.animation.IAnimationLogic;
import com.icelandic_courses.elie.myfirstapp.animation.NoAnimationLogic;
import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.logic.ILogic;
import com.icelandic_courses.elie.myfirstapp.trace.TraceChangeHandler;
import com.icelandic_courses.elie.myfirstapp.trace.TrackingHandler;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverter;
import com.icelandic_courses.elie.myfirstapp.transformation.PixelToPitchConverterDescription;
import com.icelandic_courses.elie.myfirstapp.util.Position;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Endos on 10.09.2015.
 */
public class GameView extends View {

    private final static String TAG = GameView.class.getSimpleName();

    private Timer timer;

    private ILogic logic;
    private IAnimationLogic animationLogic;
    private TrackingHandler trackingHandler;
    private PixelToPitchConverterDescription converterDescription;

    private RectF m_circle = new RectF();
    private Paint m_paintCircle = new Paint();

    private int m_cellWidth;
    private int m_cellHeight;

    private int boardWidth;
    private int boardHeight;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_paintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        m_paintCircle.setAntiAlias(true);

        //auto invalidate every 30 milliseconds
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        }, 0, 30);
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

        drawDots(canvas);
    }

    private void drawDots(Canvas canvas){

        //not initialized yet
        if(animationLogic == null) {
            return;
        }

        for(AnimationDot animationDot : animationLogic.getDots()) {

            //calculate ovals boundaries
            float x = animationDot.getCurrentPosition().getX();
            float y = animationDot.getCurrentPosition().getY();
            m_circle.set(x - m_cellWidth / 2f, y - m_cellHeight / 2f, x + m_cellWidth / 2f, y + m_cellHeight / 2f);
            m_circle.inset(m_cellWidth * 0.2f, m_cellHeight * 0.2f);

            //draw colored oval
            setPaintColor(animationDot.getColor());
            canvas.drawOval(m_circle, m_paintCircle);
        }
    }

    private void setPaintColor(DotColor dotColor){
        switch (dotColor){
            case BLUE:
                m_paintCircle.setColor(getResources().getColor(R.color.blue));
                break;
            case RED:
                m_paintCircle.setColor(getResources().getColor(R.color.red));
                break;
            case GREEN:
                m_paintCircle.setColor(getResources().getColor(R.color.green));
                break;
        }
    }

    public void initLogic(ILogic logic) {
        this.logic = logic;

        //settings
        int padding = 10;
        int pixelSize = Math.min(getWidth(), getHeight());

        // pixel <--> pitch converter
        converterDescription = new PixelToPitchConverterDescription(
                logic.getPitchSize(),
                pixelSize,
                padding
        );
        PixelToPitchConverter converter = new PixelToPitchConverter(converterDescription);

        //animation logic
        animationLogic = new NoAnimationLogic(logic, converter);

        //tracking handler
        trackingHandler = new TrackingHandler(logic, converter);

        //add trace changed handler
        trackingHandler.registerTraceChangeHandler(new TraceChangeHandler() {
            @Override
            public void onTraceChange(List<Position<Integer>> trace) {
                //TODO
            }
        });

        //set cell size, because the pitch size wasn't clear before
        setCellSize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(trackingHandler != null){
            return trackingHandler.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }
}
