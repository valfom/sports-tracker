package com.valfom.tracker;

public class Track {
	 
    private int id;
    private String date;
    private double distance;
    private long duration;
    private float maxSpeed;
    private double avgSpeed;
    private double avgPace;
    
    public Track() {}
    
    public Track(int id, String date, double distance, long duration, float maxSpeed, 
    		double avgSpeed, double avgPace) {
    	
        this.id = id;
        this.date = date;
        this.distance = distance;
        this.duration = duration;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.avgPace = avgPace;
    }
 
    public Track(String date, double distance, long duration, float maxSpeed, double avgSpeed, double avgPace){
    	
    	this.date = date;
        this.distance = distance;
        this.duration = duration;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.avgPace = avgPace;
    }

	public double getAvgSpeed() {
		
		return avgSpeed;
	}

	public void setAvgSpeed(double avgSpeed) {
		
		this.avgSpeed = avgSpeed;
	}

	public double getAvgPace() {
		
		return avgPace;
	}

	public void setAvgPace(double avgPace) {
		
		this.avgPace = avgPace;
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

	public long getDuration() {
		
		return duration;
	}

	public void setDuration(long duration) {
		
		this.duration = duration;
	}

	public float getMaxSpeed() {
		
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		
		this.maxSpeed = maxSpeed;
	}
}