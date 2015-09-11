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
import com.icelandic_courses.elie.myfirstapp.animation.AbstractAnimationLogic;
import com.icelandic_courses.elie.myfirstapp.animation.AnimationDot;

import com.icelandic_courses.elie.myfirstapp.logic.DotColor;
import com.icelandic_courses.elie.myfirstapp.trace.TrackingHandler;

import java.util.ArrayList;


/**
 * Created by Endos on 10.09.2015.
 */
public class GameView extends View {

    private final static String TAG = GameView.class.getSimpleName();

    private TrackingHandler trackingHandler;

    private int num_cells = ClassicGameActivity.getPitchSize();

    private RectF m_circle = new RectF();
    Paint m_paintCircle = new Paint();

    private int m_cellWidth;
    private int m_cellHeight;

    private int m_screenWidth;
    private int m_screenHeight;

    private ArrayList<AnimationDot> animationDotArrayList;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_paintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        m_paintCircle.setAntiAlias(true);

        animationDotArrayList = new ArrayList(AbstractAnimationLogic.getDots());


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        m_screenWidth  = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        m_screenHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int size = Math.min(m_screenWidth, m_screenHeight);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(),
                size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged( int xNew, int yNew, int xOld, int yOld ) {
        int   boardWidth = (xNew - getPaddingLeft() - getPaddingRight());
        int   boardHeight = (yNew - getPaddingTop() - getPaddingBottom());

        m_cellWidth = Math.min(boardWidth / num_cells, boardHeight / num_cells);
        m_cellHeight = Math.min(boardWidth / num_cells, boardHeight / num_cells);

        m_circle.set(0, 0, m_cellWidth, m_cellHeight);
        m_circle.inset(m_cellWidth * 0.1f, m_cellHeight * 0.1f);
        m_circle.offset(getPaddingLeft(), getPaddingTop());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Circles
        if(!animationDotArrayList.isEmpty()){
            drawCircles(canvas);
        }

    }

    private void drawCircles(Canvas canvas){
        for(int i = 0; i < animationDotArrayList.size(); i++){
            setPaintColor(animationDotArrayList.get(i).getColor());
            float x = animationDotArrayList.get(i).getCurrentPosition().getX();
            float y = animationDotArrayList.get(i).getCurrentPosition().getY();
            m_circle.set(x - m_cellWidth / 2f, y - m_cellHeight / 2f, x + m_cellWidth / 2f, y + m_cellHeight / 2f);
            m_circle.inset(m_cellWidth * 0.2f, m_cellHeight * 0.2f);
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

    public void setTrackingHandler(TrackingHandler trackingHandler) {
        this.trackingHandler = trackingHandler;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(trackingHandler != null){
            return trackingHandler.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }
}
