package com.valfom.tracker;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.R.color;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.Overlay;

public class TrackerMapInfoFragment extends SherlockFragment {
	
	private static MapView mapView;
	private static MapController mapController; 
		        
	public static final String TAG = "TrackerMapFragment";
		        
	public TrackerMapInfoFragment() {}
		        
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
		
		View vBtns = getLayoutInflater(savedInstanceState).inflate(R.layout.map_over_view_btns, null);

		vBtns.setBackgroundColor(color.abs__background_holo_dark);
		
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
					
					((TrackerInfoActivity) getActivity()).viewPager.setSwipingEnabled(!((TrackerInfoActivity) getActivity()).viewPager.isSwipingEnabled());
					
					btnLock.setText(((TrackerInfoActivity) getActivity()).viewPager.isSwipingEnabled() ? "Unlocked" : "Locked");
				}
            }
        );
		
		vBtns.setLayoutParams(new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT, 400, 0, LayoutParams.TOP_LEFT));
		
		mapView.addView(vBtns);
		
		final TrackerDB db = new TrackerDB(getActivity());
		db.getRoute(2);
		
		List<Overlay> overlays = mapView.getOverlays();
		
		overlays.add(new TrackerRouteOverlay());
		
		return mapView;
	}
}


