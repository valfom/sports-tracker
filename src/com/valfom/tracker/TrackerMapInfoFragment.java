package com.valfom.tracker;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.Overlay;

public class TrackerMapInfoFragment extends SherlockFragment {
	
	private static MapView mapView;
		        
	public TrackerMapInfoFragment() {}
		        
	@Override
	public void onCreate(Bundle savedInstanceState) {
		            
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		mapView = new MapView(getActivity(), "0gXrA3OG3rX_KPSAWRCG_dSHPmZnlnnmLRUssxg");
		MapController mapController = mapView.getController();
		
		mapController.setZoom(15);
		
		mapView.setSatellite(false);
		
		View vBtnMap = getLayoutInflater(savedInstanceState).inflate(R.layout.map_btn_map, null);
		View vBtnLock = getLayoutInflater(savedInstanceState).inflate(R.layout.map_btn_lock, null);

		final ImageButton btnMap = (ImageButton) vBtnMap.findViewById(R.id.btnMap);
		
		btnMap.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					mapView.setSatellite(!mapView.isSatellite());
				}
            }
        );
		
		final ImageButton btnLock = (ImageButton) vBtnLock.findViewById(R.id.btnLock);
		
		btnLock.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					((TrackerInfoActivity) getActivity()).viewPager.setSwipingEnabled(!((TrackerInfoActivity) getActivity()).viewPager.isSwipingEnabled());
					
					btnLock.setImageResource(((TrackerInfoActivity) getActivity()).viewPager.isSwipingEnabled() ? R.drawable.ic_map_locked : R.drawable.ic_map_unlocked);
					
					mapView.setClickable(!mapView.isClickable());
				}
            }
        );
		
		vBtnMap.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 10, 150, LayoutParams.TOP_LEFT));
		
		mapView.addView(vBtnMap);
		
		vBtnLock.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 10, 250, LayoutParams.TOP_LEFT));
		
		mapView.addView(vBtnLock);
		
		Intent intent = getActivity().getIntent();
        final int trackId = intent.getIntExtra("trackId", 1);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		
		mapOverlays.add(new TrackerRouteOverlay(trackId, TrackerRouteOverlay.FLAGS_MODE_START_FINISH, true));
		
		TrackerDB db = new TrackerDB(getActivity());
		
		Cursor cursor = db.getAllMarkers(trackId);
		
		if (cursor.getCount() > 0) {
			
			Drawable marker = getResources().getDrawable(R.drawable.ic_marker);
			
			TrackerItemizedOverlay itemizedOverlay = new TrackerItemizedOverlay(marker, mapView.getContext(), false);
			
			for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
			    
				GeoPoint geoPoint = new GeoPoint(cursor.getInt(1), cursor.getInt(2));
				
				TrackerOverlayItem overlayItem = new TrackerOverlayItem(cursor.getInt(0), geoPoint, cursor.getString(3), cursor.getString(4));
				
				itemizedOverlay.addOverlay(overlayItem);
			}
			
			mapOverlays.add(itemizedOverlay);
		}
		
		return mapView;
	}
}