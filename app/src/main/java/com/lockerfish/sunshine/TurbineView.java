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

public class TurbineView extends View {

	private final String TAG = getClass().getSimpleName();

	private Bitmap mPole;
	private Bitmap mRotor;
	private Paint mTurbine;
	private Float mDegrees;
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

		Log.v(TAG, "onDraw ");

		canvas.drawBitmap(mPole, 10, mRotor.getHeight()/2, mTurbine);
		canvas.drawBitmap(mRotor, rotate(mRotor, 0, 0), mTurbine);
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
        setMeasuredDimension(200, 200); 
    }

    public void setDegrees(Float degrees) {
    	mDegrees = degrees;
    }

    public void setSpeed(Float speed) {
    	mSpeed = speed;
    }
}