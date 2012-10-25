package com.valfom.tracker;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.R.color;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.Overlay;

public class TrackerMapFragment extends SherlockFragment {
	
	private static MapView mapView;
	private static MapController mapController; 
		        
	public static final String TAG = "TrackerMapFragment";
		        
	public TrackerMapFragment() {}
		        
	@Override
	public void onCreate(Bundle savedInstanceState) {
		            
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	
		mapView = new MapView(getActivity(), "0gXrA3OG3rX_KPSAWRCG_dSHPmZnlnnmLRUssxg");
		mapController = mapView.getController();
		
		mapController.setZoom(15);
		
		mapView.setSatellite(false);
		
		View vBtns = getLayoutInflater(savedInstanceState).inflate(R.layout.map_over_view_btns, null);
		View vValues = getLayoutInflater(savedInstanceState).inflate(R.layout.map_over_view_values, null);

		vBtns.setBackgroundColor(color.abs__background_holo_dark);
		vValues.setBackgroundColor(color.abs__background_holo_dark);
		
		TextView tvDur = (TextView) vValues.findViewById(R.id.tvDurationMap);
		TextView tvDist = (TextView) vValues.findViewById(R.id.tvDistanceMap);
		
		tvDur.setTextColor(Color.WHITE);
		tvDist.setTextColor(Color.WHITE);
		
		final Button btnSatellite = (Button) vBtns.findViewById(R.id.btnSatellite);
		
		btnSatellite.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					mapView.setSatellite(!mapView.isSatellite());
					
					btnSatellite.setText(mapView.isSatellite()? "Satellite" : "Map"); 
				}
            }
        );
		
		final Button btnLock = (Button) vBtns.findViewById(R.id.btnLock);
		
		btnLock.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					((TrackerActivity) getActivity()).viewPager.setSwipingEnabled(!((TrackerActivity) getActivity()).viewPager.isSwipingEnabled());
					
					btnLock.setText(((TrackerActivity) getActivity()).viewPager.isSwipingEnabled() ? "Unlocked" : "Locked");
					
					mapView.setClickable(!mapView.isClickable());
				}
            }
        );
		
		vBtns.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 400, 0, LayoutParams.TOP_LEFT));
		
		mapView.addView(vBtns);
		
		vValues.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 0, 0, LayoutParams.TOP_LEFT));
		
		mapView.addView(vValues);
		
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
		
		return mapView;
	}
}

