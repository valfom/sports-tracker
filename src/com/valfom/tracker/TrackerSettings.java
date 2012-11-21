package com.valfom.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TrackerSettings {
	
	private Context context;

	private String speedUnit;
	private String distanceUnit;
	private int unitId;
	private String paceUnit;
	private boolean autoPause;
	private String autoPauseThreshold;
	
	public TrackerSettings(Context context) {
		
		this.context = context;
	}
	
	private void update() {
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        if (prefs.getString("units", context.getString(R.string.settings_units_metric))
        		.compareTo(context.getString(R.string.settings_units_metric)) == 0) {
        	
        	speedUnit = context.getString(R.string.kph);
        	distanceUnit = context.getString(R.string.km);
        	unitId = 0;
        	paceUnit = "min/km";
        } else {
        	
        	speedUnit = context.getString(R.string.mph);
        	distanceUnit = context.getString(R.string.mi);
        	unitId = 1;
        	paceUnit = "min/mi";
        }
        
        autoPause = prefs.getBoolean("autoPause", false);
        autoPauseThreshold = prefs.getString("autoPauseThreshold", context.getString(R.string.settings_default_value_autopause_threshold));
	}
	
	public boolean isAutoPause() {
		
		update();
		
		return autoPause;
	}
	
	public String getAutoPauseThreshold() {
		
		update();
		
		return autoPauseThreshold;
	}
	
	public float convertSpeed(float speed) {
		
		if (getUnitId() == 0) return (speed * 3600 / 1000);
			
		return (float) (speed * 2.2369);
	}
	
	public double convertDistance(double distance) {
		
		if (getUnitId() == 0) return (distance / 1000);

		return (distance / 1609.344);
	}
	
	public double getDistanceOneUnit() {
		
		if (getUnitId() == 0) return 1000;

		return 1609.344;
	}
	
	public String getPaceUnit() {
		
		update();
		
		return paceUnit;
	}
	
	public int getUnitId() {
		
		update();
		
		return unitId;
	}
	
	public String getSpeedUnit() {
		
		update();
		
		return speedUnit;
	}

	public String getDistanceUnit() {
		
		update();
		
		return distanceUnit;
	}
}