package com.valfom.tracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class TrackerChooseActivityDialogFragment extends SherlockDialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle("Choose Activity").setItems(R.array.activities_array,
				new DialogInterface.OnClickListener() {
			
					public void onClick(DialogInterface dialog, int which) {
						
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				        Editor prefsEditor = prefs.edit();
				        
				        prefsEditor.putInt("activity", which);
				        
				        prefsEditor.commit();
					}
				});
		
		return builder.create();
	}
}
