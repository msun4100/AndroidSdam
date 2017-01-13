package kr.me.sdam.detail;

import java.io.Serializable;

import kr.me.sdam.common.CommonResult;

public class Detail2Result implements Serializable{
	public CommonResult article;
	public Detail4BestReply bestReply;
	public Detail5Replies[] replies;
	
}
