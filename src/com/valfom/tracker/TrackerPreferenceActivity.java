package com.valfom.tracker;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class TrackerPreferenceActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
    public static final String KEY_LIST_UNITS_PREFERENCE = "units";
    public static final String KEY_ET_ROUTE_LOW_TRESHOLD_PREFERENCE = "lowThreshold";
    public static final String KEY_ET_ROUTE_MIDDLE_TRESHOLD_PREFERENCE = "middleThreshold";
    public static final String KEY_ET_AUTOPAUSE_TRESHOLD_PREFERENCE = "autoPauseThreshold";

    private ListPreference listUnits;
    private EditTextPreference edAutoPauseThreshold;
    private EditTextPreference edLowThreshold;
    private EditTextPreference edMiddleThreshold;
    private Preference custVersion;
    private Preference custAbout;
    private Preference custRate;
    
    private TrackerSettings settings;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        settings = new TrackerSettings(this);
        
        addPreferencesFromResource(R.xml.settings);

        listUnits = (ListPreference) getPreferenceScreen().findPreference("units");
        edAutoPauseThreshold = (EditTextPreference) getPreferenceScreen().findPreference("autoPauseThreshold");
        custVersion = (Preference) getPreferenceScreen().findPreference("version");
        custAbout = (Preference) getPreferenceScreen().findPreference("about");
        custRate = (Preference) getPreferenceScreen().findPreference("rate");
        edLowThreshold = (EditTextPreference) getPreferenceScreen().findPreference("lowThreshold");
        edMiddleThreshold = (EditTextPreference) getPreferenceScreen().findPreference("middleThreshold");
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
        
        String lowThresholdSummary = sharedPreferences.getString("lowThreshold", getString(R.string.settings_default_value_low_threshold)) + " " + settings.getSpeedUnit();
        
        edLowThreshold.setSummary(lowThresholdSummary);
        edLowThreshold.setPositiveButtonText("Save");
        edLowThreshold.setNegativeButtonText("Cancel");
        
        String middleThresholdSummary = sharedPreferences.getString("middleThreshold", getString(R.string.settings_default_value_middle_threshold)) + " " + settings.getSpeedUnit();
        
        edMiddleThreshold.setSummary(middleThresholdSummary);
        edMiddleThreshold.setPositiveButtonText("Save");
        edMiddleThreshold.setNegativeButtonText("Cancel");
        
        String autoPauseThresholdSummary = sharedPreferences.getString("autoPauseThreshold", getString(R.string.settings_default_value_autopause_threshold)) + " " + settings.getSpeedUnit();
        
        edAutoPauseThreshold.setSummary(autoPauseThresholdSummary);
        edAutoPauseThreshold.setPositiveButtonText("Save");
        edAutoPauseThreshold.setNegativeButtonText("Cancel");
        
        PackageInfo pInfo = null;
        
		try {
			
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
		}
		
        String version = pInfo.versionName;
        
        custVersion.setSummary(version);
        
        custAbout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	
        	public boolean onPreferenceClick(Preference preference) {
                
        		Intent about = new Intent(TrackerPreferenceActivity.this, TrackerAboutActivity.class);
        		startActivity(about);
        		
                return true;
        	}
        });
        
        custRate.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	
        	public boolean onPreferenceClick(Preference preference) {
                
        		Uri uri = Uri.parse("market://details?id=" + getPackageName());
        		Intent market = new Intent(Intent.ACTION_VIEW, uri);
        		
        		try {
        			
        		  startActivity(market);
        		  
        		} catch (ActivityNotFoundException e) {
        			
        			e.printStackTrace();
        		}
        		
                return true;
        	}
        });
          
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

	private CharSequence[] getUnits() {
		
		CharSequence[] cs = new String[2];
		
		cs[0] =  getString(R.string.settings_units_metric);
		cs[1] =  getString(R.string.settings_units_imperial);
		
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
        	
        	String lowThresholdSummary = sharedPreferences.getString("lowThreshold", getString(R.string.settings_default_value_low_threshold)) + " " + settings.getSpeedUnit();
            
            edLowThreshold.setSummary(lowThresholdSummary);
            
            String middleThresholdSummary = sharedPreferences.getString("middleThreshold", getString(R.string.settings_default_value_middle_threshold)) + " " + settings.getSpeedUnit();
            
            edMiddleThreshold.setSummary(middleThresholdSummary);
            
            String autoPauseThresholdSummary = sharedPreferences.getString("autoPauseThreshold", getString(R.string.settings_default_value_autopause_threshold)) + " " + settings.getSpeedUnit();
            
            edAutoPauseThreshold.setSummary(autoPauseThresholdSummary);
        
    	} else if (key.equals(KEY_ET_ROUTE_LOW_TRESHOLD_PREFERENCE)) {
    	
    		String lowThreshold = sharedPreferences.getString("lowThreshold", getString(R.string.settings_default_value_low_threshold));
    		
    		if (lowThreshold.equals("")) {
    			
    			lowThreshold = getString(R.string.settings_default_value_low_threshold);
    			edLowThreshold.setText(lowThreshold);
    		}
    		
    		String lowThresholdSummary = lowThreshold + " " + settings.getSpeedUnit();
            
            edLowThreshold.setSummary(lowThresholdSummary);
    	
	    } else if (key.equals(KEY_ET_ROUTE_MIDDLE_TRESHOLD_PREFERENCE)) {
	    	
	    	String middleThreshold = sharedPreferences.getString("middleThreshold", getString(R.string.settings_default_value_middle_threshold));
    		
    		if (middleThreshold.equals("")) {
    			
    			middleThreshold = getString(R.string.settings_default_value_middle_threshold);
    			edMiddleThreshold.setText(middleThreshold);
    		}
    		
    		String middleThresholdSummary = middleThreshold + " " + settings.getSpeedUnit();
            
            edMiddleThreshold.setSummary(middleThresholdSummary);
	    	
	    } else if (key.equals(KEY_ET_AUTOPAUSE_TRESHOLD_PREFERENCE)) {

	    	String autoPauseThreshold = sharedPreferences.getString("autoPauseThreshold", getString(R.string.settings_default_value_autopause_threshold));
    		
    		if (autoPauseThreshold.equals("")) { 
    			
    			autoPauseThreshold = getString(R.string.settings_default_value_autopause_threshold);
    			edAutoPauseThreshold.setText(autoPauseThreshold);
    		}
    		
    		String autoPauseThresholdSummary = autoPauseThreshold + " " + settings.getSpeedUnit();
            
            edAutoPauseThreshold.setSummary(autoPauseThresholdSummary);
	    }
    }
}