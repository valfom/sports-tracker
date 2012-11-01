package com.valfom.tracker;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.Overlay;

public class TrackerMapFragment extends SherlockFragment {
	
	public static MapView mapView;
		        
	public TrackerMapFragment() {}
		        
	@Override
	public void onCreate(Bundle savedInstanceState) {
		            
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	
		mapView = new MapView(getActivity(), "0gXrA3OG3rX_KPSAWRCG_dSHPmZnlnnmLRUssxg");
		final MapController mapController = mapView.getController();
		
		mapController.setZoom(15);
		
		mapView.setSatellite(false);
		
		List<Overlay> overlays = mapView.getOverlays();
		final TrackerMyLocationOverlay myLocationOverlay = new TrackerMyLocationOverlay(getActivity(), mapView);
		
		overlays.add(myLocationOverlay);
		
		myLocationOverlay.enableMyLocation();
		
		myLocationOverlay.runOnFirstFix(new Runnable() {
			
			public void run() {
				
				GeoPoint geoPoint = myLocationOverlay.getMyLocation();
				mapController.animateTo(geoPoint);
			}
		});
		
		View vValues = getLayoutInflater(savedInstanceState).inflate(R.layout.map_over_view_values, null);
		View vBtnMap = getLayoutInflater(savedInstanceState).inflate(R.layout.map_btn_map, null);
		View vBtnLock = getLayoutInflater(savedInstanceState).inflate(R.layout.map_btn_lock, null);
		View vBtnMyLocation = getLayoutInflater(savedInstanceState).inflate(R.layout.map_btn_my_location, null);

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
					
					((TrackerMainActivity) getActivity()).viewPager.setSwipingEnabled(!((TrackerMainActivity) getActivity()).viewPager.isSwipingEnabled());
					
					btnLock.setImageResource(((TrackerMainActivity) getActivity()).viewPager.isSwipingEnabled() ? R.drawable.ic_map_locked : R.drawable.ic_map_unlocked);
					
					mapView.setClickable(!mapView.isClickable());
				}
            }
        );
		
		final ImageButton btnMyLocation = (ImageButton) vBtnMyLocation.findViewById(R.id.btnMyLocation);
		
		btnMyLocation.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					GeoPoint geoPoint = myLocationOverlay.getMyLocation();
					
					if (geoPoint != null) mapController.animateTo(geoPoint);
					else Toast.makeText(getActivity(), "Unable to find your location", Toast.LENGTH_SHORT).show();
				}
            }
        );
		
		vBtnMap.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 10, 150, LayoutParams.TOP_LEFT));
		
		mapView.addView(vBtnMap);
		
		vBtnLock.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 10, 250, LayoutParams.TOP_LEFT));
		
		mapView.addView(vBtnLock);
		
		vBtnMyLocation.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 10, 350, LayoutParams.TOP_LEFT));
		
		mapView.addView(vBtnMyLocation);
		
		vValues.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 10, 10, LayoutParams.TOP_LEFT));
		
		mapView.addView(vValues);
		
		return mapView;
	}
}