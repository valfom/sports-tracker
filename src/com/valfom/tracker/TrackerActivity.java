package com.valfom.tracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class TrackerActivity extends Activity implements ActionBar.OnNavigationListener {
	
	private static final int MIN_UPDATE_TIME = 1000;
	private static final int MIN_UPDATE_DISTANCE = 0;
	
	private static boolean isPaused = false;
	
	private static Timer timer;
	private static LocationManager locationManager;
	private static ProgressDialog progressDialog;
	
	// UI
	private static TextView timeTV;
	private static Button startBtn;
	private static Button stopBtn;
	private static Button pauseBtn;
	private static TextView distanceTV;
	private static TextView curSpeedTV;
	private static TextView maxSpeedTV;
	
	private static Location prevLocation;
	
	private static float maxSpeed = 0;
	private static double distance = 0;
	private long millis = 0;
	private String startDate;
	
	private String units;
	
	public static DatabaseHandler db;
	
	private static boolean flag = false;
	
	TrackerMainFragment frag_main;
	TrackerListFragment frag_list;
	FragmentTransaction fTrans;
	
	public static TrackListAdapter adapter;
	
	class TrackerTimer extends TimerTask {
	
		private long pauseTime = 0;
		private long startTime = System.currentTimeMillis();

		@Override
        public void run() {
        	
            TrackerActivity.this.runOnUiThread(new Runnable() {

                public void run() {
                	
                	if (!isPaused) {
            
	                	millis = System.currentTimeMillis() - startTime - pauseTime;
	                	
	                	int seconds = (int) (millis / 1000);
	                	int minutes = seconds / 60;
	                	seconds     = seconds % 60;
	                	int hours = minutes / 60;
	                	minutes = minutes % 60;
	
	                	timeTV.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                	} else {
                		
                		pauseTime = System.currentTimeMillis() - startTime - millis;
                	}
                }
            });
        }
   };
	
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        db = new DatabaseHandler(this);

        
//        frag_main = new TrackerMainFragment();
//        frag_list = new TrackerListFragment();
//        
//        fTrans = getFragmentManager().beginTransaction();
//        
//        fTrans.add(R.id.frgmCont, frag_main);
//        fTrans.commit();
        
        //------------------------------------------------
        
        ArrayList<HashMap<String, String>> tracksList = new ArrayList<HashMap<String, String>>();
        
//      DatabaseHandler db = new DatabaseHandler(this);
      
      List<Track> allTracks = TrackerActivity.db.getAllTracks();
      
      for (int i = 0; i < allTracks.size(); i++) {

      	HashMap<String, String> map = new HashMap<String, String>();
      	
      	map.put(TrackerListFragment.KEY_ID, String.valueOf(allTracks.get(i).getId()));
          map.put(TrackerListFragment.KEY_DATE, allTracks.get(i).getDate());
          map.put(TrackerListFragment.KEY_DISTANCE, String.valueOf(allTracks.get(i).getDistance() / 1000));
          map.put(TrackerListFragment.KEY_TIME, String.valueOf(allTracks.get(i).getTime() / 1000 / 60 / 60));
          
          tracksList.add(map);
      }
      
      adapter = new TrackListAdapter(TrackerActivity.this, tracksList);
        
        //------------------------------------------------
        
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.title_section_tracker),
                                getString(R.string.title_section_tracks),
                        }),
                this);
        
//        getPreferences();
//        
//        timeTV = (TextView) findViewById(R.id.timeTV);
//        startBtn = (Button) findViewById(R.id.startBtn);
//        stopBtn = (Button) findViewById(R.id.stopBtn);
//        pauseBtn = (Button) findViewById(R.id.pauseBtn);
//        distanceTV = (TextView) findViewById(R.id.distanceTV);
//        curSpeedTV = (TextView) findViewById(R.id.curSpeedTV);
//        maxSpeedTV = (TextView) findViewById(R.id.maxSpeedTV);
//        
//        stopBtn.setVisibility(View.GONE);
//        pauseBtn.setVisibility(View.GONE);
//        
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        
//        startBtn.setOnClickListener(new View.OnClickListener() {
//        	
//            public void onClick(View v) {
//            	
//            	if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            	
//            		Intent locationSettingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
//            		startActivity(locationSettingsIntent);
//            	} else {
//	                
//	                progressDialog = ProgressDialog.show(TrackerActivity.this, "", "Starting GPS...");
//	                progressDialog.setCancelable(true);
//	                
//	                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
//
//	                	  public void onCancel(DialogInterface dialog) {
//	                	   
//	                		  unregisterAllListeners();
//	                	  }
//	               });
//	                
//	               registerListener();	                	          
//            	}
//            }
//        });
//        
//        stopBtn.setOnClickListener(new View.OnClickListener() {
//        	
//            public void onClick(View v) {
//
//            	stop();
//                
//                unregisterAllListeners();
//            }
//        });
//        
//        pauseBtn.setOnClickListener(new View.OnClickListener() {
//        	
//            public void onClick(View v) {
//            	
//            	pause();
//            }
//        });
    }
    
    public boolean onNavigationItemSelected(int position, long id) {
    	
    	FragmentManager fm = getFragmentManager();
    	FragmentTransaction ft = fm.beginTransaction();
    	
    	switch (position) {
    	
    		case 0:
//    	        TrackerMainFragment trackerMainFragment = (TrackerMainFragment) fm.findFragmentById(R.id.container);
    	        
//    	        if (trackerMainFragment == null) {
    	        	
    	        	ft.replace(R.id.container, new TrackerMainFragment());
    	        	ft.commit();
//    	        }
    	        return true;
    		case 1:
//    	        TrackerListFragment trackerListFragment = (TrackerListFragment) fm.findFragmentById(R.id.container);
    	        
//    	        if (trackerListFragment == null) {
    	        	
    	        	ft.replace(R.id.container, new TrackerListFragment());
    	        	ft.commit();
//    	        }
    	        return true;
    	    default:
    	    	return false;
    	}
    }
    
    private void start() {
    	
    	maxSpeed = 0;
    	distance = 0;
    	
    	prevLocation = null;
    	
    	isPaused = false;
    	
    	startBtn.setVisibility(View.GONE);
    	stopBtn.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);
        
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy H:m:s");
        startDate = df.format(new Date());

        timer = new Timer();
        timer.schedule(new TrackerTimer(), 0, 1000);
    }
    
    private void stop() {
    	
    	flag = false;
    	
    	timer.cancel();
    	timer = null;
    	
    	timeTV.setText(R.string.time_default_value);
    	distanceTV.setText(R.string.default_value);
    	curSpeedTV.setText(R.string.default_value);
    	maxSpeedTV.setText(R.string.default_value);
    	
    	startBtn.setVisibility(View.VISIBLE);
    	stopBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.GONE);
        
