package com.valfom.tracker;

import android.app.ActionBar;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class TrackerActionBar {
	
	private FragmentActivity activity;
	private static ActionBar actionBar;

	public TrackerActionBar(FragmentActivity a) {
		
		super();
		
		activity = a;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			
			actionBar = activity.getActionBar();
		}
	}
	
	public void setPage(String page) {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			
			if ((page.compareTo("Tracker") == 0) || (page.compareTo("List") == 0)) {
				
				actionBar.setDisplayShowTitleEnabled(false);
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				
				SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(actionBar.getThemedContext(), R.array.dropdown_items,
				        android.R.layout.simple_spinner_dropdown_item);
				actionBar.setListNavigationCallbacks(spinnerAdapter, new ActionBar.OnNavigationListener() {
					
					public boolean onNavigationItemSelected(int itemPosition, long itemId) {
						
						FragmentManager fm = activity.getSupportFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();
						
						TrackerInfoFragment frInfo = (TrackerInfoFragment) fm.findFragmentById(R.id.fragment_container_info);
						if (frInfo != null)
							ft.remove(frInfo);
						
						switch (itemPosition) {
						
						case 0:
							ft.replace(R.id.fragment_container, new TrackerMainFragment(), "Main");
							ft.commit();
							
							return true;
						case 1:
							ft.replace(R.id.fragment_container, new TrackerListFragment(), "List");
							ft.commit();
							
							return true;
						default:
							return false;	
						}
					}
				});
				
				int position = (page.compareTo("Tracker") == 0)? 0 : 1;
				
				actionBar.setSelectedNavigationItem(position);
				
			} else if (page.compareTo("Info") == 0) {
				
				actionBar.setDisplayShowTitleEnabled(true);
				actionBar.setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
				actionBar.setTitle("Info");
			}
		}
	}
}
