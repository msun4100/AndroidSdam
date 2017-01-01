package kr.me.sdam.search;

import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.R;

public class SearchDataManager {
	private static SearchDataManager instance;
	public static SearchDataManager getInstance() {
		if(instance == null ) {
			instance = new SearchDataManager();
		}
		return instance;
	}
	
	private SearchDataManager(){}
	
	public List<SearchResult> getItemDataList() {
		ArrayList<SearchResult> items = new ArrayList<SearchResult>();
		
		for(int i=0; i<10; i++) {
//			SearchData id = new SearchData();
			SearchResult id = new SearchResult();
//			id.iconOdd = R.drawable.gallery_photo_5;
//			id.iconEven = R.drawable.happy_000;
			items.add(id);
			
		}
		return items;
	}
}
