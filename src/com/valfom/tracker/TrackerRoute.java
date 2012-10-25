package com.valfom.tracker;

import java.util.ArrayList;
import java.util.List;

public class TrackerRoute {
	
	private List<TrackerPoint> arr = new ArrayList<TrackerPoint>();

	public TrackerPoint getPoint(int pos) {
		
		return arr.get(pos);
	}
	
	public void addPoint(TrackerPoint point) {
		
		arr.add(point);
	}
	
	public int getCount() {
		
		return arr.size();
	}
}