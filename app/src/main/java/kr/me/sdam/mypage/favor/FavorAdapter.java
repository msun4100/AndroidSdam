package kr.me.sdam.mypage.favor;

import java.io.Serializable;
import java.util.ArrayList;

import kr.me.sdam.mypage.mylist.MyList2Result;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FavorAdapter extends BaseAdapter
implements Serializable, FavorView.OnLikeClickListener{
	
	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(FavorAdapter adapter, View view, Favor2Result item, int type);
	}
	
	OnAdapterItemClickListener mListener;
	
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
		mListener = listener;
	}
	
	
//	===============
	ArrayList<Favor2Result> items = new ArrayList<Favor2Result>();
	Context mContext;
	int totalCount;
	String keyword;
	
	public FavorAdapter(Context context) {
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
	
	public void add(Favor2Result item){
		items.add(item);
		notifyDataSetChanged();
	}
	
	public void remove(Favor2Result item){
		items.remove(item);
		notifyDataSetChanged();
	}
	
	
	public void addAll(ArrayList<Favor2Result> items) {
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
		FavorView view;
		if (convertView == null) {
			view = new FavorView(mContext);
			view.setOnLikeClickListener(this);
		} else {
			view = (FavorView)convertView;
		}
		view.setItemData(items.get(position));
		return view;
	}


	@Override
	public void onLikeClick(View v, Favor2Result item, int type) {
		if (mListener != null) {
			mListener.onAdapterItemClick(this, v, item, type);
		}			

	}
	public void setLikeNum(Favor2Result item){
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
