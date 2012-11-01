package com.valfom.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class TrackerInfoFragment extends SherlockFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_info, container, false);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		
		TextView dateTV = (TextView) getView().findViewById(R.id.dateTV);
		TextView distTV = (TextView) getView().findViewById(R.id.distTV);
		TextView timeTV = (TextView) getView().findViewById(R.id.timeTV);
		TextView maxSpeedTV = (TextView) getView().findViewById(R.id.maxSpeedTV);
		
		final Button saveBtn = (Button) getView().findViewById(R.id.saveBtn);
		final Button deleteBtn = (Button) getView().findViewById(R.id.deleteBtn);
		
		final LinearLayout llBtnsInfo = (LinearLayout) getView().findViewById(R.id.llBtnsInfo);
		
        Intent intent = getActivity().getIntent();
        final int trackId = intent.getIntExtra("trackId", 1);
        
        final TrackerDB db = new TrackerDB(getActivity());
        TrackerTrack track = db.getTrack(trackId);
        db.close();
        
        if (intent.hasExtra("choise") && intent.getBooleanExtra("choise", false)) {
        	
        	llBtnsInfo.setVisibility(View.VISIBLE);
        }
        
        TrackerSettings settings = new TrackerSettings(getActivity());
        
        dateTV.setText(track.getDate());
        distTV.setText(String.valueOf(settings.convertDistance(track.getDistance())));
        timeTV.setText(String.valueOf((track.getDuration() / 1000 / 60 / 60) + ":" + (track.getDuration() / 1000 / 60) + ":" + (track.getDuration() / 1000)));
        maxSpeedTV.setText(String.valueOf(settings.convertSpeed(track.getMaxSpeed())));
        
        saveBtn.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				
				llBtnsInfo.setVisibility(View.GONE);
			}
		});
		
		deleteBtn.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				
//				Toast.makeText(getActivity(), "Track deleted", Toast.LENGTH_SHORT).show();
				
				db.deleteTrack(trackId);
				db.deleteRoute(trackId);
				
				getActivity().onBackPressed();
	        }
		});
		
		super.onActivityCreated(arg0);
	}
}
