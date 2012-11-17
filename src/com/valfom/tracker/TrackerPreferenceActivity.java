package com.valfom.tracker;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
    public static final String KEY_LIST_AUTO_PAUSE_PREFERENCE = "autoPause";
    public static final String KEY_LIST_ACTIVITY_PREFERENCE = "activity";
    public static final String KEY_CB_DISPLAY_SPEED_PREFERENCE = "displaySpeed";
    public static final String KEY_CB_DISPLAY_PACE_PREFERENCE = "displayPace";
    public static final String KEY_CB_DISPLAY_ALTITUDE_PREFERENCE = "displayAltitude";
    public static final String KEY_ET_ROUTE_LOW_TRESHOLD_PREFERENCE = "lowTreshold";
    public static final String KEY_ET_ROUTE_MIDDLE_TRESHOLD_PREFERENCE = "middleTreshold";
    public static final String KEY_CB_CUSTOM_ROUTE_PREFERENCE = "customRoute";

    private ListPreference listUnits;
    private ListPreference listAutoPause;
    private EditTextPreference edLowTreshold;
    private EditTextPreference edMiddleTreshold;
    private Preference custAppVersion;
    private Preference custAbout;
    private Preference custRate;
    private CheckBoxPreference cbCustomRoute;
    
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
        listAutoPause = (ListPreference) getPreferenceScreen().findPreference("autoPause");
        custAppVersion = (Preference) getPreferenceScreen().findPreference("appVersion");
        custAbout = (Preference) getPreferenceScreen().findPreference("about");
        custRate = (Preference) getPreferenceScreen().findPreference("rate");
        edLowTreshold = (EditTextPreference) getPreferenceScreen().findPreference("lowTreshold");
        edMiddleTreshold = (EditTextPreference) getPreferenceScreen().findPreference("middleTreshold");
        cbCustomRoute = (CheckBoxPreference) getPreferenceScreen().findPreference("customRoute");
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

        edLowTreshold.setEnabled(cbCustomRoute.isChecked());
        edMiddleTreshold.setEnabled(cbCustomRoute.isChecked());
        
        String lowTreshold = sharedPreferences.getString("lowTreshold", getString(R.string.settings_low_treshold)) + " " + settings.getSpeedUnit();
        
        edLowTreshold.setSummary(lowTreshold);
        edLowTreshold.setPositiveButtonText("Save");
        edLowTreshold.setNegativeButtonText("Cancel");
        
        String middleTreshold = sharedPreferences.getString("middleTreshold", getString(R.string.settings_middle_treshold)) + " " + settings.getSpeedUnit();
        
        edMiddleTreshold.setSummary(middleTreshold);
        edMiddleTreshold.setPositiveButtonText("Save");
        edMiddleTreshold.setNegativeButtonText("Cancel");
        
        listUnits.setSummary(sharedPreferences.getString("units", getString(R.string.settings_units_metric)));
        listUnits.setEntries(getUnits());
        listUnits.setEntryValues(getUnits());
        
        String summary = sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off));
    	
    	if (summary.compareTo("Off") != 0)
    		summary += (" " + settings.getSpeedUnit());
    	
    	listAutoPause.setSummary(summary);
        listAutoPause.setEntries(getLimits(false));
        listAutoPause.setEntryValues(getLimits(true));
        
        PackageInfo pInfo = null;
        
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
        String version = pInfo.versionName;
        
        custAppVersion.setSummary(version);
        
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
        		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        		
        		try {
        			
        		  startActivity(goToMarket);
        		  
        		} catch (ActivityNotFoundException e) {
        			
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
	
	private CharSequence[] getLimits(boolean value) {
		
		CharSequence[] cs = new String[5];
		
		if (value) {
		
			cs[0] =  getString(R.string.settings_autopause_off);
			cs[1] =  getString(R.string.settings_autopause_limit_0);
			cs[2] =  getString(R.string.settings_autopause_limit_2);
			cs[3] =  getString(R.string.settings_autopause_limit_5);
			cs[4] =  getString(R.string.settings_autopause_limit_7);
			
		} else {
			
			cs[0] =  getString(R.string.settings_autopause_off);
			cs[1] =  getString(R.string.settings_autopause_limit_0) + " " + settings.getSpeedUnit();
			cs[2] =  getString(R.string.settings_autopause_limit_2) + " " + settings.getSpeedUnit();
			cs[3] =  getString(R.string.settings_autopause_limit_5) + " " + settings.getSpeedUnit();
			cs[4] =  getString(R.string.settings_autopause_limit_7) + " " + settings.getSpeedUnit();
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
        	
        } else if (key.equals(KEY_LIST_AUTO_PAUSE_PREFERENCE)) {
        	
        	String summary = sharedPreferences.getString("autoPause", getString(R.string.settings_autopause_off));
        	
        	if (summary.compareTo("Off") != 0)
        		summary += (" " + settings.getSpeedUnit());
        	
        	listAutoPause.setSummary(summary);
        	
        } else if (key.equals(KEY_CB_CUSTOM_ROUTE_PREFERENCE)) {
        	
        	edLowTreshold.setEnabled(cbCustomRoute.isChecked());
            edMiddleTreshold.setEnabled(cbCustomRoute.isChecked());
        	
        }
    }
}