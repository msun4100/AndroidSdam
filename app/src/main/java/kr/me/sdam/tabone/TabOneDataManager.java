package kr.me.sdam.tabone;

import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.tabtwo.TabTwoResult;
import android.util.Log;

public class TabOneDataManager {
	private static TabOneDataManager instance;

	public static TabOneDataManager getInstance() {
		if (instance == null) {
			instance = new TabOneDataManager();
		}
		return instance;
	}

	ArrayList<TabOneResult> items = new ArrayList<TabOneResult>();

	private TabOneDataManager() {
		// items= new ArrayList<Result>();
	}

	public void addToTabOneDataManagerList(TabOneResult item) {
		items.add(item);
		Log.i("items size", "" + items.size());
		// Toast.makeText(MyApplication.getContext(),
		// "items size()"+items.size(), Toast.LENGTH_SHORT).show();
	}

	public List<TabOneResult> getTabOneResultList() {

		// // ArrayList<Result> items = new ArrayList<Result>();
		// for(int i=0; i<items.size(); i++) {
		// Result id = new Result();
		//
		// id.category="catergory "+i;
		// id.content="content "+i;
		// // id.good_num=i;
		// // id.locate=i;
		// // items.add(id);
		//
		// }
		return items;
	}

	public List<TabOneResult> getTabOneDummyList() {

		ArrayList<TabOneResult> items = new ArrayList<TabOneResult>();
		for (int i = 0; i < 20; i++) {
			TabOneResult id = new TabOneResult();

			id.category = "" + i;
			id.content = "content " + i;
			id.goodNum = i;
			id.locate = i;
			items.add(id);

		}
		return items;
	}

}
