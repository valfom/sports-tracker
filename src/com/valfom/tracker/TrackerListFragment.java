package com.valfom.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TrackerListFragment extends ListFragment {
	
	static final String KEY_TRACK = "track";
    static final String KEY_ID = "id";
    static final String KEY_DATE = "date";
    static final String KEY_DISTANCE = "distance";
    static final String KEY_TIME = "time";
    
    ListView list;
//    TrackListAdapter adapter;
    
    private View selectedRowView;
    
    protected Object mActionMode;
    public int selectedItem = -1;

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		ArrayList<HashMap<String, String>> tracksList = new ArrayList<HashMap<String, String>>();
	      
//      DatabaseHandler db = new DatabaseHandler(this);
      
      List<Track> allTracks = TrackerActivity.db.getAllTracks();
      
      for (int i = 0; i < allTracks.size(); i++) {

      	HashMap<String, String> map = new HashMap<String, String>();
      	
      	map.put(KEY_ID, String.valueOf(allTracks.get(i).getId()));
          map.put(KEY_DATE, allTracks.get(i).getDate());
          map.put(KEY_DISTANCE, String.valueOf(allTracks.get(i).getDistance() / 1000));
          map.put(KEY_TIME, String.valueOf(allTracks.get(i).getTime() / 1000 / 60 / 60));
          
          tracksList.add(map);
      }
      
      ListView listView = (ListView) getView().findViewById(R.id.list);

      listView.setAdapter(TrackerActivity.adapter);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
    	return inflater.inflate(R.layout.fragment_list, null);
    }
  
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment trackerListFragment = fragmentManager.findFragmentById(R.id.container);
		fragmentTransaction.remove(trackerListFragment);
		
		TextView idTV = (TextView)v.findViewById(R.id.idTV);
		int trackId = Integer.parseInt(idTV.getText().toString());
		
		Fragment trackInfoFragment = new TrackInfoFragment();
		
		Bundle args = new Bundle();
		
//		Log.d("DIST", String.valueOf(trackId));
        args.putInt("id", trackId);
        trackInfoFragment.setArguments(args);
        
        fragmentTransaction.add(R.id.container_track_info, trackInfoFragment);
        
        String tag = null;
        fragmentTransaction.addToBackStack(tag);
		
		fragmentTransaction.commit();
	}
  
  @Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		
	    /*ArrayList<HashMap<String, String>> tracksList = new ArrayList<HashMap<String, String>>();
      
//      DatabaseHandler db = new DatabaseHandler(this);
      
      List<Track> allTracks = TrackerActivity.db.getAllTracks();
      
      for (int i = 0; i < allTracks.size(); i++) {

      	HashMap<String, String> map = new HashMap<String, String>();
      	
      	map.put(KEY_ID, String.valueOf(allTracks.get(i).getId()));
          map.put(KEY_DATE, allTracks.get(i).getDate());
          map.put(KEY_DISTANCE, String.valueOf(allTracks.get(i).getDistance() / 1000));
          map.put(KEY_TIME, String.valueOf(allTracks.get(i).getTime() / 1000 / 60 / 60));
          
          tracksList.add(map);
      }
      
//      adapter = new TrackListAdapter(TrackerActivity.this, tracksList);
      
      ListView listView = (ListView) getView().findViewById(R.id.list);

      listView.setAdapter(TrackerActivity.adapter);// .setListAdapter(adapter);
      
//      ListView listView = getListView();
      
      listView.setOnItemLongClickListener(new OnItemLongClickListener() {

          public boolean onItemLongClick(AdapterView<?> parent, View view,
              int position, long id) {

            if (mActionMode != null) {
              return false;
            }
            selectedItem = position;
            selectedRowView = view;

            // Start the CAB using the ActionMode.Callback defined above
//            mActionMode = TrackerListActivity.this.startActionMode(mActionModeCallback);
            view.setSelected(true);
            return true;
          }
        });
      
      listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);*/
	}
	
	//---------------------------------------------------------------------------
	
	/*private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	      // Inflate a menu resource providing context menu items
	      MenuInflater inflater = mode.getMenuInflater();
	      // Assumes that you have "contexual.xml" menu resources
	      inflater.inflate(R.menu.menu_list_row_selection, menu);
	      return true;
	    }

	    // Called each time the action mode is shown. Always called after
	    // onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	      return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	switch (item.getItemId()) {
	    	case R.id.menu_row_delete:	    		
	    		TextView idTV = (TextView) selectedRowView.findViewById(R.id.idTV);
	    		
//	    		DatabaseHandler db = new DatabaseHandler(TracksListActivity.this);
	    		TrackerActivity.db.deleteTrack(Integer.parseInt(idTV.getText().toString()));
	    		selectedRowView = null;
	    		
	    		// Action picked, so close the CAB
	    		mode.finish();
	    		return true;
	    	default:
	    		return false;
	      }
	    }

	    // Called when the user exits the action mode
	    public void onDestroyActionMode(ActionMode mode) {
	      mActionMode = null;
	      selectedItem = -1;
	    }
	  };*/
	  
	  //---------------------------------------------------------------------------
}
