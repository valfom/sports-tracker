package com.valfom.tracker;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.valfom.tracker.TrackerMainFragment.OnButtonClickedListener;
import com.valfom.tracker.TrackerMainFragment.OnStatusRestoredListener;

public class TrackerActivity extends FragmentActivity implements
		OnButtonClickedListener, OnStatusRestoredListener {

	public final static String BROADCAST_ACTION = "com.valfom.servicetest";

	private static LocationManager locationManager;
	public static ProgressDialog progressDialog;
	
	public static String state = "stopped";
	public static PowerManager.WakeLock wl;
	
	private long lastBackPressTime = 0;
	private Toast toastOnExit;
	
	final DB db = new DB(this);

	
	private void updateUI(Intent intent) {

		long duration = intent.getLongExtra("duration", 0);
		double distance = intent.getDoubleExtra("distance", 0);
		float speed = intent.getFloatExtra("speed", 0);
		float maxSpeed = intent.getFloatExtra("maxSpeed", 0);
		float avgSpeed = intent.getFloatExtra("avgSpeed", 0);
		double paceLast = intent.getDoubleExtra("paceLast", 0);
		double avgPace = intent.getDoubleExtra("avgPace", 0);

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
	        	
	        	if (settings.getUnitId() == 0) {
	        		
	        		distance = (distance / 1000); // meters to kilometers
	        		speed = (speed * 3600 / 1000);
	        		maxSpeed = (maxSpeed * 3600 / 1000);
	        		avgSpeed = (avgSpeed * 3600 / 1000);
	        		
	        	} else if (settings.getUnitId() == 1) {
	        		
	        		distance = (distance / 1609.344); // meters to miles
	        		speed = (float) (speed * 2.2369);
	        		maxSpeed = (float) (maxSpeed * 2.2369);
	        		avgSpeed = (float) (avgSpeed * 2.2369);
	        	}
	
				((TextView) fragmentMain.getView().findViewById(R.id.timeTV))
						.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
				((TextView) fragmentMain.getView().findViewById(R.id.distanceTV))
						.setText(String.format("%.2f", distance));
				((TextView) fragmentMain.getView().findViewById(R.id.curSpeedTV))
						.setText(String.format("%02.0f", speed));
				((TextView) fragmentMain.getView().findViewById(R.id.maxSpeedTV))
						.setText(String.format("%02.0f", maxSpeed));
				((TextView) fragmentMain.getView().findViewById(R.id.avgSpeedTV))
					.setText(String.format("%02.0f", avgSpeed));
				
				((TextView) fragmentMain.getView().findViewById(R.id.paceLastTV))
					.setText(String.format("%.2f", paceLast));
				((TextView) fragmentMain.getView().findViewById(R.id.avgPaceTV))
					.setText(String.format("%.2f", avgPace));
			}
		}
	}
	
	private void startUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_container); //********************************

		if (fragmentMain != null) {

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.GONE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
		}
	}
	
	private void stopUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_container); //********************************

		if (fragmentMain != null) {

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.GONE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.GONE);
			
			((TextView) fragmentMain.getView().findViewById(R.id.timeTV)).setText(R.string.default_value_time);
			((TextView) fragmentMain.getView().findViewById(R.id.distanceTV)).setText(R.string.default_value_distance);
			((TextView) fragmentMain.getView().findViewById(R.id.curSpeedTV)).setText(R.string.default_value_speed);
			((TextView) fragmentMain.getView().findViewById(R.id.avgSpeedTV)).setText(R.string.default_value_speed);
			((TextView) fragmentMain.getView().findViewById(R.id.maxSpeedTV)).setText(R.string.default_value_speed);
			
			((TextView) fragmentMain.getView().findViewById(R.id.paceLastTV)).setText(R.string.default_value_distance);
			((TextView) fragmentMain.getView().findViewById(R.id.avgPaceTV)).setText(R.string.default_value_distance);
		}
	}
	
	private void pauseUI() {
		
		TrackerMainFragment fragmentMain = (TrackerMainFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_container); //********************************

		if (fragmentMain != null) {
			
			long millis = TrackerService.millis;
			int seconds = (int) (millis / 1000);
        	int minutes = seconds / 60;
        	seconds     = seconds % 60;
        	int hours = minutes / 60;
        	minutes = minutes % 60;
        	
        	TrackerSettings settings = new TrackerSettings(this);
        	
        	double distance = TrackerService.distance;
        	float maxSpeed = TrackerService.maxSpeed;
        	float avgSpeed = TrackerService.avgSpeed;
        	double paceLast = TrackerService.paceLast;
        	double avgPace = TrackerService.avgPace;
        	
        	if (settings.getUnitId() == 0) {
        		
        		distance = (distance / 1000); // meters to kilometers
        		maxSpeed = (maxSpeed * 3600 / 1000);
        		avgSpeed = (avgSpeed * 3600 / 1000);
        		
        	} else if (settings.getUnitId() == 1) {
        		
        		distance = (distance / 1609.344); // meters to miles
        		maxSpeed = (float) (maxSpeed * 2.2369);
        		avgSpeed = (float) (avgSpeed * 2.2369);
        	}

			((TextView) fragmentMain.getView().findViewById(R.id.startBtn)).setVisibility(View.GONE);
			((TextView) fragmentMain.getView().findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
			((TextView) fragmentMain.getView().findViewById(R.id.pauseBtn)).setText(R.string.resume_btn);
			
			((TextView) fragmentMain.getView().findViewById(R.id.timeTV))
				.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
			((TextView) fragmentMain.getView().findViewById(R.id.distanceTV)).setText(String.format("%.2f", distance));
			((TextView) fragmentMain.getView().findViewById(R.id.curSpeedTV)).setText(R.string.default_value_speed);
			((TextView) fragmentMain.getView().findViewById(R.id.avgSpeedTV)).setText(String.format("%02.0f", avgSpeed));
			((TextView) fragmentMain.getView().findViewById(R.id.maxSpeedTV)).setText(String.format("%02.0f", maxSpeed));
			
			((TextView) fragmentMain.getView().findViewById(R.id.paceLastTV)).setText(String.format("%.2f", paceLast));
			((TextView) fragmentMain.getView().findViewById(R.id.avgPaceTV)).setText(String.format("%.2f", avgPace));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			final ActionBar actionBar = getActionBar();
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			
			SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.dropdown_items,
			        android.R.layout.simple_spinner_dropdown_item);
			actionBar.setListNavigationCallbacks(spinnerAdapter, new ActionBar.OnNavigationListener() {
				
				public boolean onNavigationItemSelected(int itemPosition, long itemId) {
					
					FragmentManager fm = getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					
					TrackerInfoFragment frInfo = (TrackerInfoFragment) fm.findFragmentById(R.id.fragment_container_info);
					if (frInfo != null)
						ft.remove(frInfo);
					
					switch (itemPosition) {
					
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
			});
		}
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
		
		//----------
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		ft.replace(R.id.fragment_container, new TrackerMainFragment(), "Main");
		ft.commit();
		//----------
	}
	
	private void goToInfo() {
		
		int trackId = db.getLastId();
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		Fragment listFragment = fragmentManager.findFragmentById(R.id.fragment_container);
		fragmentTransaction.remove(listFragment);

		Fragment infoFragment = new TrackerInfoFragment();

		Bundle args = new Bundle();
		args.putInt("id", trackId);
		args.putString("type", "choise");
		infoFragment.setArguments(args);

		fragmentTransaction.add(R.id.fragment_container_info, infoFragment, "Info");

		fragmentTransaction.addToBackStack(null);
		
		fragmentTransaction.commit();
	}
	
	@Override
	public void onBackPressed() {
		
		TrackerInfoFragment frInfo = (TrackerInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_info);
		
		if ((frInfo == null)) {

			if (this.lastBackPressTime < System.currentTimeMillis() - 2000) {
				
			    toastOnExit = Toast.makeText(this, getString(R.string.general_on_exit), Toast.LENGTH_SHORT);
			    toastOnExit.show();
			    this.lastBackPressTime = System.currentTimeMillis();
			    
			  } else {
			    
				  if (toastOnExit != null)
					  toastOnExit.cancel();
				  
				  super.onBackPressed();
			 }
		} else {
			
			super.onBackPressed();
		}
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			
			if (intent.hasExtra("destroyed")) {
				
				if (!intent.getBooleanExtra("canceled", false))
					goToInfo();
				
			} else {
			
				if (!wl.isHeld())
					enableKeepScreenOn();
			
				updateUI(intent);
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

	@Override
	protected void onDestroy() {
		
		unregisterReceiver(broadcastReceiver);
		
		if (wl.isHeld())
			wl.release();
		
		super.onDestroy();
	}
	
//	@Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            NavUtils.navigateUpFromSameTask(this);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

	public boolean onNavigationItemSelected(int position, long id) {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		TrackerInfoFragment frInfo = (TrackerInfoFragment) fm.findFragmentById(R.id.fragment_container_info);
		if (frInfo != null)
			ft.remove(frInfo);
		
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
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			getMenuInflater().inflate(R.menu.menu_tracker_activity, menu);
		} else {
			
			getMenuInflater().inflate(R.menu.menu_tracker_activity_support, menu);
		}

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			
			Intent settingsActivity = new Intent(TrackerActivity.this,
					TrackerPreferenceActivity.class);
			startActivity(settingsActivity);
			
			return true;
			
		} else {
			
			if (item.getItemId() == 2131296295) {
				
				onNavigationItemSelected(0, 0);
				
			} else if (item.getItemId() == 2131296296) {
				
				onNavigationItemSelected(1, 1);
			} else if (item.getItemId() == 2131296294) {
				
				Intent settingsActivity = new Intent(TrackerActivity.this,
						TrackerPreferenceActivity.class);
				startActivity(settingsActivity);
			}
			
			return true;
		}
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
	
	public void onStatusRestored(String state) {
	
		if (state.compareTo("started") == 0)
			startUI();
		else if (state.compareTo("paused") == 0)
			pauseUI();
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

				progressDialog = ProgressDialog.show(TrackerActivity.this, "", getString(R.string.general_starting_gps));
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
			disableKeepScreenOn();
			
			break;
		case TrackerMainFragment.BTN_PAUSE:

			break;
		default:
			break;
		}
	}
}