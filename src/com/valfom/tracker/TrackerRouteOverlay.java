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
	
    private int color = Color.RED;
	
	public TrackerRouteOverlay() {}
	
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
	    
	    super.draw(canvas, mapView, shadow);
	}
}