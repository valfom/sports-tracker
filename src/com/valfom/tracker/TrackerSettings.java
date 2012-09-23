package com.valfom.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class TrackerSettings {
	
	private Context context;

	private String speedUnit;
	private String distanceUnit;
	private boolean keepScreenOn;
	private String autopauseLimit;
	private int unitId;
	private String paceUnit;
	private String paceTitleUnit;
	
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
        	paceTitleUnit = "Last km";
        } else {
        	
        	speedUnit = context.getString(R.string.mph);
        	distanceUnit = context.getString(R.string.mi);
        	unitId = 1;
        	paceUnit = "min/mi";
        	paceTitleUnit = "Last mi";
        }
       
        keepScreenOn = prefs.getBoolean("keepScreenOn", false);
        autopauseLimit = prefs.getString("autoPause", context.getString(R.string.settings_autopause_off));
	}
	
	public float convertSpeed (float speed) {
		
		return 0;
	}
	
	public double convertDistance (double distance) {
		
		if (getUnitId() == 0) {
    		
    		distance = (distance / 1000);
		} else if (getUnitId() == 0) {
			
			distance = (distance / 1609.344);
		}
		
		return distance;
	}
	
	public double getDistanceOneUnit() {
		
		if (getUnitId() == 0)
    		
    		return 1000;
		else 
			return 1609.344;
	}
	
	public String getPaceUnit() {
		
		update();
		
		return paceUnit;
	}

	public String getPaceTitleUnit() {
		
		update();
		
		return paceTitleUnit;
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

	public boolean isKeepScreenOn() {
		
		update();
		
		return keepScreenOn;
	}

	public String getAutopauseLimit() {
		
		update();
		
		return autopauseLimit;
	}
}
