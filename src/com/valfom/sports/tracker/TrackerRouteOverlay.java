package com.valfom.sports.tracker;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.preference.PreferenceManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TrackerRouteOverlay extends Overlay {
	
	public final static int FLAGS_MODE_NO = 0;
	public final static int FLAGS_MODE_FINISH = 1;
	
    private Integer trackId = null;
    private int flagsMode;
    
	private Paint paint = new Paint();
	private Point fromPoint = new Point();
	private Point toPoint = new Point();
	private Path path = new Path();
	
    private TrackerPoint trackerPointPrev = null;
    private TrackerPoint trackerPointCur = null;
    
    private GeoPoint fromGeoPoint = null;
    private GeoPoint toGeoPoint = null;
    
    private TrackerRoute route = null;
    private boolean animateToStart = false;
    private int accuracy = 3;
    private int low;
    private int middle;
    private int type;
	
    public TrackerRouteOverlay() {
		
    	this.flagsMode = FLAGS_MODE_NO;
	}
    
	public TrackerRouteOverlay(int trackId, boolean animateToStart) {
		
		this.flagsMode = FLAGS_MODE_FINISH;
		this.trackId = trackId;
		this.animateToStart = animateToStart;
	}
	
	private void initPaint() {
		
		paint.setDither(true);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		
		TrackerDB db = new TrackerDB(mapView.getContext());
		TrackerSettings settings = new TrackerSettings(mapView.getContext());
		
		if (trackId == null) route = db.getRoute();
		else if (trackId == TrackerStorage.trackId) route = TrackerStorage.route;
		else {
				
			TrackerStorage.trackId = trackId;
			TrackerStorage.route = route = db.getRoute(trackId);
		}
			
		int zoomLvl = mapView.getZoomLevel();
		
		if (zoomLvl == 19) accuracy = 2;
		else if (zoomLvl >= 20) accuracy = 1;
		
		Projection projection = mapView.getProjection();
	    
		int count = route.getCount();
		
		if (count >= 2) {
			
			trackerPointPrev = route.getPoint(0);
			fromGeoPoint = new GeoPoint(trackerPointPrev.getLatitude(), trackerPointPrev.getLongtitude());
			
			projection.toPixels(fromGeoPoint, fromPoint);
			
			if (animateToStart) {
				
				mapView.getController().animateTo(fromGeoPoint);
				
				animateToStart = false;
			}
			
			initPaint();
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mapView.getContext());
		
		if (!sharedPreferences.getBoolean("customRoute", false)) { 
			
//			Log.d("LALA", "route start");
			
			paint.setColor(Color.parseColor("#ff4683ec"));
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeWidth(6);
			
			for (int i = accuracy; i < count; i += accuracy) {
		    	
		    	trackerPointCur = route.getPoint(i);
		    	toGeoPoint = new GeoPoint(trackerPointCur.getLatitude(), trackerPointCur.getLongtitude());
		    	
		    	projection.toPixels(toGeoPoint, toPoint);
		    	
		    	if ((toPoint.x > 0) && (toPoint.y > 0) && (toPoint.x < canvas.getWidth()) && (toPoint.y < canvas.getHeight())) {
		    	
		    		projection.toPixels(fromGeoPoint, fromPoint);
		    		
		    		path.moveTo(fromPoint.x, fromPoint.y);
		            path.lineTo(toPoint.x, toPoint.y);
		            
		    	} else if ((fromPoint.x > 0) && (fromPoint.y > 0) && (fromPoint.x < canvas.getWidth()) && (fromPoint.y < canvas.getHeight())) {
		    		
		    		projection.toPixels(fromGeoPoint, fromPoint);
		    		
		    		path.moveTo(fromPoint.x, fromPoint.y);
		            path.lineTo(toPoint.x, toPoint.y);
		    	}
		    	
		    	fromGeoPoint = toGeoPoint;
		    }
			
			canvas.drawPath(path, paint);
			
			path.reset();
			
//			Log.d("LALA", "route stop");
			
		} else { // Custom route
			
			// TODO: Оптимизировать получение значений скоростных границ
			
//			Log.d("LALA", "custom route start");
			
			low = Integer.valueOf(sharedPreferences.getString("lowThreshold", mapView.getContext().getString(R.string.settings_default_value_low_threshold)));
			middle = Integer.valueOf(sharedPreferences.getString("middleThreshold", mapView.getContext().getString(R.string.settings_default_value_middle_threshold)));
			
			paint.setStrokeWidth(8);
			paint.setStrokeCap(Paint.Cap.BUTT);
			
			int speedPrev = (int) settings.convertSpeed(trackerPointPrev.getSpeed());
			
			if (speedPrev < low) { 
				
				paint.setColor(Color.YELLOW); 
				type = 0; 
				
			} else if ((speedPrev > low) && (speedPrev < middle)) { 
				
				paint.setColor(Color.GREEN); 
				type = 1; 
				
			} else { 
				
				paint.setColor(Color.RED); 
				type = 2; 
			}
			
			for (int i = accuracy; i < count; i += accuracy) {
		    	
		    	trackerPointCur = route.getPoint(i);
		    	toGeoPoint = new GeoPoint(trackerPointCur.getLatitude(), trackerPointCur.getLongtitude());
		    	
		    	projection.toPixels(toGeoPoint, toPoint);
		    	
		    	if ((toPoint.x > 0) && (toPoint.y > 0) && (toPoint.x < canvas.getWidth()) && (toPoint.y < canvas.getHeight())) {
		    	
		    		projection.toPixels(fromGeoPoint, fromPoint);
		    		
		    		path.moveTo(fromPoint.x, fromPoint.y);
		            path.lineTo(toPoint.x, toPoint.y);
		            
		    	} else if ((fromPoint.x > 0) && (fromPoint.y > 0) && (fromPoint.x < canvas.getWidth()) && (fromPoint.y < canvas.getHeight())) {
		    		
		    		projection.toPixels(fromGeoPoint, fromPoint);
		    		
		    		path.moveTo(fromPoint.x, fromPoint.y);
		            path.lineTo(toPoint.x, toPoint.y);
		    	}
		    	
		    	fromGeoPoint = toGeoPoint;
		    	
		    	int speedCur = (int) settings.convertSpeed(trackerPointCur.getSpeed()); 
		    	int newType = type;
		    	
		    	if (speedCur < low) newType = 0;
				else if ((speedCur > low) && (speedCur < middle)) newType = 1;
				else newType = 2;
		    	
		    	if (type != newType) {
		    		
		    		canvas.drawPath(path, paint);
					
					path.reset();
					
					type = newType;
					
					if (speedCur < low) paint.setColor(Color.YELLOW); 
					else if ((speedCur > low) && (speedCur < middle)) paint.setColor(Color.GREEN); 
					else paint.setColor(Color.RED); 
		    	}
		    }
			
			canvas.drawPath(path, paint);
			
			path.reset();
			
//			Log.d("LALA", "custom route stop");
		}
		}
		
		if (flagsMode == FLAGS_MODE_FINISH) {
	    	
			trackerPointCur = route.getPoint(route.getCount() - 1);
	    	
	    	if (trackerPointCur != null) {
		    	
		    	toGeoPoint = new GeoPoint(trackerPointCur.getLatitude(), trackerPointCur.getLongtitude());
		    	projection.toPixels(toGeoPoint, toPoint);
		    	
		    	Bitmap flagStop = BitmapFactory.decodeResource(mapView.getResources(), R.drawable.ic_flag_finish);
		    	
		    	canvas.drawBitmap(flagStop, toPoint.x - flagStop.getWidth() / 2, toPoint.y - flagStop.getHeight() / 2, null);
	    	}
	    }
	    
	    super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onTap(GeoPoint geoPoint, MapView mapView) {
		
		return true;
	}
}   
