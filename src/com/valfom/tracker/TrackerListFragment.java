package com.valfom.tracker;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.SparseBooleanArray;
//import android.view.ActionMode;
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

public class TrackerListFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		loadTracks();
		
		getListView().setEmptyView(getActivity().findViewById(R.id.empty));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			
			getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {
	
			    public void onItemCheckedStateChanged(ActionMode mode, int position,
			                                      long id, boolean checked) {}
		
			    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		
			    	switch (item.getItemId()) {
			            case R.id.menu_row_delete:
			            	
			            	DB db = new DB(getActivity());
			            	
			            	SparseBooleanArray checked = getListView().getCheckedItemPositions();
			            	
			                for (int i = 0; i < checked.size(); i++) {
			                 
			                	if(checked.valueAt(i) == true) {
			                    
			                		Cursor c = (Cursor) getListView().getItemAtPosition(checked.keyAt(i));
			                		db.deleteTrack(c.getInt(0));
			                    }
			                }
			                
			                loadTracks();
			                
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
	
	@SuppressWarnings("deprecation")
	private void loadTracks() {
		
		DB db;
		SimpleCursorAdapter scAdapter;
		Cursor cursor;
		
		db = new DB(getActivity());
		
		cursor = db.getAllTracks();
	    
	    String[] from = new String[] { DB.KEY_SC_ID, DB.KEY_DATE, 
	    		DB.KEY_DIST, DB.KEY_TIME };
	    int[] to = new int[] { R.id.idTV, R.id.dateTV, R.id.distanceTV, R.id.timeTV };
	    
	    scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_row, cursor, from, to);
		
		setListAdapter(scAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_list, null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		
		TextView idTV = (TextView) v.findViewById(R.id.idTV);
		int trackId = Integer.parseInt(idTV.getText().toString());

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		TrackerListFragment listFragment = (TrackerListFragment) fragmentManager.findFragmentById(R.id.fragment_container);
		fragmentTransaction.remove(listFragment);

		TrackerInfoFragment infoFragment = new TrackerInfoFragment();

		Bundle args = new Bundle();
		args.putInt("id", trackId);
		infoFragment.setArguments(args);

		fragmentTransaction.add(R.id.fragment_container_info, infoFragment, "Info");

		fragmentTransaction.addToBackStack(null);

		fragmentTransaction.commit();
	}
}
