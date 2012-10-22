package com.valfom.tracker;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
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
		
		List<Overlay> overlays = mapView.getOverlays();
		TrackerMyLocationOverlay myLocationOverlay = new TrackerMyLocationOverlay(getActivity(), mapView);
		
		overlays.add(myLocationOverlay);
		
		myLocationOverlay.enableMyLocation();
		
		return mapView;
	}
}

