package com.valfom.sports.tracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TrackerSimpleCursorAdapter extends SimpleCursorAdapter {

	private Cursor cursor;
	private Context context;
	private int layout;
	
	public TrackerSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		
		super(context, layout, c, from, to, flags);
		
		this.layout = layout;
		this.context = context;
		this.cursor = c;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
    	
		if (convertView == null) {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(layout, null, true);
		}
		
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.tvDuration);
        TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
        TextView tvActivity = (TextView) convertView.findViewById(R.id.tvActivity);
        
        cursor.moveToPosition(position);
        	
	    int id = cursor.getInt(cursor.getColumnIndex(TrackerDB.KEY_PREFIX_ID));
	    double distance = cursor.getDouble(cursor.getColumnIndex(TrackerDB.KEY_DIST));
	    String date = cursor.getString(cursor.getColumnIndex(TrackerDB.KEY_DATE));
	    long duration = cursor.getLong(cursor.getColumnIndex(TrackerDB.KEY_DURATION));
	    int activityId = cursor.getInt(cursor.getColumnIndex(TrackerDB.KEY_ACTIVITY));
	    
	    TrackerSettings settings = new TrackerSettings(context);
	    
	    distance = settings.convertDistance(distance);
	    
	    long millis = duration;
		int seconds = (int) (millis / 1000);
		int minutes = seconds / 60;
		seconds     = seconds % 60;
		int hours = minutes / 60;
		minutes = minutes % 60;
	    
		tvId.setVisibility(View.GONE);
		
	    tvId.setText(String.valueOf(id));
	    tvDate.setText(date);
	    tvDistance.setText(String.format("%.2f", distance));
	    tvDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
	    
	    String[] activities = convertView.getResources().getStringArray(R.array.activities_array);
	    
	    tvActivity.setText(activities[activityId]);
	    
        return convertView;
    }
}
