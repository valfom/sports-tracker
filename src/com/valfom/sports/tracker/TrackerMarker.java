package com.valfom.sports.tracker;

public class TrackerMarker {

	private int latitude;
	private int longtitude;
	private String title;
	private String msg;
	
	public TrackerMarker(int latitude, int longtitude, String title, String msg) {
		
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.title = title;
		this.msg = msg;
	}

	public int getLatitude() {
		
		return latitude;
	}

	public void setLatitude(int latitude) {
		
		this.latitude = latitude;
	}

	public int getLongtitude() {
		
		return longtitude;
	}

	public void setLongtitude(int longtitude) {
		
		this.longtitude = longtitude;
	}

	public String getTitle() {
		
		return title;
	}

	public void setTitle(String title) {
		
		this.title = title;
	}

	public String getMsg() {
		
		return msg;
	}

	public void setMsg(String msg) {
		
		this.msg = msg;
	}
}
