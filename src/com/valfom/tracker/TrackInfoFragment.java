package com.valfom.tracker;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrackInfoFragment extends Fragment {

	private TextView dateTV;
	private TextView distTV;
	private TextView timeTV;
	private TextView maxSpeedTV;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		
	}

	@Override
	public void onResume() {
		
		super.onResume();
		
		dateTV = (TextView) getView().findViewById(R.id.dateTV);
		distTV = (TextView) getView().findViewById(R.id.distTV);
		timeTV = (TextView) getView().findViewById(R.id.timeTV);
        maxSpeedTV = (TextView) getView().findViewById(R.id.maxSpeedTV);
		
//		Intent i = getIntent();
//        int id = i.getIntExtra("id", 0);
        
//        DatabaseHandler db = new DatabaseHandler(this);
        
        Bundle args = getArguments();
        
        DatabaseHandler db = new DatabaseHandler(getActivity());
        
        Track track = db.getTrack(args.getInt("id"));
        db.close();
        
        Log.d("DIST", track.getDate() + " " + String.valueOf(track.getDistance() / 1000) + " " + String.valueOf((track.getTime() / 1000 / 60 / 60) + ":" + (track.getTime() / 1000 / 60) + ":" + (track.getTime() / 1000)));
        dateTV.setText(track.getDate());
        distTV.setText(String.valueOf(track.getDistance() / 1000));
        timeTV.setText(String.valueOf((track.getTime() / 1000 / 60 / 60) + ":" + (track.getTime() / 1000 / 60) + ":" + (track.getTime() / 1000)));
        maxSpeedTV.setText(String.valueOf(track.getMaxSpeed() * 18 / 5));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
//		View v = inflater.inflate(R.layout.fragment_info, container, false);
		
		
        
   
		
		return inflater.inflate(R.layout.fragment_info, container, false);
	}
}

