package kr.me.sdam.detail;

import kr.me.sdam.MyApplication;
import kr.me.sdam.R;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

public class MyDetailReplyView extends FrameLayout{
	public MyDetailReplyView(Context context) {
		super(context);
		init();
	}

	public MyDetailReplyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private final int TV_DEFAULT_COLOR = R.color.edit_text_default_color;
	private final int TV_TAG_COLOR = R.color.sdam_text_color;
	
//	public final static String[] nicks = {"담쟁이","남방긴수염고래", "스피아민트", "쥬시후레시", "후레시민트", "nick5", "nick6", "nick7", "nick8", "nick9"};
	public static final int REPLY_NICKS_COUNT = 100;
	public String[] nicks;
	
	ImageView replyBestIcon;
	ImageView replyMyIcon;
	TextView replyNickView;
	TextView replyContentView;
	TextView replyTimeView;
	ImageView replyLikeIcon;
	TextView replyLikeCountView;
	
	Detail5Replies mItem;
	boolean isFirst = false;
	View layoutView; // bottom margin 주는용도
	
	View frameReplyNick;
	View frameReplyLike;
	public interface OnReplyLikeClickListener {
		public void onReplyLikeClick(View v, Detail5Replies item,int type);
	}
	OnReplyLikeClickListener mListener;
	public void setOnReplyLikeClickListener(OnReplyLikeClickListener listener) {
		mListener = listener;
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.item_detail_reply_layout, this);
		layoutView = (View)findViewById(R.id.detail_reply_layout);
		replyBestIcon = (ImageView)findViewById(R.id.image_reply_best);
		replyMyIcon = (ImageView)findViewById(R.id.image_reply_me);
		replyNickView = (TextView)findViewById(R.id.text_reply_nick);
		frameReplyNick = (View)findViewById(R.id.frame_reply_nick);
		replyContentView = (TextView)findViewById(R.id.text_reply_content);
		replyTimeView = (TextView)findViewById(R.id.text_reply_time);
		
