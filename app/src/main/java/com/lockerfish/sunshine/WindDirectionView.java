package com.lockerfish.sunshine;

import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Log;
import android.graphics.Matrix;
import android.view.accessibility.AccessibilityEvent;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityManager;

public class WindDirectionView extends View {

	private final String TAG = getClass().getSimpleName();
	private final int mDesiredWidth = 400;
	private final int mDesiredHeight = 400;

	private Bitmap mWindDirection;
	private Paint mDirection;
	private Float mDegrees;

	public WindDirectionView(Context context) {
		super(context);
		Log.v(TAG, "WindDirectionView-context: " + context);
		init();
	}

	public WindDirectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.v(TAG, "WindDirectionView-context: " + context + " attrs: " + attrs);
		init();
	}

	public WindDirectionView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		Log.v(TAG, "WindDirectionView-context: " + " attrs: " + attrs + " defaultStyle: " + defaultStyle);
		init();
		AccessibilityManager accessibilityManager = 
			(AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		if (accessibilityManager.isEnabled()) {
			sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
		}
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		event.getText().add("north");
		return true;
	}

	private void init() {
		Log.v(TAG, "init");

		mWindDirection = BitmapFactory.decodeResource(getResources(), R.drawable.wind_direction);

		mDirection = new Paint(Paint.ANTI_ALIAS_FLAG);
		mDirection.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int x = getPaddingLeft() + 20;
		int y = getPaddingRight() + 20;

		canvas.drawBitmap(mWindDirection, x, y, mDirection);
		invalidate();		
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		Log.v(TAG, "onMeasure");

	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    Log.v(TAG, "widthMode: " + widthMode + ", heightMode: " + heightMode);
	    Log.v(TAG, "widthSize: " + widthSize + ", heightSize: " + heightSize);

	    int width;
	    int height;

	    //Measure Width
	    if (widthMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        width = widthSize;
	    } else if (widthMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        width = Math.min(mDesiredWidth, widthSize);
	    } else {
	        //Be whatever you want
	        width = mDesiredWidth;
	    }

	    //Measure Height
	    if (heightMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        height = heightSize;
	    } else if (heightMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        height = Math.min(mDesiredHeight, heightSize);
	    } else {
	        //Be whatever you want
	        height = mDesiredHeight;
	    }

		Log.v(TAG, "width: " + width + ", height: " + height);

	    //MUST CALL THIS
	    setMeasuredDimension(width, height);
	}

    public void setDegrees(Float degrees) {
    	mDegrees = degrees;
    }

}