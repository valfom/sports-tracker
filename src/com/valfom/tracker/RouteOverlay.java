package com.valfom.tracker;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteOverlay extends Overlay {
	
//    private GeoPoint gp1;
//    private GeoPoint gp2;
    private int color;
// 
//    public RouteOverlay(GeoPoint gp1, GeoPoint gp2, int color) {
//    	
//        this.gp1 = gp1;
//        this.gp2 = gp2;
//        this.color = color;
//    }
//
//	@Override
//	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
//		
//	    Projection projection = mapView.getProjection();
//	    
//	    Paint paint = new Paint();
//	    Point point = new Point();
//	    
//	    projection.toPixels(gp1, point);
//	    paint.setColor(color);
//	    Point point2 = new Point();
//	    projection.toPixels(gp2, point2);
//	    paint.setStrokeWidth(5);
//	    paint.setAlpha(120);
//	    canvas.drawLine(point.x, point.y, point2.x, point2.y, paint);
//	    
//	    super.draw(canvas, mapView, shadow);
//	}
	
	public RouteOverlay(int color) {
		
		this.color = color;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		
		List<GeoPoint> route = Route.getRoute();
		
		Projection projection = mapView.getProjection();
		Paint paint = new Paint();
	    Point prevPoint = new Point();
	    Point curPoint = new Point();
	    
	    GeoPoint prevGeoPoint;
	    GeoPoint curGeoPoint;
	    
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