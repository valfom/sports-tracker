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
		
		mapController.setZoom(17);
		
		mapView.setClickable(true);
		mapView.setSatellite(false);
		
		View v = getLayoutInflater(savedInstanceState).inflate(R.layout.map_over_view, null);

		v.setBackgroundColor(color.abs__background_holo_dark);
		
		TextView tvDur = (TextView) v.findViewById(R.id.tvDurationMap);
		TextView tvDist = (TextView) v.findViewById(R.id.tvDistanceMap);
		
		tvDur.setTextColor(Color.WHITE);
		tvDist.setTextColor(Color.WHITE);
		
		final Button btnSatellite = (Button) v.findViewById(R.id.btnSatellite);
		
		btnSatellite.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					mapView.setSatellite(!mapView.isSatellite());
					
					btnSatellite.setText(mapView.isSatellite()? "Satellite" : "Map"); 
				}
            }
        );
		
		final Button btnLock = (Button) v.findViewById(R.id.btnLock);
		
		btnLock.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					((TrackerActivity) getActivity()).mViewPager.setSwipingEnabled(!((TrackerActivity) getActivity()).mViewPager.isSwipingEnabled());
					
					btnLock.setText(((TrackerActivity) getActivity()).mViewPager.isSwipingEnabled() ? "Unlocked" : "Locked");
				}
            }
        );
		
		v.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 0, 0, LayoutParams.TOP_LEFT));
		
		mapView.addView(v);
		
		List<Overlay> overlays = mapView.getOverlays();
		TrackerMyLocationOverlay myLocationOverlay = new TrackerMyLocationOverlay(getActivity(), mapView);
		
		overlays.add(myLocationOverlay);
		
		myLocationOverlay.enableMyLocation();
		
		return mapView;
	}
}

