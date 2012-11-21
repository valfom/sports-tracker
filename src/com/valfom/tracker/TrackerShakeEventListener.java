package com.valfom.tracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class TrackerShakeEventListener implements SensorEventListener {

	/** Minimum movement force to consider. */
	private static final int MIN_FORCE = 15;

	/**
	 * Minimum times in a shake gesture that the direction of movement needs to
	 * change.
	 */
	private static final int MIN_DIRECTION_CHANGE = 4;

	/** Maximum pause between movements. */
	private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 150;

	/** Maximum allowed time for shake gesture. */
	private static final int MAX_TOTAL_DURATION_OF_SHAKE = 450;

	/** Time when the gesture started. */
	private long mFirstDirectionChangeTime = 0;

	/** Time when the last movement started. */
	private long mLastDirectionChangeTime;

	/** How many movements are considered so far. */
	private int mDirectionChangeCount = 0;

	/** The last x position. */
	private float lastX = 0;

	/** The last y position. */
	private float lastY = 0;

	/** The last z position. */
	private float lastZ = 0;

	private OnShakeListener mShakeListener;

	public interface OnShakeListener {

		void onShake();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		
		mShakeListener = listener;
	}

	public void onSensorChanged(SensorEvent se) {
		
		// Get sensor data
		float x = se.values[0];
		float y = se.values[1];
		float z = se.values[2];

		// Calculate movement
		float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);
//		float totalMovement = Math.abs(x + y - lastX - lastY);

		if (totalMovement > MIN_FORCE) {

			// get time
			long now = System.currentTimeMillis();

			// store first movement time
			if (mFirstDirectionChangeTime == 0) {
				
				mFirstDirectionChangeTime = now;
				mLastDirectionChangeTime = now;
			}

			// check if the last movement was not long ago
			long lastChangeWasAgo = now - mLastDirectionChangeTime;
			
			if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {

				// store movement data
				mLastDirectionChangeTime = now;
				mDirectionChangeCount++;

				// store last sensor data
				lastX = x;
				lastY = y;
				lastZ = z;

				// check how many movements are so far
				if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {

					// check total duration
					long totalDuration = now - mFirstDirectionChangeTime;
					
					if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
						
						mShakeListener.onShake();
						resetShakeParameters();
					}
				}

			} else {
				
				resetShakeParameters();
			}
		}
	}

	private void resetShakeParameters() {
		
		mFirstDirectionChangeTime = 0;
		mDirectionChangeCount = 0;
		mLastDirectionChangeTime = 0;
		lastX = 0;
		lastY = 0;
		lastZ = 0;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
