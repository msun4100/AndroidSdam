package kr.me.sdam.tabthree;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public class TabThreeDataManager {
	private static TabThreeDataManager instance;
	public static TabThreeDataManager getInstance() {
		if(instance == null) {
			instance = new TabThreeDataManager();
		}
		return instance;
	}
	ArrayList<TabThreeResult> items = new ArrayList<TabThreeResult>();
	private TabThreeDataManager() {
	}
	
	public void addDataManagerList(TabThreeResult item){
		items.add(item);
	}
	public void addAllDataManagerList(ArrayList<TabThreeResult> list){
		this.items.addAll(list);
	}
	public void clearDataManagerList(){
		this.items.clear();
	}
	public List<TabThreeResult> getDataManagerList() {
		return items;
	}

}
