package kr.me.sdam.mypage.mylist;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public class MyListDataManager {
	private static MyListDataManager instance;

	public static MyListDataManager getInstance() {
		if (instance == null) {
			instance = new MyListDataManager();
		}
		return instance;
	}

	ArrayList<MyList2Result> items = new ArrayList<MyList2Result>();

	private MyListDataManager() {
		// items= new ArrayList<Result>();
	}

	public void addToMyListDataManagerList(MyList2Result item) {
		items.add(item);
		Log.i("items size", "" + items.size());
		// Toast.makeText(MyApplication.getContext(),
		// "items size()"+items.size(), Toast.LENGTH_SHORT).show();
	}

	public List<MyList2Result> getMyListResultList() {
		return items;
	}

	public List<MyList2Result> getMyListDummyList() {

		ArrayList<MyList2Result> items = new ArrayList<MyList2Result>();
		for (int i = 0; i < 1; i++) {
			MyList2Result id = new MyList2Result();
			id.content = "해당 카테고리의 게시글이 없습니다.\n당신의 이야기를 들려주세요.";
			id.category="11";
			id.image="111";
			id.num = -1;
			id.locate=99999;
//			id.timeStamp.value=1;
//			id.timeStamp.time="second";
//			id.goodNum = i;
//			id.locate = i;
			items.add(id);

		}
		return items;
	}

}
