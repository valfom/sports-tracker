package com.valfom.tracker;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class TrackerButtonsActivity extends SherlockActivity implements OnClickListener {

	private SensorManager mSensorManager;
	private TrackerShakeEventListener mShakeEventListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Intent intent = getIntent();
		
		int tabId = intent.getIntExtra("tabId", 1);
		boolean started = intent.getBooleanExtra("started", false);
		
		if (tabId == 0) {
			
			setContentView(R.layout.dialog_map_buttons);
			
			Button btnMap = (Button) findViewById(R.id.ivMapDialog);
			
			btnMap.setOnClickListener(this);
			
			Button btnLock = (Button) findViewById(R.id.ivLockDialog);
			
			btnLock.setOnClickListener(this);
			
			Button btnLocation = (Button) findViewById(R.id.ivMyLocationDialog);
			
			btnLocation.setOnClickListener(this);
			
			if (started) {
			
				Button btnAddMarker = (Button) findViewById(R.id.ivAddMarkerDialog);
			
				btnAddMarker.setOnClickListener(this);
				btnAddMarker.setVisibility(View.VISIBLE);
			}
		
		} else if (tabId == 1) {
			
			setContentView(R.layout.dialog_main_buttons);
			
			Button btnStart = (Button) findViewById(R.id.ivStartDialog);
			
			btnStart.setOnClickListener(this);
			
			if (started) {

				btnStart.setVisibility(View.GONE);
				
				Button btnStop = (Button) findViewById(R.id.ivStopDialog);
				
				btnStop.setOnClickListener(this);
				btnStop.setVisibility(View.VISIBLE);
			
				Button btnPause = (Button) findViewById(R.id.ivPauseDialog);
			
				btnPause.setOnClickListener(this);
				btnPause.setVisibility(View.VISIBLE);
			}
			
		} else if (tabId == 3) {
			
			setContentView(R.layout.dialog_info_buttons);
			
			Button btnSave = (Button) findViewById(R.id.ivSaveDialog);
			
			btnSave.setOnClickListener(this);
			
			Button btnDelete = (Button) findViewById(R.id.ivDeleteDialog);
				
			btnDelete.setOnClickListener(this);
		}
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mShakeEventListener = new TrackerShakeEventListener();
		
		mShakeEventListener.setOnShakeListener(new TrackerShakeEventListener.OnShakeListener() {

			public void onShake() {
				
				finish();
			}
		});
	}
	
	@Override
	public void onClick(View v) {

		Intent result = new Intent();
		
		switch(v.getId()) {
		
			case R.id.ivMapDialog:
				result.putExtra("btnId", R.id.ivMapDialog);
				break;
				
			case R.id.ivLockDialog:
				result.putExtra("btnId", R.id.ivLockDialog);
				break;
				
			case R.id.ivMyLocationDialog:
				result.putExtra("btnId", R.id.ivMyLocationDialog);
				break;
				
			case R.id.ivAddMarkerDialog:
				result.putExtra("btnId", R.id.ivAddMarkerDialog);
				break;
				
			case R.id.ivStartDialog:
				result.putExtra("btnId", R.id.ivStartDialog);
				break;
				
			case R.id.ivStopDialog:
				result.putExtra("btnId", R.id.ivStopDialog);
				break;
			
			case R.id.ivPauseDialog:
				result.putExtra("btnId", R.id.ivPauseDialog);
				break;
				
			case R.id.ivSaveDialog:
				result.putExtra("btnId", R.id.ivSaveDialog);
				break;
			
			case R.id.ivDeleteDialog:
				result.putExtra("btnId", R.id.ivDeleteDialog);
				break;
				
			default:
				break;
		}
		
		setResult(RESULT_OK, result);
		finish();
	}
	
	@Override
	protected void onPause() {
		
		mSensorManager.unregisterListener(mShakeEventListener);
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		
		mSensorManager.registerListener(mShakeEventListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
	}
}
