package kr.me.sdam.common;

import java.io.Serializable;
import java.util.ArrayList;

import kr.me.sdam.tabone.TabOneItemView;
import kr.me.sdam.tabone.TabOneResult;
import kr.me.sdam.tabthree.TabThreeItemView;
import kr.me.sdam.tabtwo.TabTwoItemView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CommonResultAdapter extends BaseAdapter 
implements Serializable, CommonResultView.OnLikeClickListener {
	
	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(CommonResultAdapter adapter, View view, CommonResultItem item, int type);
	}
	
	OnAdapterItemClickListener mListener;
	
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
		mListener = listener;
	}
	
	
//	===============
	public static final int VIEW_TYPE_COUNT = 3;

	public static final int VIEW_INDEX_TAB_ONE = 0;
	public static final int VIEW_INDEX_TAB_TWO = 1;
	public static final int VIEW_INDEX_TAB_THREE = 2;
	
	
	ArrayList<CommonResultItem> items = new ArrayList<CommonResultItem>();
	Context mContext;
	int totalCount;
	String keyword;
	
	public CommonResultAdapter(Context context) {
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
	
	
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(int position) {
		switch(items.get(position).type){
		case CommonResultItem.TAB_ONE:
			return VIEW_INDEX_TAB_ONE;
		case CommonResultItem.TAB_TWO:
			return VIEW_INDEX_TAB_TWO;
		case CommonResultItem.TAB_THREE:
			default:
			return VIEW_INDEX_TAB_THREE;
		}
	}
	
	public void add(CommonResultItem item){
		items.add(item);
		notifyDataSetChanged();
	}
	public void remove(int index){
		items.remove(index);
		notifyDataSetChanged();
	}
	public void remove(CommonResultItem item){
		items.remove(item);
		notifyDataSetChanged();
	}
	
	public void addAll(ArrayList<CommonResultItem> items) {
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
//		CommonResultView view;
//		if (convertView == null) {
//			view = new CommonResultView(mContext);
//			view.setOnLikeClickListener(this);
//		} else {
//			view = (CommonResultView)convertView;
//		}
//		view.setItemData(items.get(position));
//		return view;
		switch(getItemViewType(position)){
		case VIEW_INDEX_TAB_ONE:{
			TabOneItemView view;
			if (convertView != null && convertView instanceof TabOneItemView){
				view = (TabOneItemView) convertView;
			} else {
				view = new TabOneItemView(mContext);
			}
			view.setItemData(items.get(position));
			return view;
		}
		case VIEW_INDEX_TAB_TWO:{
			TabTwoItemView view;
			if(convertView != null && convertView instanceof TabTwoItemView){
				view = (TabTwoItemView) convertView;
			} else {
				view = new TabTwoItemView(mContext);
			}
			view.setItemData(items.get(position));
			return view;
		}
		case VIEW_INDEX_TAB_THREE:
		default: {
			TabThreeItemView view;
			if(convertView != null && convertView instanceof TabTwoItemView){
				view = (TabThreeItemView) convertView;
			} else {
				view = new TabThreeItemView(mContext);
			}
			view.setItemData(items.get(position));
			return view;
		}
		}//switch
		
		
	}

	@Override
	public void onLikeClick(View v, CommonResultItem item, int type) {
		if (mListener != null) {
			mListener.onAdapterItemClick(this, v, item, type);
		}			
	}
	public void setLikeNum(CommonResultItem item){
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
