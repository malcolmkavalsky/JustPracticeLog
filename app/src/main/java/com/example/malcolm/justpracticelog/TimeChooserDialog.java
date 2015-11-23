package com.example.malcolm.justpracticelog;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.widget.TimePicker;

public class TimeChooserDialog {
	TimeChooseListener timeChooseListener;
	TimePickerDialog timePickerDialog;
	Activity parent;
	String result;
	int hour = 0;
	int minute = 15;
	
	TimeChooserDialog(Activity parent, int hour, int minute) {
		this.parent = parent;
		this.hour = hour;
		this.minute = minute;
	}
	

	
	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hour, int minute) {
			result = pad(hour)+":" + pad(minute);
			timeChooseListener.chose(result);			
		}
	};
	
	public void open() {
		timePickerDialog =  new TimePickerDialog(parent, timePickerListener, hour, minute,true);
		timePickerDialog.show();
	}
	
	public void setTimeChooseListener(TimeChooseListener l) {
		timeChooseListener = l;
	}
	
	private String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
}
