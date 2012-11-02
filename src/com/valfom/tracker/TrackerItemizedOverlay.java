package com.valfom.tracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class TrackerItemizedOverlay extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public TrackerItemizedOverlay(Drawable defaultMarker) {
		
		super(boundCenterBottom(defaultMarker));
	}
	
	public TrackerItemizedOverlay(Drawable defaultMarker, Context context) {
		
		super(boundCenterBottom(defaultMarker));
		
		mContext = context;
	}
	
	public void addOverlay(OverlayItem item) {
		
		mOverlays.add(item);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {

		return mOverlays.get(i);
	}

	@Override
	public int size() {
		
		return mOverlays.size();
	}

	@Override
	public boolean onTap(int i) {
		
		OverlayItem item = mOverlays.get(i);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

    	builder.setMessage(item.getSnippet()).setTitle(item.getTitle());
    	
    	builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
    		
            public void onClick(DialogInterface dialog, int id) {
                
	            dialog.cancel();
            }
        });
    	builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
    		
            public void onClick(DialogInterface dialog, int id) {
                
            	dialog.cancel();
            }
        });

    	AlertDialog dialog = builder.create();
    	
    	dialog.show();
		
		return true;
	}

	
}
