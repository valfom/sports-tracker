package com.valfom.tracker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.valfom.tracker.TrackerMainFragment.OnButtonClickedListener;

public class TrackerActivity extends Activity implements
		ActionBar.OnNavigationListener, OnButtonClickedListener {

	public final static String BROADCAST_ACTION = "com.valfom.servicetest";

	private static LocationManager locationManager;
	public static ProgressDialog progressDialog;
	
	public static String status = "stopped";
	public static PowerManager.WakeLock wl;
	
	private void updateUI(Intent intent) {

		long duration = intent.getLongExtra("duration", 0);
		double distance = intent.getDoubleExtra("distance", 0);
		float speed = intent.getFloatExtra("speed", 0);
		float maxSpeed = intent.getFloatExtra("maxSpeed", 0);

		TrackerMainFragment fragmentMain = (TrackerMainFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_container_main);
			
		if (fragmentMain != null) {
			
			long millis = duration;
			int seconds = (int) (millis / 1000);
        	int minutes = seconds / 60;
        	seconds     = seconds % 60;
        	int hours = minutes / 60;
        	minutes = minutes % 60;
        	
        	TrackerSettings settings = new TrackerSettings(this);
        	
        	if (settings.getUnitId() == 0) {
        		
        		distance = (distance / 1000); // meters to kilometers
        		speed = (speed * 3600 / 1000);
        		maxSpeed = (maxSpeed * 3600 / 1000);
        		
        	} else if (settings.getUnitId() == 1) {
        		
        		distance = (distance / 1609.344); // meters to miles
        		speed = (float) (speed * 2.2369);
        		maxSpeed = (float) (maxSpeed * 2.2369);
        	}

			((TextView) fragmentMain.getView().findViewById(R.id.timeTV))
					.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
			((TextView) fragmentMain.getView().findViewById(R.id.distanceTV))
					.setText(String.format("%.2f", distance));
			((TextView) fragmentMain.getView().findViewById(R.id.curSpeedTV))
					.setText(String.format("%02.0f", speed));
			((TextView) fragmentMain.getView().findViewById(R.id.maxSpeedTV))
					.setText(String.format("%02.0f", maxSpeed));
		}
	}
	
	private void startUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_container_main);

		if (fragmentMain != null) {

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.GONE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
		}
	}
	
	private void stopUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_container_main);

		if (fragmentMain != null) {

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.GONE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.GONE);
			
			((TextView) fragmentMain.getView().findViewById(R.id.timeTV)).setText(R.string.default_value_time);
			((TextView) fragmentMain.getView().findViewById(R.id.distanceTV)).setText(R.string.default_value_distance);
			((TextView) fragmentMain.getView().findViewById(R.id.curSpeedTV)).setText(R.string.default_value_speed);
			((TextView) fragmentMain.getView().findViewById(R.id.maxSpeedTV)).setText(R.string.default_value_speed);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
//		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.dropdown_items,
		        android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(spinnerAdapter, this);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

			if (status.compareTo("started") != 0)
				status = "started";
			
			if (!wl.isHeld())
				enableKeepScreenOn();
			
			updateUI(intent);
		}
	};

	public void startService() {

		Intent intent = new Intent(this, TrackerService.class);
		startService(intent);
	}

	public void stopService() {

		Intent intent = new Intent(this, TrackerService.class);
		stopService(intent);
	}

	@Override
	protected void onDestroy() {

//		Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onPause() {
		
//		Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
		super.onPause();
	}

	@Override
	protected void onRestart() {
		
//		Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
		super.onRestart();
	}

	@Override
	protected void onResume() {
		
//		Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
		super.onResume();
	}

	public boolean onNavigationItemSelected(int position, long id) {

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		TrackerMainFragment frMain = (TrackerMainFragment) fm.findFragmentById(R.id.fragment_container_main);
		TrackerListFragment frList = (TrackerListFragment) fm.findFragmentById(R.id.fragment_container_list);
		
		if (frMain == null)
			ft.add(R.id.fragment_container_main, new TrackerMainFragment());
		if (frList == null)
			ft.add(R.id.fragment_container_list, new TrackerListFragment());
		
		ft.commit();
		
		switch (position) {

		case 0:
			findViewById(R.id.fragment_container_list).setVisibility(View.GONE);
			findViewById(R.id.fragment_container_main).setVisibility(View.VISIBLE);

			return true;
		case 1:
			findViewById(R.id.fragment_container_list).setVisibility(View.VISIBLE);
			findViewById(R.id.fragment_container_main).setVisibility(View.GONE);

			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.menu_tracker_activity, menu);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		Intent settingsActivity = new Intent(TrackerActivity.this,
				TrackerPreferenceActivity.class);
		startActivity(settingsActivity);

		return super.onMenuItemSelected(featureId, item);
	}
	
	public void enableKeepScreenOn() {
		
		TrackerSettings settings = new TrackerSettings(this);
		
		if (settings.isKeepScreenOn())
			wl.acquire();
	}

	public void disableKeepScreenOn() {
		
		if (wl.isHeld())
			wl.release();
	}
	
	public void onButtonClicked(int btn) {

		switch (btn) {

		case TrackerMainFragment.BTN_START:

			if (!locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

				Intent locationSettingsIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(locationSettingsIntent);
			} else {

				progressDialog = ProgressDialog.show(TrackerActivity.this, "",
						"Starting GPS...");
				progressDialog.setCancelable(true);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog
						.setOnCancelListener(new DialogInterface.OnCancelListener() {

							public void onCancel(DialogInterface dialog) {

								stopUI();
								stopService();
								disableKeepScreenOn();
							}
						});

				startUI();
				startService();
				enableKeepScreenOn();
			}

			break;
		case TrackerMainFragment.BTN_STOP:

			stopUI();
			stopService();
			status = "stopped";
			disableKeepScreenOn();

			break;
		case TrackerMainFragment.BTN_PAUSE:

			break;
		default:
			break;
		}
	}

}