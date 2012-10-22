package com.valfom.tracker;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class TrackerViewPager extends ViewPager {

	private boolean swipingEnabled = true;
	
    public boolean isSwipingEnabled() {
    	
		return swipingEnabled;
	}

	public void setSwipingEnabled(boolean swipingEnabled) {
		
		this.swipingEnabled = swipingEnabled;
	}

	public TrackerViewPager(Context context) {
        
    	super(context);
    }

    public TrackerViewPager(Context context, AttributeSet attrs) {
        
    	super(context, attrs);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
    	if (this.swipingEnabled) return super.onTouchEvent(event);
        
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	
    	if (this.swipingEnabled) return super.onInterceptTouchEvent(event);
    	
    	return false;
    }
}
