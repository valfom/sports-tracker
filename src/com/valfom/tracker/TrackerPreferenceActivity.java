package com.valfom.tracker;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        addPreferencesFromResource(R.xml.settings);

        listUnits = (ListPreference) getPreferenceScreen().findPreference("units");
        cbKeepScreenOn = (CheckBoxPreference) getPreferenceScreen().findPreference("keepScreenOn");
        listAutoPause = (ListPreference) getPreferenceScreen().findPreference("autoPause");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        switch (item.getItemId()) {
        
            case android.R.id.home:
                
                Intent mainActivity = new Intent(this, TrackerActivity.class);
                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivity);
                
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
        
        listAutoPause.setSummary(sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off)));
        listAutoPause.setEntries(getLimits());
        listAutoPause.setEntryValues(getLimits());
          
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
	private CharSequence[] getUnits() {
		
		CharSequence[] cs = new String[2];
		
		cs[0] =  getString(R.string.settings_units_metric);
		cs[1] =  getString(R.string.settings_units_imperial);
		
		return cs;
	}
	
	private CharSequence[] getLimits() {
		
		CharSequence[] cs = new String[4];
		
		TrackerSettings settings = new TrackerSettings(this);
		
		cs[0] =  getString(R.string.settings_autopause_off);
		cs[1] =  getString(R.string.settings_autopause_limit_2) + " " + settings.getSpeedUnit();
		cs[2] =  getString(R.string.settings_autopause_limit_5) + " " + settings.getSpeedUnit();
		cs[3] =  getString(R.string.settings_autopause_limit_7) + " " + settings.getSpeedUnit();
		
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
        	
        	listAutoPause.setSummary(sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off)));
        	listAutoPause.setEntries(getLimits());
            listAutoPause.setEntryValues(getLimits());
        	
        } else if (key.equals(KEY_CB_KEEP_SCREEN_ON_PREFERENCE)) {
        	
        	
        } else if (key.equals(KEY_LIST_AUTO_PAUSE_PREFERENCE)) {
        	
        	listAutoPause.setSummary(sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off)));
        }
    }
}
