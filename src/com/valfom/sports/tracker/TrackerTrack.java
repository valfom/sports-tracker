package com.valfom.sports.tracker;

public class TrackerTrack {

	private int id;
	private int activityId;
	private String date;
	private double distance;
	private long duration;
	private float maxSpeed;
	private double avgSpeed;
	private double avgPace;
	private double maxPace;
	private double altitudeGain;
	private double altitudeLoss;
	private double maxAltitude;
	private double minAltitude;

	public TrackerTrack() {
	}

	public TrackerTrack(int id, int activityId, String date, double distance,
			long duration, float maxSpeed, double avgSpeed, double avgPace,
			double maxPace, double altitudeGain, double altitudeLoss,
			double maxAltitude, double minAltitude) {

		this.id = id;
		this.activityId = activityId;
		this.date = date;
		this.distance = distance;
		this.duration = duration;
		this.maxSpeed = maxSpeed;
		this.avgSpeed = avgSpeed;
		this.avgPace = avgPace;
		this.maxPace = maxPace;
		this.altitudeGain = altitudeGain;
		this.altitudeLoss = altitudeLoss;
		this.maxAltitude = maxAltitude;
		this.minAltitude = minAltitude;
	}

	public TrackerTrack(int activityId, String date, double distance,
			long duration, float maxSpeed, double avgSpeed, double avgPace,
			double maxPace, double altitudeGain, double altitudeLoss,
			double maxAltitude, double minAltitude) {

		this.activityId = activityId;
		this.date = date;
		this.distance = distance;
		this.duration = duration;
		this.maxSpeed = maxSpeed;
		this.avgSpeed = avgSpeed;
		this.avgPace = avgPace;
		this.maxPace = maxPace;
		this.altitudeGain = altitudeGain;
		this.altitudeLoss = altitudeLoss;
		this.maxAltitude = maxAltitude;
		this.minAltitude = minAltitude;
	}

	public int getActivity() {

		return activityId;
	}

	public void setActivity(int activityId) {

		this.activityId = activityId;
	}

	public double getMaxPace() {

		return maxPace;
	}

	public void setMaxPace(double maxPace) {

		this.maxPace = maxPace;
	}

	public double getAltitudeGain() {

		return altitudeGain;
	}

	public void setAltitudeGain(double altitudeGain) {

		this.altitudeGain = altitudeGain;
	}

	public double getAltitudeLoss() {

		return altitudeLoss;
	}

	public void setAltitudeLoss(double altitudeLoss) {

		this.altitudeLoss = altitudeLoss;
	}

	public double getAvgSpeed() {

		return avgSpeed;
	}

	public double getMaxAltitude() {
		
		return maxAltitude;
	}

	public void setMaxAltitude(double maxAltitude) {
		
		this.maxAltitude = maxAltitude;
	}

	public double getMinAltitude() {
		
		return minAltitude;
	}

	public void setMinAltitude(double minAltitude) {
		
		this.minAltitude = minAltitude;
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