package com.valfom.sports.tracker;

public class TrackerPoint {

	private int latitude;
	private int longtitude;
	private int speed;
	private int altitude;
	
	public TrackerPoint() {}
	
	public TrackerPoint(int latitude, int longtitude, int speed, int altitude) {
		
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.speed = speed;
		this.altitude = altitude;
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
	
	public int getSpeed() {
		
		return speed;
	}
	
	public void setSpeed(int speed) {
		
		this.speed = speed;
	}
	
	public int getAltitude() {
		
		return altitude;
	}
	
	public void setAltitude(int altitude) {
		
		this.altitude = altitude;
	}
}
