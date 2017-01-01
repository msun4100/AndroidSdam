package kr.me.sdam.common;

import java.io.Serializable;

public class CommonResultItem implements Serializable {
	public static final int TAB_ONE = 0;
	public static final int TAB_TWO = 1;
	public static final int TAB_THREE = 2;
	public int type;
	
	public static final String REQUEST_NUMBER = "requestnumber";
	public static final String REQUEST_OBJECT = "requestobject";
	
	public static final int TYPE_INVALID = -1;
	public static final int TYPE_DISTANCE = 0;
	public static final int TYPE_REPORT = 1;
	public static final int TYPE_REPLY = 2;
	public static final int TYPE_LIKE = 3;
	
	public int num;
	public String writer;
	public double locate;
	public int locked;
	public String content;
	public int repNum;
	public int goodNum;
	public int myGood;
	public CommonTimeStamp timeStamp;
	public int emotion;
	public String category="00";
	public String image="000";
	public boolean isSelected=false;
	public boolean isMyHide=false;
	@Override
	public String toString() {
		return content+" "+ category;
	}
	
}
