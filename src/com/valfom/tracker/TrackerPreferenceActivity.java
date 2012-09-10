package com.valfom.tracker;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class TrackerPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
    public static final String KEY_LIST_UNITS_PREFERENCE = "units";

    private ListPreference listUnits;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        addPreferencesFromResource(R.xml.settings);

//        mEditTextNickname = (EditTextPreference) getPreferenceScreen().findPreference("nnEditPref");
        listUnits = (ListPreference) getPreferenceScreen().findPreference("units");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, TrackerActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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

        listUnits.setSummary(sharedPreferences.getString("units", ""));
        
        listUnits.setEntries(getUnits());
        listUnits.setEntryValues(getUnits());
          
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
	private CharSequence[] getUnits() {
		
		CharSequence[] cs = new String[2];
		
		cs[0] =  getResources().getString(R.string.units_metric);
		cs[1] =  getResources().getString(R.string.units_imperial);
		
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
        	
        	listUnits.setSummary(sharedPreferences.getString("units", "")); 
        }
    }
}
