package me.imthelion.socialserver.objects;

import java.time.MonthDay;
import java.time.YearMonth;

public class DailyTime {
	
	private final short day;
	private final short month;
	private final int year;
	
	public DailyTime(short day, short month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
		
	}
	
	public boolean isLater(DailyTime time) {
		if(year>time.getYear())return true;
		else if(month>time.getMonth())return true;
		else if(day>time.getDay())return true;
		return false;
	}
	public boolean isSame(DailyTime time) {
		return day==time.getDay()&&month==time.getMonth()&&year==time.getYear();
	}
	
	public int getYear() {
		return year;
	}
	public short getMonth() {
		return month;
	}
	public short getDay() {
		return day;
	}
	
	public static DailyTime now() {
		return new DailyTime((short)MonthDay.now().getDayOfMonth(), (short)MonthDay.now().getMonthValue(), YearMonth.now().getYear());
	}

}
