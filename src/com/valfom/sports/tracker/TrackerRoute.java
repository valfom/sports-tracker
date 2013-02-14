package com.valfom.sports.tracker;

import java.util.ArrayList;

public class TrackerRoute {
	
	private ArrayList<TrackerPoint> route = new ArrayList<TrackerPoint>();

	public TrackerPoint getPoint(int pos) {
		
		if ((pos >= route.size()) || (route.size() == 0)) return null;
		
		return route.get(pos);
	}
	
	public void addPoint(TrackerPoint point) {
		
		route.add(point);
	}
	
	public int getCount() {
		
		return route.size();
	}
}