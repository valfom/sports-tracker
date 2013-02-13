package com.valfom.tracker;

import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.Overlay;
import com.valfom.tracker.TrackerMainFragment.OnButtonClickedListener;
import com.valfom.tracker.TrackerMapFragment.OnStateRestoredListener;

public class TrackerMainActivity extends SherlockFragmentActivity 
		implements OnButtonClickedListener, OnStateRestoredListener, ActionBar.TabListener, TrackerSelectActivityDialogFragment.SelectActivityDialogListener {

	public static final String BROADCAST_ACTION = "com.valfom.tracker.service";

	private ProgressDialog progressDialog;
	
	private SensorManager mSensorManager;
	private TrackerShakeEventListener mShakeEventListener;
	private Vibrator vibrator;
	
	private long lastBackPressTime = 0;
	private Toast toastOnExit;
	
	private TrackerSectionsPagerAdapter sectionsPagerAdapter;
	protected TrackerViewPager viewPager;
	
	private static boolean started = false; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
        sectionsPagerAdapter = new TrackerSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getSupportActionBar();
        
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        viewPager = (TrackerViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            
        	@Override
            public void onPageSelected(int position) {
                
        		actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++)
        	
        	actionBar.addTab(actionBar.newTab()
        			.setText(sectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        
        viewPager.setCurrentItem(1);
        
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mShakeEventListener = new TrackerShakeEventListener();

		mShakeEventListener.setOnShakeListener(new TrackerShakeEventListener.OnShakeListener() {

			public void onShake() {
				
//				vibrator.vibrate(100);

				int tabId = viewPager.getCurrentItem();
				
				if ((tabId == 0) || (tabId == 1)) {
				
			        Intent buttons = new Intent(TrackerMainActivity.this, TrackerButtonsActivity.class);
			        buttons.putExtra("tabId", tabId);
			        buttons.putExtra("started", started);
			        
			        if (tabId == 0) buttons.putExtra("isMapLocked", viewPager.isSwipingEnabled());
			        
			        startActivityForResult(buttons, 1);
				}
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent result) {

		if (result != null) {
			
			int btnId = result.getIntExtra("btnId", -1);
			
			switch (btnId) {
			
				case R.id.ivMapSatelliteDialog:
					ImageView ivMap = (ImageView) viewPager.getChildAt(1).findViewById(R.id.btnMap);
					ivMap.performClick();
					break;
					
				case R.id.ivLockUnlockDialog:
					ImageView ivLock = (ImageView) viewPager.getChildAt(1).findViewById(R.id.btnLock);
					ivLock.performClick();
					break;
					
				case R.id.ivMyLocationDialog:
					ImageView ivLocation = (ImageView) viewPager.getChildAt(1).findViewById(R.id.btnMyLocation);
					ivLocation.performClick();
					break;
					
				case R.id.ivAddMarkerDialog:
					ImageView ivAddMarker = (ImageView) viewPager.getChildAt(1).findViewById(R.id.btnAddMarker);
					ivAddMarker.performClick();
					break;
					
				case R.id.ivStartDialog:
					Button btnStart = (Button) viewPager.getChildAt(0).findViewById(R.id.startBtn);
					btnStart.performClick();
					break;
					
				case R.id.ivStopDialog:
					Button btnStop = (Button) viewPager.getChildAt(0).findViewById(R.id.stopBtn);
					btnStop.performClick();
					break;
				
				case R.id.ivPauseDialog:
					Button btnPause = (Button) viewPager.getChildAt(0).findViewById(R.id.pauseBtn);
					btnPause.performClick();
					break;
					
				default:
					break;
			}
		}
	}

	@Override
	protected void onPause() {
		
		TrackerSettings settings = new TrackerSettings(this);
		
		if (settings.isShaking())
			mSensorManager.unregisterListener(mShakeEventListener);
		
		unregisterReceiver(broadcastReceiver);
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		
		IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
		registerReceiver(broadcastReceiver, intentFilter);
		
		TrackerSettings settings = new TrackerSettings(this);
		
		if (settings.isShaking())
			mSensorManager.registerListener(mShakeEventListener,
					mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_UI);
	}

	private void updateUI(Intent intent) {

		drawRoute();
		
		long duration = intent.getLongExtra("duration", 0);
		double distance = intent.getDoubleExtra("distance", 0);
		float speed = intent.getFloatExtra("speed", 0);
		float maxSpeed = intent.getFloatExtra("maxSpeed", 0);
		float avgSpeed = intent.getFloatExtra("avgSpeed", 0);
		double maxPace = intent.getDoubleExtra("maxPace", 0);
		double avgPace = intent.getDoubleExtra("avgPace", 0);
		double lossAltitude = intent.getDoubleExtra("lossAltitude", 0);
		double gainAltitude = intent.getDoubleExtra("gainAltitude", 0);
		double minAltitude = intent.getDoubleExtra("minAltitude", 0);
		double maxAltitude = intent.getDoubleExtra("maxAltitude", 0);
		
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
    	
    	View v = viewPager.getChildAt(0);
    	
		((TextView) v.findViewById(R.id.tvDuration))
				.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
		((TextView) v.findViewById(R.id.tvDistance))
				.setText(String.format("%.2f", distance));
		((TextView) v.findViewById(R.id.tvCurSpeed))
				.setText(String.format("%02.0f", speed));
		((TextView) v.findViewById(R.id.tvMaxSpeed))
				.setText(String.format("%02.0f", maxSpeed));
		((TextView) v.findViewById(R.id.tvAvgSpeed))
			.setText(String.format("%02.0f", avgSpeed));
		
		if (hoursMaxPace > 0)
			((TextView) v.findViewById(R.id.tvMaxPace))
				.setText(String.format("%02d:%02d:%02d",hoursMaxPace, minutesMaxPace, secondsMaxPace));
		else
			((TextView) v.findViewById(R.id.tvMaxPace))
				.setText(String.format("%02d:%02d", minutesMaxPace, secondsMaxPace));
		
		if (hoursAvgPace > 0)
			((TextView) v.findViewById(R.id.tvAvgPace))
				.setText(String.format("%02d:%02d:%02d",hoursAvgPace, minutesAvgPace, secondsAvgPace));
		else
			((TextView) v.findViewById(R.id.tvAvgPace))
				.setText(String.format("%02d:%02d", minutesAvgPace, secondsAvgPace));
		
		((TextView) v.findViewById(R.id.tvAltitudeLoss))
			.setText(String.format("%02.0f", lossAltitude));
		((TextView) v.findViewById(R.id.tvAltitudeGain))
			.setText(String.format("%02.0f", gainAltitude));
		
		((TextView) v.findViewById(R.id.tvMaxAltitude))
				.setText(String.format("%02.0f", maxAltitude));
		((TextView) v.findViewById(R.id.tvMinAltitude))
				.setText(String.format("%02.0f", minAltitude));
		
		View vMap = viewPager.getChildAt(1);
		
		((TextView) vMap.findViewById(R.id.tvDurationMap))
				.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
		((TextView) vMap.findViewById(R.id.tvDistanceMap))
				.setText(String.format("%.2f", distance));
	}
	
	private void drawRoute() {
		
		List<Overlay> overlays = TrackerMapFragment.mapView.getOverlays();
		
		Overlay myLocationOverlay = null;
		Overlay itemizedOverlay = null;
		
		if (overlays.size() == 1) myLocationOverlay = overlays.get(0);
		else if (overlays.size() == 3) {
			
			myLocationOverlay = overlays.get(1);
			itemizedOverlay = overlays.get(2);
			
		} else myLocationOverlay = overlays.get(1);
			
		overlays.clear();
		
		overlays.add(0, new TrackerRouteOverlay());
		
		overlays.add(1, myLocationOverlay);
		
		if (itemizedOverlay != null) overlays.add(itemizedOverlay);
	}
	
	private void startUI() {
		
		View vMain = viewPager.getChildAt(0);
		
		((TextView) vMain.findViewById(R.id.startBtn)).setVisibility(View.GONE);
		((TextView) vMain.findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
		((TextView) vMain.findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
		
		View vMap = viewPager.getChildAt(1);
		
		((RelativeLayout) vMap.findViewById(R.id.rlAddMarker)).setVisibility(View.VISIBLE);
	}
	
	private void clearMap() {
		
		List<Overlay> mapOverlays = null;
		Overlay myLocationOverlay = null;
		
		mapOverlays = TrackerMapFragment.mapView.getOverlays();
		
		myLocationOverlay = mapOverlays.get(0);
		
		mapOverlays.clear();
		mapOverlays.add(myLocationOverlay);
	}
	
	private void stopUI() {
		
		clearMap();
		
		if (viewPager.getCurrentItem() == 1) {
			
			View vMain = viewPager.getChildAt(0);
		
			((TextView) vMain.findViewById(R.id.startBtn)).setVisibility(View.VISIBLE);
			((TextView) vMain.findViewById(R.id.stopBtn)).setVisibility(View.GONE);
			((TextView) vMain.findViewById(R.id.pauseBtn)).setVisibility(View.GONE);
			
			((TextView) vMain.findViewById(R.id.tvDuration)).setText(R.string.default_value_duration);
			((TextView) vMain.findViewById(R.id.tvDistance)).setText(R.string.default_value_distance);
			((TextView) vMain.findViewById(R.id.tvCurSpeed)).setText(R.string.default_value_speed);
			((TextView) vMain.findViewById(R.id.tvAvgSpeed)).setText(R.string.default_value_speed);
			((TextView) vMain.findViewById(R.id.tvMaxSpeed)).setText(R.string.default_value_speed);
			((TextView) vMain.findViewById(R.id.tvMaxPace)).setText(R.string.default_value_pace);
			((TextView) vMain.findViewById(R.id.tvAvgPace)).setText(R.string.default_value_pace);
			((TextView) vMain.findViewById(R.id.tvAltitudeGain)).setText(R.string.default_value_altitude);
			((TextView) vMain.findViewById(R.id.tvAltitudeLoss)).setText(R.string.default_value_altitude);
			((TextView) vMain.findViewById(R.id.tvMaxAltitude)).setText(R.string.default_value_altitude);
			((TextView) vMain.findViewById(R.id.tvMinAltitude)).setText(R.string.default_value_altitude);
			
			((TextView) vMain.findViewById(R.id.tvAutoPause)).setVisibility(View.GONE);
			
			View vMap = viewPager.getChildAt(1);
			
			((TextView) vMap.findViewById(R.id.tvDurationMap)).setText(R.string.default_value_duration);
			((TextView) vMap.findViewById(R.id.tvDistanceMap)).setText(R.string.default_value_distance);
			
			((RelativeLayout) vMap.findViewById(R.id.rlAddMarker)).setVisibility(View.GONE);
		}
	}
	
	private void pauseUI() {
		
		if (viewPager.getCurrentItem() == 1) {
			
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
        	double gainAltitude = TrackerService.altitudeGain;
        	double lossAltitude = TrackerService.altitudeLoss;
        	double maxAltitude = (TrackerService.maxAltitude != null) ? TrackerService.maxAltitude : 0;
        	double minAltitude = (TrackerService.minAltitude != null) ? TrackerService.minAltitude : 0;
        	
        	TrackerSettings settings = new TrackerSettings(this);
        	
        	distance = settings.convertDistance(distance);
        	maxSpeed = settings.convertSpeed(maxSpeed);
        	avgSpeed = settings.convertSpeed(avgSpeed);
        	
        	View v = viewPager.getChildAt(0);
        	
			((TextView) v.findViewById(R.id.startBtn)).setVisibility(View.GONE);
			((TextView) v.findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) v.findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
			((TextView) v.findViewById(R.id.pauseBtn)).setText(R.string.btn_resume);
			
			((TextView) v.findViewById(R.id.tvDuration)).setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
			((TextView) v.findViewById(R.id.tvDistance)).setText(String.format("%.2f", distance));
			
			((TextView) v.findViewById(R.id.tvCurSpeed)).setText(R.string.default_value_speed);
			((TextView) v.findViewById(R.id.tvAvgSpeed)).setText(String.format("%02.0f", avgSpeed));
			((TextView) v.findViewById(R.id.tvMaxSpeed)).setText(String.format("%02.0f", maxSpeed));
			
			((TextView) v.findViewById(R.id.tvMaxPace)).setText(String.format("%.2f", maxPace));
			((TextView) v.findViewById(R.id.tvAvgPace)).setText(String.format("%.2f", avgPace));
			
			((TextView) v.findViewById(R.id.tvAltitudeLoss)).setText(String.format("%02.0f", lossAltitude));
			((TextView) v.findViewById(R.id.tvAltitudeGain)).setText(String.format("%02.0f", gainAltitude));
		
			((TextView) v.findViewById(R.id.tvMaxAltitude)).setText(String.format("%02.0f", maxAltitude));
			((TextView) v.findViewById(R.id.tvMinAltitude)).setText(String.format("%02.0f", minAltitude));
		}
	}
	
	private void showInfo() {
		
		TrackerDB db = new TrackerDB(this);
		
		int trackId = db.getLastTrackId();
		
		Intent info = new Intent(this, TrackerInfoActivity.class);
		info.putExtra("trackId", trackId);
		info.putExtra("choise", true);
		
		startActivity(info);
	}
	
	private void autoPauseUI(boolean pausedBySpeed) {
		
		View v = viewPager.getChildAt(0);
		
		((TextView) v.findViewById(R.id.tvCurSpeed)).setText(R.string.default_value_speed);
		
		if (pausedBySpeed)
			((TextView) v.findViewById(R.id.tvAutoPause)).setVisibility(View.VISIBLE);
		else
			((TextView) v.findViewById(R.id.tvAutoPause)).setVisibility(View.GONE);
	}
	
	public void onStateRestored(String state) {
		
		if (state.compareTo("started") == 0) {
			
			startUI();
			
			if (!TrackerService.isLocationReceived) setProgressDialog();
			
		} else if (state.compareTo("paused") == 0) {
			
			pauseUI();
		}
	}
	
	public void onButtonClicked(int btn) {

		switch (btn) {

		case TrackerMainFragment.BTN_START:

			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

				Intent locationSettings = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(locationSettings);
			} else {

				setProgressDialog();

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
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

			if (progressDialog != null)
				progressDialog.dismiss();
			
			if (intent.hasExtra("destroyed") && (intent.getBooleanExtra("destroyed", true))) {
				
				if (!intent.getBooleanExtra("canceled", true)) showInfo();
				
			} else if (intent.hasExtra("pausedBySpeed")) {
				
				autoPauseUI(intent.getBooleanExtra("pausedBySpeed", false));
					
			} else updateUI(intent);
		}
	};

	public void setProgressDialog() {
		
		if (progressDialog != null) progressDialog.dismiss();
		
		progressDialog = ProgressDialog.show(TrackerMainActivity.this, "", getString(R.string.general_starting_gps));
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					public void onCancel(DialogInterface dialog) {

						stopUI();
						stopService();
					}
				});
	}
	
	public void startService() {

		started = true;
		
		Intent service = new Intent(this, TrackerService.class);
		startService(service);
	}

	public void stopService() {

		started = false;
		
		Intent service = new Intent(this, TrackerService.class);
		stopService(service);
	}
	
	public class TrackerSectionsPagerAdapter extends FragmentPagerAdapter {

	    public TrackerSectionsPagerAdapter(FragmentManager fm) {
	    	
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int i) {
	    	
			switch (i) {
			
				case 0:
					return new TrackerMapFragment();
				case 1:
					return new TrackerMainFragment();
				case 2:
					return new TrackerListFragment();
				default:
					return new TrackerMainFragment();	
			}
	    }

	    @Override
	    public int getCount() {
	    	
	        return 3;
	    }

	    public Integer getPageIcon(int position) {
	    	
	        switch (position) {
	        
	            case 0: return R.drawable.ic_map;
	            case 1: return R.drawable.ic_map_marker;
	            case 2: return R.drawable.ic_map_my_location;
	        }
	        
	        return null;
	    }
	    
	    @Override
	    public CharSequence getPageTitle(int position) {
	    	
	        switch (position) {
	        
	            case 0: return getString(R.string.tab_map).toUpperCase();
	            case 1: return getString(R.string.tab_main).toUpperCase();
	            case 2: return getString(R.string.tab_list).toUpperCase();
	        }
	        
	        return null;
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
			
		Intent settings = new Intent(this, TrackerPreferenceActivity.class);
		startActivity(settings);
		
		return true;
	}
	
	@Override
	public void onBackPressed() {
		
		if (lastBackPressTime < (System.currentTimeMillis() - 2000)) {
			
		    toastOnExit = Toast.makeText(this, getString(R.string.general_on_exit), Toast.LENGTH_SHORT);
		    toastOnExit.show();
		    lastBackPressTime = System.currentTimeMillis();
		    
		  } else {
		    
			  if (toastOnExit != null) toastOnExit.cancel();
			  
			  super.onBackPressed();
		 }
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			
			if (viewPager.getCurrentItem() < (viewPager.getChildCount() - 1))
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
			
			return true;
			
		case KeyEvent.KEYCODE_DPAD_LEFT:
			
			if (viewPager.getCurrentItem() > 0)
				viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
			
			return true;
			
		default:
			return super.onKeyUp(keyCode, event);
		}
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
		viewPager.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public void onActivitySelected() {
		
		View vMain = viewPager.getChildAt(0);
		
		TextView tvActivity = ((TextView) vMain.findViewById(R.id.tvActivity));
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		int activityId = sharedPreferences.getInt("activity", 0);
		
		String[] activities = getResources().getStringArray(R.array.activities_array);
	    
	    tvActivity.setText(activities[activityId]);
	}
}