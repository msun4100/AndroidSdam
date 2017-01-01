package kr.me.sdam.detail;

import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.MyApplication;
import kr.me.sdam.R;

public class DetailDataManager {
	private static DetailDataManager instance;
	public static DetailDataManager getInstance() {
		if(instance == null) {
			instance = new DetailDataManager();
		}
		return instance;
	}
	
	private DetailDataManager(){}
	
	ArrayList<Detail5Replies> items;
	public String[] nicks;// = getResources().getStringArray(R.array.reply_nicks);
	public String[] getNicksArray(){
		if(nicks == null){
			nicks = MyApplication.getContext().getResources().getStringArray(R.array.reply_nicks);
		}
		return nicks;
	}
	
	public List<Detail5Replies> getItemDetailReplyList() {
		if(items == null){
			items = new ArrayList<Detail5Replies>();
		}
		return items;
	}
	
	public List<Detail5Replies> getItemDetailDummyDataList() {
	
		ArrayList<Detail5Replies> dummyItems = new ArrayList<Detail5Replies>();
		for(int i=0; i<20; i++) {
			Detail5Replies did = new Detail5Replies();
//			did.replyContent = "reply : " + i ;
//			did.replyTime = ""+i+"분전";
//			did.iconLike = R.drawable.ic_launcher;
//			did.replyLikeCount = i;
//			items.add(did);
			
			did.active=0;
			did.repAuthor="Author"+i;
			did.repContent="init reply "+i;
			did.repGoodNum=+i;
			did.repNum=i;
			dummyItems.add(did);
		}
		return dummyItems;
	}
	
}
