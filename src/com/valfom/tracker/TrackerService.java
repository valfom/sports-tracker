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

	private static final String TAG = "TrackerService";
	
	private static final int MIN_UPDATE_TIME = 1000;
	private static final int MIN_UPDATE_DISTANCE = 0;
	
	ExecutorService es;
	private static LocationManager locationManager;
	
	private static Timer timer;
	public static boolean isPaused = false;
	public static Location prevLocation;
	private static float maxSpeed = 0;
	private long millis = 0;
	private static double distance = 0;
	private static boolean flag = false;
	
	private String startDate;
	
//	private final Handler handler = new Handler();
    Intent intent1;
    Intent intent2;
    int counter = 0;
	
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		
		intent1 = new Intent(TrackerActivity.BROADCAST_ACTION);
		intent2 = new Intent(TrackerActivity.BROADCAST_ACTION);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		registerListener();
		
		es = Executors.newFixedThreadPool(2);
		
		sendNotification();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		
		DatabaseHandler db = new DatabaseHandler(this);
		
		db.addTrack(new Track(startDate, distance, millis, maxSpeed));
		
		flag = false;
    	
    	timer.cancel();
    	timer = null;
		
		unregisterAllListeners();
	}

//	private Runnable sendUpdatesToUI = new Runnable() {
//    	public void run() {
//    	    DisplayLoggingInfo();    		
//    	    handler.postDelayed(this, 5000); // 5 seconds
//    	}
//    };
    
//    private void DisplayLoggingInfo() {
//    	Log.d(TAG, "entered DisplayLoggingInfo");
// 
//    	intent1.putExtra("duration", millis);
//    	intent1.putExtra("distance", distance);
//    	intent1.putExtra("speed", 
//    	intent1.putExtra("maxSpeed"
//    	sendBroadcast(intent1);
//    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

//		handler.removeCallbacks(sendUpdatesToUI);
//        handler.postDelayed(sendUpdatesToUI, 1000);
		
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

//		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
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

//		nm.notify(1, notif);
	}
	
	class MyRun implements Runnable {

	    int time;
	    int startId;
	    int task;

	    public MyRun(int startId) {
	    	
	      this.startId = startId;
	    }

	    public void run() {
	    	
	    	
	    	
//	      Intent intent = new Intent(TrackerActivity.BROADCAST_ACTION);
//	      Log.d(LOG_TAG, "MyRun#" + startId + " start, time = " + time);
//	      try {
//	        // сообщаем об старте задачи
//	        intent.putExtra(MainActivity.PARAM_TASK, task);
//	        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_START);
//	        sendBroadcast(intent);
//
//	        // начинаем выполнение задачи
//	        TimeUnit.SECONDS.sleep(time);
//
//	        // сообщаем об окончании задачи
//	        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
//	        intent.putExtra(MainActivity.PARAM_RESULT, time * 100);
//	        sendBroadcast(intent);
//
//	      } catch (InterruptedException e) {
//	        e.printStackTrace();
//	      }
	    }
	    
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
	
	private void updateWithNewLocation(Location location) {
	    
		if (location != null && !isPaused) {
			
			if (!flag) {
			
				flag = true;
				
				TrackerActivity.progressDialog.dismiss();
				
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy H:m:s");
		        startDate = df.format(new Date());

		        timer = new Timer();
		        timer.schedule(new TrackerTimer(), 0, 1000);
			}
	     
			float speedMPerS = location.getSpeed();
			float speedKPerH = speedMPerS * 3600 / 1000;
			
			
//			curSpeedTV.setText(String.format("%.1f", speedKPerH));
			
			if (speedMPerS > maxSpeed) {
				
				maxSpeed = speedMPerS;
				
//				maxSpeedTV.setText(String.format("%.1f", speedKPerH));
			}
			
			if ((prevLocation != null) && (speedMPerS != 0)) {
				
				double lat1 = round(prevLocation.getLatitude(), 16);
				double lng1 = round(prevLocation.getLongitude(), 16);
				
				double lat2 = round(location.getLatitude(), 16);
				double lng2 = round(location.getLongitude(), 16);
				
				distance += calculateDistance(lat1, lng1, lat2, lng2);
				
//				distanceTV.setText(String.format("%.2f", distance));
				
//				Fragment frag1 = getFragmentManager().findFragmentById(R.id.container);
//			    ((TextView)frag1.getView().findViewById(R.id.textView)).setText("Text from Fragment 2:" + s);
			}
			
			prevLocation = location;
			
			intent2.putExtra("distance", distance);
	    	intent2.putExtra("speed", speedKPerH);
	    	intent2.putExtra("maxSpeed", maxSpeed);
	    	sendBroadcast(intent2);
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

//		Toast.makeText(getApplicationContext(), String.valueOf(dist), Toast.LENGTH_SHORT).show();

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
	                	
	                	if (!isPaused) {
	            
		                	millis = System.currentTimeMillis() - startTime - pauseTime;
		                	
//		                	int seconds = (int) (millis / 1000);
//		                	int minutes = seconds / 60;
//		                	seconds     = seconds % 60;
//		                	int hours = minutes / 60;
//		                	minutes = minutes % 60;
		
//		                	timeTV.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
		                	intent1.putExtra("duration", millis);
		                	sendBroadcast(intent1);
	                	} else {
	                		
	                		pauseTime = System.currentTimeMillis() - startTime - millis;
	                	}
//	                }
//	            });
	        }
	   };
}
