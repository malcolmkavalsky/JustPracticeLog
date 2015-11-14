package com.example.malcolm.justpracticelog;

import java.util.Calendar;

public class MyTime {
	static public int toSeconds(int hour, int minute, int seconds) {
		return hour*3600 + minute*60 + seconds;
	}
	static public int toHour(int seconds) {
		return seconds / 3600;
	}
	static public int toMinutes(int seconds) {
		int hour = seconds / 3600;
		int minute = (seconds - (hour * 3600))/60;
		return minute;
	}
	static public int toSeconds(int seconds) {
		int hour = seconds / 3600;
		int minute = (seconds - (hour * 3600))/60;
		int second = seconds - (hour * 3600) - (minute * 60);
		return second;
	}
	static public int now() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);
		return toSeconds(hour,minutes,seconds);
	}	
	static public String toString(int hour, int minute, int seconds) {
		return String.format("%02d:%02d:%02d", hour, minute, seconds);
	}
	static public int fromString(String str) {
		String[] parts = str.split(":");
		int hour = Integer.parseInt(parts[0]);
		int minute = Integer.parseInt(parts[1]);
		int seconds = Integer.parseInt(parts[2]);

		return toSeconds(hour,minute,seconds);
	}

	static public String toString(int hour, int minute) {
		return String.format("%d:%02d", hour, minute);
	}
	static public String toString(int seconds) {
		return toString(toHour(seconds),toMinutes(seconds),toSeconds(seconds));
	}
	static public String minutesToString(int minutes) {
		if( minutes < 60 )
			return Integer.toString(minutes);
		else {
			int hours = minutes / 60;
			int mins = minutes - hours * 60;
			return String.format("%d:%02d", hours, mins);
		}
	}
	
	static public String toHourMins(int hms) {
		return minutesToString(hms/60);
	}
	static String MinsToHoursMins(int minutes) {
		int hours = minutes / 60;
		int mins;
		if( hours > 0) {
			mins = minutes - hours * 60;
			return String.format("%d:%02d", hours, mins);
		}
		else
			return String.format("%d", minutes);
	} 
	static int HoursMinsToMins(String hhmm) {
		String fields[] = hhmm.split(":");
		int hours=0, mins=0;
		if( fields.length == 2 ) {
			hours = Integer.valueOf(fields[0]);
			mins = Integer.valueOf(fields[1]);
		} else {
			mins = Integer.valueOf(fields[0]);
		}
		return hours * 60 + mins;
	} 
}
