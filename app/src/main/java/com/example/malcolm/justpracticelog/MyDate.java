package com.example.malcolm.justpracticelog;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyDate {

	static public int toYmd(int year,int month,int day) {
		int ymd = day & 0x1f;
		ymd = ymd + ((month & 0xf) << 5);
		ymd = ymd + ((year & 0xffff) << 9);

		return ymd;
	}
	static public int toYmd(Calendar cal) {
		return toYmd(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
	}
	static int dayOfWeek(int ymd) { // Note:Sunday = 1, not 0 !!
		Calendar cal = ymdToCal(ymd);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	static public Calendar ymdToCal(int ymd) {
		int day = toDay(ymd);
		int month = toMonth(ymd);
		int year = toYear(ymd);
		return new GregorianCalendar(year, month, day);
	}
	
	static public int toDay(int ymd) {
		return ymd & 0x1f;
	}
	static public int toMonth(int ymd) {
		return (ymd >> 5) & 0xf;
	}
	static public int toYear(int ymd) {
		return (ymd >> 9) & 0xfff;
	}

	static public String toString(int year,int month,int day) {
		return String.format("%d/%d/%d", day, month, year);
	}

	static public int fromString(String str) {
		String[] parts = str.split("/");
		int day = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int year = Integer.parseInt(parts[2]);

		if( month > 12 )
			Log.e("MyDate:Bad Month", str + "month is " + month);
		return toYmd(year,month-1,day);
	}

	static public String toString(int ymd) {
		int day = toDay(ymd);
		int month = toMonth(ymd);
		int year = toYear(ymd);
		
		return toString(year,month+1,day);
	}
	
	
	static public int today() {
		Calendar cal = Calendar.getInstance();
		return toYmd(cal);
	}	
	
	static public int weekDay(int ymd) {
		Calendar cal = ymdToCal(ymd);
		
		return cal.get(Calendar.DAY_OF_WEEK) - 1;
	}
	static public boolean isToday(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		
		Calendar today = Calendar.getInstance();
		int thisYear = today.get(Calendar.YEAR);
		int thisMonth = today.get(Calendar.MONTH);
		int thisDay = today.get(Calendar.DAY_OF_MONTH);
		
		if( year == thisYear && month == thisMonth && day == thisDay )
			return true;
		else
			return false;
	}
	static public int firstDayOfMonth(int month, int year) {
		Calendar cal = new GregorianCalendar(year, month, 1);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	static public int daysInMonth(int month, int year) {
		Calendar cal = new GregorianCalendar(year, month, 1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	static public int startOfWeek(int ymd) {
		Calendar cal = ymdToCal(ymd);
		int day = cal.get(Calendar.DAY_OF_WEEK); // Sunday=1 ... Saturday=7
		while( day > 1) {
			ymd = previousDay(ymd);
			day--;
		}
		return ymd;
	}
	
	static public int nextDay(int ymd) {
		int day = toDay(ymd);
		int month = toMonth(ymd);
		int year = toYear(ymd);
		if( day < daysInMonth(month,year)) {
			day++;		
		} else {
			if( month < 11) {
				day=1;
				month++;
			} else {
				year++;
				day=1;
				month=0;
			}
		}
		Calendar cal = new GregorianCalendar(year, month, day);
		return toYmd(cal);
	}
	static public int nextWeek(int ymd) {
		for( int i=0; i<7;i++)
			ymd = nextDay(ymd);
		return ymd;
	}
	static public int previousDay(int ymd) {
		int day = toDay(ymd);
		int month = toMonth(ymd);
		int year = toYear(ymd);
		if( day > 1) {
			day--;		
		} else {
			if( month > 0) {
				month--;
				day = daysInMonth(month,year);
			} else {
				year--;
				month=11;
				day=31;
			}
		}
		Calendar cal = new GregorianCalendar(year, month, day);
		return toYmd(cal);
	}
	static public int previousWeek(int ymd) {
		for( int i=0; i<7;i++)
			ymd = previousDay(ymd);
		return ymd;
	}
	static public int daysInPreviousMonth(int month, int year) {
		if( month == 0) {
			month = 11;
			year--;
		} else {
			month--;
		}
		Calendar cal = new GregorianCalendar(year, month, 1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	static public int timeToYear(long time) {
		Date date = new Date(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}	
	static public int timeToMonth(long time) {
		Date date = new Date(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH);
	}
	static public int timeToDay(long time) {
		Date date = new Date(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	static public Date timeToDate(long time) {
		return new Date(time);
	}
	static public long DateToTime(Date date) {
		return date.getTime();
	}
	static public long mktime(int year, int month, int day) {
		Calendar cal = new GregorianCalendar(year, month, day);
		return cal.getTimeInMillis();
	}
}