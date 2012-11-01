package com.valfom.tracker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class TrackerMainFragment extends SherlockFragment {

	public static final int BTN_START = 0;
	public static final int BTN_STOP = 1;
	public static final int BTN_PAUSE = 2;
	
	private static OnButtonClickedListener mListener;
	private static OnStateRestoredListener mStateListener;
	
	private static Button startBtn;
	private static Button stopBtn;
	private static Button pauseBtn;
	
	private static TextView tvCurSpeed;
	private static TextView tvDistanceUnit;
	private static TextView tvCurSpeedUnit;
	private static TextView tvMaxSpeedUnit;
	private static TextView tvAvgSpeedUnit;
	private static TextView tvMaxPaceUnit;
	private static TextView tvAvgPaceUnit;
	
	private static RelativeLayout rlSpeed;
	private static RelativeLayout rlPace;
	private static RelativeLayout rlAltitude;
	
	private static ImageView ivActivity;
	
	public interface OnButtonClickedListener {
		
        public void onButtonClicked(int btn);
    }
	
	public interface OnStateRestoredListener {
		
        public void onStateRestored(String state);
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
        	
            mStateListener = (OnStateRestoredListener) activity;
            
        } catch (ClassCastException e) {
        	
            throw new ClassCastException(activity.toString() + " must implement OnStateRestoredListener");
        }
    }
	
	@Override
	public void onResume() {

		super.onResume();
		
		TrackerSettings settings = new TrackerSettings(getActivity());
		
		tvDistanceUnit.setText(settings.getDistanceUnit());
		tvCurSpeedUnit.setText(settings.getSpeedUnit());
		tvMaxSpeedUnit.setText(settings.getSpeedUnit());
		tvAvgSpeedUnit.setText(settings.getSpeedUnit());
		tvMaxPaceUnit.setText(settings.getPaceUnit());
		tvAvgPaceUnit.setText(settings.getPaceUnit());
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		if (sharedPreferences.getBoolean("displaySpeed", true)) rlSpeed.setVisibility(View.VISIBLE);
		else rlSpeed.setVisibility(View.GONE);
		
		if (sharedPreferences.getBoolean("displayPace", true)) rlPace.setVisibility(View.VISIBLE);
		else rlPace.setVisibility(View.GONE);
			
		if (sharedPreferences.getBoolean("displayAltitude", true)) rlAltitude.setVisibility(View.VISIBLE);
		else rlAltitude.setVisibility(View.GONE);
		
//		String activity = sharedPreferences.getString("activity", getString(R.string.settings_activity_running));
		
		ivActivity.setImageResource(R.drawable.ic_launcher);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		rlSpeed = (RelativeLayout) getView().findViewById(R.id.RelativeLayoutSpeed);
		rlPace = (RelativeLayout) getView().findViewById(R.id.RelativeLayoutPace);
		rlAltitude = (RelativeLayout) getView().findViewById(R.id.RelativeLayoutAltitude);
		
		startBtn = (Button) getView().findViewById(R.id.startBtn);
		stopBtn = (Button) getView().findViewById(R.id.stopBtn);
		pauseBtn = (Button) getView().findViewById(R.id.pauseBtn);
		
		tvCurSpeed = (TextView) getView().findViewById(R.id.tvCurSpeed);
		
		tvDistanceUnit = (TextView) getView().findViewById(R.id.tvDistanceUnit);
		tvCurSpeedUnit = (TextView) getView().findViewById(R.id.tvCurSpeedUnit);
		tvMaxSpeedUnit = (TextView) getView().findViewById(R.id.tvMaxSpeedUnit);
		tvAvgSpeedUnit = (TextView) getView().findViewById(R.id.tvAvgSpeedUnit);
		tvMaxPaceUnit = (TextView) getView().findViewById(R.id.tvMaxPaceUnit);
		tvAvgPaceUnit = (TextView) getView().findViewById(R.id.tvAvgPaceUnit);
		
		ivActivity = (ImageView) getView().findViewById(R.id.ivActivity);
		
//		stopBtn.setVisibility(View.GONE);
//		pauseBtn.setVisibility(View.GONE);
		
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
	        		pauseBtn.setText(R.string.btn_pause);
	        	else
	        		pauseBtn.setText(R.string.btn_resume);
	        	
	        	tvCurSpeed.setText(R.string.default_value_speed);
	        	
	        	TrackerService.isPaused = !TrackerService.isPaused;
	        	
	        	TrackerService.prevLocation = null;
	        	  
	        	mListener.onButtonClicked(BTN_PAUSE);
	        }
		});
		
		String state;
		
		if (isServiceRunning()) {
			
			if (!TrackerService.isPaused)
				state = "started";
			else
				state = "paused";
		} else
			state = "stopped";
		
		mStateListener.onStateRestored(state);
	}
	
	private boolean isServiceRunning() {
		
	    ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
	    
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
	        if (TrackerService.class.getName().equals(service.service.getClassName()))
	            return true;
	    
	    return false;
	}
}