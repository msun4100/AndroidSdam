package kr.me.sdam.tabtwo;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TabTwoAdapter extends BaseAdapter 
implements Serializable, TabTwoItemView.OnLikeClickListener {
	
	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(TabTwoAdapter adapter, View view, TabTwoResult item, int type);
	}
	
	OnAdapterItemClickListener mListener;
	
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
		mListener = listener;
	}
	
	
//	===============
	ArrayList<TabTwoResult> items = new ArrayList<TabTwoResult>();
	Context mContext;
	int totalCount;
	String keyword;
	
	public TabTwoAdapter(Context context) {
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
	public void remove(TabTwoResult item){
		items.remove(item);
		notifyDataSetChanged();
	}
	public void add(TabTwoResult item){
		items.add(item);
//		NewTabTwoDataManager.getInstance().addDataManagerList(item);
		
		notifyDataSetChanged();
	}
	
	
	public void addAll(ArrayList<TabTwoResult> items) {
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
		TabTwoItemView view;
		if (convertView == null) {
			view = new TabTwoItemView(mContext);
			view.setOnLikeClickListener(this);
		} else {
			view = (TabTwoItemView)convertView;
		}
		view.setItemData(items.get(position));
		return view;
	}

	@Override
	public void onLikeClick(View v, TabTwoResult item, int type) {
		if (mListener != null) {
			mListener.onAdapterItemClick(this, v, item, type);
		}			
	}
	
	public void setLikeNum(TabTwoResult item){
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
