package com.valfom.tracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;

public class TrackerMainFragment extends SherlockFragment {

	public static final int BTN_START = 0;
	public static final int BTN_STOP = 1;
	public static final int BTN_PAUSE = 2;
	
	private static OnButtonClickedListener onButtonClickedListener;
	
	private Button btnStart;
	private Button btnStop;
	private Button btnPause;
	
	private TextView tvCurSpeed;
	private TextView tvDistanceUnit;
	private TextView tvCurSpeedUnit;
	private TextView tvMaxSpeedUnit;
	private TextView tvAvgSpeedUnit;
	private TextView tvMaxPaceUnit;
	private TextView tvAvgPaceUnit;
	
	private RelativeLayout rlSpeed;
	private RelativeLayout rlPace;
	private RelativeLayout rlAltitude;
	
	private TextView tvActivity;
	
	public interface OnButtonClickedListener {
		
        public void onButtonClicked(int btn);
    }
	
	@Override
    public void onAttach(Activity activity) {
		
        super.onAttach(activity);
        
        try {
        	
        	onButtonClickedListener = (OnButtonClickedListener) activity;
            
        } catch (ClassCastException e) {
        	
            throw new ClassCastException(activity.toString() + " must implement OnButtonClickedListener");
        }
    }
	
	@Override
	public void onResume() {

		TrackerSettings settings = new TrackerSettings(getActivity());
		
		tvDistanceUnit.setText(settings.getDistanceUnit());
		tvCurSpeedUnit.setText(settings.getSpeedUnit());
		tvMaxSpeedUnit.setText(settings.getSpeedUnit());
		tvAvgSpeedUnit.setText(settings.getSpeedUnit());
		tvMaxPaceUnit.setText(settings.getPaceUnit());
		tvAvgPaceUnit.setText(settings.getPaceUnit());
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
//		if (sharedPreferences.getBoolean("displaySpeed", true)) rlSpeed.setVisibility(View.VISIBLE);
//		else rlSpeed.setVisibility(View.GONE);
		
//		if (sharedPreferences.getBoolean("displayPace", true)) rlPace.setVisibility(View.VISIBLE);
//		else rlPace.setVisibility(View.GONE);
			
//		if (sharedPreferences.getBoolean("displayAltitude", true)) rlAltitude.setVisibility(View.VISIBLE);
//		else rlAltitude.setVisibility(View.GONE);
		
		int activityId = sharedPreferences.getInt("activity", 0);
		
		String[] activities = getView().getResources().getStringArray(R.array.activities_array);
	    
	    tvActivity.setText(activities[activityId]);
		
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_main_test, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		ScrollView svMain = (ScrollView) getView().findViewById(R.id.svMain);
		
		svMain.setVerticalScrollBarEnabled(false);
		svMain.setHorizontalScrollBarEnabled(false);
		
		rlSpeed = (RelativeLayout) getView().findViewById(R.id.RelativeLayoutSpeed);
		rlPace = (RelativeLayout) getView().findViewById(R.id.RelativeLayoutPace);
		rlAltitude = (RelativeLayout) getView().findViewById(R.id.RelativeLayoutAltitude);
		
		btnStart = (Button) getView().findViewById(R.id.startBtn);
		btnStop = (Button) getView().findViewById(R.id.stopBtn);
		btnPause = (Button) getView().findViewById(R.id.pauseBtn);
		
		tvCurSpeed = (TextView) getView().findViewById(R.id.tvCurSpeed);
		
		tvDistanceUnit = (TextView) getView().findViewById(R.id.tvDistanceUnit);
		tvCurSpeedUnit = (TextView) getView().findViewById(R.id.tvCurSpeedUnit);
		tvMaxSpeedUnit = (TextView) getView().findViewById(R.id.tvMaxSpeedUnit);
		tvAvgSpeedUnit = (TextView) getView().findViewById(R.id.tvAvgSpeedUnit);
		tvMaxPaceUnit = (TextView) getView().findViewById(R.id.tvMaxPaceUnit);
		tvAvgPaceUnit = (TextView) getView().findViewById(R.id.tvAvgPaceUnit);
		
		tvActivity = (TextView) getView().findViewById(R.id.tvActivity);
		
		tvActivity.setOnClickListener(new View.OnClickListener() {
        	
	        public void onClick(View v) {
	        	
	        	SherlockDialogFragment chooseActivity = new TrackerSelectActivityDialogFragment();
	        	chooseActivity.show(getActivity().getSupportFragmentManager(), "activities");
	        }
		});
		
		btnStart.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
        	  
				onButtonClickedListener.onButtonClicked(BTN_START);
			}
		});
		
		btnStop.setOnClickListener(new View.OnClickListener() {
        	
			public void onClick(View v) {
				
				onButtonClickedListener.onButtonClicked(BTN_STOP);
	        }
		});
		
		btnPause.setOnClickListener(new View.OnClickListener() {
        	
	        public void onClick(View v) {
	        	
	        	if (TrackerService.isPaused)
	        		btnPause.setText(R.string.btn_pause);
	        	else
	        		btnPause.setText(R.string.btn_resume);
	        	
	        	tvCurSpeed.setText(R.string.default_value_speed);
	        	
	        	TrackerService.isPaused = !TrackerService.isPaused;
	        	
	        	TrackerService.prevLocation = null;
	        	  
	        	onButtonClickedListener.onButtonClicked(BTN_PAUSE);
	        }
		});
	}

}