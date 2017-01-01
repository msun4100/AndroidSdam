package kr.me.sdam.autologin.popupwindow;

import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.MyApplication;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

public class MySignupPopupAdapter extends BaseAdapter{
	public MySignupPopupAdapter(Context context) {
		mContext =context;
	}
	
	ArrayList<String> items = new ArrayList<String>();
	Context mContext;
	
	public ArrayList<String> getItemList(){
		return this.items;
	}
	public void add(String item) {
		items.add(item);
		notifyDataSetChanged();
	}
	public void addAll(List<String> items) {
		this.items.addAll(items);
		notifyDataSetChanged();
	}
	
	public void remove(int index){
		items.remove(index);
		notifyDataSetChanged();
	}
	
	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		MySignupPopupView view;
		if(convertView == null ) {
			view = new MySignupPopupView(mContext);
		} else {
			view = (MySignupPopupView)convertView;
		}
		
		view.setItemData(items.get(position));
		DisplayMetrics dm = MyApplication.getContext().getResources().getDisplayMetrics();
		int size = Math.round( 30 * dm.density);
		LayoutParams param = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
		param.height= size;		
		view.setLayoutParams(param);
		
		return view;
	}
	public View getView(int position, View convertView, ViewGroup parent, String text) {
		
		MySignupPopupView view;
		if(convertView == null ) {
			view = new MySignupPopupView(mContext);
		} else {
			view = (MySignupPopupView)convertView;
		}
		view.setItemData(text);
		return view;
	}
}
