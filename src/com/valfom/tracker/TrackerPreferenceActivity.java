package com.valfom.tracker;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class TrackerPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
    public static final String KEY_LIST_UNITS_PREFERENCE = "units";
    public static final String KEY_CB_KEEP_SCREEN_ON_PREFERENCE = "keepScreenOn";
    public static final String KEY_LIST_AUTO_PAUSE_PREFERENCE = "autoPause";

    private ListPreference listUnits;
    private CheckBoxPreference cbKeepScreenOn;
    private ListPreference listAutoPause;
    
    TrackerSettings settings;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	final ActionBar actionBar = getActionBar();
        	actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        settings = new TrackerSettings(this);
        
        addPreferencesFromResource(R.xml.settings);

        listUnits = (ListPreference) getPreferenceScreen().findPreference("units");
        cbKeepScreenOn = (CheckBoxPreference) getPreferenceScreen().findPreference("keepScreenOn");
        listAutoPause = (ListPreference) getPreferenceScreen().findPreference("autoPause");
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
        	
        	if (!cbKeepScreenOn.isChecked() && TrackerActivity.wl.isHeld())
        		TrackerActivity.wl.release();
        	
        } else if (key.equals(KEY_LIST_AUTO_PAUSE_PREFERENCE)) {
        	
        	String summary = sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off));
        	
        	if (summary.compareTo("Off") != 0)
        		summary += (" " + settings.getSpeedUnit());
        	
        	listAutoPause.setSummary(summary);
        }
    }
}