package com.valfom.sports.tracker;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class TrackerButtonsMapDialogFragment extends SherlockDialogFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.dialog_map_buttons, container, false);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		
        return dialog;
	}
}
