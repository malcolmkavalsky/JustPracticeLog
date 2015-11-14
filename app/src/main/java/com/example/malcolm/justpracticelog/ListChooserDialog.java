package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class ListChooserDialog {
	private Dialog dialog;
	private Activity parent;
	private ListChooserListener listener;
	private boolean enable_add;
	private boolean enable_all;
	
	ListChooserDialog(Activity parent) {
		this.parent = parent;
		enable_add = false;
    }
	
	public void enableAdd() {
		enable_add = true;
	}	
	public void enableAll() {
		enable_all = true;
	}	
	
	public void open(String title, CharSequence[] names)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(parent);
		builder.setTitle(title);
		builder.setItems (names, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int position) {
				dialog.dismiss();		
				listener.chose(position);
			}}
		);
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();		
				listener.cancelled();
			}}
		);
		if( enable_add) {
			builder.setPositiveButton("New", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();	
					listener.add();
				}}
					);
		}
		if( enable_all) {
			builder.setNeutralButton("All", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();	
					listener.all();
				}}
					);
		}
		dialog = builder.create();
		dialog.show();
	}
	public void setListChooserListener(ListChooserListener l) {
		listener = l;
	}	
}
