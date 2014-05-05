package com.bradnicolle.joystickdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoyStick extends View {
	OnChangeEventListener mListener;
	
	Paint fillPaint;
	Paint strokePaint;
	private float xpad, ypad;
	private float ww, hh, radius;
	
	private boolean pressed = false;
	private PointF thumb;

	public JoyStick(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		fillPaint = new Paint();
		fillPaint.setAntiAlias(true);
		fillPaint.setColor(Color.rgb(51, 181, 229)); // Android holo blue
		fillPaint.setStyle(Paint.Style.FILL);
		
		strokePaint = new Paint();
		strokePaint.setAntiAlias(true);
		strokePaint.setColor(Color.LTGRAY);
		strokePaint.setStyle(Paint.Style.STROKE);
		strokePaint.setStrokeJoin(Paint.Join.ROUND);
		strokePaint.setStrokeWidth(1f);
		
		thumb = new PointF(0,0);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// Account for padding
		xpad = (float) (getPaddingLeft() + getPaddingRight());
		ypad = (float) (getPaddingTop() + getPaddingBottom());

		ww = (float) w - xpad;
		hh = (float) h - ypad;
		// Circle half the size of view
		radius = Math.min(ww, hh)/4;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.translate(ww/2 + xpad/2, hh/2 + ypad/2);
		canvas.drawCircle(0, 0, radius, strokePaint);
		canvas.drawCircle(thumb.x, thumb.y, radius/2.5f, fillPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX() - (ww + xpad)/2;
		float eventY = event.getY() - (hh + ypad)/2;
		
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pressed = false;
			if (insideRadius(eventX, eventY)) {
				pressed = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			// Only process if action originated within JoyStick radius
			if (pressed) {
				thumb.x = eventX;
				thumb.y = eventY;
				
				// Now constrain thumb to inside max radius
				constrain(thumb, radius);
				
				// Update listener
				if (mListener != null) {
					mListener.onChange(thumb);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// Spring thumb back to centre
			thumb.x = 0;
			thumb.y = 0;
			// Update listener
			if (mListener != null) {
				mListener.onChange(thumb);
			}
			pressed = false;
			break;
		default:
			return false;
		}
		invalidate();
		return true;
	}
	
	// Returns true if x and y are inside radius
	private boolean insideRadius(float x, float y) {
		float len = (float)(Math.pow(x, 2) + Math.pow(y, 2));
		float radlen = (float)Math.pow(radius, 2);
		
		return (len < radlen);
	}
	
	private void constrain(PointF point, float radius) {
		float len = (float)(Math.pow(point.x, 2) + Math.pow(point.y, 2));
		float radlen = (float)Math.pow(radius, 2);
		
		if (len > radlen) {
			// Make unit vector and multiply by radius to contrain to circle
			point.x = radius * (point.x / (float)Math.sqrt(len));
			point.y = radius * (point.y / (float)Math.sqrt(len));
		}
	}
	
	public void setOnChangeEventListener(OnChangeEventListener listener) {
		mListener = listener;
	}
	
	public interface OnChangeEventListener {
		public void onChange(PointF point);
	}

}
