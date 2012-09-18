package com.valfom.tracker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrackInfoFragment extends Fragment {

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		TextView dateTV = (TextView) getView().findViewById(R.id.dateTV);
		TextView distTV = (TextView) getView().findViewById(R.id.distTV);
		TextView timeTV = (TextView) getView().findViewById(R.id.timeTV);
		TextView maxSpeedTV = (TextView) getView().findViewById(R.id.maxSpeedTV);
		
        Bundle args = getArguments();
        
        DatabaseHandler db = new DatabaseHandler(getActivity());
        
        Track track = db.getTrack(args.getInt("id"));
        db.close();
        
        dateTV.setText(track.getDate());
        distTV.setText(String.valueOf(track.getDistance()));
        timeTV.setText(String.valueOf((track.getTime() / 1000 / 60 / 60) + ":" + (track.getTime() / 1000 / 60) + ":" + (track.getTime() / 1000)));
        maxSpeedTV.setText(String.valueOf(track.getMaxSpeed()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_info, container, false);
	}
}