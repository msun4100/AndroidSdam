package kr.me.sdam.tabtwo;

import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.MyApplication;
import android.util.Log;
import android.widget.Toast;

import kr.me.sdam.R;

public class TabTwoDataManager {
	private static TabTwoDataManager instance;
	public static TabTwoDataManager getInstance() {
		if(instance == null) {
			instance = new TabTwoDataManager();
		}
		return instance;
	}
	ArrayList<TabTwoResult> items = new ArrayList<TabTwoResult>();
	private TabTwoDataManager() {
//		items= new ArrayList<Result>();
	}
	
	public void addToTabTwoDataManagerList(TabTwoResult item){
		items.add(item);
		Log.i("items size", ""+items.size());
//		Toast.makeText(MyApplication.getContext(), "items size()"+items.size(), Toast.LENGTH_SHORT).show();
	}
	public List<TabTwoResult> getTabTwoResultList() {

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
