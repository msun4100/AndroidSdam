package kr.me.sdam.write;

import kr.me.sdam.PropertyManager;

public class ArticleParams {
	public String userId;
	public String content;
	public String age;
	public String category;
	public String image;
	public String emotion;
	public int lock;
	public int unlocate;
//	public String nickname;
	
	public ArticleParams(){
		this.userId=PropertyManager.getInstance().getUserId();
		this.content="default content";
		this.age=""+(PropertyManager.getInstance().getMaxAge()-PropertyManager.getInstance().getMinAge() );
		this.category="00";
		this.image="000";
		this.emotion="0";
		this.lock=0;
		this.unlocate=0;
//		this.nickname="0";
	}
	
	public void setArticleParams(){
		
	}
}
