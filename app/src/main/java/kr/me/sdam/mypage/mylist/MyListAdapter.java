package kr.me.sdam.mypage.mylist;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyListAdapter extends BaseAdapter
implements Serializable, MyListView.OnLikeClickListener{
	
	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(MyListAdapter adapter, View view, MyList2Result item, int type);
	}
	
	OnAdapterItemClickListener mListener;
	
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
		mListener = listener;
	}
	
	
//	===============
	ArrayList<MyList2Result> items = new ArrayList<MyList2Result>();
	Context mContext;
	int totalCount;
	String keyword;
	
	public MyListAdapter(Context context) {
		mContext = context;
	}
	
	public void setTotalCount(int total) {
		totalCount =total;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void remove(MyList2Result item){
		items.remove(item);
		notifyDataSetChanged();
	}
	
	public void add(MyList2Result item){
		items.add(item);
		notifyDataSetChanged();
	}
	
	public void addAll(ArrayList<MyList2Result> items) {
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
		MyListView view;
		if (convertView == null) {
			view = new MyListView(mContext);
			view.setOnLikeClickListener(this);
		} else {
			view = (MyListView)convertView;
		}
		view.setItemData(items.get(position));
		return view;
	}

	@Override
	public void onLikeClick(View v, MyList2Result item, int type) {
		if (mListener != null) {
			mListener.onAdapterItemClick(this, v, item, type);
		}			
	}
	public void setLikeNum(MyList2Result item){
		if(item.myGood==0){
			item.myGood=1;
			item.goodNum++;
		} else{
			item.myGood=0;
			item.goodNum--;
		}
		notifyDataSetChanged();
	}
	
}