		frameReplyLike = (View)findViewById(R.id.frame_reply_like);
		replyLikeIcon = (ImageView)findViewById(R.id.image_reply_like);
		replyLikeCountView = (TextView)findViewById(R.id.text_reply_like_count);
		
//		replyNickView.setOnClickListener(mViewListener);
//		replyLikeIcon.setOnClickListener(mViewListener);
		frameReplyNick.setOnClickListener(mViewListener);
		frameReplyLike.setOnClickListener(mViewListener);
		
		
		nicks = DetailDataManager.getInstance().getNicksArray();
	}//init()
	
	public void setItemData(Detail5Replies item) {
		mItem = item;
		replyMyIcon.setVisibility(View.GONE);
		replyContentView.setText("");
		if(!item.isBest){
			replyBestIcon.setVisibility(View.GONE);
		} else {
			replyBestIcon.setImageResource(R.drawable.b_main_view_2_detail_comments_best);
//			layoutView.setBackgroundColor(Color.BLUE);
//			layoutView.setBackgroundResource(R.drawable.b_main_view_contents_bg);
			DisplayMetrics dm = getResources().getDisplayMetrics();
			int size = Math.round( 3 * dm.density);
			LayoutParams param = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			param.bottomMargin= size;		
			layoutView.setLayoutParams(param);
			replyBestIcon.setVisibility(View.VISIBLE);
		}
		
		if(item.timeStamp != null){
			String timeStr="time";
			if(item.timeStamp.time.equals("hours")){
				timeStr = "시간 전";
			} else if(item.timeStamp.time.equals("dates")){
				timeStr = "일 전";
			} else if(item.timeStamp.time.equals("minutes")){
				timeStr = "분 전";
			} else if(item.timeStamp.time.equals("second")){
				timeStr = "초 전";
			} else if(item.timeStamp.time.equals("months")){
				timeStr = "달 전";
			} else if(item.timeStamp.time.equals("years")){
				timeStr = "년 전";
			}
			replyTimeView.setText(item.timeStamp.value + timeStr);
		}
		
		SpannableString text = new SpannableString( item.repContent);
		text.setSpan(new ForegroundColorSpan(0xFF3E3E3E), 0, text.length(), 0);
		if(item.tag!=null){
			for(int i=0; i<item.tag.length; i++){
				String nickname = nicks[ (Integer.parseInt(item.tag[i])) ] ;
				String tempText = text.toString();
				int lastLen = 0;
				for(;;){
					if(tempText.contains(nickname) && item.repContent.length() >= item.tag[i].length() ){ //2번째 케이스는 이미 써진 더미에 대한 버그잡기위해
						int start = tempText.indexOf( nickname.substring(0) ); //substring(0) 고정
						int end = start + nicks[(Integer.parseInt(item.tag[i]))].length() ;
						if(start != -1){
							text.setSpan(new ForegroundColorSpan(0xFF26A7C7), lastLen + start, lastLen+end, 0);	
						}
						tempText = tempText.substring(end);
//						Log.i("tempText", tempText);
						lastLen = lastLen+end ;
					} else {
						lastLen = 0;
						break;
					}
				}
			}
		}
		replyContentView.setText(text, BufferType.SPANNABLE);
		
		if(item.me==1){
			replyMyIcon.setImageResource(R.drawable.b_main_view_2_detail_comments_my);
			replyMyIcon.setVisibility(View.VISIBLE);
			if(item.master==1){// 1,1
//				replyNickView.setText("담쟁이");
				replyNickView.setText(nicks[0]);
				
			} else { //1,0
				int nickIndex = 0;
				if(item.nickname > REPLY_NICKS_COUNT){ //서버에서받지 않을 때 == 처음 글 쓸 때
					int ranValue = 1+ (int)( Math.random() * (REPLY_NICKS_COUNT-1) ); // 0~9 random -> 1~9 random
					nickIndex=ranValue;
				} else {
					nickIndex = item.nickname;
				}
				replyNickView.setText( nicks[nickIndex] );//중복처리한 인덱스	
			}
		} else {
			if(item.master==1){ //0,1
				replyNickView.setText(nicks[0]);
//				replyNickView.setText("담쟁이");
			} else {
				int nickIndex = 0;
				if(item.nickname > REPLY_NICKS_COUNT){ //서버에서받지 않을 때 == 처음 글 쓸 때
					int ranValue = 1+ (int)( Math.random() * (REPLY_NICKS_COUNT-1) );
					nickIndex=ranValue;
				} else {
					nickIndex = item.nickname;
				}
				replyNickView.setText( nicks[nickIndex] );//중복처리한 인덱스	
			}
		}
		replyNickView.setTextColor(0xFF898989);
		if(item.myGood==0){
			replyLikeIcon.setImageResource(R.drawable.b_main_view_2_detail_comments_like_off);
			int color = getResources().getColor(R.color.reply_zero_color);
			replyLikeCountView.setTextColor(color);
		} else if(item.myGood >= 1){
			replyLikeIcon.setImageResource(R.drawable.b_main_view_2_detail_comments_like_on);
			int color = getResources().getColor(R.color.reply_color);
			replyLikeCountView.setTextColor(color);
		}	
		replyLikeCountView.setText(""+item.repGoodNum);
	}
	public OnClickListener mViewListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.frame_reply_nick:
				if(mListener != null) {
					mListener.onReplyLikeClick(MyDetailReplyView.this, mItem,Detail1Info.TYPE_REPLY_TAG);
				}
				break;
			case R.id.frame_reply_like:
				if(mListener != null) {
					mListener.onReplyLikeClick(MyDetailReplyView.this, mItem,Detail1Info.TYPE_REPLY_LIKE);
				}
				break;
				default:
					break;
			}
		}
	};
}
