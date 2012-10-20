package com.valfom.tracker;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;

public class Route {

	private static List<GeoPoint> route = new ArrayList<GeoPoint>();
	
	public static void addGeoPoint(GeoPoint geoPoint) {
		
		route.add(geoPoint);
	}

	public static List<GeoPoint> getRoute() {
		
		return route;
	}
}