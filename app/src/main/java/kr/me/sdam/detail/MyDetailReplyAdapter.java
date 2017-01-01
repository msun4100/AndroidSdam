package kr.me.sdam.detail;

import java.io.Serializable;
import java.util.ArrayList;

import kr.me.sdam.tabthree.TabThreeResult;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyDetailReplyAdapter extends BaseAdapter
implements Serializable, MyDetailReplyView.OnReplyLikeClickListener{

	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(MyDetailReplyAdapter adapter, View view, Detail5Replies item, int type);
		
	}
	OnAdapterItemClickListener mListener;
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener){
		mListener = listener;
	}
	
	ArrayList<Detail5Replies> items = new ArrayList<Detail5Replies>();
	Context mContext;
	
	public MyDetailReplyAdapter(Context context) {
		mContext = context;
	}
//	custom function
	public void setLikeNum(Detail5Replies item){
		if(item.myGood==0){
			item.myGood=1;
			item.repGoodNum++;
		} else{
			item.myGood=0;
			item.repGoodNum--;
		}
		notifyDataSetChanged();
	}
	
	public void dupExistSetLikeNum(Detail5Replies item){
		setLikeNum(item);
		if(item.isBest == true){
			for(Detail5Replies r : this.items ){
				if(item.repContent.equals(r.repContent) && r.isBest==false){
					setLikeNum(r);
//					if(r.isBest==false){
//						setLikeNum(r);	
//					}
				}
			}	
		} else {
			for(Detail5Replies r : this.items ){
				if(item.repContent.equals(r.repContent) && r.isBest==true){
					setLikeNum(r);
				}
			}	
		}
	}
	public void remove(Detail5Replies item){
		items.remove(item);
		notifyDataSetChanged();
	}
//	custom function
	public void add(Detail5Replies item) {
		items.add(item);
		notifyDataSetChanged();
	}
//	custom function
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
		MyDetailReplyView view;
		if(convertView == null) {
			view = new MyDetailReplyView(mContext);
			view.setOnReplyLikeClickListener(this);
		} else  {
			view = (MyDetailReplyView)convertView;
		}
		view.setItemData(items.get(position));
		return view;
	}

	@Override
	public void onReplyLikeClick(View v, Detail5Replies item, int type) {
		if(mListener != null) {
			mListener.onAdapterItemClick(this, v, item, type);
			
		}		
	}

}
