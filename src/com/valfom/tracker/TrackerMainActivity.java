package com.valfom.tracker;

import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
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
import com.valfom.tracker.TrackerMainFragment.OnStateRestoredListener;

public class TrackerMainActivity extends SherlockFragmentActivity 
		implements OnButtonClickedListener, OnStateRestoredListener, ActionBar.TabListener {

	public final static String BROADCAST_ACTION = "com.valfom.tracker.service";

	public static ProgressDialog progressDialog;
	
	public static PowerManager.WakeLock wl;
	
	private long lastBackPressTime = 0;
	private Toast toastOnExit;
	
	TrackerSectionsPagerAdapter sectionsPagerAdapter;
	TrackerViewPager viewPager;

	private final TrackerDB db = new TrackerDB(this);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
        sectionsPagerAdapter = new TrackerSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getSupportActionBar();
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

        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            
        	actionBar.addTab(actionBar.newTab()
        			.setText(sectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
        
        viewPager.setCurrentItem(1);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (viewPager.getCurrentItem() < viewPager.getChildCount())
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
    	
//        	Typeface font = Typeface.createFromAsset(getAssets(), "Prosto.ttf");  
    	
    	View v = viewPager.getChildAt(0);
    	
//        	TextView dur = (TextView) v.findViewById(R.id.tvDuration);
//        	dur.setTypeface(font);
//        	dur.setTextSize(40);
//
//        	TextView dist = (TextView) v.findViewById(R.id.tvDistance);
//        	dist.setTypeface(font);
//        	dist.setTextSize(40);
//    	
//        	TextView d = (TextView) v.findViewById(R.id.tvDistanceUnit);
//        	d.setTypeface(font);
//        	d.setTextSize(10);
//    	
//        	TextView du = (TextView) v.findViewById(R.id.tvDurationTitle);
//        	du.setTypeface(font);
//        	du.setTextSize(10);
//    	
//        	TextView duq = (TextView) v.findViewById(R.id.tvMaxSpeedUnit);
//        	duq.setTypeface(font);
//        	duq.setTextSize(10);
    	
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
		
		View v1 = viewPager.getChildAt(1);
		
		((TextView) v1.findViewById(R.id.tvDurationMap))
				.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
		((TextView) v1.findViewById(R.id.tvDistanceMap))
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
		
		overlays.add(0, new TrackerRouteOverlay(TrackerRouteOverlay.FLAGS_MODE_START));
		
		overlays.add(1, myLocationOverlay);
		
		if (itemizedOverlay != null) overlays.add(itemizedOverlay);
	}
	
	private void startUI() {
		
		if (viewPager.getCurrentItem() == 1) {
			
			View v = viewPager.getChildAt(0);
			
			((TextView) v.findViewById(R.id.startBtn)).setVisibility(View.GONE);
			((TextView) v.findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) v.findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
			
			View v1 = viewPager.getChildAt(1);
			
			((RelativeLayout) v1.findViewById(R.id.rlAddMarker)).setVisibility(View.VISIBLE);
		}
	}
	
	private void stopUI() {
		
		if (viewPager.getCurrentItem() == 1) {
			
			View v = viewPager.getChildAt(0);
		
			((TextView) v.findViewById(R.id.startBtn)).setVisibility(View.VISIBLE);
			((TextView) v.findViewById(R.id.stopBtn)).setVisibility(View.GONE);
			((TextView) v.findViewById(R.id.pauseBtn)).setVisibility(View.GONE);
			
			((TextView) v.findViewById(R.id.tvDuration)).setText(R.string.default_value_duration);
			((TextView) v.findViewById(R.id.tvDistance)).setText(R.string.default_value_distance);
			((TextView) v.findViewById(R.id.tvCurSpeed)).setText(R.string.default_value_speed);
			((TextView) v.findViewById(R.id.tvAvgSpeed)).setText(R.string.default_value_speed);
			((TextView) v.findViewById(R.id.tvMaxSpeed)).setText(R.string.default_value_speed);
			((TextView) v.findViewById(R.id.tvMaxPace)).setText(R.string.default_value_pace);
			((TextView) v.findViewById(R.id.tvAvgPace)).setText(R.string.default_value_pace);
			((TextView) v.findViewById(R.id.tvAltitudeGain)).setText(R.string.default_value_altitude);
			((TextView) v.findViewById(R.id.tvAltitudeLoss)).setText(R.string.default_value_altitude);
			((TextView) v.findViewById(R.id.tvMaxAltitude)).setText(R.string.default_value_altitude);
			((TextView) v.findViewById(R.id.tvMinAltitude)).setText(R.string.default_value_altitude);
			
			((TextView) v.findViewById(R.id.tvAutoPause)).setVisibility(View.GONE);
			
			View v1 = viewPager.getChildAt(1);
			
			((TextView) v1.findViewById(R.id.tvDurationMap)).setText(R.string.default_value_duration);
			((TextView) v1.findViewById(R.id.tvDistanceMap)).setText(R.string.default_value_distance);
			
			((RelativeLayout) v1.findViewById(R.id.rlAddMarker)).setVisibility(View.GONE);
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
        	double maxAltitude = TrackerService.maxAltitude;
        	double minAltitude = TrackerService.minAltitude;
        	
        	TrackerSettings settings = new TrackerSettings(this);
        	
        	distance = settings.convertDistance(distance);
        	maxSpeed = settings.convertSpeed(maxSpeed);
        	avgSpeed = settings.convertSpeed(avgSpeed);
        	
        	View v = viewPager.getChildAt(0);
        	
			((TextView) v.findViewById(R.id.startBtn)).setVisibility(View.GONE);
			((TextView) v.findViewById(R.id.stopBtn)).setVisibility(View.VISIBLE);
			((TextView) v.findViewById(R.id.pauseBtn)).setVisibility(View.VISIBLE);
			((TextView) v.findViewById(R.id.pauseBtn)).setText(R.string.btn_resume);
			
			((TextView) v.findViewById(R.id.tvDuration))
					.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
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
		
		int trackId = db.getLastTrackId();
		
		Intent trackInfo = new Intent(TrackerMainActivity.this, TrackerInfoActivity.class);
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
				
				View v = viewPager.getChildAt(0);
				
				((TextView) v.findViewById(R.id.tvCurSpeed)).setText(R.string.default_value_speed);
				
				if (intent.getBooleanExtra("pausedBySpeed", false))
					((TextView) v.findViewById(R.id.tvAutoPause)).setVisibility(View.VISIBLE);
				else
					((TextView) v.findViewById(R.id.tvAutoPause)).setVisibility(View.GONE);
					
			} else updateUI(intent);
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

			
		Intent settingsActivity = new Intent(TrackerMainActivity.this,
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
			
			if (!TrackerService.locationReceived) setProgressDialog();
			
		} else if (state.compareTo("paused") == 0) {
			
			enableKeepScreenOn();
			pauseUI();
		}
	}
	
	public void setProgressDialog() {
		
		if (progressDialog != null) progressDialog.dismiss();
		
		progressDialog = ProgressDialog.show(TrackerMainActivity.this, "", getString(R.string.general_starting_gps));
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
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
		viewPager.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
}