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

public class TurbineView extends View {

	private final String TAG = getClass().getSimpleName();
	private final int mDesiredWidth = 400;
	private final int mDesiredHeight = 400;

	private Bitmap mPole;
	private Bitmap mRotor;
	private Paint mTurbine;
	private Float mSpeed;
	private Float mRotation = 359f;

	public TurbineView(Context context) {
		super(context);
		init();
	}

	public TurbineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TurbineView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		init();
		AccessibilityManager accessibilityManager = 
			(AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
		if (accessibilityManager.isEnabled()) {
			sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
		}
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		event.getText().add("fast");
		return true;
	}

	private void init() {
		Log.v(TAG, "init");

		mPole = BitmapFactory.decodeResource(getResources(), R.drawable.turbine_pole);
		mRotor = BitmapFactory.decodeResource(getResources(), R.drawable.turbine_rotor);

		mTurbine = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTurbine.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int x = getPaddingLeft() + 20;
		int y = getPaddingRight() + 20;

		canvas.drawBitmap(mPole, x + 20, mRotor.getHeight()/2, mTurbine);
		canvas.drawBitmap(mRotor, rotate(mRotor, x, y), mTurbine);

		invalidate();		
	}

    public Matrix rotate(Bitmap bm, int x, int y){
        Matrix mtx = new Matrix();
        mtx.postRotate(mRotation, bm.getWidth() / 2, bm.getHeight() / 2);
        mtx.postTranslate(x, y);  //The coordinates where we want to put our bitmap
        mRotation -= mSpeed; //degree of rotation
        return mtx;
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

    public void setSpeed(Float speed) {
    	mSpeed = speed;
    }
}