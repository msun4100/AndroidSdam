package kr.me.sdam.tabthree;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TabThreeAdapter extends BaseAdapter
implements Serializable, TabThreeItemView.OnLikeClickListener {
	
	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(TabThreeAdapter adapter, View view, TabThreeResult item, int type);
	}
	
	OnAdapterItemClickListener mListener;
	
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
		mListener = listener;
	}
	
	
//	===============
	public ArrayList<TabThreeResult> getItems(){
		return items;
	}
	ArrayList<TabThreeResult> items = new ArrayList<TabThreeResult>();
	Context mContext;
	int totalCount;
	String keyword;
	
	public TabThreeAdapter(Context context) {
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
	
	public void add(TabThreeResult item){
		items.add(item);
//		NewTabTwoDataManager.getInstance().addDataManagerList(item);
		notifyDataSetChanged();
	}
	public void remove(int index){
		items.remove(index);
		notifyDataSetChanged();
	}
	public void remove(TabThreeResult item){
		items.remove(item);
		notifyDataSetChanged();
	}
	
	public void addAll(ArrayList<TabThreeResult> items) {
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
		TabThreeItemView view;
		if (convertView == null) {
			view = new TabThreeItemView(mContext);
			view.setOnLikeClickListener(this);
		} else {
			view = (TabThreeItemView)convertView;
		}
		view.setItemData(items.get(position));
		return view;
	}

	@Override
	public void onLikeClick(View v, TabThreeResult item, int type) {
		if (mListener != null) {
			mListener.onAdapterItemClick(this, v, item, type);
		}			
	}
	public void setLikeNum(TabThreeResult item){
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
