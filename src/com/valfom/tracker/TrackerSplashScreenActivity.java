package com.valfom.tracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

public class TrackerSplashScreenActivity extends SherlockActivity {

	private static final int SPLASH_SCREEN_DISPLAY_TIMEOUT = 2000;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash_screen);

		Handler handler = new Handler();

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				Intent main = new Intent(TrackerSplashScreenActivity.this, TrackerMainActivity.class);
				startActivity(main);
				
				finish();

			}

		}, SPLASH_SCREEN_DISPLAY_TIMEOUT);
	}
}
