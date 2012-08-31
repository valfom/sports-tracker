package com.valfom.tracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class TrackerActivity extends Activity {
	
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
	
	private static boolean flag = false;
	
	/*DEBUG*/
	private static NumberPicker debugRoundNum;
	private static Button clearBtn;
	/*-----*/
	
	class TrackerTimer extends TimerTask {
		
		private long millis = 0;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /*DEBUG*/
        debugRoundNum = (NumberPicker) findViewById(R.id.debugRoundNum);
        debugRoundNum.setMinValue(0);
        debugRoundNum.setMaxValue(50);
        debugRoundNum.setValue(16);
        
        clearBtn = (Button) findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            
            	distance = 0;
            	prevLocation = null;
            	
            	distanceTV.setText(R.string.default_value);
            }
        });
        /*-----*/
        
        timeTV = (TextView) findViewById(R.id.timeTV);
        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        distanceTV = (TextView) findViewById(R.id.distanceTV);
        curSpeedTV = (TextView) findViewById(R.id.curSpeedTV);
        maxSpeedTV = (TextView) findViewById(R.id.maxSpeedTV);
        
        stopBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.GONE);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        startBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            	
            		Intent locationSettingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
            		startActivity(locationSettingsIntent);
            	} else {
	                
	                progressDialog = ProgressDialog.show(TrackerActivity.this, "", "Starting GPS...");
	                progressDialog.setCancelable(true);
	                
	                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){

	                	  public void onCancel(DialogInterface dialog) {
	                	   
	                		  unregisterAllListeners();
	                	  }
	               });
	                
	               registerListener();	                	          
            	}
            }
        });
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {

            	stop();
                
                unregisterAllListeners();
            }
        });
        
        pauseBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	pause();
            }
        });
    }
    
    private void start() {
    	
    	maxSpeed = 0;
    	distance = 0;
    	
    	prevLocation = null;
    	
    	isPaused = false;
    	
    	startBtn.setVisibility(View.GONE);
    	stopBtn.setVisibility(View.VISIBLE);
        pauseBtn.setVisibility(View.VISIBLE);

        timer = new Timer();
        timer.schedule(new TrackerTimer(), 0, 1000);
    }
    
    private void stop() {
    	
    	flag = false;
    	
    	timer.cancel();
    	
    	timeTV.setText(R.string.time_default_value);
    	distanceTV.setText(R.string.default_value);
    	curSpeedTV.setText(R.string.default_value);
    	maxSpeedTV.setText(R.string.default_value);
    	
    	startBtn.setVisibility(View.VISIBLE);
    	stopBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.GONE);
    }
    
    private void pause() {
    	
    	if (isPaused)
    		pauseBtn.setText(R.string.pause_btn);
    	else
    		pauseBtn.setText(R.string.resume_btn);
    	
    	isPaused = !isPaused;
    }
    
    private void updateWithNewLocation(Location location) {
	    
		if (location != null && !isPaused) {
			
			if (!flag) {
			
				flag = true;
				
				progressDialog.dismiss();
				
				start();
			}
	     
			float speed = location.getSpeed();
			
			curSpeedTV.setText(String.format("%.1f", speed));
			
			if (speed > maxSpeed) {
				
				maxSpeed = speed;
				
				maxSpeedTV.setText(String.format("%.1f", speed));
			}
			
			if (prevLocation != null) {
				
				double lat1 = round(prevLocation.getLatitude(), debugRoundNum.getValue());
				double lng1 = round(prevLocation.getLongitude(), debugRoundNum.getValue());
				
				double lat2 = round(location.getLatitude(), debugRoundNum.getValue());
				double lng2 = round(location.getLongitude(), debugRoundNum.getValue());
				
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
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
			Log.d("DIST", "CHANGED");
		}
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
}