package kr.me.sdam.search;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import kr.me.sdam.common.CommonResult;
import kr.me.sdam.common.CommonResultAdapter;

public class MySearchAdapter extends CommonResultAdapter implements Serializable {
    public boolean isExists(CommonResult commonResult){
        boolean bool = false;
        for(CommonResult cr : items){
            if(cr.num == commonResult.num){
                bool = true;
                break;
            }
        }
        return bool;
    }
}
//public class MySearchAdapter extends BaseAdapter
//implements Serializable, SearchView.OnLikeClickListener{
//
//	public interface OnAdapterItemClickListener {
//		public void onAdapterItemClick(MySearchAdapter adapter, View view, SearchResult item, int type);
//	}
//
//	OnAdapterItemClickListener mListener;
//
//	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
//		mListener = listener;
//	}
//
//
////	===============
//	ArrayList<SearchResult> items = new ArrayList<SearchResult>();
//	Context mContext;
//	int totalCount;
//	String keyword;
//
//	public MySearchAdapter(Context context) {
//		mContext = context;
//	}
//
//	public void setTotalCount(int total) {
//		totalCount =total;
//	}
//
//	public int getTotalCount() {
//		return totalCount;
//	}
//
//	public void setKeyword(String keyword) {
//		this.keyword = keyword;
//	}
//
//	public String getKeyword() {
//		return keyword;
//	}
//
//	public void add(SearchResult item){
//		items.add(item);
////		NewTabTwoDataManager.getInstance().addDataManagerList(item);
//		notifyDataSetChanged();
//	}
//	public void remove(int index){
//		items.remove(index);
//		notifyDataSetChanged();
//	}
//	public void remove(SearchResult item){
//		items.remove(item);
//		notifyDataSetChanged();
//	}
//
//	public void addAll(ArrayList<SearchResult> items) {
//		this.items.addAll(items);
//		notifyDataSetChanged();
//	}
//
//	public void clear() {
//		items.clear();
//		notifyDataSetChanged();
//	}
//	@Override
//	public int getCount() {
//		return items.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return items.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		SearchView view;
//		if (convertView == null) {
//			view = new SearchView(mContext);
//			view.setOnLikeClickListener(this);
//		} else {
//			view = (SearchView)convertView;
//		}
//		view.setItemData(items.get(position));
//		return view;
//	}
//
//	@Override
//	public void onLikeClick(View v, SearchResult item, int type) {
//		if (mListener != null) {
//			mListener.onAdapterItemClick(this, v, item, type);
//		}
//	}
//	public void setLikeNum(SearchResult item){
//		if(item.myGood==0){
//			item.myGood=1;
//			item.goodNum++;
//		} else{
//			item.myGood=0;
//			item.goodNum--;
//		}
//		notifyDataSetChanged();
//	}
//}
