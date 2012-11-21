package com.valfom.tracker;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class TrackerMapFragment extends SherlockFragment {

	private static final int MIN_UPDATE_TIME = 1000;
	private static final int MIN_UPDATE_DISTANCE = 0;

	public static MapView mapView = null;
	private static boolean added = false;
	private MyLocationOverlay myLocationOverlay = null;

	private LocationManager locationManager = null;

	private TextView tvDistanceMapUnit;

	private static OnStateRestoredListener mStateListener;
	
	private View vValues = null;
	private View vBtnMap = null;
	private View vBtnLock = null;
	private View vBtnMyLocation = null;
	private View vBtnMarker = null;

	public interface OnStateRestoredListener {

		public void onStateRestored(String state);
	}

	public TrackerMapFragment() {
	}

	@Override
	public void onAttach(Activity activity) {

		try {

			mStateListener = (OnStateRestoredListener) activity;

		} catch (ClassCastException e) {

			throw new ClassCastException(activity.toString()
					+ " must implement OnStateRestoredListener");
		}

		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		registerListener();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		tvDistanceMapUnit = (TextView) getView().findViewById(
				R.id.tvDistanceMapUnit);
	}

	@Override
	public void onDestroy() {

		unregisterAllListeners();

		myLocationOverlay.disableMyLocation();

		super.onDestroy();
	}

	@Override
	public void onResume() {

		super.onResume();
		
		getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		        
		    	int width = getView().getWidth();
		    	int height = getView().getHeight();
		    	
		    	vBtnMap.setLayoutParams(new MapView.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
						width - vBtnMap.getWidth() / 2 - 30, 
						height / 2 - vBtnMap.getHeight() / 2 - 30  - vBtnLock.getHeight() / 2,
						LayoutParams.CENTER));
		    	
		    	vBtnLock.setLayoutParams(new MapView.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
						width - vBtnLock.getWidth() / 2 - 30, 
						height / 2,
						LayoutParams.CENTER));
		    	
		    	vBtnMyLocation.setLayoutParams(new MapView.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
						width - vBtnMyLocation.getWidth() / 2 - 30, 
						height / 2 + vBtnLock.getHeight() / 2 + 30 + vBtnMyLocation.getHeight() / 2,
						LayoutParams.CENTER));
		    	
		    	vBtnMarker.setLayoutParams(new MapView.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
						width - vBtnMarker.getWidth() / 2 - 30, 
						height / 2 + vBtnLock.getHeight() / 2 + 30 + vBtnMyLocation.getHeight() / 2 + 30 + vBtnMarker.getHeight() / 2,
						LayoutParams.CENTER));
		    	
		    	vValues.setLayoutParams(new MapView.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 
						width / 2, 
						20,
						LayoutParams.CENTER_HORIZONTAL));

				getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
		    }
		});

		TrackerSettings settings = new TrackerSettings(getActivity());

		tvDistanceMapUnit.setText(settings.getDistanceUnit());

		String state;

		if (isServiceRunning()) {

			if (!TrackerService.isPaused)
				state = "started";
			else
				state = "paused";
		} else
			state = "stopped";

		mStateListener.onStateRestored(state);
	}

	private boolean isServiceRunning() {

		ActivityManager manager = (ActivityManager) getActivity()
				.getSystemService(Context.ACTIVITY_SERVICE);

		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE))
			if (TrackerService.class.getName().equals(
					service.service.getClassName()))
				return true;

		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mapView = new MapView(getActivity(),
				"0gXrA3OG3rX_KPSAWRCG_dSHPmZnlnnmLRUssxg");

		final MapController mapController = mapView.getController();

		mapController.setZoom(15);

		mapView.setSatellite(false);

		final List<Overlay> mapOverlays = mapView.getOverlays();
		myLocationOverlay = new MyLocationOverlay(getActivity(), mapView);

		mapOverlays.add(myLocationOverlay);

		myLocationOverlay.enableMyLocation();

		myLocationOverlay.runOnFirstFix(new Runnable() {

			public void run() {

				GeoPoint geoPoint = myLocationOverlay.getMyLocation();
				mapController.animateTo(geoPoint);
			}
		});

		Drawable marker = getResources().getDrawable(R.drawable.ic_marker);
		final TrackerItemizedOverlay itemizedOverlay = new TrackerItemizedOverlay(
				marker, getActivity(), true);

		vValues = getLayoutInflater(savedInstanceState).inflate(
				R.layout.map_over_view_values, null);
		vBtnMap = getLayoutInflater(savedInstanceState).inflate(
				R.layout.map_btn_map, null);
		vBtnLock = getLayoutInflater(savedInstanceState).inflate(
				R.layout.map_btn_lock, null);
		vBtnMyLocation = getLayoutInflater(savedInstanceState).inflate(
				R.layout.map_btn_my_location, null);
		vBtnMarker = getLayoutInflater(savedInstanceState).inflate(
				R.layout.map_btn_marker, null);

		final ImageButton btnAddMarker = (ImageButton) vBtnMarker
				.findViewById(R.id.btnAddMarker);

		btnAddMarker.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				GeoPoint geoPoint = myLocationOverlay.getMyLocation();

				if (geoPoint == null)
					Toast.makeText(getActivity(),
							"Unable to find your location", Toast.LENGTH_SHORT)
							.show();
				else {

					int lat = geoPoint.getLatitudeE6();
					int lng = geoPoint.getLongitudeE6();

					String title = lat / 1E6 + " " + lng / 1E6;
					String msg = "Your message";

					TrackerDB db = new TrackerDB(getActivity());

					TrackerMarker marker = new TrackerMarker(lat, lng, title,
							msg);

					Integer markerId = db.addMarker(marker);

					if (markerId != null) {

						TrackerOverlayItem overlayItem = new TrackerOverlayItem(
								markerId, geoPoint, title, msg);

						itemizedOverlay.addOverlay(overlayItem);

						if (!added) {

							mapOverlays.add(itemizedOverlay);

							added = true;
						}
					}
				}
			}
		});

		final ImageButton btnMap = (ImageButton) vBtnMap
				.findViewById(R.id.btnMap);

		btnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mapView.setSatellite(!mapView.isSatellite());
			}
		});

		final ImageButton btnLock = (ImageButton) vBtnLock
				.findViewById(R.id.btnLock);

		btnLock.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				((TrackerMainActivity) getActivity()).viewPager
						.setSwipingEnabled(!((TrackerMainActivity) getActivity()).viewPager
								.isSwipingEnabled());

				btnLock.setImageResource(((TrackerMainActivity) getActivity()).viewPager
						.isSwipingEnabled() ? R.drawable.ic_map_locked
						: R.drawable.ic_map_unlocked);

				mapView.setClickable(!mapView.isClickable());
			}
		});

		final ImageButton btnMyLocation = (ImageButton) vBtnMyLocation
				.findViewById(R.id.btnMyLocation);

		btnMyLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				GeoPoint geoPoint = myLocationOverlay.getMyLocation();

				if (geoPoint != null)
					mapController.animateTo(geoPoint);
				else
					Toast.makeText(getActivity(),
							"Unable to find your location", Toast.LENGTH_SHORT)
							.show();
			}
		});

		mapView.addView(vBtnMap);
		mapView.addView(vBtnLock);
		mapView.addView(vBtnMyLocation);
		mapView.addView(vBtnMarker);
		mapView.addView(vValues);

		return mapView;
	}

	private LocationListener gpsProviderListener = new LocationListener() {

		public void onLocationChanged(Location location) {
		}

		public void onProviderDisabled(String provider) {

			myLocationOverlay.disableMyLocation();
		}

		public void onProviderEnabled(String provider) {

			registerListener();

			myLocationOverlay.enableMyLocation();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	private void unregisterAllListeners() {

		locationManager.removeUpdates(gpsProviderListener);
	}

	private void registerListener() {

		unregisterAllListeners();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, gpsProviderListener);
	}
}