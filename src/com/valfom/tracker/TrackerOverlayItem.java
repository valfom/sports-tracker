package com.valfom.tracker;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class TrackerOverlayItem extends OverlayItem {
	
	private int id;

	public TrackerOverlayItem(int id, GeoPoint geoPoint, String title, String msg) {
		
		super(geoPoint, title, msg);
		
		this.id = id;
	}

	public int getId() {
		
		return id;
	}
}
