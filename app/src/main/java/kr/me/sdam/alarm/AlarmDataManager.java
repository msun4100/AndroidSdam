package kr.me.sdam.alarm;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import kr.me.sdam.R;
import kr.me.sdam.tabone.TabOneResult;

public class AlarmDataManager {
	private static AlarmDataManager instance;
	public static AlarmDataManager getInstance() {
		if(instance == null) {
			instance = new AlarmDataManager();
		}
		return instance;
	}
	
	private AlarmDataManager(){}
	ArrayList<AlarmData> mItems ;
	public void setItemDataList( ArrayList<AlarmData> items){
		if( mItems == null){
			mItems = new ArrayList<AlarmData>();
		}
		mItems = items;
	}
	public List<AlarmData> getItemDataList() {
	
//		ArrayList<AlarmData> items = new ArrayList<AlarmData>();
//		for(int i=0; i<10; i++) {
//			AlarmData aid = new AlarmData();
//			if(i<2){
//				aid.alarmType = 0;
//				aid.alarmDesc = "누군가 나의 담에 댓글을 남겼습니다.";
//			}
//			else if(i< 5){
//				aid.alarmType = 1;
//				aid.alarmDesc = "누군가 나의 담을 좋아합니다.";
//			} else {
//				aid.alarmType =2;
//				aid.alarmDesc = "공지사항이 있습니다.";
//			}
//			
//			
//			items.add(aid);
//		}
		return mItems;
	}
	
}
