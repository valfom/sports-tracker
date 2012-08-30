package com.valfom.tracker;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TrackerActivity extends Activity {
	
	private static boolean isPaused = false;
	long startTime = 0;
	TextView timeTV;
	Timer timer;
	long pauseTime = 0;
	long millis;
	
	private static final int MIN_UPDATE_TIME = 1000;
	private static final int MIN_UPDATE_DISTANCE = 0;
	
	private LocationManager locationManager;
	private Criteria criteria;
	
	Location prevLocation = null;
	
	float maxSpeed = 0;
	double distance = 0;
    
	class secondTask extends TimerTask {

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
        
        timeTV = (TextView) findViewById(R.id.timeTV);
        
        final Button startBtn = (Button) findViewById(R.id.startBtn);
        final Button stopBtn = (Button) findViewById(R.id.stopBtn);
        final Button pauseBtn = (Button) findViewById(R.id.pauseBtn);
        
        stopBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.GONE);
        
        startBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	startBtn.setVisibility(View.GONE);
            	stopBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);
                
                pauseTime = 0;
                startTime = System.currentTimeMillis();
                timer = new Timer();
                timer.schedule(new secondTask(), 0, 1000);
                
                registerListener();
            }
        });
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	timer.cancel();
            	
            	timeTV.setText(R.string.time_default_value);
            	
            	isPaused = false;
            	
            	maxSpeed = 0;
            	distance = 0;
            	
            	startBtn.setVisibility(View.VISIBLE);
            	stopBtn.setVisibility(View.GONE);
                pauseBtn.setVisibility(View.GONE);
                
                unregisterAllListeners();
            }
        });
        
        pauseBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	if (isPaused)
            		pauseBtn.setText(R.string.pause_btn);
            	else
            		pauseBtn.setText(R.string.start_btn);
            	
            	isPaused = !isPaused;
            }
        });
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && 
      		  !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        
        	Toast.makeText(getApplicationContext(), "all providers disabled", Toast.LENGTH_SHORT).show();
        }
        
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
    }
    
    private void updateWithNewLocation(Location location) {
	    
		if (location != null) {
			
//			Toast.makeText(getApplicationContext(), "location", Toast.LENGTH_SHORT).show();
	     
			Float speed = location.getSpeed();
			
			if (speed > maxSpeed) {
				
				maxSpeed = speed;
				
				TextView maxSpeedTV = (TextView) findViewById(R.id.maxSpeedTV);
				maxSpeedTV.setText(String.format("%.1f", speed));
			}
			
			if (prevLocation != null) {
				
				Double lat1 = prevLocation.getLatitude();
				Double lng1 = prevLocation.getLongitude();
				
				Double lat2 = location.getLatitude();
				Double lng2 = location.getLongitude();
				
				distance += distance(lat1, lng1, lat2, lng2);
				
				TextView distanceTV = (TextView) findViewById(R.id.distanceTV);
				distanceTV.setText(String.format("%.2f", distance));
				
//				Toast.makeText(getApplicationContext(), String.valueOf(lat2) + " " + String.valueOf(lng2), Toast.LENGTH_SHORT).show();
			}
			
			prevLocation = location;
			
			TextView curSpeedTV = (TextView) findViewById(R.id.curSpeedTV);
			curSpeedTV.setText(String.format("%.1f", speed));
		}
	}
	
	private LocationListener bestProviderListener = new LocationListener() {
	
		public void onLocationChanged(Location location) {
			
			updateWithNewLocation(location);
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {
	
			registerListener();
		}
		public void onStatusChanged(String provider,int status, Bundle extras) {}
	};
	
	private LocationListener bestAvailableProviderListener = new LocationListener() {
	
		public void onProviderEnabled(String provider) {}
		
		public void onProviderDisabled(String provider) {
	
			registerListener();
		}
	
		public void onLocationChanged(Location location) {

			updateWithNewLocation(location);
		}
		
		public void onStatusChanged(String provider,int status, Bundle extras) {}
	};
	
	private void registerListener() {
		
		unregisterAllListeners();
		
		String bestProvider = locationManager.getBestProvider(criteria, false);
		String bestAvailableProvider = locationManager.getBestProvider(criteria, true);
		
		if (bestProvider == null) {
			
		} else if (bestProvider.equals(bestAvailableProvider)) {
			
			locationManager.requestLocationUpdates(bestAvailableProvider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, bestAvailableProviderListener);
		} else {
		
			locationManager.requestLocationUpdates(bestProvider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, bestProviderListener);
		
			if (bestAvailableProvider != null) {
				locationManager.requestLocationUpdates(bestAvailableProvider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, bestAvailableProviderListener);
			} else {
		
				List<String> allProviders = locationManager.getAllProviders();
				
				for (String provider : allProviders)
					locationManager.requestLocationUpdates(provider, 0, 0,bestProviderListener);
			}
		}
	}
	
	private void unregisterAllListeners() {
		
		locationManager.removeUpdates(bestProviderListener);
		locationManager.removeUpdates(bestAvailableProviderListener);
	}

	private double distance(double llat1, double llong1, double llat2, double llong2) {
		
//		радиус сферы (Земли)
		int rad = 6372795;

//		координаты двух точек
//		double llong1 = 37.497074;
//		double llat1 = 55.618672;
//
//		double llong2 = 37.502776;
//		double llat2 = 55.619391;
		
//		37.499665,55.618978 200
//		37.497074,55.618672 370
		
//		37.502776,55.619391

//		в радианах
		double lat1 = llat1*Math.PI/180;
		double lat2 = llat2*Math.PI/180;
		double long1 = llong1*Math.PI/180;
		double long2 = llong2*Math.PI/180;

//		косинусы и синусы широт и разницы долгот
		double cl1 = Math.cos(lat1);
		double cl2 = Math.cos(lat2);
		double sl1 = Math.sin(lat1);
		double sl2 = Math.sin(lat2);
		double delta = long2 - long1;
		double cdelta = Math.cos(delta);
		double sdelta = Math.sin(delta);

//		вычисления длины большого круга
		double y = Math.sqrt(Math.pow(cl2*sdelta,2)+Math.pow(cl1*sl2-sl1*cl2*cdelta,2));
		double x = sl1*sl2+cl1*cl2*cdelta;
		double ad = Math.atan2(y,x);
		double dist = ad*rad;
		
//		Toast.makeText(getApplicationContext(), String.valueOf(dist), Toast.LENGTH_SHORT).show();

		return dist;
	}
}