package com.b00445970;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends Activity {

	Button startBtn;
	Button settingBtn;
	Button helpBtn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		startBtn = (Button) this.findViewById(R.id.startBtn);
		settingBtn = (Button) this.findViewById(R.id.settingBtn);
		helpBtn = (Button) this.findViewById(R.id.helpBtn);

		// go to MainActivity
		startBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent();
				i.setClassName("com.b00445970", "com.b00445970.MainActivity");
				// finish();
				startActivity(i);
			}
		});

		// go to SettingsActivity
		settingBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent();
				i.setClassName("com.b00445970",
						"com.b00445970.SettingsActivity");
				// finish();
				startActivity(i);
			}
		});

		// go to HelpActivity
		helpBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent i = new Intent();
				i.setClassName("com.b00445970", "com.b00445970.HelpActivity");
				// finish();
				startActivity(i);
			}
		});
	}
}