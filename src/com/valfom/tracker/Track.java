package com.valfom.tracker;

public class Track {
	 
    private int id;
    private String date;
    private double distance;
    private long time;
    private float maxSpeed;
 
    public Track() {}
    
    public Track(int id, String date, double distance, long time, float maxSpeed) {
    	
        this.id = id;
        this.date = date;
        this.distance = distance;
        this.time = time;
        this.maxSpeed = maxSpeed;
    }
 
    public Track(String date, double distance, long time, float maxSpeed){
    	
    	this.date = date;
        this.distance = distance;
        this.time = time;
        this.maxSpeed = maxSpeed;
    }

	public int getId() {
		
		return id;
	}

	public void setId(int id) {
		
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		
		this.date = date;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		
		this.distance = distance;
	}

	public long getTime() {
		
		return time;
	}

	public void setTime(long time) {
		
		this.time = time;
	}

	public float getMaxSpeed() {
		
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		
		this.maxSpeed = maxSpeed;
	}
}
