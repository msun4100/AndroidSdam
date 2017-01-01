package kr.me.sdam.mypage.calendar;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CalendarAdapter extends BaseAdapter {

	Context mContext;
	CalendarData mData;
	
	ArrayList<CalendarItem> items = new ArrayList<CalendarItem>();
	
	public CalendarAdapter(Context context, CalendarData data) {
		mContext = context;
		mData = data;
	}
	
	public void setCalendarData(CalendarData data) {
		mData = data;
		notifyDataSetChanged();
	}
	
	public void addEmotion(MyCalendarResult result){
		items.get( Integer.parseInt(result.date) ).emotionIcon = result.emotion;
		notifyDataSetChanged();
	}
	
	public void add(CalendarItem item){
		items.add(item);
		notifyDataSetChanged();
	}
	
	public void addAll(ArrayList<CalendarItem> items){
		this.items.addAll(items);
		notifyDataSetChanged();
	}
	
	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		int size = 0;
		if (mData != null) {
			size = mData.days.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		return mData.days.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CalendarItemView view = (CalendarItemView)convertView;
		if (view == null) {
			view = new CalendarItemView(mContext);
		}
		view.setData(mData.days.get(position));
		return view;
	}

}
