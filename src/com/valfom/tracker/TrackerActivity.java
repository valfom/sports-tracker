package com.valfom.tracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TrackerActivity extends Activity {
	
	private static boolean isPaused = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button startBtn = (Button) findViewById(R.id.startBtn);
        final Button stopBtn = (Button) findViewById(R.id.stopBtn);
        final Button pauseBtn = (Button) findViewById(R.id.pauseBtn);
        
        stopBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.GONE);
        
        startBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	startBtn.setVisibility(View.GONE);
            	stopBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.VISIBLE);
            }
        });
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	startBtn.setVisibility(View.VISIBLE);
            	stopBtn.setVisibility(View.GONE);
                pauseBtn.setVisibility(View.GONE);
            }
        });
        
        pauseBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	if (isPaused)
            		pauseBtn.setText(R.string.pause_btn);
            	else
            		pauseBtn.setText(R.string.start_btn);
            	
            	isPaused = !isPaused;
            }
        });
    }
}