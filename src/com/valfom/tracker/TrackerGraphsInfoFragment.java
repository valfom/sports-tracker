package com.valfom.tracker;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;

public class TrackerGraphsInfoFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_graphs, null);
	}
	
	@SuppressLint("SetJavaScriptEnabled") @Override
	public void onActivityCreated(Bundle savedInstanceState) {

		TrackerDB db = new TrackerDB(getActivity());
		
		Intent intent = getActivity().getIntent();
        int trackId = intent.getIntExtra("trackId", 1);
        
        TrackerSettings settings = new TrackerSettings(getActivity());
		
		TrackerRoute route = db.getRoute(trackId);
		
		int num = route.getCount();
		
		final JSONArray speedData = new JSONArray();
		final JSONArray altitudeData = new JSONArray();

		for (int i = 0; i < num; i++) {
			
			JSONArray speedEntry = new JSONArray();
			JSONArray altitudeEntry = new JSONArray();

			speedEntry.put(i);
			
			try {
				speedEntry.put(settings.convertSpeed(route.getPoint(i).getSpeed()));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			speedData.put(speedEntry);
			
			altitudeEntry.put(i);
			
			altitudeEntry.put(route.getPoint(i).getAltitude());
			
			altitudeData.put(altitudeEntry);
		}

        final WebView wv = (WebView) getView().findViewById(R.id.wvGraphs);

        wv.setVerticalScrollBarEnabled(false);
        wv.setHorizontalScrollBarEnabled(false);
        
        wv.getSettings().setJavaScriptEnabled(true);

        wv.loadUrl("file:///android_asset/graphs/graphs.html");
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

        	public void run() {

        		wv.loadUrl("javascript:setGraphsData(" + speedData.toString() + "," + altitudeData.toString() + "," + speedData.length() + ")");
        	}
        }, 1000);
		
		super.onActivityCreated(savedInstanceState);
	}
}
