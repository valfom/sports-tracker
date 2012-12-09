package com.valfom.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class TrackerSelectActivityDialogFragment extends SherlockDialogFragment {
	
	public interface SelectActivityDialogListener {
        
		public void onActivitySelected();
    }
	
	SelectActivityDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		
		try {
            mListener = (SelectActivityDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SelectActivityDialogListener");
        }
		
		super.onAttach(activity);
	}

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
				        
				        mListener.onActivitySelected();
					}
				});
		
		return builder.create();
	}
}
