package com.valfom.tracker;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.valfom.tracker.TrackerMainFragment.OnButtonClickedListener;
import com.valfom.tracker.TrackerMainFragment.OnStateRestoredListener;

public class TrackerActivity extends SherlockFragmentActivity 
		implements OnButtonClickedListener, OnStateRestoredListener, ActionBar.OnNavigationListener {

	public final static String BROADCAST_ACTION = "com.valfom.tracker.service";

	public static ProgressDialog progressDialog;
	
	public static PowerManager.WakeLock wl;
	
	public static ActionBar actionBar;
	
	private long lastBackPressTime = 0;
	private Toast toastOnExit;
	
	private final DB db = new DB(this);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		actionBar = getSupportActionBar();
		
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.dropdown_items,
		        android.R.layout.simple_spinner_dropdown_item);
		
		actionBar.setListNavigationCallbacks(spinnerAdapter, this);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		unregisterReceiver(broadcastReceiver);
		
		disableKeepScreenOn();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	private void updateUI(Intent intent) {

		long duration = intent.getLongExtra("duration", 0);
		double distance = intent.getDoubleExtra("distance", 0);
		float speed = intent.getFloatExtra("speed", 0);
		float maxSpeed = intent.getFloatExtra("maxSpeed", 0);
		float avgSpeed = intent.getFloatExtra("avgSpeed", 0);
		double maxPace = intent.getDoubleExtra("maxPace", 0);
		double avgPace = intent.getDoubleExtra("avgPace", 0);
		double lossAltitude = intent.getDoubleExtra("lossAltitude", 0);
		double gainAltitude = intent.getDoubleExtra("gainAltitude", 0);
		
		if (getSupportFragmentManager().findFragmentById(R.id.fragment_container).getTag().compareTo("Main") == 0) {
			
			TrackerMainFragment fragmentMain = (TrackerMainFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragment_container);
			
			if (fragmentMain != null) {
				
				long millis = duration;
				int seconds = (int) (millis / 1000);
	        	int minutes = seconds / 60;
	        	seconds     = seconds % 60;
	        	int hours = minutes / 60;
	        	minutes = minutes % 60;
	        	
	        	TrackerSettings settings = new TrackerSettings(this);
	        	
	        	distance = settings.convertDistance(distance);
	        	speed = settings.convertSpeed(speed);
	        	maxSpeed = settings.convertSpeed(maxSpeed);
	        	avgSpeed = settings.convertSpeed(avgSpeed);
	        	
	        	long millisAvgPace = (long) avgPace;
				int secondsAvgPace = (int) (millisAvgPace / 1000);
	        	int minutesAvgPace = secondsAvgPace / 60;
	        	secondsAvgPace = secondsAvgPace % 60;
	        	int hoursAvgPace = minutesAvgPace / 60;
	        	minutesAvgPace = minutesAvgPace % 60;
	        	
	        	long millisMaxPace = (long) maxPace;
				int secondsMaxPace = (int) (millisMaxPace / 1000);
	        	int minutesMaxPace = secondsMaxPace / 60;
	        	secondsMaxPace = secondsMaxPace % 60;
	        	int hoursMaxPace = minutesMaxPace / 60;
	        	minutesMaxPace = minutesMaxPace % 60;
	
				((TextView) fragmentMain.getView().findViewById(R.id.tvDuration))
						.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
				((TextView) fragmentMain.getView().findViewById(R.id.tvDistance))
						.setText(String.format("%.2f", distance));
				((TextView) fragmentMain.getView().findViewById(R.id.tvCurSpeed))
						.setText(String.format("%02.0f", speed));
				((TextView) fragmentMain.getView().findViewById(R.id.tvMaxSpeed))
						.setText(String.format("%02.0f", maxSpeed));
				((TextView) fragmentMain.getView().findViewById(R.id.tvAvgSpeed))
					.setText(String.format("%02.0f", avgSpeed));
				
				if (hoursMaxPace > 0)
					((TextView) fragmentMain.getView().findViewById(R.id.tvMaxPace))
						.setText(String.format("%02d:%02d:%02d",hoursMaxPace, minutesMaxPace, secondsMaxPace));
				else
					((TextView) fragmentMain.getView().findViewById(R.id.tvMaxPace))
						.setText(String.format("%02d:%02d", minutesMaxPace, secondsMaxPace));
				
				if (hoursAvgPace > 0)
					((TextView) fragmentMain.getView().findViewById(R.id.tvAvgPace))
						.setText(String.format("%02d:%02d:%02d",hoursAvgPace, minutesAvgPace, secondsAvgPace));
				else
					((TextView) fragmentMain.getView().findViewById(R.id.tvAvgPace))
						.setText(String.format("%02d:%02d", minutesAvgPace, secondsAvgPace));
				
				((TextView) fragmentMain.getView().findViewById(R.id.tvAltitudeLoss))
					.setText(String.format("%02.0f", lossAltitude));
				((TextView) fragmentMain.getView().findViewById(R.id.tvAltitudeGain))
					.setText(String.format("%02.0f", gainAltitude));
			}
		}
	}
	
	private void startUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_container);

		if (fragmentMain != null) {

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.INVISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
		}
	}
	
	private void stopUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_container);

		if (fragmentMain != null) {

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.GONE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.GONE);
			
			((TextView) fragmentMain.getView().findViewById(R.id.tvDuration)).setText(R.string.default_value_duration);
			((TextView) fragmentMain.getView().findViewById(R.id.tvDistance)).setText(R.string.default_value_distance);
			((TextView) fragmentMain.getView().findViewById(R.id.tvCurSpeed)).setText(R.string.default_value_speed);
			((TextView) fragmentMain.getView().findViewById(R.id.tvAvgSpeed)).setText(R.string.default_value_speed);
			((TextView) fragmentMain.getView().findViewById(R.id.tvMaxSpeed)).setText(R.string.default_value_speed);
			((TextView) fragmentMain.getView().findViewById(R.id.tvMaxPace)).setText(R.string.default_value_pace);
			((TextView) fragmentMain.getView().findViewById(R.id.tvAvgPace)).setText(R.string.default_value_pace);
			((TextView) fragmentMain.getView().findViewById(R.id.tvAltitudeGain)).setText(R.string.default_value_altitude);
			((TextView) fragmentMain.getView().findViewById(R.id.tvAltitudeLoss)).setText(R.string.default_value_altitude);
		}
	}
	
	private void pauseUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_container);

		if (fragmentMain != null) {
			
			long millis = TrackerService.millis;
			int seconds = (int) (millis / 1000);
        	int minutes = seconds / 60;
        	seconds     = seconds % 60;
        	int hours = minutes / 60;
        	minutes = minutes % 60;
        	
        	double distance = TrackerService.distance;
        	float maxSpeed = TrackerService.maxSpeed;
        	float avgSpeed = TrackerService.avgSpeed;
        	double maxPace = TrackerService.maxPace;
        	double avgPace = TrackerService.avgPace;
        	
        	TrackerSettings settings = new TrackerSettings(this);
        	
        	distance = settings.convertDistance(distance);
        	maxSpeed = settings.convertSpeed(maxSpeed);
        	avgSpeed = settings.convertSpeed(avgSpeed);

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.INVISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setText(R.string.btn_resume);
			
			((TextView) fragmentMain.getView().findViewById(R.id.tvDuration))
				.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
			((TextView) fragmentMain.getView().findViewById(R.id.tvDistance)).setText(String.format("%.2f", distance));
			((TextView) fragmentMain.getView().findViewById(R.id.tvCurSpeed)).setText(R.string.default_value_speed);
			((TextView) fragmentMain.getView().findViewById(R.id.tvAvgSpeed)).setText(String.format("%02.0f", avgSpeed));
			((TextView) fragmentMain.getView().findViewById(R.id.tvMaxSpeed)).setText(String.format("%02.0f", maxSpeed));
			
			((TextView) fragmentMain.getView().findViewById(R.id.tvMaxPace)).setText(String.format("%.2f", maxPace));
			((TextView) fragmentMain.getView().findViewById(R.id.tvAvgPace)).setText(String.format("%.2f", avgPace));
		}
	}
	
	private void showInfo() {
		
		int trackId = db.getLastTrackId();
		
		Intent trackInfo = new Intent(TrackerActivity.this, TrackerInfoActivity.class);
		trackInfo.putExtra("trackId", trackId);
		trackInfo.putExtra("choise", true);
		startActivity(trackInfo);
	}
	
	@Override
	public void onBackPressed() {
		
		if (lastBackPressTime < System.currentTimeMillis() - 2000) {
			
		    toastOnExit = Toast.makeText(this, getString(R.string.general_on_exit), Toast.LENGTH_SHORT);
		    toastOnExit.show();
		    lastBackPressTime = System.currentTimeMillis();
		    
		  } else {
		    
			  if (toastOnExit != null) toastOnExit.cancel();
			  
			  super.onBackPressed();
		 }
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
		
			if (intent.hasExtra("destroyed") && (intent.getBooleanExtra("destroyed", true))) {
				
				if (!intent.getBooleanExtra("canceled", true)) showInfo();
				
			} else if (intent.hasExtra("pausedBySpeed")) {
			
				TrackerMainFragment fragmentMain = (TrackerMainFragment) getSupportFragmentManager()
						.findFragmentById(R.id.fragment_container);
				
				if (intent.getBooleanExtra("pausedBySpeed", true))
					((TextView) fragmentMain.getView().findViewById(R.id.tvAutoPause)).setVisibility(View.VISIBLE);
				else
					((TextView) fragmentMain.getView().findViewById(R.id.tvAutoPause)).setVisibility(View.GONE);
					
				Log.d("LALA", "pausedBySpeed");
			} else {
				updateUI(intent);
				Log.d("LALA", "updateUI");
			}
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

	public boolean onNavigationItemSelected(int position, long id) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		switch (position) {
		
		case 0:
			ft.replace(R.id.fragment_container, new TrackerMainFragment(), "Main");
			ft.commit();
			
			return true;
		case 1:
			ft.replace(R.id.fragment_container, new TrackerListFragment(), "List");
			ft.commit();
			
			return true;
		default:
			return false;	
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.menu_tracker_activity, menu);
		
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

			
		Intent settingsActivity = new Intent(TrackerActivity.this,
				TrackerPreferenceActivity.class);
		startActivity(settingsActivity);
		
		return true;
	}
	
	public void enableKeepScreenOn() {
		
		TrackerSettings settings = new TrackerSettings(this);
		
		if (settings.isKeepScreenOn() && !wl.isHeld())
			wl.acquire();
	}
	
	public void disableKeepScreenOn() {
		
		if (wl.isHeld())
			wl.release();
	}
	
	public void onStateRestored(String state) {
	
		if (state.compareTo("started") == 0) {
			startUI();
			
			enableKeepScreenOn();
			
			if (!TrackerService.locationReceived)
				setProgressDialog();
		} else if (state.compareTo("paused") == 0) {
			
			enableKeepScreenOn();
			pauseUI();
		}
	}
	
	public void setProgressDialog() {
		
		if (progressDialog != null)
			progressDialog.dismiss();
		
		progressDialog = ProgressDialog.show(TrackerActivity.this, "", getString(R.string.general_starting_gps));
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					public void onCancel(DialogInterface dialog) {

						stopUI();
						stopService();
						disableKeepScreenOn();
					}
				});
	}
	
	public void onButtonClicked(int btn) {

		switch (btn) {

		case TrackerMainFragment.BTN_START:

			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

				Intent locationSettingsIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(locationSettingsIntent);
			} else {

				setProgressDialog();

				startUI();
				startService();
				enableKeepScreenOn();
			}

			break;
		case TrackerMainFragment.BTN_STOP:

//			stopUI();
			stopService();
			disableKeepScreenOn();
			
			break;
		case TrackerMainFragment.BTN_PAUSE:

			break;
		default:
			break;
		}
	}
}