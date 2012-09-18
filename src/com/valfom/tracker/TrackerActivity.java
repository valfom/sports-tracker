package com.valfom.tracker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
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
	
	private void updateUI(Intent intent) {

		long duration = intent.getLongExtra("duration", 0);
		float distance = intent.getFloatExtra("distance", 0);
		float speed = intent.getFloatExtra("speed", 0);
		float maxSpeed = intent.getFloatExtra("maxSpeed", 0);

		TrackerMainFragment fragmentMain = (TrackerMainFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_container);

		if (fragmentMain != null) {
			
			long millis = duration;
			int seconds = (int) (millis / 1000);
        	int minutes = seconds / 60;
        	seconds     = seconds % 60;
        	int hours = minutes / 60;
        	minutes = minutes % 60;

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
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//	    
//		super.onSaveInstanceState(outState);
//	    outState.putString("status", status);
//	    Log.d("LALA", status + "_save");
//	}
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		
//		super.onRestoreInstanceState(savedInstanceState);
//	    status = savedInstanceState.getString("status");
//	    
//	    if (status == "started") startUI();
//	    else if (status == "stopped") stopUI();
//	    
//	    Log.d("LALA", status + "_restore");
//	}
	
	private void startUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_container);

		if (fragmentMain != null) {

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.GONE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
		}
	}
	
	private void stopUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_container);

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
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.dropdown_items,
		        android.R.layout.simple_spinner_dropdown_item);
		actionBar.setListNavigationCallbacks(spinnerAdapter, this);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

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
		
		switch (position) {

		case 0:
			ft.replace(R.id.fragment_container, new TrackerMainFragment());
			ft.commit();

			return true;
		case 1:
			ft.replace(R.id.fragment_container, new TrackerListFragment());
			ft.commit();

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
							}
						});

				startUI();
				startService();
			}

			break;
		case TrackerMainFragment.BTN_STOP:

			stopUI();
			stopService();

			break;
		case TrackerMainFragment.BTN_PAUSE:

			break;
		default:
			break;
		}
	}

}