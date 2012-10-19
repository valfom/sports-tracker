package com.valfom.tracker;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class Route {

	private static List<Location> route = new ArrayList<Location>();
	
	public static void addLocation(Location location) {
		
		route.add(location);
	}

	public static List<Location> getRoute() {
		
		return route;
	}

	public static void setRoute(List<Location> route) {
		
		Route.route = route;
	}
}
