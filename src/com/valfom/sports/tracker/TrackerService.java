package com.valfom.sports.tracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class TrackerService extends Service {

	private static final int MIN_UPDATE_TIME = 1000;
	private static final int MIN_UPDATE_DISTANCE = 0;
	
	private LocationManager locationManager = null;
	private Timer timer = null;
	public static Location prevLocation = null;
	
	public static boolean isPaused = false;
	public boolean isPausedBySpeed = false;
	public static boolean isLocationReceived = false;
	
	private String startDate = null;
	
	public static long millis = 0;
	public static double distance = 0;
	
	// Speed
	public static float speed = 0;
	public static float maxSpeed = 0;
	public static float avgSpeed = 0;
	private float avgSpeedSum = 0;
	private int avgSpeedCounter = 0;
	
	// Pace
	private double paceLast = 0;
	private float timeStartLast = 0;
	private double distanceStartLast = 0;
	
	public static double avgPace = 0;
	private double avgPaceSum = 0;
	private int avgPaceCounter = 0;
	public static double maxPace = 0;
	
	// Altitude
	private double curAltitude = 0;
	private double lastAltitude = 0;
	public static double altitudeLoss = 0;
	public static double altitudeGain = 0;
	public static Double maxAltitude = null;
	public static Double minAltitude = null;
	
	private ArrayList<Double> altitudeArr = new ArrayList<Double>();
	private int lastAdded = 0;
	
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		
		prevLocation = null;
		isLocationReceived = false;
		millis = 0;
		distance = 0;
		speed = 0;
		maxSpeed = 0;
		avgSpeed = 0;
		avgPace = 0;
		maxPace = 0;
		altitudeLoss = 0;
		altitudeGain = 0;
		maxAltitude = null;
		minAltitude = null;
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		registerListener();
	}

	@Override
	public void onDestroy() {
		
		if (timer != null) {
    		
    		timer.cancel();
    		timer = null;
    	}
		
		unregisterAllListeners();
		
		Intent result = new Intent(TrackerMainActivity.BROADCAST_ACTION);
		result.putExtra("destroyed", true);
		result.putExtra("canceled", !isLocationReceived);
		sendBroadcast(result);
		
		TrackerDB db = new TrackerDB(this);
		
		if (isLocationReceived) {
			
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			int activityId = sharedPreferences.getInt("activity", 0);

			db.addTrack(new TrackerTrack(activityId, startDate, distance, millis, maxSpeed, avgSpeed, avgPace, maxPace, altitudeGain, altitudeLoss));
			
			int trackId = db.getLastTrackId();
			
			db.saveRoute(trackId);
			db.saveMarkers(trackId);
		}
		
		db.clearRoute();
		db.clearMarkers();
		
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		TrackerDB db = new TrackerDB(this);
		
		db.clearRoute();
		db.clearMarkers();
		
		db.close();
		
		return START_STICKY;
	}

	void sendNotification() {

		Intent intent = new Intent(this, TrackerMainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		builder.setContentIntent(pendingIntent)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(getString(R.string.notif_ticker))
				.setWhen(System.currentTimeMillis()).setOngoing(true)
				.setContentTitle(getString(R.string.notif_title))
				.setContentText(getString(R.string.notif_text));

		Notification notif = builder.build();

		startForeground(1, notif);
	}
	
	private LocationListener gpsProviderListener = new LocationListener() {
    	
		public void onLocationChanged(Location location) {
			
			update(location);
		}
		
		public void onProviderDisabled(String provider) {
			
			unregisterAllListeners();
		}
		
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
	
	private void update(Location location) {
	    
		if ((location != null) && (!isPaused)) {
			
			if (!isLocationReceived) {
				
				isLocationReceived = true;
				
				sendNotification();
				
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
		        startDate = df.format(new Date());

		        timer = new Timer();
		        timer.schedule(new TrackerTimer(), 0, 1000);
			}
			
			speed = location.getSpeed();
			
			TrackerSettings settings = new TrackerSettings(this);
        	
			if (settings.isAutoPause()) {
				
				float custSpeed = settings.convertSpeed(speed);
	        	
	        	Intent result = new Intent(TrackerMainActivity.BROADCAST_ACTION);
	        	
	        	if (custSpeed <= Integer.parseInt(settings.getAutoPauseThreshold())) {
	        		
	        		isPausedBySpeed = true;
	        		
	        		result.putExtra("pausedBySpeed", true);
	        		sendBroadcast(result);
	        		
	        	} else {
	        		
	       			isPausedBySpeed = false;
	       			
	       			result.putExtra("pausedBySpeed", false);
	        		sendBroadcast(result);
	        	}
	        	
			} else if (isPausedBySpeed) {
				
				isPausedBySpeed = false;
				
				Intent result = new Intent(TrackerMainActivity.BROADCAST_ACTION);
				result.putExtra("pausedBySpeed", false);
        		sendBroadcast(result);
			}
			
			if (!isPausedBySpeed) {
				
				if (speed > maxSpeed) maxSpeed = speed;
				
				if (distance - distanceStartLast >= settings.getDistanceOneUnit()) {

					paceLast = (millis - timeStartLast);
					
					if ((maxPace == 0) || (paceLast < maxPace))
						maxPace = paceLast;
					
					avgPaceCounter ++;
					avgPaceSum += paceLast;
					avgPace = avgPaceSum / avgPaceCounter;
					
					distanceStartLast = Math.round(distance);
					timeStartLast = millis;
				}
				
				avgSpeedCounter++;
				avgSpeedSum += speed;
				avgSpeed = avgSpeedSum / avgSpeedCounter;
				
				if (location.hasAltitude() && (speed != 0)) {
					
					curAltitude = location.getAltitude();
					
					if (altitudeArr.size() < 20) {
						
						altitudeArr.add(lastAdded, curAltitude);
						lastAdded++;
						
					} else {
					
						if (lastAdded >= (altitudeArr.size() - 1)) lastAdded = 0;
						
						altitudeArr.set(lastAdded, curAltitude);
						lastAdded++;
						
						double altitudeSum = 0;
						
						for (int i = 0; i < altitudeArr.size(); i++)
							altitudeSum += altitudeArr.get(i);
						
						double curAvgAltitude = altitudeSum / altitudeArr.size();
						
						if ((minAltitude == null) || (curAvgAltitude < minAltitude))
							minAltitude = curAvgAltitude;
						if ((maxAltitude == null) || (curAvgAltitude > maxAltitude))
							maxAltitude = curAvgAltitude;
						
						if (lastAltitude != 0) {
						
							double dif = lastAltitude - curAvgAltitude;
							
							if (dif < 0) altitudeGain += Math.abs(dif);
							else altitudeLoss += dif;
						}
						
						lastAltitude = curAvgAltitude;
					}
				}
				
				if ((prevLocation != null) && (speed != 0)) {
					
					double lat1 = round(prevLocation.getLatitude(), 16);
					double lng1 = round(prevLocation.getLongitude(), 16);
					
					double lat2 = round(location.getLatitude(), 16);
					double lng2 = round(location.getLongitude(), 16);
					
					double curDistance = calculateDistance(lat1, lng1, lat2, lng2);
					
					if (curDistance < 1000) distance += curDistance;
				}
				
				prevLocation = location;
				
				int lat = (int) (location.getLatitude() * 1E6);
				int lng = (int) (location.getLongitude() * 1E6);
				int altitude = (int) location.getAltitude();

				TrackerDB db = new TrackerDB(this);
				
				TrackerPoint point = new TrackerPoint(lat, lng, (int) speed, altitude);
				
				db.addPoint(point);
				
				db.close();
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
        	
        	if (!isPaused && !isPausedBySpeed) {
        		
            	millis = System.currentTimeMillis() - startTime - pauseTime;
            	
            	Intent result = new Intent(TrackerMainActivity.BROADCAST_ACTION);
            	
            	result.putExtra("duration", millis);
            	result.putExtra("distance", distance);
    	    	result.putExtra("speed", speed);
    	    	result.putExtra("maxSpeed", maxSpeed);
    	    	result.putExtra("avgSpeed", avgSpeed);
    	    	result.putExtra("avgPace", avgPace);
    	    	result.putExtra("maxPace", maxPace);
    	    	result.putExtra("gainAltitude", altitudeGain);
    	    	result.putExtra("lossAltitude", altitudeLoss);
    	    	result.putExtra("minAltitude", minAltitude);
    	    	result.putExtra("maxAltitude", maxAltitude);
    	    	
            	sendBroadcast(result);
        	} else {
        		
        		pauseTime = System.currentTimeMillis() - startTime - millis;
        	}
        }
   };
}