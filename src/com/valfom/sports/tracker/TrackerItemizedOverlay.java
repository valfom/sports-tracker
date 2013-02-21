package com.valfom.sports.tracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class TrackerItemizedOverlay extends ItemizedOverlay<TrackerOverlayItem> {
	
	private ArrayList<TrackerOverlayItem> overlays = new ArrayList<TrackerOverlayItem>();
	private Context context;
	private boolean allowDeleteMarkers;
	    
	public TrackerItemizedOverlay(Drawable defaultMarker, Context context, boolean allowDeleteMarkers) {
		
		super(boundCenterBottom(defaultMarker));
		
		this.context = context;
		this.allowDeleteMarkers = allowDeleteMarkers;
	}
	
	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    	
    	shadow = false;
    	
    	super.draw(canvas, mapView, shadow);
    }
	
	public void addOverlay(TrackerOverlayItem item) {
		
		overlays.add(item);
		populate();
	}

	@Override
	protected TrackerOverlayItem createItem(int i) {

		return overlays.get(i);
	}

	@Override
	public int size() {
		
		return overlays.size();
	}

	@Override
	public boolean onTap(final int i) {
		
		final TrackerOverlayItem item = overlays.get(i);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setTitle(item.getTitle());
		builder.setMessage(item.getSnippet());
    	
    	builder.setNeutralButton(context.getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
    		
            public void onClick(DialogInterface dialog, int id) {
                
	            dialog.cancel();
            }
        });
    	
    	if (allowDeleteMarkers) {
    	
	    	builder.setNegativeButton(context.getString(R.string.dialog_btn_delete), new DialogInterface.OnClickListener() {
	    		
	            public void onClick(DialogInterface dialog, int id) {
	            	
	            	TrackerDB db = new TrackerDB(context);
	            	
	            	int markerId = item.getId();
	            	
	            	db.deleteMarker(markerId);
	            	
	            	db.close();
	            	
	            	overlays.remove(i);
	            	
	            	populate();
	            	
	            	dialog.cancel();
	            }
	        });
    	}

    	AlertDialog dialog = builder.create();
    	
    	dialog.show();
		
		return true;
	}
}
