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
    private final boolean D = Log.isLoggable(TAG, Log.DEBUG);
	
	private int mDesiredWidth = 100;
	private int mDesiredHeight = 100;

	private Bitmap mWindDirection;
	private Paint mDirection;
	private Float mDegrees = 0f;

	private Context mContext;

	public WindDirectionView(Context context) {
		super(context);
		if (D) { Log.v(TAG, "WindDirectionView: context: " + context);}
		mContext = context;
		init();
	}

	public WindDirectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (D) {Log.v(TAG, "WindDirectionView: context: " + context + " attrs: " + attrs);}
		mContext = context;
		init();
	}

	public WindDirectionView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		if (D) {Log.v(TAG, "WindDirectionView: context: " + context 
			+ " attrs: " + attrs 
			+ " defaultStyle: " + defaultStyle);
		}		
		mContext = context;
		init();
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		if (D) { Log.v(TAG, "dispatchPopulateAccessibilityEvent: event: " + event);}
		event.getText().add("north");
		return true;
	}

	private void init() {
		if (D) { Log.v(TAG, "init");}

		mWindDirection = BitmapFactory.decodeResource(getResources(), R.drawable.wind_direction);

		mDirection = new Paint(Paint.ANTI_ALIAS_FLAG);
		mDirection.setStyle(Paint.Style.FILL);

		mDesiredWidth = mWindDirection.getWidth();
		mDesiredHeight = mWindDirection.getHeight();

		AccessibilityManager accessibilityManager = 
			(AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
		if (accessibilityManager.isEnabled()) {
			sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// if (D) { Log.v(TAG, "onDraw");}

		super.onDraw(canvas);

		int x = getPaddingLeft() + 20;
		int y = getPaddingRight() + 20;

		canvas.drawBitmap(mWindDirection, rotate(mWindDirection, x, y), mDirection);
	}

	public Matrix rotate(Bitmap bm, int x, int y) {
		Matrix mtx = new Matrix();
		mtx.postRotate(mDegrees, bm.getWidth()/2, bm.getHeight()/2);
		mtx.postTranslate(x, y);
		return mtx;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (D) {Log.v(TAG, "onMeasure: widthMeasureSpec: " + widthMeasureSpec 
			+ " heightMesaureSpec: " + heightMeasureSpec);
		}

	    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    if (D) { Log.v(TAG, "widthMode: " + widthMode + ", heightMode: " + heightMode); }
	    if (D) { Log.v(TAG, "widthSize: " + widthSize + ", heightSize: " + heightSize); }

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

		if (D) { Log.v(TAG, "width: " + width + ", height: " + height); }

	    //MUST CALL THIS
	    setMeasuredDimension(width, height);
	}

    public void setDegrees(Float degrees) {
    	if (D) { Log.v(TAG, "setDegrees: degrees: " + degrees);}
    	mDegrees = degrees;
    }

}