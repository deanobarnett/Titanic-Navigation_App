package com.b00445970;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = "Compass";
	private Camera camera = null;

	ImageView point; // pin image
	TextView sensorData;
	TextView locData; // distance info
	RelativeLayout mainLayout;
	SensorManager sensorMan = null;
	Button mapBtn = null;
	Button move = null;
	boolean cameraOn = false;
	boolean active = true; // tracks whether this activity is active
	// vars used for pin location
	float diff;
	int height;
	int width;
	float dist;
	int radiusDist;
	String distUnits;
	double dir;
	Location titanicLoc;
	Vibrator vibrator;
	static final Double titanicLong = -5.909786;
	static final Double titanicLat = 54.607956;
	double myLong = 0.00;
	double myLat = 0.00;
	float myBearing = 0;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
			
		active = true; // activity is active
		// Add Vibrator
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// get the settings from the SettingsActivity
		distUnits = SettingsActivity.getDistUnits();
		radiusDist = SettingsActivity.getRadiusDist();

		// set up distance text view
		locData = (TextView) findViewById(R.id.locData);
		locData.setTextSize(18);

		// get device screen height and width for formatting
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;

		// set up pin point image
		point = (ImageView) findViewById(R.id.point);

		// button to go to map view, hidden initially
		mapBtn = (Button) findViewById(R.id.mapBtn);
		mapBtn.setVisibility(View.GONE);
		mapBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// finish();
				Intent i = new Intent();
				i.setClassName("com.b00445970",
						"com.b00445970.CustomMapActivity");
				startActivity(i);
			}
		});

		// setup vibrate button
		move = (Button) findViewById(R.id.move);
		move.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				int dot = 100;
				int dash = 300;
				int short_gap = 100;

				long[] close = { 0, dot, short_gap, dot, short_gap, dot };
				long[] far = { 0, dash, short_gap, dash, short_gap, dash };
				long[] onTarget = { 0, dash };

				// vibrate sequence based on direction from target
				if (diff < 45 && diff > -45) {
					vibrator.vibrate(onTarget, -1);
				} else if (diff > 45 && diff < 90) {
					vibrator.vibrate(close, -1);
				} else if (diff < 320 && diff > 270) {
					vibrator.vibrate(close, -1);
				} else if (diff > 9 && diff < 270) {
					vibrator.vibrate(far, -1);
				}
			}
		});

		// Camera init
		SurfaceView surface = (SurfaceView) findViewById(R.id.camView);
		SurfaceHolder holder = surface.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		// Gyroscope init
		sensorData = (TextView) findViewById(R.id.sensorData);
		sensorMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorMan.registerListener(sensorListener,
				sensorMan.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		// sensorData.setText("Units: " + distUnits + "\nDist: " + radiusDist);

		// Location init
		titanicLoc = new Location("");
		titanicLoc.setLatitude(titanicLat);
		titanicLoc.setLongitude(titanicLong);
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 5000, 5,
				locationListener);

	}

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			locData.setText("Loc Changed");
			updateWithNewLocation(location);
		}

		private void updateWithNewLocation(Location location) {
			// get new location
			if (location != null) {
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				float bearing = location.getBearing();

				myBearing = bearing;
				myLat = lat;
				myLong = lng;

			} else {
				locData.setText("No Location Found");
			}
			dist = location.distanceTo(titanicLoc) / 1000;
			dist = (float)Math.round(dist * 100) / 100;

			dir = GetDirection(myLat, myLong, titanicLat, titanicLong);
			
			//output based on settings from SettingActivity
			//goes to info page if within radius
			if (distUnits == "km") {
				locData.setText("Distance: " + dist + distUnits);
				if (dist < radiusDist && active) {
					active = false;
					finish();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://www.titanic-facts.com"));
					startActivity(intent);
				}
			} else {
				locData.setText("Distance: " + convertKmToMiles(dist)
						+ distUnits);
				if (convertKmToMiles(dist) < radiusDist && active) {
					active = false;
					finish();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://www.titanic-facts.com"));
					startActivity(intent);
				}
			}

		}
	};

	final SensorEventListener sensorListener = new SensorEventListener() {
		//get sensor data and update image location
		@Override
		public void onSensorChanged(SensorEvent evt) {
			float gyroAzimuth = Math.round(evt.values[0]);
			float gyroPitch = Math.round(evt.values[1]);
			float gyroRoll = Math.round(evt.values[2]);
			activateMapMode(gyroRoll, gyroPitch);
			updatePoint(gyroAzimuth);
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	};
	
	//start sensors again when activity is back in use
	protected void onResume() {

		Log.d(TAG, "onResume");
		super.onResume();
		sensorMan.registerListener(sensorListener,
				sensorMan.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		camera.startPreview();
	}
	
	//remove sensors when activity is not in use
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		sensorMan.unregisterListener(sensorListener);
		camera.release();
	}

	//remove sensors when activity is not in use
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
		sensorMan.unregisterListener(sensorListener);
	}

	// changes camera orientation on rotate
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

		Camera.Parameters parameters = camera.getParameters();
		parameters.set("orientation", "portrait");
		camera.setParameters(parameters);

	}

	// start camera
	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		try {
			camera = camera.open();
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			Camera.Parameters params = camera.getParameters();
			
//			Adding Flash. Not enogh time to complete. Not fully working
//			String cameraMode;
//			
//			if (cameraOn) // we are being ask to turn it on
//	        {
//				cameraMode = Camera.Parameters.FLASH_MODE_TORCH; // add permission
//	        }
//	        else  // we are being asked to turn it off
//	        {
//	        	cameraMode =  Camera.Parameters.FLASH_MODE_AUTO;
//	        }
//
		} catch (Exception e) {
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		camera.stopPreview();
		camera.release();

	}

	//takes in orientation data and shows map button when device is flat
	private void activateMapMode(double roll, double pitch) {
		int rollInt = (int)roll;
		int pitchInt = (int)pitch;// added to remove button flicker
		
		if (rollInt < 30.00 && rollInt > -30.00 && pitchInt < 30.00 && pitchInt > -30.00) {
			mapBtn.setVisibility(View.VISIBLE);			
		} else {
			mapBtn.setVisibility(View.GONE);
		}
	}

	//moves pin point image according to direction facing
	private void updatePoint(float y) {
		int midScreen = width / 2;
		diff = (float) (dir - y); //difference in degrees off two points
		float screenViewpoint = width / 90; // 90 degrees of view on screen
		float ret = diff * screenViewpoint + midScreen;
		LayoutParams par = (LayoutParams) point.getLayoutParams();

		if (diff < 45 && diff > -45) { // in screen viewpoint
			Math.round(ret);
			par.leftMargin = (int) ret;
		} else if (diff < -45 || diff > 175) { // past left of screen
			par.leftMargin = 5;
		} else if (diff > 45) { // past right of screen
			par.leftMargin = 700;
		}

		point.setLayoutParams(par);
	}

	private float convertKmToMiles(float dist) {
		float ret = (float) (dist * 0.6214);
		
		return (float)Math.round(ret * 100) / 100;

	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad / Math.PI * 180.0);
	}

	//get direction from my location to the destination
	//Code for this from stackoverflow.com User: Elanchezhian Babu P
	private double GetDirection(double lat1, double lon1, double lat2,
			double lon2) {
		// code for Direction in Degrees
		// Difference between 2 co-ords taken
		double dlat = deg2rad(lat1) - deg2rad(lat2);
		double dlon = deg2rad(lon1) - deg2rad(lon2);
		// Great Circle Navigation equation used
		double y = Math.sin(dlon) * Math.cos(dlat);
		double x = Math.cos(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				- Math.sin(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(dlon);
		// atan2 used to convert co-ords to polar co-ords to find
		// the arc tangent
		double direct = Math.round(rad2deg(Math.atan2(y, x))); 
		if (direct < 0)
			direct = direct + 360;
		return (direct);
	}

	public static Double getTitaniclong() {
		return titanicLong;
	}

	public static Double getTitaniclat() {
		return titanicLat;
	}

}