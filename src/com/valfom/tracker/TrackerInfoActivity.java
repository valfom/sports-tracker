package com.valfom.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class TrackerInfoActivity extends SherlockActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		TextView dateTV = (TextView) findViewById(R.id.dateTV);
		TextView distTV = (TextView) findViewById(R.id.distTV);
		TextView timeTV = (TextView) findViewById(R.id.timeTV);
		TextView maxSpeedTV = (TextView) findViewById(R.id.maxSpeedTV);
		
		final Button saveBtn = (Button) findViewById(R.id.saveBtn);
		final Button deleteBtn = (Button) findViewById(R.id.deleteBtn);
		
		saveBtn.setVisibility(View.GONE);
		deleteBtn.setVisibility(View.GONE);
		
		Intent intent = getIntent();
		
		final int trackId = intent.getIntExtra("trackId", 1);
		
        final DB db = new DB(this);
        Track track = db.getTrack(trackId);
        db.close();
        
        if (intent.hasExtra("choise") && intent.getBooleanExtra("choise", false)) {
        	
        	saveBtn.setVisibility(View.VISIBLE);
    		deleteBtn.setVisibility(View.VISIBLE);
        }
        
        TrackerSettings settings = new TrackerSettings(this);
        
        dateTV.setText(track.getDate());
        distTV.setText(String.valueOf(settings.convertDistance(track.getDistance())));
        timeTV.setText(String.valueOf((track.getDuration() / 1000 / 60 / 60) + ":" + (track.getDuration() / 1000 / 60) + ":" + (track.getDuration() / 1000)));
        maxSpeedTV.setText(String.valueOf(settings.convertSpeed(track.getMaxSpeed())));
        
        saveBtn.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				
				saveBtn.setVisibility(View.GONE);
				deleteBtn.setVisibility(View.GONE);
			}
		});
		
		deleteBtn.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				
//				Toast.makeText(getActivity(), "Track deleted", Toast.LENGTH_SHORT).show();
				
				db.deleteTrack(trackId);
				
				onBackPressed();
	        }
		});
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
}