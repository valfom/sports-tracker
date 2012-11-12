package com.valfom.tracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ChooseActivityDialogFragment extends SherlockDialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle("Choose Activity").setItems(R.array.activities_array,
				new DialogInterface.OnClickListener() {
			
					public void onClick(DialogInterface dialog, int which) {}
				});
		
		return builder.create();
	}
}
