package com.b00445970;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class HelpSettings extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Text taken from string.xml and put in tab
		TextView textview = new TextView(this);
		textview.setTextSize(18);
		textview.setMovementMethod(new ScrollingMovementMethod());
		textview.setVerticalScrollBarEnabled(true);
		textview.setText(R.string.settings_help);
		setContentView(textview);
	}
}
