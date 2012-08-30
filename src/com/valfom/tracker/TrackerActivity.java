package com.valfom.tracker;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrackerActivity extends Activity {
	
	private static boolean isPaused = false;
	long starttime = 0;
	TextView timeTV;
	Timer timer;
	long pausetime = 0;
	long millis;
    
	class secondTask extends TimerTask {

        @Override
        public void run() {
            TrackerActivity.this.runOnUiThread(new Runnable() {

                public void run() {
       
                	
                	if (!isPaused) {
            
	                	millis = System.currentTimeMillis() - starttime - pausetime;
	                	int seconds = (int) (millis / 1000);
	                	int minutes = seconds / 60;
	                	seconds     = seconds % 60;
	                	int hours = minutes / 60;
	                	minutes = minutes % 60;
	
	                	timeTV.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                	} else {
                		
                		pausetime = System.currentTimeMillis() - starttime - millis;
                	}
                }
            });
        }
   };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        timeTV = (TextView) findViewById(R.id.timeTV);
        
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
                
                starttime = System.currentTimeMillis();
                timer = new Timer();
                timer.schedule(new secondTask(), 0, 1000);
            }
        });
        
        stopBtn.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	
            	timer.cancel();
            	
            	timeTV.setText(R.string.time_default_value);
            	
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