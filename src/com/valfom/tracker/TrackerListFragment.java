package com.valfom.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
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
import android.widget.Toast;

public class TrackerListFragment extends ListFragment {

	public static final String KEY_ID = "id";
	public static final String KEY_DATE = "date";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_TIME = "time";

	private TrackListAdapter adapter;
	
	public static ArrayList<HashMap<String, String>> tracksList;


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		tracksList = new ArrayList<HashMap<String, String>>();

		DatabaseHandler db = new DatabaseHandler(getActivity());

		List<Track> allTracks = db.getAllTracks();

		for (int i = 0; i < allTracks.size(); i++) {

			HashMap<String, String> map = new HashMap<String, String>();

			map.put(KEY_ID, String.valueOf(allTracks.get(i).getId()));
			map.put(KEY_DATE, allTracks.get(i).getDate());
			map.put(KEY_DISTANCE,
					String.valueOf(allTracks.get(i).getDistance() / 1000));
			map.put(KEY_TIME,
					String.valueOf(allTracks.get(i).getTime() / 1000 / 60 / 60));

			tracksList.add(map);
//			Log.d("List", allTracks.get(i).getDate());
		}
//
//		Toast toast = Toast.makeText(getActivity(),
//				String.valueOf(allTracks.size()), Toast.LENGTH_LONG);
//		toast.show();
//
		adapter = new TrackListAdapter(getActivity(), tracksList);
//
		setListAdapter(adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_list, null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		Fragment trackerListFragment = fragmentManager
				.findFragmentById(R.id.container_track_list);
		fragmentTransaction.remove(trackerListFragment);

		TextView idTV = (TextView) v.findViewById(R.id.idTV);
		int trackId = Integer.parseInt(idTV.getText().toString());

		Fragment trackInfoFragment = new TrackInfoFragment();

		Bundle args = new Bundle();

		Log.d("DIST", String.valueOf(trackId));
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
	}
}
