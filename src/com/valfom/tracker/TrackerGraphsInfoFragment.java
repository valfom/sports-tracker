package com.valfom.tracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.jjoe64.graphview.BarGraphView;
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
		
		TrackerRoute route = db.getRoute(trackId);
		
		int num = route.getCount();
		
		GraphViewData[] data = new GraphViewData[num];
		
		for (int i = 0; i < num; i++) {
			
			data[i] = new GraphViewData(i, route.getPoint(i).getSpeed());
		}
		
		GraphView graphView = new LineGraphView(getActivity(), "Speed");
		
		GraphViewSeries seriesSpeed = new GraphViewSeries(data);
		
		graphView.addSeries(seriesSpeed);
		
//		graphView.setViewPort(0, 100);
//		graphView.setScrollable(true);
		graphView.setScalable(true);
		
		((LineGraphView) graphView).setBackgroundColor(Color.TRANSPARENT);
		
		LinearLayout layout = (LinearLayout) getView().findViewById(R.id.graphSpeed);
		layout.addView(graphView);
		
		data = new GraphViewData[num];
		
		for (int i = 0; i < num; i++) {
			
			data[i] = new GraphViewData(i, route.getPoint(i).getAltitude());
		}
		
		graphView = new BarGraphView(getActivity(), "Altitude");
		
//		((BarGraphView) graphView).setDrawBackground(true);
		
		((BarGraphView) graphView).setBackgroundColor(Color.TRANSPARENT);
		
		graphView.addSeries(new GraphViewSeries(data));
		
		graphView.setViewPort(0, 70);
		graphView.setScrollable(true);
//		graphView.setScalable(true);
		
		layout = (LinearLayout) getView().findViewById(R.id.graphAltitude);
		layout.addView(graphView);
		
		super.onActivityCreated(arg0);
	}
}
