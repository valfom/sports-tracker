package com.valfom.tracker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TrackerRouteOverlay extends Overlay {
	
	public static final int FLAGS_MODE_NO = 0;
	public static final int FLAGS_MODE_START = 1;
	public static final int FLAGS_MODE_START_FINISH = 2;
	
    private int color = Color.RED;
    private int flagsMode;
    private int trackId = -1;
	
    public TrackerRouteOverlay(int flagsMode) {
		
		this.flagsMode = flagsMode;
	}
    
	public TrackerRouteOverlay(int flagsMode, int trackId) {
		
		this.flagsMode = flagsMode;
		this.trackId = trackId;
	}
	
	public int getColor() {
		
		return color;
	}

	public void setColor(int color) {
		
		this.color = color;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		
		TrackerDB db = new TrackerDB(mapView.getContext());
		
		TrackerRoute route = new TrackerRoute();
		
		if (trackId == -1)
			route = db.getRouteObj();
		else
			route = db.getRouteObj(trackId);
		
		Projection projection = mapView.getProjection();
		Paint paint = new Paint();
	    Point prevPoint = new Point();
	    Point curPoint = new Point();
	    
	    GeoPoint prevGeoPoint = null;
	    GeoPoint curGeoPoint = null;
	    
	    paint.setColor(color);
	    paint.setStrokeWidth(5);
	    paint.setAlpha(255);
	    
	    TrackerPoint trackerPointPrev = null;
	    TrackerPoint trackerPointCur = null;
	    
	    for (int i = 1; i < route.getCount(); i++) {
	    	
	    	trackerPointPrev = route.getPoint(i - 1);
	    	prevGeoPoint = new GeoPoint(trackerPointPrev.getLatitude(), trackerPointPrev.getLongtitude());
	    	trackerPointCur = route.getPoint(i);
	    	curGeoPoint = new GeoPoint(trackerPointCur.getLatitude(), trackerPointCur.getLongtitude());
	    	
	    	projection.toPixels(prevGeoPoint, prevPoint);
	    	projection.toPixels(curGeoPoint, curPoint);
	    	
	    	canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, paint);
	    }
		
// --------------------------------- 80 - 90 ms	---------------------------------------------------	

//		int numPoints;
//		
//		if (trackId == -1)
//			numPoints = db.getPointsCount();
//		else
//			numPoints = db.getPointsCount(trackId);
//		
//		Projection projection = mapView.getProjection();
//		Paint paint = new Paint();
//	    Point prevPoint = new Point();
//	    Point curPoint = new Point();
//	    
//	    GeoPoint prevGeoPoint = null;
//	    GeoPoint curGeoPoint = null;
//	    
//	    paint.setColor(color);
//	    paint.setStrokeWidth(5);
//	    paint.setAlpha(255);
//	    
//	    TrackerPoint trackerPointPrev = null;
//	    TrackerPoint trackerPointCur = null;
//		
//		for (int i = 2; i < numPoints; i++) {
//			
//			if (trackId == -1) {
//				
//				trackerPointPrev = db.getPoint(i - 1);
//				trackerPointCur = db.getPoint(i);
//			} else {
//				
//				trackerPointPrev = db.getPoint(i - 1, trackId);
//				trackerPointCur = db.getPoint(i, trackId);
//			}
//			
//			prevGeoPoint = new GeoPoint(trackerPointPrev.getLatitude(), trackerPointPrev.getLongtitude());
//			curGeoPoint = new GeoPoint(trackerPointCur.getLatitude(), trackerPointCur.getLongtitude());
//			
//			projection.toPixels(prevGeoPoint, prevPoint);
//	    	projection.toPixels(curGeoPoint, curPoint);
//	    	
//	    	canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, paint);
//		}
//		
//		db.close();
		
		// --------------------------------- 30 - 50 ms	---------------------------------------------------
		
//		Cursor route;
//		
//		if (trackId == -1)
//			route = db.getRoute();
//		else
//			route = db.getRoute(trackId);
//		
//		if (route.getCount() > 0) {
//		
//			Projection projection = mapView.getProjection();
//			Paint paint = new Paint();
//		    Point prevPoint = new Point();
//		    Point curPoint = new Point();
//		    
//		    GeoPoint prevGeoPoint = null;
//		    GeoPoint curGeoPoint = null;
//		    
//		    paint.setColor(color);
//		    paint.setStrokeWidth(5);
//		    paint.setAlpha(255);
//		    
//		    route.moveToFirst();
//		    
//		    for (int i = 1; i < route.getCount(); i++) {
//		    	
//		    	route.moveToPosition(i - 1);
//		    	prevGeoPoint = new GeoPoint(route.getInt(0), route.getInt(1));
//		    	route.moveToPosition(i);
//		    	curGeoPoint = new GeoPoint(route.getInt(0), route.getInt(1));
//		    	
//		    	projection.toPixels(prevGeoPoint, prevPoint);
//		    	projection.toPixels(curGeoPoint, curPoint);
//		    	
//		    	canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, paint);
//		    }
//		    
//		    route.close();
//		    db.close();
//		}
		    
// FLAGS
		    
//		    paint.setColor(Color.BLACK);
//		    paint.setStrokeWidth(6);
//		    paint.setAlpha(255);
//		    
//		    if (flagsMode == 1) {
//		    	
//		    	
//		    	
//		    } else if (flagsMode == 2) {
//		    	
//		    	Point point = new Point();
//		    	GeoPoint geoPoint = null;
//		    	
//		    	geoPoint = route.get(0);
//		    	projection.toPixels(geoPoint, point);
//		    	
//		    	float[] pointsStart = new float[] { point.x, point.y, point.x, point.y - 60, 
//		    			point.x, point.y - 60, point.x + 35, point.y - 50,
//		    			point.x + 35, point.y - 50, point.x, point.y - 30,}; 
//		    	
//		    	canvas.drawLines(pointsStart, paint);
//		    	
//		    	geoPoint = route.get(route.size() - 1);
//		    	projection.toPixels(geoPoint, point);
//		    	paint.setAntiAlias(true);
//		    	
//		    	float[] pointsFinish = new float[] { point.x, point.y, point.x, point.y - 60, 
//		    			point.x, point.y - 60, point.x + 35, point.y - 50,
//		    			point.x + 35, point.y - 50, point.x, point.y - 30,};
//		    	
//		    	canvas.drawLines(pointsFinish, paint);
//		    }
	    
	    super.draw(canvas, mapView, shadow);
	}
}