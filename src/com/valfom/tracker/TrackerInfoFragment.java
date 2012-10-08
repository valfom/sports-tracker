package com.valfom.tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TrackerInfoFragment extends Fragment {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		TrackerActionBar actionBar = new TrackerActionBar(getActivity());
		actionBar.setPage("Info");
		
		TextView dateTV = (TextView) getView().findViewById(R.id.dateTV);
		TextView distTV = (TextView) getView().findViewById(R.id.distTV);
		TextView timeTV = (TextView) getView().findViewById(R.id.timeTV);
		TextView maxSpeedTV = (TextView) getView().findViewById(R.id.maxSpeedTV);
		
		final Button saveBtn = (Button) getView().findViewById(R.id.saveBtn);
		final Button deleteBtn = (Button) getView().findViewById(R.id.deleteBtn);
		
		saveBtn.setVisibility(View.GONE);
		deleteBtn.setVisibility(View.GONE);
		
        Bundle args = getArguments();
        
        final DB db = new DB(getActivity());
        final int id = args.getInt("id");
        
        Track track = db.getTrack(id);
        db.close();
        
        if (args.getBoolean("choise")) {
        	
        	saveBtn.setVisibility(View.VISIBLE);
    		deleteBtn.setVisibility(View.VISIBLE);
        }
        
        dateTV.setText(track.getDate());
        distTV.setText(String.valueOf(track.getDistance()));
        timeTV.setText(String.valueOf((track.getDuration() / 1000 / 60 / 60) + ":" + (track.getDuration() / 1000 / 60) + ":" + (track.getDuration() / 1000)));
        maxSpeedTV.setText(String.valueOf(track.getMaxSpeed()));
        
        saveBtn.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				
//				Toast.makeText(getActivity(), "Track saved", Toast.LENGTH_SHORT).show();
        	 
				saveBtn.setVisibility(View.GONE);
				deleteBtn.setVisibility(View.GONE);
			}
		});
		
		deleteBtn.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				
//				Toast.makeText(getActivity(), "Track deleted", Toast.LENGTH_SHORT).show();
				
				db.deleteTrack(id);
				
				getActivity().onBackPressed();
	        }
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_info, container, false);
	}
}