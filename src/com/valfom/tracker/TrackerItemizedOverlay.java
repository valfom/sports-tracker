package com.valfom.tracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class TrackerItemizedOverlay extends ItemizedOverlay<TrackerOverlayItem> {
	
	private ArrayList<TrackerOverlayItem> mOverlays = new ArrayList<TrackerOverlayItem>();
	private Context mContext;
	private boolean allowDeleteMarkers;
	    
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    	
    	shadow = false;
    	
    	super.draw(canvas, mapView, shadow);
    }
	
	public TrackerItemizedOverlay(Drawable defaultMarker, Context context, boolean allowDeleteMarkers) {
		
		super(boundCenterBottom(defaultMarker));
		
		mContext = context;
		this.allowDeleteMarkers = allowDeleteMarkers;
	}
	
	public void addOverlay(TrackerOverlayItem item) {
		
		mOverlays.add(item);
		populate();
	}

	@Override
	protected TrackerOverlayItem createItem(int i) {

		return mOverlays.get(i);
	}

	@Override
	public int size() {
		
		return mOverlays.size();
	}

	@Override
	public boolean onTap(final int i) {
		
		final TrackerOverlayItem item = mOverlays.get(i);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setMessage(item.getTitle());
    	
    	builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
    		
            public void onClick(DialogInterface dialog, int id) {
                
	            dialog.cancel();
            }
        });
    	
    	if (allowDeleteMarkers) {
    	
	    	builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
	    		
	            public void onClick(DialogInterface dialog, int id) {
	            	
	            	TrackerDB db = new TrackerDB(mContext);
	            	
	            	int markerId = item.getId();
	            	
	            	db.deleteMarker(markerId);
	            	
	            	mOverlays.remove(i);
	            	
	            	dialog.cancel();
	            }
	        });
    	}

    	AlertDialog dialog = builder.create();
    	
    	dialog.show();
		
		return true;
	}
}
