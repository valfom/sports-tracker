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
import android.view.Menu;
import android.view.MenuItem;
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
//		float distance = intent.getFloatExtra("distance", 0);
//		float speed = intent.getFloatExtra("speed", 0);
//		float maxSpeed = intent.getFloatExtra("maxSpeed", 0);

		TrackerMainFragment fragmentMain = (TrackerMainFragment) getFragmentManager()
				.findFragmentById(R.id.container_tracker);

		if (fragmentMain != null) {
			
			long millis = duration;
			int seconds = (int) (millis / 1000);
        	int minutes = seconds / 60;
        	seconds     = seconds % 60;
        	int hours = minutes / 60;
        	minutes = minutes % 60;

			((TextView) fragmentMain.getView().findViewById(R.id.timeTV))
					.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
//			((TextView) fragmentMain.getView().findViewById(R.id.distanceTV))
//					.setText(String.valueOf(distance));
//			((TextView) fragmentMain.getView().findViewById(R.id.curSpeedTV))
//					.setText(String.valueOf(speed));
//			((TextView) fragmentMain.getView().findViewById(R.id.maxSpeedTV))
//					.setText(String.valueOf(maxSpeed));
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

		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {

				updateUI(intent);
			}
		};

		IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);

		registerReceiver(broadcastReceiver, intentFilter);
	}

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

		super.onDestroy();
	}

	public boolean onNavigationItemSelected(int position, long id) {

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		switch (position) {

		case 0:
			TrackerListFragment fragmentList = (TrackerListFragment) fm
					.findFragmentById(R.id.container_track_list);
			if (fragmentList != null)
				ft.remove(fragmentList);

			ft.add(R.id.container_tracker, new TrackerMainFragment());
			ft.commit();

			return true;
		case 1:

			TrackerMainFragment fragmentMain = (TrackerMainFragment) fm
					.findFragmentById(R.id.container_tracker);
			if (fragmentMain != null)
				ft.remove(fragmentMain);

			ft.add(R.id.container_track_list, new TrackerListFragment());
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

								stopService();
							}
						});

				startService();
			}

			break;
		case TrackerMainFragment.BTN_STOP:

			stopService();

			break;
		case TrackerMainFragment.BTN_PAUSE:

			break;
		default:
			break;
		}
	}

}