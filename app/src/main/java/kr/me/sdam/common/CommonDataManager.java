package kr.me.sdam.common;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class CommonDataManager {
	private static CommonDataManager instance;
	public static CommonDataManager getInstance() {
		if(instance == null) {
			instance = new CommonDataManager();
		}
		return instance;
	}
	ArrayList<CommonResultItem> items = new ArrayList<CommonResultItem>();
	private CommonDataManager() {
//		items= new ArrayList<Result>();
	}
	
	public void addToTabThreeDataManagerList(CommonResultItem item){
		items.add(item);
		Log.i("items size", ""+items.size());
//		Toast.makeText(MyApplication.getContext(), "items size()"+items.size(), Toast.LENGTH_SHORT).show();
	}
	public List<CommonResultItem> getTabThreeResultList() {

////		ArrayList<Result> items = new ArrayList<Result>();
//		for(int i=0; i<items.size(); i++) {
//			Result id = new Result();
//
//			id.category="catergory "+i;
//			id.content="content "+i;
////			id.good_num=i;
////			id.locate=i;
////			items.add(id);
//			
//		}
		return items;
	}
}
