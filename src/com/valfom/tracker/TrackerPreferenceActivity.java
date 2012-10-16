package com.valfom.tracker;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class TrackerPreferenceActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
    public static final String KEY_LIST_UNITS_PREFERENCE = "units";
    public static final String KEY_CB_KEEP_SCREEN_ON_PREFERENCE = "keepScreenOn";
    public static final String KEY_LIST_AUTO_PAUSE_PREFERENCE = "autoPause";
    public static final String KEY_LIST_ACTIVITY_PREFERENCE = "activity";
    public static final String KEY_CB_DISPLAY_SPEED_PREFERENCE = "displaySpeed";
    public static final String KEY_CB_DISPLAY_PACE_PREFERENCE = "displayPace";
    public static final String KEY_CB_DISPLAY_ALTITUDE_PREFERENCE = "displayAltitude";

    private ListPreference listUnits;
    private CheckBoxPreference cbKeepScreenOn;
    private ListPreference listAutoPause;
    private ListPreference listActivity;
//    private CheckBoxPreference cbDisplaySpeed;
//    private CheckBoxPreference cbDisplayPace;
//    private CheckBoxPreference cbDisplayAltitude;
    
    TrackerSettings settings;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        settings = new TrackerSettings(this);
        
        addPreferencesFromResource(R.xml.settings);

        listUnits = (ListPreference) getPreferenceScreen().findPreference("units");
        cbKeepScreenOn = (CheckBoxPreference) getPreferenceScreen().findPreference("keepScreenOn");
        listAutoPause = (ListPreference) getPreferenceScreen().findPreference("autoPause");
        listActivity = (ListPreference) getPreferenceScreen().findPreference("activity");
//        cbDisplaySpeed = (CheckBoxPreference) getPreferenceScreen().findPreference("displaySpeed");
//        cbDisplayPace = (CheckBoxPreference) getPreferenceScreen().findPreference("displayPace");
//        cbDisplayAltitude = (CheckBoxPreference) getPreferenceScreen().findPreference("displayAltitude");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        switch (item.getItemId()) {
        
            case android.R.id.home:
                
            	onBackPressed();
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void onResume() {
    	
        super.onResume();
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        listUnits.setSummary(sharedPreferences.getString("units", getString(R.string.settings_units_metric)));
        listUnits.setEntries(getUnits());
        listUnits.setEntryValues(getUnits());
        
        String summary = sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off));
    	
    	if (summary.compareTo("Off") != 0)
    		summary += (" " + settings.getSpeedUnit());
    	
    	listAutoPause.setSummary(summary);
        listAutoPause.setEntries(getLimits(false));
        listAutoPause.setEntryValues(getLimits(true));
        
        listActivity.setSummary(sharedPreferences.getString("activity", getString(R.string.settings_activity_running)));
        listActivity.setEntries(getActivities());
        listActivity.setEntryValues(getActivities());
          
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	private CharSequence[] getUnits() {
		
		CharSequence[] cs = new String[2];
		
		cs[0] =  getString(R.string.settings_units_metric);
		cs[1] =  getString(R.string.settings_units_imperial);
		
		return cs;
	}
	
	private CharSequence[] getActivities() {
		
		CharSequence[] cs = new String[4];
		
		cs[0] =  getString(R.string.settings_activity_running);
		cs[1] =  getString(R.string.settings_activity_walking);
		cs[2] =  getString(R.string.settings_activity_cycling);
		cs[3] =  getString(R.string.settings_activity_snowboarding);
		
		return cs;
	}
	
	private CharSequence[] getLimits(boolean value) {
		
		CharSequence[] cs = new String[4];
		
		if (value) {
		
			cs[0] =  getString(R.string.settings_autopause_off);
			cs[1] =  getString(R.string.settings_autopause_limit_2);
			cs[2] =  getString(R.string.settings_autopause_limit_5);
			cs[3] =  getString(R.string.settings_autopause_limit_7);
			
		} else {
			
			cs[0] =  getString(R.string.settings_autopause_off);
			cs[1] =  getString(R.string.settings_autopause_limit_2) + " " + settings.getSpeedUnit();
			cs[2] =  getString(R.string.settings_autopause_limit_5) + " " + settings.getSpeedUnit();
			cs[3] =  getString(R.string.settings_autopause_limit_7) + " " + settings.getSpeedUnit();
		}
		
		return cs;
	}
	
    @SuppressWarnings("deprecation")
	@Override
    protected void onPause() {
        
    	super.onPause();
          
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }
    
    private boolean isServiceRunning() {
		
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
	        if (TrackerService.class.getName().equals(service.service.getClassName()))
	            return true;
	    
	    return false;
	}

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    	
    	if (key.equals(KEY_LIST_UNITS_PREFERENCE)) {
        	
        	listUnits.setSummary(sharedPreferences.getString("units", getString(R.string.settings_units_metric)));
        	
        	String summary = sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off));
        	
        	if (summary.compareTo("Off") != 0)
        		summary += (" " + settings.getSpeedUnit());
        	
        	listAutoPause.setSummary(summary);
        	listAutoPause.setEntries(getLimits(false));
            listAutoPause.setEntryValues(getLimits(true));
        	
        } else if (key.equals(KEY_CB_KEEP_SCREEN_ON_PREFERENCE)) {
        	
        	if (!cbKeepScreenOn.isChecked() && TrackerActivity.wl.isHeld()) TrackerActivity.wl.release();
        	else if (cbKeepScreenOn.isChecked() && isServiceRunning()) TrackerActivity.wl.acquire();
        	
        } else if (key.equals(KEY_LIST_AUTO_PAUSE_PREFERENCE)) {
        	
        	String summary = sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off));
        	
        	if (summary.compareTo("Off") != 0)
        		summary += (" " + settings.getSpeedUnit());
        	
        	listAutoPause.setSummary(summary);
        	
        } else if (key.equals(KEY_LIST_ACTIVITY_PREFERENCE)) {
        	
        	String summary = sharedPreferences.getString("activity", getString(R.string.settings_activity_running));
        	
        	listActivity.setSummary(summary);
        	
        } else if (key.equals(KEY_CB_DISPLAY_SPEED_PREFERENCE)) {
        	
        	
        } else if (key.equals(KEY_CB_DISPLAY_PACE_PREFERENCE)) {
        	
        } else if (key.equals(KEY_CB_DISPLAY_ALTITUDE_PREFERENCE)) {
        	
        }
    }
}