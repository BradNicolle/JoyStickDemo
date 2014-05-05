package com.bradnicolle.joystickdemo;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements JoyStick.OnChangeEventListener {
	JoyStick stick;
	TextView out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		stick = (JoyStick)findViewById(R.id.stick);
		stick.setOnChangeEventListener(this);
		
		out = (TextView)findViewById(R.id.out);
	}

	@Override
	public void onChange(PointF point) {
		out.setText("X: " + point.x + "\nY: " + point.y);
	}

}
