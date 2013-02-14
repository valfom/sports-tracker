package com.valfom.sports.tracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class TrackerListFragment extends SherlockListFragment {

	@Override
	public void onResume() {

		super.onResume();
		
		updateList();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		getListView().setEmptyView(getActivity().findViewById(R.id.empty));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			
			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

			    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}
		
			    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		
			    	switch (item.getItemId()) {
			    	
			            case R.id.menu_row_delete:
			            	
			            	TrackerDB db = new TrackerDB(getActivity());
			            	
			            	SparseBooleanArray checked = getListView().getCheckedItemPositions();
			            	
			                for (int i = 0; i < checked.size(); i++) {
			                 
			                	if(checked.valueAt(i)) {
			                    
			                		Cursor cursor = (Cursor) getListView().getItemAtPosition(checked.keyAt(i));
			                		db.deleteTrack(cursor.getInt(0));
			                    }
			                }
			                
			                db.close();
			                
			                updateList();
			                
			                mode.finish();
			                
			                return true;
			            default:
			                return false;
			        }
			    }
		
			    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		
			    	MenuInflater inflater = mode.getMenuInflater();
			        inflater.inflate(R.menu.menu_list_row_selection, menu);
			        
			        return true;
			    }
		
			    public void onDestroyActionMode(ActionMode mode) {}
		
			    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			        
			    	return false;
			    }
			});
		}
	}
	
	public void updateList() {
		
		TrackerDB db = new TrackerDB(getActivity());
		
		Cursor cursor = db.getAllTracks();
		
	    String[] from = new String[] { TrackerDB.KEY_PREFIX_ID, TrackerDB.KEY_DATE, 
	    		TrackerDB.KEY_DIST, TrackerDB.KEY_DURATION, TrackerDB.KEY_ACTIVITY };
	    int[] to = new int[] { R.id.tvId, R.id.tvDate, R.id.tvDistance, R.id.tvDuration, R.id.tvActivity };
	    
	    // TODO: Подумать насчет флагов, CursorLoader'a и т.д.
	    
	    TrackerSimpleCursorAdapter scAdapter = new TrackerSimpleCursorAdapter(getActivity(), R.layout.list_row, cursor, from, to, 0);
		
	    setListAdapter(scAdapter);
	    
	    db.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_list, null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		
		TextView idTV = (TextView) v.findViewById(R.id.tvId);
		
		int trackId = Integer.parseInt(idTV.getText().toString());
		
		Intent info = new Intent(getActivity(), TrackerInfoActivity.class);
		info.putExtra("trackId", trackId);
		startActivity(info);
	}
}