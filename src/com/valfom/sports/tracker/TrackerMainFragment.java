package com.valfom.sports.tracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableRow;
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
	
	private TableRow trSpeedTitle1;
	private TableRow trSpeed1;
	private TableRow trSpeedTitle2;
	private TableRow trSpeed2;
	private TableRow trPaceTitle;
	private TableRow trPace;
	private TableRow trAltitudeTitle1;
	private TableRow trAltitude1;
	private TableRow trAltitudeTitle2;
	private TableRow trAltitude2;
	private TableRow trAltitudeTitle3;
	private TableRow trAltitude3;
	
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
		
		if (sharedPreferences.getBoolean("displaySpeed", true)) {
			
			trSpeedTitle1.setVisibility(View.VISIBLE);
			trSpeedTitle2.setVisibility(View.VISIBLE);
			trSpeed1.setVisibility(View.VISIBLE);
			trSpeed2.setVisibility(View.VISIBLE);
		} else {
			
			trSpeedTitle1.setVisibility(View.GONE);
			trSpeedTitle2.setVisibility(View.GONE);
			trSpeed1.setVisibility(View.GONE);
			trSpeed2.setVisibility(View.GONE);
		}
		
		if (sharedPreferences.getBoolean("displayPace", true)) {
			
			trPaceTitle.setVisibility(View.VISIBLE);
			trPace.setVisibility(View.VISIBLE);
		} else {
			
			trPaceTitle.setVisibility(View.GONE);
			trPace.setVisibility(View.GONE);
		}
			
		if (sharedPreferences.getBoolean("displayAltitude", true)) {
			
			trAltitudeTitle1.setVisibility(View.VISIBLE);
			trAltitudeTitle2.setVisibility(View.VISIBLE);
			trAltitudeTitle3.setVisibility(View.VISIBLE);
			trAltitude1.setVisibility(View.VISIBLE);
			trAltitude2.setVisibility(View.VISIBLE);
			trAltitude3.setVisibility(View.VISIBLE);
		} else {
			
			trAltitudeTitle1.setVisibility(View.GONE);
			trAltitudeTitle2.setVisibility(View.GONE);
			trAltitudeTitle3.setVisibility(View.GONE);
			trAltitude1.setVisibility(View.GONE);
			trAltitude2.setVisibility(View.GONE);
			trAltitude3.setVisibility(View.GONE);
		}
		
		int activityId = sharedPreferences.getInt("activity", 0);
		
		String[] activities = getView().getResources().getStringArray(R.array.activities_array);
	    
	    tvActivity.setText(activities[activityId]);
		
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		ScrollView svMain = (ScrollView) getView().findViewById(R.id.svMain);
		
		svMain.setVerticalScrollBarEnabled(false);
		svMain.setHorizontalScrollBarEnabled(false);
		
		
		trSpeedTitle1 = (TableRow) getView().findViewById(R.id.trSpeedTitle1);
		trSpeed1 = (TableRow) getView().findViewById(R.id.trSpeed1);
		trSpeedTitle2 = (TableRow) getView().findViewById(R.id.trSpeedTitle2);
		trSpeed2 = (TableRow) getView().findViewById(R.id.trSpeed2);
		trPaceTitle = (TableRow) getView().findViewById(R.id.trPaceTitle);
		trPace = (TableRow) getView().findViewById(R.id.trPace);
		trAltitudeTitle1 = (TableRow) getView().findViewById(R.id.trAltitudeTitle1);
		trAltitude1 = (TableRow) getView().findViewById(R.id.trAltitude1);
		trAltitudeTitle2 = (TableRow) getView().findViewById(R.id.trAltitudeTitle2);
		trAltitude2 = (TableRow) getView().findViewById(R.id.trAltitude2);
		trAltitudeTitle3 = (TableRow) getView().findViewById(R.id.trAltitudeTitle3);
		trAltitude3 = (TableRow) getView().findViewById(R.id.trAltitude3);
		
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