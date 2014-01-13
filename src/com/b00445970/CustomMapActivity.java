package com.b00445970;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CustomMapActivity extends MapActivity {
	MapView mapView;
	private MapController mapController;
	private CustomItemizedOverlay itemizedOverlay;
	private CustomItemizedOverlay itemizedOverlay2;
	private List<Overlay> mapOverlays;
	Location titanicLoc;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		mapView = (MapView) findViewById(R.id.map_view);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.pin);
		itemizedOverlay = new CustomItemizedOverlay(drawable, this);
		itemizedOverlay2 = new CustomItemizedOverlay(drawable, this);

		// get titanic location
		titanicLoc = new Location("");
		titanicLoc.setLatitude(MainActivity.getTitaniclat());
		titanicLoc.setLongitude(MainActivity.getTitaniclong());

		mapController = mapView.getController();

		// Map init
		mapView.setSatellite(true);
		mapView.setStreetView(false);
		mapView.displayZoomControls(true);
		mapController.setZoom(14);

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		String provider = locationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 1000, 1,
				locationListener);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private final LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};

	// creates marker on location
	private void updateWithNewLocation(Location location) {
		if (location != null) {
			Double geoLat = location.getLatitude() * 1E6;
			Double geoLng = location.getLongitude() * 1E6;

			Double titanicLat = titanicLoc.getLatitude() * 1E6;
			Double titanicLng = titanicLoc.getLongitude() * 1E6;
			GeoPoint point1 = new GeoPoint(geoLat.intValue(), geoLng.intValue());
			GeoPoint point2 = new GeoPoint(titanicLat.intValue(),
					titanicLng.intValue());

			mapOverlays.clear();
			OverlayItem overlayitem1 = new OverlayItem(point1, "You Are Here",
					"");
			OverlayItem overlayitem2 = new OverlayItem(point2,
					"You Are Going Here", "");
			itemizedOverlay.addOverlay(overlayitem1);
			itemizedOverlay2.addOverlay(overlayitem2);
			mapOverlays.add(itemizedOverlay);
			mapOverlays.add(itemizedOverlay2);

			mapController.animateTo(point1);
		}
	}
}
