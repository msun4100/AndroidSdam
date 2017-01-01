package kr.me.sdam.autologin;

import java.io.Serializable;

public class RegisterInfoData implements Serializable {
	public RegisterInfoData(){
		this.userId=null;
		this.pw=null;
		this.birth=0;
		this.sex=0;
		this.pushId=null;
	}
	public String userId;
	public String pw;
	public int birth;
	public int sex;
	public String pushId;
}
