package kr.me.sdam.detail;

import java.io.Serializable;
import java.util.ArrayList;

import kr.me.sdam.common.CommonTimeStamp;

public class Detail5Replies implements Serializable{
	public int repNum;
	public String repAuthor;
	public int active;
	public int me;
	public int master;
	public String repContent;
	public CommonTimeStamp timeStamp;
	public int repGoodNum;
	public int myGood;
	public int nickname=99999;
	public String[] tag;
//	public boolean isSelected=false;
//	public boolean isMine=false;
	public boolean isBest=false;
	public Detail5Replies(){}
}
