package kr.me.sdam.tabone;

import java.io.Serializable;
import java.util.ArrayList;



import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TabOneAdapter extends BaseAdapter
implements Serializable, TabOneItemView.OnLikeClickListener{
	
	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(TabOneAdapter adapter, View view, TabOneResult item, int type);
	}
	
	OnAdapterItemClickListener mListener;
	
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
		mListener = listener;
	}
	
//	===============
	ArrayList<TabOneResult> items = new ArrayList<TabOneResult>();
	Context mContext;
	int totalCount;
	String keyword;
	
//	public void addAll(ArrayList<MovieItem> items) {
//		this.items.addAll(items);
//		notifyDataSetChanged();
//	}
	
	
	public TabOneAdapter(Context context) {
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
	
	public void remove(TabOneResult item){
		items.remove(item);
		notifyDataSetChanged();
	}
	
	public void add(TabOneResult item){
		items.add(item);
//		NewTabTwoDataManager.getInstance().addDataManagerList(item);
		notifyDataSetChanged();
	}
	
	
	public void addAll(ArrayList<TabOneResult> items) {
		this.items.addAll(items);
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
		TabOneItemView view;
		if (convertView == null) {
			view = new TabOneItemView(mContext);
			view.setOnLikeClickListener(this);
		} else {
			view = (TabOneItemView)convertView;
		}
		view.setItemData(items.get(position));
		return view;
	}

	@Override
	public void onLikeClick(View v, TabOneResult item, int type) {
		if (mListener != null) {
			mListener.onAdapterItemClick(this, v, item, type);
		}			
	}
	public void setLikeNum(TabOneResult item){
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
