package com.valfom.tracker;

import java.util.List;

import android.content.Context;
import android.location.Location;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class TrackerMyLocationOverlay extends MyLocationOverlay {

	MapView mapView;
	
	public TrackerMyLocationOverlay(Context context, MapView mapView) {
		
		super(context, mapView);
		
		this.mapView = mapView;
	}

	@Override
	public synchronized void onLocationChanged(Location location) {
		
		super.onLocationChanged(location);

		if (location != null) {
			
//			Double lat = location.getLatitude() * 1E6;
//			Double lng = location.getLongitude() * 1E6;
//			GeoPoint geoPoint = new GeoPoint(lat.intValue(), lng.intValue());
			
//			MapController mapController = mapView.getController();
//			mapController.animateTo(geoPoint);
			
			drawRoute();
		}
	}

	private void drawRoute() {
		   
		List<Overlay> overlays = mapView.getOverlays();

		Overlay myLocationOverlay = null;
		
		if (overlays.size() == 1)
			myLocationOverlay = overlays.get(0);
		else
			myLocationOverlay = overlays.get(1);
		
		overlays.clear();
		
		overlays.add(0, new TrackerRouteOverlay());
		
		overlays.add(1, myLocationOverlay);
	}
}
