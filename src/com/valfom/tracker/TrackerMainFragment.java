package com.valfom.tracker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TrackerMainFragment extends Fragment {

	public static final int BTN_START = 0;
	public static final int BTN_STOP = 1;
	public static final int BTN_PAUSE = 2;
	
	OnButtonClickedListener mListener;
	OnStatusRestoredListener mStatusListener;
	
//	private static TextView timeTV;
	private static Button startBtn;
	private static Button stopBtn;
	private static Button pauseBtn;
//	private static TextView distanceTV;
	private static TextView curSpeedTV;
//	private static TextView maxSpeedTV;
	
	private static TextView distanceUnitTV;
	private static TextView speedUnitTV;
	private static TextView maxSpeedUnitTV;
	private static TextView avgSpeedUnitTV;
	private static TextView paceLastUnitTV;
	private static TextView avgPaceUnitTV;
	
	private static TextView paceLastTitleTV;
//	private static TextView avgPaceTitleUnitTV;
	
	public interface OnButtonClickedListener {
		
        public void onButtonClicked(int btn);
    }
	
	public interface OnStatusRestoredListener {
		
        public void onStatusRestored(String status);
    }
	
	@Override
    public void onAttach(Activity activity) {
		
        super.onAttach(activity);
        
        try {
            mListener = (OnButtonClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnButtonClickedListener");
        }
        
        try {
            mStatusListener = (OnStatusRestoredListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnStatusRestoredListener");
        }
    }
	
	@Override
	public void onResume() {

		super.onResume();
		
		TrackerSettings settings = new TrackerSettings(getActivity());
		
		distanceUnitTV.setText(settings.getDistanceUnit());
		speedUnitTV.setText(settings.getSpeedUnit());
		maxSpeedUnitTV.setText(settings.getSpeedUnit());
		avgSpeedUnitTV.setText(settings.getSpeedUnit());
		paceLastUnitTV.setText(settings.getPaceUnit());
		avgPaceUnitTV.setText(settings.getPaceUnit());
		paceLastTitleTV.setText(settings.getPaceTitleUnit());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
	
//		timeTV = (TextView) getView().findViewById(R.id.timeTV);
		startBtn = (Button) getView().findViewById(R.id.startBtn);
		stopBtn = (Button) getView().findViewById(R.id.stopBtn);
		pauseBtn = (Button) getView().findViewById(R.id.pauseBtn);
//		distanceTV = (TextView) getView().findViewById(R.id.distanceTV);
		curSpeedTV = (TextView) getView().findViewById(R.id.curSpeedTV);
//		maxSpeedTV = (TextView) getView().findViewById(R.id.maxSpeedTV);
		
		distanceUnitTV = (TextView) getView().findViewById(R.id.distanceUnitTV);
		speedUnitTV = (TextView) getView().findViewById(R.id.speedUnitTV);
		maxSpeedUnitTV = (TextView) getView().findViewById(R.id.maxSpeedUnitTV);
		avgSpeedUnitTV = (TextView) getView().findViewById(R.id.avdSpeedUnitTV);
		paceLastUnitTV = (TextView) getView().findViewById(R.id.paceLastUnitTV);
		avgPaceUnitTV = (TextView) getView().findViewById(R.id.avgPaceUnitTV);
		
		paceLastTitleTV = (TextView) getView().findViewById(R.id.paceLastTitleTV);
//		avgPaceTitleUnitTV = (TextView) getView().findViewById(R.id.avgPaceTitleTV);
		
		TrackerSettings settings = new TrackerSettings(getActivity());
		
		distanceUnitTV.setText(settings.getDistanceUnit());
		speedUnitTV.setText(settings.getSpeedUnit());
		maxSpeedUnitTV.setText(settings.getSpeedUnit());
		avgSpeedUnitTV.setText(settings.getSpeedUnit());
		paceLastUnitTV.setText(settings.getPaceUnit());
		avgPaceUnitTV.setText(settings.getPaceUnit());
		
		paceLastTitleTV.setText(settings.getPaceTitleUnit());
		
		stopBtn.setVisibility(View.GONE);
		pauseBtn.setVisibility(View.GONE);
		
		startBtn.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
        	  
				mListener.onButtonClicked(BTN_START);
			}
		});
		
		stopBtn.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				
				mListener.onButtonClicked(BTN_STOP);
	        }
		});
		
		pauseBtn.setOnClickListener(new View.OnClickListener() {
        	
	        public void onClick(View v) {
	        	
	        	if (TrackerService.isPaused)
	        		pauseBtn.setText(R.string.pause_btn);
	        	else
	        		pauseBtn.setText(R.string.resume_btn);
	        	
	        	curSpeedTV.setText(R.string.default_value_speed);
	        	
	        	TrackerService.isPaused = !TrackerService.isPaused;
	        	
	        	TrackerService.prevLocation = null;
	        	  
	        	mListener.onButtonClicked(BTN_PAUSE);
	        }
		});
		
		String state;
		
		if (isMyServiceRunning()) {
			
			if (!TrackerService.isPaused)
				state = "started";
			else
				state = "paused";
		} else
			state = "stopped";
		
		mStatusListener.onStatusRestored(state);
	}
	
	private boolean isMyServiceRunning() {
		
	    ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
	    
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
	        if (TrackerService.class.getName().equals(service.service.getClassName()))
	            return true;
	    
	    return false;
	}
}
