package com.valfom.tracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TrackerRouteOverlay extends Overlay {
	
	public final static int FLAGS_MODE_NO = 0;
	public final static int FLAGS_MODE_START = 1;
	public final static int FLAGS_MODE_START_FINISH = 2;
	
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
    
    private int accuracy = 3;
	
    public TrackerRouteOverlay(int flagsMode) {
		
    	this.flagsMode = flagsMode;
    	
//    	initPaint();
	}
    
	public TrackerRouteOverlay(int trackId, int flagsMode) {
		
		this.flagsMode = flagsMode;
		this.trackId = trackId;
		
//		initPaint();
	}
	
	private void initPaint() {
		
		paint.setDither(true);
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#ff4683ec"));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(6);
		paint.setShadowLayer(2, 0, 0, Color.BLACK);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		
		TrackerDB db = new TrackerDB(mapView.getContext());
		
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
			
			initPaint();
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
	    }
		
		canvas.drawPath(path, paint);
		
		path.reset();
		
		if (flagsMode == FLAGS_MODE_START) {
			
			paint.setColor(Color.BLACK);
		    paint.setStrokeWidth(4);
	    	
	    	trackerPointPrev = route.getPoint(0);
	    	
	    	if (trackerPointPrev != null) {
	    	
		    	fromGeoPoint = new GeoPoint(trackerPointPrev.getLatitude(), trackerPointPrev.getLongtitude());
		    	projection.toPixels(fromGeoPoint, fromPoint);
		    	
		    	canvas.drawLine(fromPoint.x, fromPoint.y, fromPoint.x, fromPoint.y - 40, paint);
		    	
		    	paint.setColor(Color.parseColor("#66CC66"));
		    	paint.setStyle(Style.FILL);
		    	
		    	canvas.drawRect(fromPoint.x + 2, fromPoint.y - 40, fromPoint.x + 29, fromPoint.y - 20, paint);
	    	}
			
		} else if (flagsMode == FLAGS_MODE_START_FINISH) {
	    	
	    	paint.setColor(Color.BLACK);
		    paint.setStrokeWidth(4);
	    	
	    	trackerPointPrev = route.getPoint(0);
	    	fromGeoPoint = new GeoPoint(trackerPointPrev.getLatitude(), trackerPointPrev.getLongtitude());
	    	projection.toPixels(fromGeoPoint, fromPoint);
	    	
	    	canvas.drawLine(fromPoint.x, fromPoint.y, fromPoint.x, fromPoint.y - 40, paint);
	    	
	    	paint.setColor(Color.parseColor("#66CC66"));
	    	paint.setStyle(Style.FILL);
	    	
	    	canvas.drawRect(fromPoint.x + 2, fromPoint.y - 40, fromPoint.x + 29, fromPoint.y - 20, paint);
	    	
	    	paint.setStyle(Paint.Style.STROKE);
	    	paint.setColor(Color.BLACK);
		    paint.setStrokeWidth(4);
	    	
	    	trackerPointCur = route.getPoint(route.getCount() - 1);
	    	toGeoPoint = new GeoPoint(trackerPointCur.getLatitude(), trackerPointCur.getLongtitude());
	    	projection.toPixels(toGeoPoint, toPoint);
	    	
	    	canvas.drawLine(toPoint.x, toPoint.y, toPoint.x, toPoint.y - 40, paint);
	    	
	    	paint.setColor(Color.parseColor("#FF3300"));
	    	paint.setStyle(Style.FILL);
	    	
	    	canvas.drawRect(toPoint.x + 2, toPoint.y - 40, toPoint.x + 29, toPoint.y - 20, paint);
	    }
	    
	    super.draw(canvas, mapView, shadow);
	}

	@Override
	public boolean onTap(GeoPoint geoPoint, MapView mapView) {
		
		return true;
	}
}   
