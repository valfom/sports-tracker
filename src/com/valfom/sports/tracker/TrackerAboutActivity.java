package com.valfom.sports.tracker;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class TrackerAboutActivity extends SherlockActivity {
	
	private TextView tvAppVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		
		final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        tvAppVersion = (TextView) findViewById(R.id.tvAppVersion);
	}
	
	@Override
	protected void onResume() {

		PackageInfo pInfo = null;
        
		try {
			
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
		}
		
        String appVersion = pInfo.versionName;
		
        tvAppVersion.setText(getString(R.string.about_version) + " " + appVersion);
        
		super.onResume();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
        
	        case android.R.id.home:
	            
	        	onBackPressed();
	            
	            return true;
	        	
	        default:
	        	return super.onMenuItemSelected(featureId, item);
		}
	}
}
