package kr.me.sdam.mypage.favor;

import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.mypage.mylist.MyList2Result;
import android.util.Log;


public class FavorDataManager {
	private static FavorDataManager instance;

	public static FavorDataManager getInstance() {
		if (instance == null) {
			instance = new FavorDataManager();
		}
		return instance;
	}

	ArrayList<Favor2Result> items = new ArrayList<Favor2Result>();

	private FavorDataManager() {
		// items= new ArrayList<Result>();
	}

	public void addToFavorDataManagerList(Favor2Result item) {
		items.add(item);
		Log.i("items size", "" + items.size());
		// Toast.makeText(MyApplication.getContext(),
		// "items size()"+items.size(), Toast.LENGTH_SHORT).show();
	}

	public List<Favor2Result> getFavorResultList() {

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

	public List<Favor2Result> getFavorDummyList() {

		ArrayList<Favor2Result> items = new ArrayList<Favor2Result>();
		for (int i = 0; i < 1; i++) {
			Favor2Result id = new Favor2Result();

			id.content = "해당 카테고리의 게시글이 없습니다.\n당신의 이야기를 들려주세요.";
			id.category="11";
			id.image="111";
			id.num = -1;
			id.locate=99999;
			items.add(id);

		}
		return items;
	}
	
//	public List<MyList2Result> getMyListDummyList() {
//
//		ArrayList<MyList2Result> items = new ArrayList<MyList2Result>();
//		for (int i = 0; i < 1; i++) {
//			MyList2Result id = new MyList2Result();
//			id.content = "해당 카테고리의 게시글이 없습니다.\n당신의 이야기를 들려주세요.";
//			id.category="11";
//			id.image="111";
//			id.num = -1;
//			id.locate=99999;
////			id.timeStamp.value=1;
////			id.timeStamp.time="second";
////			id.goodNum = i;
////			id.locate = i;
//			items.add(id);
//
//		}
//		return items;
//	}

}
