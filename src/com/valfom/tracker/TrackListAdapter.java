package com.valfom.tracker;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class TrackListAdapter extends BaseAdapter {
 
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
 
    public TrackListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
    	
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
    	
        return data.size();
    }
 
    public Object getItem(int position) {
    	
        return position;
    }
 
    public long getItemId(int position) {
    	
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        View vi = convertView;
        
        if(convertView == null)
            vi = inflater.inflate(R.layout.list_row, null);
 
        TextView dateTV = (TextView) vi.findViewById(R.id.dateTV);
        TextView distanceTV = (TextView) vi.findViewById(R.id.distanceTV);
        TextView timeTV = (TextView) vi.findViewById(R.id.timeTV);
        TextView idTV = (TextView) vi.findViewById(R.id.idTV);
        
        idTV.setVisibility(View.GONE);
 
        HashMap<String, String> track = new HashMap<String, String>();
        track = data.get(position);
 
        idTV.setText(track.get(TrackerListFragment.KEY_ID));
        dateTV.setText(track.get(TrackerListFragment.KEY_DATE));
        distanceTV.setText(track.get(TrackerListFragment.KEY_DISTANCE));
        timeTV.setText(track.get(TrackerListFragment.KEY_TIME));
        
        return vi;
    }
}
