package kr.me.sdam.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.R;
import kr.me.sdam.common.event.EventInfo;
import kr.me.sdam.tabone.TabOneItemView;
import kr.me.sdam.tabthree.TabThreeItemView;
import kr.me.sdam.tabtwo.TabTwoItemView;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CommonResultAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>
		implements OnItemClickListener, OnItemLongClickListener, ItemViewHolder.OnLikeClickListener{
	public List<CommonResult> items = new ArrayList<>();

	public interface OnAdapterItemClickListener {
		public void onAdapterItemClick(CommonResultAdapter adapter, View view, int position, CommonResult item, int type);
	}
	OnAdapterItemClickListener mListener;
	public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
		mListener = listener;
	}

	@Override
	public void onLikeClick(View v, int position, CommonResult item, int type) {
		if (mListener != null) {
			mListener.onAdapterItemClick(this, v, position,item, type);
		}
	}
	//start recyclerView.OnItemClick implements
	OnItemClickListener itemClickListener;
	public void setOnItemClickListener(OnItemClickListener listener) {
		itemClickListener = listener;
	}
	@Override
	public void onItemClick(View view, int position) {
		if (itemClickListener != null) {
			itemClickListener.onItemClick(view, position);
		}
	}//end of recyclerView.OnItemClick implements


	OnItemLongClickListener itemLongClickListener;
	public void setOnItemLongClickListener(OnItemLongClickListener listener){
		itemLongClickListener = listener;
	}
	@Override
	public void onItemLongClick(View view, int position) {
		if (itemLongClickListener != null) {
			itemLongClickListener.onItemLongClick(view, position);
		}
	}

	// CRUD Functions
	public void findOneAndModify(CommonResult child, String mode){
		switch (mode){
			case EventInfo.MODE_CREATE:
				add(child);
				break;
			case EventInfo.MODE_READ:

				break;
			case EventInfo.MODE_UPDATE:
				update(child);
				break;
			case EventInfo.MODE_DELETE:
				remove(child.num);
				break;
			default:
				break;
		}
		notifyDataSetChanged();
	}


	public void add(CommonResult item){
		items.add(item);
		notifyDataSetChanged();
	}

	public void remove(CommonResult item){
		for(CommonResult cr : items){
			if(cr.num == item.num){
				int index = items.indexOf(cr);
				items.remove(index);
				break;
			}
		}
		notifyDataSetChanged();
	}

	public void remove(int num){
		for(CommonResult cr : items){
			if(cr.num == num){
				int index = items.indexOf(cr);
				items.remove(index);
				break;
			}
		}
		notifyDataSetChanged();
	}

	public void addAll(List<CommonResult> items) {
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	public void update(CommonResult commonResult){
		for(CommonResult cr : items){
			if(cr.num == commonResult.num){
				int index = items.indexOf(cr);
				if(index != -1){
					items.set(index, commonResult);
//					items.get(index).num = commonResult.num;
//					items.get(index).writer = commonResult.writer;
//					items.get(index).locate = commonResult.locate;
//					items.get(index).locked = commonResult.locked;
//					items.get(index).content= commonResult.content;
//					items.get(index).repNum = commonResult.repNum;
//					items.get(index).goodNum = commonResult.goodNum;
//					items.get(index).myGood = commonResult.myGood;
//					items.get(index).timeStamp = commonResult.timeStamp;
//					items.get(index).emotion = commonResult.emotion;
//					items.get(index).category = commonResult.category;
//					items.get(index).image = commonResult.image;
//					items.get(index).isSelected = commonResult.isSelected;
//					items.get(index).isMyHide = commonResult.isMyHide;
////					items.get(index).type = commonResult.type;
				}
				break;
			}
		}
		notifyDataSetChanged();
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view;
		view = inflater.inflate(R.layout.item_common_list_layout, parent, false);
		ItemViewHolder holder = new ItemViewHolder(view, parent.getContext());
		holder.setOnItemClickListener(this);
		holder.setOnItemLongClickListener(this);
		holder.setOnLikeClickListener(this);
		return holder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		((ItemViewHolder)holder).setChildItem(items.get(position));
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public CommonResult getItem(int position) {
		if (position < 0 || position >= items.size()) return null;
		return items.get(position);
	}

	private int totalCount;
	public int getTotalCount(){
		return this.totalCount;
	}
	public void setTotalCount(int count){
		this.totalCount = count;
	}
	//like
	public void setLikeNum(CommonResult item){
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
