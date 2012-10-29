package com.valfom.tracker;

import android.content.Context;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class TrackerMyLocationOverlay extends MyLocationOverlay {

	public TrackerMyLocationOverlay(Context context, MapView mapView) {
		
		super(context, mapView);
	}
}