//        DatabaseHandler db = new DatabaseHandler(this);
        
        db.addTrack(new Track(startDate, distance, millis, maxSpeed));
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {

    	getMenuInflater().inflate(R.menu.menu_tracker_activity, menu);
            
    	return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
//		if (item.getItemId() == 0) {
		
			Intent settingsActivity = new Intent(TrackerActivity.this, TrackerPreferenceActivity.class);
			startActivity(settingsActivity);
//		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	private void pause() {
    	
    	if (isPaused)
    		pauseBtn.setText(R.string.pause_btn);
    	else
    		pauseBtn.setText(R.string.resume_btn);
    	
    	curSpeedTV.setText(R.string.default_value);
    	
    	isPaused = !isPaused;
    	
    	prevLocation = null;
    }
    
    private void updateWithNewLocation(Location location) {
	    
		if (location != null && !isPaused) {
			
			if (!flag) {
			
				flag = true;
				
				progressDialog.dismiss();
				
				start();
			}
	     
			float speedMPerS = location.getSpeed();
			float speedKPerH = speedMPerS * 3600 / 1000;
			
			
			curSpeedTV.setText(String.format("%.1f", speedKPerH));
			
			if (speedMPerS > maxSpeed) {
				
				maxSpeed = speedMPerS;
				
				maxSpeedTV.setText(String.format("%.1f", speedKPerH));
			}
			
			if ((prevLocation != null) && (speedMPerS != 0)) {
				
				double lat1 = round(prevLocation.getLatitude(), 16);
				double lng1 = round(prevLocation.getLongitude(), 16);
				
				double lat2 = round(location.getLatitude(), 16);
				double lng2 = round(location.getLongitude(), 16);
				
				distance += calculateDistance(lat1, lng1, lat2, lng2);
				
				distanceTV.setText(String.format("%.2f", distance));
				
//				Log.d("DIST", lat1 + " " + lng1 + " - " + lat2 + " " + lng2 + " = " + calculateDistance(lat1, lng1, lat2, lng2));
			}
			
			prevLocation = location;
		}
	}
    
    @Override
	protected void onDestroy() {
    
    	super.onDestroy();
    	unregisterAllListeners();
    	
    	if (timer != null)
    		timer.cancel();
	}

	public double round(double d, int p) {

    	return new BigDecimal(d).setScale(p, RoundingMode.HALF_UP).doubleValue();
	}
    
    private LocationListener gpsProviderListener = new LocationListener() {
    	
		public void onLocationChanged(Location location) {
			
			updateWithNewLocation(location);
		}
		
		public void onProviderDisabled(String provider) {}
		
		public void onProviderEnabled(String provider) {
	
			registerListener();
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
    
    private void unregisterAllListeners() {
    		
		locationManager.removeUpdates(gpsProviderListener);
	}
    	
	private void registerListener() {
		
		unregisterAllListeners();
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, gpsProviderListener);		
	}
	
	private double calculateDistance(double llat1, double llong1, double llat2, double llong2) {
		
		// http://gis-lab.info/qa/great-circles.html
		
		int rad = 6372795;

		double lat1 = llat1 * Math.PI / 180;
		double lat2 = llat2 * Math.PI / 180;
		double long1 = llong1 * Math.PI / 180;
		double long2 = llong2 * Math.PI / 180;

		double cl1 = Math.cos(lat1);
		double cl2 = Math.cos(lat2);
		double sl1 = Math.sin(lat1);
		double sl2 = Math.sin(lat2);
		double delta = long2 - long1;
		double cdelta = Math.cos(delta);
		double sdelta = Math.sin(delta);

		double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
		double x = sl1 * sl2 + cl1 * cl2 * cdelta;
		double ad = Math.atan2(y, x);
		double dist = ad * rad;
		
//		Toast.makeText(getApplicationContext(), String.valueOf(dist), Toast.LENGTH_SHORT).show();

		return dist;
	}
	
	private void getPreferences() {
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        units = prefs.getString("units", "");
        
        Boolean keepScreenOn = prefs.getBoolean("keepScreenOn", false);
        
        if (keepScreenOn)
        	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}
}