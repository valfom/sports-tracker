package com.valfom.tracker;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class TrackerLocationOverlay extends MyLocationOverlay {

	MapView mapView;
	
	public TrackerLocationOverlay(Context context, MapView mapView) {
		
		super(context, mapView);
		
		this.mapView = mapView;
	}

	@Override
	public synchronized void onLocationChanged(Location location) {

		if (location != null) {
			
			Route.addLocation(location);
		
			drawPath();
		}
		
		super.onLocationChanged(location);
	}

	private void drawPath() {
		   
		List<Overlay> overlays = mapView.getOverlays();
	 
		List<Location> route = Route.getRoute();
		
		for (int i = 1; i < route.size(); i++) {
			
			Location cur = route.get(i);
			Location prev = route.get(i - 1);
			
			Double geoLat1 = prev.getLatitude()*1E6;
			Double geoLng1 = prev.getLongitude()*1E6;
			GeoPoint point1 = new GeoPoint(geoLat1.intValue(), geoLng1.intValue());
			
			Double geoLat2 = cur.getLatitude()*1E6;
			Double geoLng2 = cur.getLongitude()*1E6;
			GeoPoint point2 = new GeoPoint(geoLat2.intValue(), geoLng2.intValue());
			
			overlays.add(new RouteOverlay(point1, point2, Color.RED));
		}
	}
}
