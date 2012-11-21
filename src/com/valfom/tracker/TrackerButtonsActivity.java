package com.valfom.tracker;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class TrackerButtonsActivity extends SherlockActivity {

	private SensorManager mSensorManager;
	private TrackerShakeEventListener mShakeEventListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Intent intent = getIntent();
		
		int tabId = intent.getIntExtra("tabId", 1);
		
		int layout;
		
		if (tabId == 0) layout = R.layout.dialog_map_buttons;
		else layout = R.layout.dialog_map_buttons;
		
		setContentView(layout);
		
		Button btnMap = (Button) findViewById(R.id.ivMapDialog);
		
		btnMap.setOnClickListener(new View.OnClickListener() {
		        	
			public void onClick(View v) {
				
				Intent result = new Intent();
				result.putExtra("btnId", R.id.ivMapDialog);
				setResult(RESULT_OK, result);
				finish();
			}
		});
		
		Button btnLock = (Button) findViewById(R.id.ivLockDialog);
		
		btnLock.setOnClickListener(new View.OnClickListener() {
		        	
			public void onClick(View v) {
				
				Intent result = new Intent();
				result.putExtra("btnId", R.id.ivLockDialog);
				setResult(RESULT_OK, result);
				finish();
			}
		});
		
		Button btnLocation = (Button) findViewById(R.id.ivLocationDialog);
		
		btnLocation.setOnClickListener(new View.OnClickListener() {
		        	
			public void onClick(View v) {
				
				Intent result = new Intent();
				result.putExtra("btnId", R.id.ivLocationDialog);
				setResult(RESULT_OK, result);
				finish();
			}
		});
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mShakeEventListener = new TrackerShakeEventListener();
		
		mShakeEventListener.setOnShakeListener(new TrackerShakeEventListener.OnShakeListener() {

			public void onShake() {
				
				finish();
			}
		});
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
