package com.valfom.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class TrackerSettings {

	private String speedUnit;
	private String distanceUnit;
	private boolean keepScreenOn;
	private String autopauseLimit;
	
	public TrackerSettings(Context context) {
		
		Toast.makeText(context, "in", Toast.LENGTH_SHORT).show();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        if (prefs.getString("units", context.getString(R.string.settings_units_metric))
        		.compareTo(context.getString(R.string.settings_units_metric)) == 0) {
        	
        	speedUnit = context.getString(R.string.kph);
        	distanceUnit = context.getString(R.string.km);
        } else {
        	
        	speedUnit = context.getString(R.string.mph);
        	distanceUnit = context.getString(R.string.mi);
        }
       
        keepScreenOn = prefs.getBoolean("keepScreenOn", false);
        autopauseLimit = prefs.getString("autopauseLimit", context.getString(R.string.settings_units_metric));
	}

	public String getSpeedUnit() {
		
		return speedUnit;
	}

	public String getDistanceUnit() {
		
		return distanceUnit;
	}

	public boolean isKeepScreenOn() {
		
		return keepScreenOn;
	}

	public String getAutopauseLimit() {
		
		return autopauseLimit;
	}
}
