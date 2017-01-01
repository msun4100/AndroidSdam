package kr.me.sdam.alarm;

public class AlarmData {
	
//	1=좋아요 2=댓글좋아요 3=댓글을남겼습니다 4=태그
	
	public static final int ALARM_LIKE = 1;
	public static final int ALARM_REPLY_LIKE = 2;
	public static final int ALARM_REPLY = 3;
	public static final int ALARM_TAG = 4;
	public static final int ALARM_NOTI = 5;//공지
	
	public static final int TYPE_ICON = 0;
	
	public int alarmType=0;
	public int num=0;
	public String alarmDesc;
	public int clicked=0;
	
	public AlarmData(){}
	public AlarmData(int type, int num){
		this.alarmType=type;
		this.num=num;
	}
}
