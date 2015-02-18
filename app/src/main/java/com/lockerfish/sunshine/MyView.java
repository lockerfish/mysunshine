package com.lockerfish.sunshine;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;

public class MyView extends View {

	public MyView(Context context) {
		super(context);
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //Get the width measurement
        int widthSize = getMeasurement(widthMeasureSpec, getDesiredWidth());

        //Get the height measurement
        int heightSize = getMeasurement(heightMeasureSpec, getDesiredHeight());

        //MUST call this to store the measurements
        setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
	}

    private int getMeasurement(int measureSpec, int contentSize) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        int resultSize = 0;
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                //Big as we want to be
                resultSize = contentSize;
                break;
            case View.MeasureSpec.AT_MOST:
                //Big as we want to be, up to the spec
                resultSize = Math.min(contentSize, specSize);
                break;
            case View.MeasureSpec.EXACTLY:
                //Must be the spec size
                resultSize = specSize;
                break;
        }

        return resultSize;
    }

    private int getDesiredWidth() {
        int leftWidth;
        if (mLeftDrawable == null) {
            leftWidth = 0;
        } else {
            leftWidth = mLeftDrawable.getIntrinsicWidth();
        }

        int rightWidth;
        if (mRightDrawable == null) {
            rightWidth = 0;
        } else {
            rightWidth = mRightDrawable.getIntrinsicWidth();
        }

        int textWidth;
        if (mTextLayout == null) {
            textWidth = 0;
        } else {
            textWidth = mTextLayout.getWidth();
        }

        return (int)(leftWidth * 0.67f) + (int)(rightWidth * 0.67f) + mSpacing + textWidth;
    }    
}