package com.valfom.tracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class TrackerService extends Service {

	private static final int MIN_UPDATE_TIME = 1000;
	private static final int MIN_UPDATE_DISTANCE = 0;
	
	ExecutorService es;
	private static LocationManager locationManager;
	
	private static Timer timer;
	public static boolean isPaused = false;
	public static boolean isPausedBySpeed = false;
	public static Location prevLocation;
	public static long millis = 0;
	public static double distance = 0;
	private static boolean flag = false;
	
	private String startDate;
	
	// Speed
	public static float speed = 0;
	
	public static float maxSpeed = 0;
	
	public static float avgSpeed = 0;
	public static float avgSpeedSum = 0;
	public static int avgSpeedCounter = 0;
	
	//Pace
	public static double paceLast = 0;
	public static float timeStartLast = 0;
	public static double distanceStartLast = 0;
	
	public static double avgPace = 0;
	public static double avgPaceSum = 0;
	public static int avgPaceCounter = 0;
	
    Intent intent1;
	
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		
		intent1 = new Intent(TrackerActivity.BROADCAST_ACTION);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		registerListener();
		
		es = Executors.newFixedThreadPool(2);
		
		sendNotification();
	}

	@Override
	public void onDestroy() {

		if (flag) {
			
			DB db = new DB(this);
		
			db.addTrack(new Track(startDate, distance, millis, maxSpeed, avgSpeed, avgPace));
			
			intent1.putExtra("canceled", false);
		} else {
			
			intent1.putExtra("canceled", true);
		}
		
		flag = false;
		
    	if (timer != null) {
    		timer.cancel();
    		timer = null;
    	}
		
		unregisterAllListeners();
		
		intent1.putExtra("destroyed", true);
		sendBroadcast(intent1);
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		maxSpeed = 0;
    	distance = 0;
    	
    	prevLocation = null;
    	
    	isPaused = false;
		
		MyRun mr = new MyRun(startId);
	    es.execute(mr);
		
		return super.onStartCommand(intent, flags, startId);
	}

	void sendNotification() {

		Intent intent = new Intent(this, TrackerActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);

		builder.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(getString(R.string.notif_ticker))
				.setWhen(System.currentTimeMillis()).setOngoing(true)
				.setContentTitle(getString(R.string.notif_title))
				.setContentText(getString(R.string.notif_text));

		Notification notif = builder.build();

		startForeground(1, notif);
	}
	
	class MyRun implements Runnable {

	    int startId;

	    public MyRun(int startId) {
	    	
	      this.startId = startId;
	    }

	    public void run() {}
	    
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
		
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, gpsProviderListener);		
	}
	
	private void updateWithNewLocation(Location location) {
	    
		if ((location != null) && (!isPaused)) {
			
			if (!flag) {
				
				flag = true;
				
				TrackerActivity.progressDialog.dismiss();
				
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy H:m:s");
		        startDate = df.format(new Date());

		        timer = new Timer();
		        timer.schedule(new TrackerTimer(), 0, 1000);
			}
			
			speed = location.getSpeed();
			
			TrackerSettings settings = new TrackerSettings(this);
        	
			if (settings.getAutopauseLimit().compareTo("Off") != 0) {
				
				double custSpeed;
				
	        	if (settings.getUnitId() == 0)
	        		custSpeed = (speed * 3600 / 1000);
	        	else 
	        		custSpeed = (speed * 2.2369);
	        	
	        	if (custSpeed < Integer.parseInt(settings.getAutopauseLimit())) {
	        		
	        		isPausedBySpeed = true;
	        	} else {
	        		
	       			isPausedBySpeed = false;
	        	}
			}
			
			if (!isPausedBySpeed) {
			
				if (speed > maxSpeed) {
					
					maxSpeed = speed;
				}
				
				if (distance - distanceStartLast >= settings.getDistanceOneUnit()) {
					
					distanceStartLast = Math.round(distance);
					timeStartLast = millis;
					
					paceLast = (millis / 1000 / 60) / settings.getDistanceOneUnit();
					
					avgPaceCounter ++;
					avgPaceSum += paceLast;
					avgPace = avgPaceSum / avgPaceCounter; 
				}
				
				avgSpeedCounter++;
				avgSpeedSum += speed;
				avgSpeed = avgSpeedSum / avgSpeedCounter;  
				
				if ((prevLocation != null) && (speed != 0)) {
					
					double lat1 = round(prevLocation.getLatitude(), 16);
					double lng1 = round(prevLocation.getLongitude(), 16);
					
					double lat2 = round(location.getLatitude(), 16);
					double lng2 = round(location.getLongitude(), 16);
					
					distance += calculateDistance(lat1, lng1, lat2, lng2);
				}
				
				prevLocation = location;
			}
		}
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

		return dist;
	}
	
	public double round(double d, int p) {

    	return new BigDecimal(d).setScale(p, RoundingMode.HALF_UP).doubleValue();
	}
		
		class TrackerTimer extends TimerTask {
			
			private long pauseTime = 0;
			private long startTime = System.currentTimeMillis();

			@Override
	        public void run() {
	        	
//	            TrackerActivity.this.runOnUiThread(new Runnable() {

//	                public void run() {
	                	
	                	if (!isPaused && !isPausedBySpeed) {
	            
		                	millis = System.currentTimeMillis() - startTime - pauseTime;
		                	
		                	intent1.putExtra("duration", millis);
		                	intent1.putExtra("distance", distance);
		        	    	intent1.putExtra("speed", speed);
		        	    	intent1.putExtra("maxSpeed", maxSpeed);
		        	    	intent1.putExtra("avgSpeed", avgSpeed);
		        	    	intent1.putExtra("avgPace", avgPace);
		        	    	intent1.putExtra("paceLast", paceLast);
		        	    	
		                	sendBroadcast(intent1);
	                	} else {
	                		
	                		pauseTime = System.currentTimeMillis() - startTime - millis;
	                	}
//	                }
//	            });
	        }
	   };
}
