package kr.me.sdam.alarm;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
public class MyAlarmAdapter extends BaseAdapter 
implements AlarmView.OnLikeClickListener{

	public MyAlarmAdapter(Context context) {
		mContext =context;
	}
	
	ArrayList<AlarmData> items = new ArrayList<AlarmData>();
	Context mContext;
	
	
	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(MyAlarmAdapter adapter, View view, AlarmData item, int type);
	}
	
	OnAdapterItemClickListener mListener;
	
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
		mListener = listener;
	}
	
	
	
	
	public void add(AlarmData item) {
		items.add(item);
		notifyDataSetChanged();
	}
	public void addAll(List<AlarmData> items) {
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
		
		AlarmView view;
		if(convertView == null ) {
			view = new AlarmView(mContext);
			view.setOnLikeClickListener(this);
		} else {
			view = (AlarmView)convertView;
		}
		view.setItemData(items.get(position));
		return view;
	}
	public void setDisplay(AlarmData item){
		if(item.clicked==0){
			item.clicked=1;
		}
//		 else {
//			item.isSelected=false;
//		}
		notifyDataSetChanged();
	}
	@Override
	public void onLikeClick(View v, AlarmData item, int type) {
		if (mListener != null) {
			mListener.onAdapterItemClick(this, v, item, type);
		}			
	}
}
