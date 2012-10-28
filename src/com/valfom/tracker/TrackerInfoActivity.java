package com.valfom.tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class TrackerInfoActivity extends SherlockFragmentActivity implements ActionBar.TabListener {
	
	TrackerInfoSectionsPagerAdapter sectionsPagerAdapter;
	TrackerViewPager viewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		sectionsPagerAdapter = new TrackerInfoSectionsPagerAdapter(getSupportFragmentManager());

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager = (TrackerViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            
        	@Override
            public void onPageSelected(int position) {
                
        		actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            
        	actionBar.addTab(actionBar.newTab()
        			.setText(sectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (viewPager.getCurrentItem() < viewPager.getChildCount())
				viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (viewPager.getCurrentItem() > 0)
				viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
			return true;
		default:
			return super.onKeyUp(keyCode, event);
		}
	}
	
	public class TrackerInfoSectionsPagerAdapter extends FragmentPagerAdapter {

	    public TrackerInfoSectionsPagerAdapter(FragmentManager fm) {
	    	
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int i) {
	    	
			switch (i) {
			
			case 0:
				return new TrackerInfoFragment();
			case 1:
				return new TrackerMapInfoFragment();
			case 2:
				return new TrackerGraphsInfoFragment();
			default:
				return new TrackerInfoFragment();	
			}
	    }

	    @Override
	    public int getCount() {
	    	
	        return 3;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	    	
	        switch (position) {
	        
	            case 0: return getString(R.string.tab_info).toUpperCase();
	            case 1: return getString(R.string.tab_route).toUpperCase();
	            case 2: return getString(R.string.tab_graphs).toUpperCase();
	        }
	        
	        return null;
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.menu_info, menu);
		
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
        
	        case android.R.id.home:
	            
	        	onBackPressed();
	            
	            return true;
	            
	        case R.id.menu_settings:
	        	Intent settingsActivity = new Intent(TrackerInfoActivity.this,
	    				TrackerPreferenceActivity.class);
	    		startActivity(settingsActivity);
	    		
	    		return true;
	    		
	        case R.id.menu_delete:
	        	
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	        	builder.setMessage("Delete this track?")
	        	       .setTitle("Deletion");
	        	
	        	final TrackerDB db = new TrackerDB(this);
	        	
	        	builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    
	                	Intent intent = getIntent();
	    	            final int trackId = intent.getIntExtra("trackId", 1);
	    	            
	    	            db.deleteTrack(trackId);
	    	            db.close();
	    	            
	    	            dialog.cancel();
	    	            
	    	            onBackPressed();
	                }
	            });
	        	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    
	                	dialog.cancel();
	                }
	            });

	        	AlertDialog dialog = builder.create();
	        	
	        	dialog.show();
	        	
	        	return true;
	        	
	        default:
	        	return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
}