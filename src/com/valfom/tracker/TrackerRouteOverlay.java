package com.valfom.tracker;

import java.util.List;

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
	
	public TrackerRouteOverlay(int i) {
		
		this.flagsMode = i;
	}
	
	public int getColor() {
		
		return color;
	}

	public void setColor(int color) {
		
		this.color = color;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		
		List<GeoPoint> route = TrackerRoute.getRoute();
		
		Projection projection = mapView.getProjection();
		Paint paint = new Paint();
	    Point prevPoint = new Point();
	    Point curPoint = new Point();
	    
	    GeoPoint prevGeoPoint = null;
	    GeoPoint curGeoPoint = null;
	    
	    paint.setColor(color);
	    paint.setStrokeWidth(5);
	    paint.setAlpha(255);
	    
	    for (int i = 1; i < route.size(); i++) {
	    	
	    	prevGeoPoint = route.get(i - 1);
	    	curGeoPoint = route.get(i);
	    	
	    	projection.toPixels(prevGeoPoint, prevPoint);
	    	projection.toPixels(curGeoPoint, curPoint);
	    	
	    	canvas.drawLine(prevPoint.x, prevPoint.y, curPoint.x, curPoint.y, paint);
	    }
	    
	    paint.setColor(Color.BLACK);
	    paint.setStrokeWidth(6);
	    paint.setAlpha(255);
	    
	    if (flagsMode == 1) {
	    	
	    	
	    	
	    } else if (flagsMode == 2) {
	    	
//	    	Point point = new Point();
//	    	GeoPoint geoPoint = null;
//	    	
//	    	geoPoint = route.get(0);
//	    	projection.toPixels(geoPoint, point);
//	    	
//	    	float[] pointsStart = new float[] { point.x, point.y, point.x, point.y - 60, 
//	    			point.x, point.y - 60, point.x + 35, point.y - 50,
//	    			point.x + 35, point.y - 50, point.x, point.y - 30,}; 
//	    	
//	    	canvas.drawLines(pointsStart, paint);
//	    	
//	    	geoPoint = route.get(route.size() - 1);
//	    	projection.toPixels(geoPoint, point);
//	    	paint.setAntiAlias(true);
//	    	
//	    	float[] pointsFinish = new float[] { point.x, point.y, point.x, point.y - 60, 
//	    			point.x, point.y - 60, point.x + 35, point.y - 50,
//	    			point.x + 35, point.y - 50, point.x, point.y - 30,};
//	    	
//	    	canvas.drawLines(pointsFinish, paint);
	    }
	    
	    super.draw(canvas, mapView, shadow);
	}
}