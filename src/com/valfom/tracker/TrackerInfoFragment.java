package com.valfom.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
		
		TextView tvDate = (TextView) v.findViewById(R.id.dateTV);
		TextView tvDistance = (TextView) v.findViewById(R.id.distTV);
		TextView tvDuration = (TextView) v.findViewById(R.id.timeTV);
		TextView tvMaxSpeed = (TextView) v.findViewById(R.id.maxSpeedTV);
		
		ImageView ivActivityInfoIcon = (ImageView) v.findViewById(R.id.ivActivityInfo);
		
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
	        
	        tvDate.setText(track.getDate());
	        tvDistance.setText(String.valueOf(settings.convertDistance(track.getDistance())));
	        tvDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
	        tvMaxSpeed.setText(String.valueOf(settings.convertSpeed(track.getMaxSpeed())));
	        
	        ivActivityInfoIcon.setImageResource(R.drawable.ic_launcher);
	        
	        if (intent.hasExtra("choise") && intent.getBooleanExtra("choise", false)) {
	        	
	        	llBtnsInfo.setVisibility(View.VISIBLE);
	        
		        btnSave.setOnClickListener(new View.OnClickListener() {
		        	
					public void onClick(View v) {
						
						llBtnsInfo.setVisibility(View.GONE);
					}
				});
				
				btnDelete.setOnClickListener(new View.OnClickListener() {
		        	
					public void onClick(View v) {
						
						db.deleteTrack(trackId);
						
						getActivity().onBackPressed();
			        }
				});
	        }
        }
		
		super.onActivityCreated(savedInstanceState);
	}
}
