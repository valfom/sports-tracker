package com.valfom.tracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class TrackerGraphsInfoFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_graphs, container, false);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {

		TrackerDB db = new TrackerDB(getActivity());
		
		Intent intent = getActivity().getIntent();
        int trackId = intent.getIntExtra("trackId", 1);
		
		TrackerRoute route = db.getRouteObj(trackId);
		
		int num = route.getCount();
		
		GraphViewData[] data = new GraphViewData[num];
		
		for (int i = 0; i < num; i++) {
			
			data[i] = new GraphViewData(i, route.getPoint(i).getSpeed());
		}
		
		GraphView graphView = new LineGraphView(getActivity(), "Speed");
		
		graphView.addSeries(new GraphViewSeries(data));
		
		graphView.setViewPort(2, 40);
//		graphView.setScrollable(true);
		graphView.setScalable(true);
		
		((LineGraphView) graphView).setBackgroundColor(Color.BLACK);
		
		LinearLayout layout = (LinearLayout) getView().findViewById(R.id.graphSpeed);
		layout.addView(graphView);
		
		data = new GraphViewData[num];
		
		for (int i = 0; i < num; i++) {
			
			data[i] = new GraphViewData(i, route.getPoint(i).getAltitude());
		}
		
		graphView = new LineGraphView(getActivity(), "Altitude");
		
		((LineGraphView) graphView).setDrawBackground(true);
		
//		((LineGraphView) graphView).setManualYAxisBounds(max, min);
		((LineGraphView) graphView).setBackgroundColor(Color.BLACK);
		
		graphView.addSeries(new GraphViewSeries(data));
		
		graphView.setViewPort(2, 10);
		graphView.setScalable(true);
		
		layout = (LinearLayout) getView().findViewById(R.id.graphAltitude);
		layout.addView(graphView);
		
		super.onActivityCreated(arg0);
	}
}
