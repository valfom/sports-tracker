package com.valfom.sports.tracker;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_info, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		View v = getView();
		
		TextView tvDate = (TextView) v.findViewById(R.id.tvDateInfo);
		TextView tvDistance = (TextView) v.findViewById(R.id.tvDistanceInfo);
		TextView tvDuration = (TextView) v.findViewById(R.id.tvDurationInfo);
		TextView tvMaxSpeed = (TextView) v.findViewById(R.id.tvMaxSpeedInfo);
		TextView tvAvgSpeed = (TextView) v.findViewById(R.id.tvAvgSpeedInfo);
		TextView tvMaxPace = (TextView) v.findViewById(R.id.tvMaxPaceInfo);
		TextView tvAvgPace = (TextView) v.findViewById(R.id.tvAvgPaceInfo);
//		TextView tvMaxAltitude = (TextView) v.findViewById(R.id.tvMaxAltitudeInfo);
//		TextView tvMinAltitude = (TextView) v.findViewById(R.id.tvMinAltitudeInfo);
		TextView tvGainAltitude = (TextView) v.findViewById(R.id.tvAltitudeGainInfo);
		TextView tvLossAltitude = (TextView) v.findViewById(R.id.tvAltitudeLossInfo);
		
		TextView tvActivityInfo = (TextView) v.findViewById(R.id.tvActivityInfo);
		
		final Button btnSave = (Button) v.findViewById(R.id.saveBtn);
		final Button btnDelete = (Button) v.findViewById(R.id.deleteBtn);
		
		final LinearLayout llBtnsInfo = (LinearLayout) v.findViewById(R.id.llBtnsInfo);
		
        Intent intent = getActivity().getIntent();
        
        final int trackId = intent.getIntExtra("trackId", -1);
        
        if (trackId != -1) {
        
	        final TrackerDB db = new TrackerDB(getActivity());
	        
	        TrackerTrack track = db.getTrack(trackId);
	        
	        db.close();
        
	        long millis = track.getDuration();
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds     = seconds % 60;
			int hours = minutes / 60;
			minutes = minutes % 60;
	        
	        TrackerSettings settings = new TrackerSettings(getActivity());
	        
	        String date = track.getDate();
			double distance = track.getDistance();
			float maxSpeed = track.getMaxSpeed();
			float avgSpeed = (float) track.getAvgSpeed();
			double maxPace = (float) track.getMaxPace();
			double avgPace = (float) track.getAvgPace();
			double lossAltitude = (float) track.getAltitudeLoss();
			double gainAltitude = (float) track.getAltitudeGain();
	        
			distance = settings.convertDistance(distance);
	    	maxSpeed = settings.convertSpeed(maxSpeed);
	    	avgSpeed = settings.convertSpeed(avgSpeed);
			
	        long millisAvgPace = (long) avgPace;
			int secondsAvgPace = (int) (millisAvgPace / 1000);
	    	int minutesAvgPace = secondsAvgPace / 60;
	    	secondsAvgPace = secondsAvgPace % 60;
	    	int hoursAvgPace = minutesAvgPace / 60;
	    	minutesAvgPace = minutesAvgPace % 60;
	    	
	    	long millisMaxPace = (long) maxPace;
			int secondsMaxPace = (int) (millisMaxPace / 1000);
	    	int minutesMaxPace = secondsMaxPace / 60;
	    	secondsMaxPace = secondsMaxPace % 60;
	    	int hoursMaxPace = minutesMaxPace / 60;
	    	minutesMaxPace = minutesMaxPace % 60;
	        
	    	tvDate.setText(date);
	        tvDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
	        tvDistance.setText(String.format("%.2f", distance));
	        tvMaxSpeed.setText(String.format("%02.0f", maxSpeed));
	        tvAvgSpeed.setText(String.format("%02.0f", avgSpeed));
	
	        if (hoursMaxPace > 0)
	        	tvMaxPace.setText(String.format("%02d:%02d:%02d",hoursMaxPace, minutesMaxPace, secondsMaxPace));
	        else
	        	tvMaxPace.setText(String.format("%02d:%02d", minutesMaxPace, secondsMaxPace));
	
			if (hoursAvgPace > 0)
				tvAvgPace.setText(String.format("%02d:%02d:%02d",hoursAvgPace, minutesAvgPace, secondsAvgPace));
			else
				tvAvgPace.setText(String.format("%02d:%02d", minutesAvgPace, secondsAvgPace));
	
			tvLossAltitude.setText(String.format("%02.0f", lossAltitude));
			tvGainAltitude.setText(String.format("%02.0f", gainAltitude));
	
	        String[] activities = v.getResources().getStringArray(R.array.activities_array);
		    
	        int activityId = track.getActivity();
	        
		    tvActivityInfo.setText(activities[activityId]);
	        
	        if (intent.hasExtra("choise") && intent.getBooleanExtra("choise", false)) {
	        	
	        	llBtnsInfo.setVisibility(View.VISIBLE);
	        
		        btnSave.setOnClickListener(new View.OnClickListener() {
		        	
					public void onClick(View v) {
						
						llBtnsInfo.setVisibility(View.GONE);
						
						getActivity().finish();// .onBackPressed();
					}
				});
				
				btnDelete.setOnClickListener(new View.OnClickListener() {
		        	
					public void onClick(View v) {
						
						db.deleteTrack(trackId);
						
						getActivity().finish();// .onBackPressed();
			        }
				});
	        }
        }
		
		super.onActivityCreated(savedInstanceState);
	}
}
