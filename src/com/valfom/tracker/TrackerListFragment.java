package com.valfom.tracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class TrackerListFragment extends ListFragment {

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		DatabaseHandler db;
		SimpleCursorAdapter scAdapter;
		Cursor cursor;
		
		db = new DatabaseHandler(getActivity());
		cursor = db.getAllTracks();
	    
	    String[] from = new String[] { DatabaseHandler.KEY_SC_ID, DatabaseHandler.KEY_DATE, 
	    		DatabaseHandler.KEY_DIST, DatabaseHandler.KEY_TIME };
	    int[] to = new int[] { R.id.idTV, R.id.dateTV, R.id.distanceTV, R.id.timeTV };

	    scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_row, cursor, from, to);
		
		setListAdapter(scAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
//		return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.fragment_list, null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		
		TextView idTV = (TextView) v.findViewById(R.id.idTV);
		int trackId = Integer.parseInt(idTV.getText().toString());

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		Fragment listFragment = fragmentManager.findFragmentById(R.id.fragment_container_list);
		fragmentTransaction.remove(listFragment);

		Fragment infoFragment = new TrackInfoFragment();

		Bundle args = new Bundle();
		args.putInt("id", trackId);
		infoFragment.setArguments(args);

		fragmentTransaction.add(R.id.fragment_container_list, infoFragment);

		fragmentTransaction.addToBackStack(null);

		fragmentTransaction.commit();
	}
}
