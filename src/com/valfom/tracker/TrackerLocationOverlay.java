package com.valfom.tracker;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
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
	protected void drawMyLocation(Canvas arg0, MapView arg1, Location arg2,
			GeoPoint arg3, long arg4) {
		
		super.drawMyLocation(arg0, arg1, arg2, arg3, arg4);
		
		Double lat = arg2.getLatitude() * 1E6;
		Double lng = arg2.getLongitude() * 1E6;
		GeoPoint geoPoint = new GeoPoint(lat.intValue(), lng.intValue());
		
		MapController mapController = mapView.getController();
		mapController.animateTo(geoPoint);
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		
		super.onLocationChanged(location);

		if (location != null) {
			
			Double lat = location.getLatitude() * 1E6;
			Double lng = location.getLongitude() * 1E6;
			GeoPoint geoPoint = new GeoPoint(lat.intValue(), lng.intValue());
			
			Route.addGeoPoint(geoPoint);
			
			drawRoute();
		}
	}

	private void drawRoute() {
		   
		List<Overlay> overlays = mapView.getOverlays();

		Overlay myLocationOverlay;
		
		if (overlays.size() == 1)
			myLocationOverlay = overlays.get(0);
		else
			myLocationOverlay = overlays.get(1);
		
		overlays.clear();
		
		overlays.add(0, new RouteOverlay(Color.RED));
		
		overlays.add(1, myLocationOverlay);
		
//		Перерисовка всех линий
//		for (int i = 1; i < route.size(); i++) {
//			
//			GeoPoint curGeoPoint = route.get(i);
//			GeoPoint prevGeoPoint = route.get(i - 1);
//			
//			overlays.add(new RouteOverlay(prevGeoPoint, curGeoPoint, Color.RED));
//		}
		
//		Дорисовка последней линии
//		if (route.size() >= 2) {
//		
//			GeoPoint curGeoPoint = route.get(route.size() - 1);
//			GeoPoint prevGeoPoint = route.get(route.size() - 2);
//			
//			overlays.add(new RouteOverlay(prevGeoPoint, curGeoPoint, Color.RED));
//		}
	}
}
