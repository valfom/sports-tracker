package com.valfom.tracker;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

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
			
//			Log.d("LALA", "locationChanged");
			
			Double lat = location.getLatitude() * 1E6;
			Double lng = location.getLongitude() * 1E6;
			GeoPoint geoPoint = new GeoPoint(lat.intValue(), lng.intValue());
			
			TrackerRoute.addGeoPoint(geoPoint);
			
			drawRoute();
		}
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
		Log.d("LALA", "Enabled");
		
		super.onProviderEnabled(arg0);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
		Log.d("LALA", "Changed");
		
		super.onStatusChanged(arg0, arg1, arg2);
	}

	private void drawRoute() {
		   
		List<Overlay> overlays = mapView.getOverlays();

		Overlay lala;
		
		if (overlays.size() == 1) {
			lala = overlays.get(0);
		} else
			lala = overlays.get(1);
		
		overlays.clear();
		
		overlays.add(0, new TrackerRouteOverlay());
		
		overlays.add(1, lala);
	}
}
