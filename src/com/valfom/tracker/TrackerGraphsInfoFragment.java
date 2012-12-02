package com.valfom.tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XLayoutStyle;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YLayoutStyle;

public class TrackerGraphsInfoFragment extends SherlockFragment {

	private XYPlot plot;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
		return inflater.inflate(R.layout.fragment_graphs, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		TrackerDB db = new TrackerDB(getActivity());
		
		Intent intent = getActivity().getIntent();
        int trackId = intent.getIntExtra("trackId", 1);
        
        TrackerSettings settings = new TrackerSettings(getActivity());
		
		TrackerRoute route = db.getRoute(trackId);
		
		int num = route.getCount();
		
        plot = (XYPlot) getView().findViewById(R.id.mySimpleXYPlot);
        
        List<Integer> data = new ArrayList<Integer>();
        
        for (int i = 0; i < num; i++)
        	data.add((int) settings.convertSpeed(route.getPoint(i).getSpeed()));
       
        XYSeries mySeries = new SimpleXYSeries(
               data /*Arrays.asList(new Integer[] { 0, 25, 55, 2, 80, 30, 99, 0, 44, 6 })*/,
               SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Speed");
        
        plot.addSeries(mySeries, new LineAndPointFormatter(Color.rgb(255, 0, 0), // line color
                											Color.rgb(255, 0, 0), // point color
                											null)); // fill color		
 
        // reposition the domain label to look a little cleaner:
        plot.position(plot.getDomainLabelWidget(), // the widget to position
               0,                                // x position value, in this case 45 pixels
               XLayoutStyle.ABSOLUTE_FROM_LEFT,   // how the x position value is applied, in this case from the left
               0,                                 // y position value
               YLayoutStyle.ABSOLUTE_FROM_BOTTOM, // how the y position is applied, in this case from the bottom
               AnchorPosition.LEFT_BOTTOM);       // point to use as the origin of the widget being positioned
 
//        plot.centerOnRangeOrigin(60);
//        plot.centerOnDomainOrigin(5);
        
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        
        plot.setBackgroundPaint(paint);
        plot.getGraphWidget().setBackgroundPaint(paint);
        plot.getGraphWidget().setGridBackgroundPaint(paint);
        
        plot.setDomainLabel("");
        plot.setRangeLabel("Speed (" + settings.getSpeedUnit() + ")");
        plot.getGraphWidget().setRangeLabelWidth(30);
 
        // Increment X-Axis by 1 value
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
 
        // Reduce the number of range labels
        plot.setTicksPerRangeLabel(1);
 
        // Reduce the number of domain labels
        plot.setTicksPerDomainLabel(1);
 
        // Remove all the developer guides from the chart
        plot.disableAllMarkup();
		
		super.onActivityCreated(savedInstanceState);
	}
	
	
}
