package kr.me.sdam.mypage.calendar;

import java.util.ArrayList;

import kr.me.sdam.R;

public class CalendarItem {
		
	public int year;
	public int month;
	public int dayOfMonth;
	public int dayOfWeek;
	public boolean inMonth;
	public int emotionIcon=99999;
	ArrayList items = new ArrayList();
	
	public CalendarItem(){}
	public CalendarItem(int year, int month, int dayOfMonth, int dayOfWeek, boolean inMonth, int emotionIcon){
		this.year=year;
		this.month=month;
		this.dayOfMonth=dayOfMonth;
		this.dayOfWeek=dayOfWeek;
		this.inMonth=inMonth;
		this.emotionIcon=emotionIcon;
		this.items = new ArrayList();
	}
}
