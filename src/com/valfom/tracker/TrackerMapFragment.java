package com.valfom.tracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
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
		
		mapView.setClickable(true);
		mapView.setSatellite(true);
		
		List<Overlay> overlays = mapView.getOverlays();
		TrackerLocationOverlay myLocationOverlay = new TrackerLocationOverlay(getActivity(), mapView);
		overlays.add(myLocationOverlay);
		
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();
		
		return mapView;
	}
}

