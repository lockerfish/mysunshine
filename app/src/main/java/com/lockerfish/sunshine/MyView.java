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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
	}
}