package com.b00445970;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	static int radiusDist = 1;
	static String distUnits = "km";
	Spinner distSpinner;
	RadioGroup radioGroup1;
	RadioButton milesRadio;
	RadioButton kmRadio;

	/** Called when the activity is first created. */
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		// spinner init
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.distArray, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		distSpinner = (Spinner) findViewById(R.id.distSpinner);
		distSpinner.setAdapter(adapter);
		if (radiusDist == 1) {
			distSpinner.setSelection(0);
		} else if (radiusDist == 2) {
			distSpinner.setSelection(1);
		} else if (radiusDist == 3) {
			distSpinner.setSelection(2);
		} else if (radiusDist == 4) {
			distSpinner.setSelection(3);
		} else if (radiusDist == 5) {
			distSpinner.setSelection(4);
		}

		distSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemSelectedListener#onItemSelected
			 * (android.widget.AdapterView, android.view.View, int, long)
			 */
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int pos,
					long id) {
				// get distance selected
				switch (pos) {
				case 1:
					radiusDist = 2;
					break;
				case 2:
					radiusDist = 3;
					break;
				case 3:
					radiusDist = 4;
					break;
				case 4:
					radiusDist = 5;
					break;
				default:
					radiusDist = 1;
					break;
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemSelectedListener#onNothingSelected
			 * (android.widget.AdapterView)
			 */
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		// radio group setup
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		kmRadio = (RadioButton) findViewById(R.id.kmRadio);
		milesRadio = (RadioButton) findViewById(R.id.milesRadio);

		if (distUnits == "miles") {
			milesRadio.setChecked(true);
		}
		radioGroup1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			// radio group changing
			@Override
			public void onCheckedChanged(RadioGroup arg0, int check) {
				if (milesRadio.isChecked()) {
					distUnits = "miles";
				} else {
					distUnits = "km";
				}

			}
		});
	}

	/**
	 * @return int radiusDist
	 */
	public static int getRadiusDist() {
		return radiusDist;
	}

	/**
	 * @param radiusDist
	 */
	public void setRadiusDist(int radiusDist) {
		this.radiusDist = radiusDist;
	}

	/**
	 * @return String distUnits
	 */
	public static String getDistUnits() {
		return distUnits;
	}

	/**
	 * @param distUnits
	 */
	public void setDistUnits(String distUnits) {
		this.distUnits = distUnits;
	}
}
