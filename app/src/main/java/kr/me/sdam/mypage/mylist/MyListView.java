package kr.me.sdam.mypage.mylist;

import kr.me.sdam.BgImage;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonResultItem;
import kr.me.sdam.mypage.favor.Favor2Result;
import kr.me.sdam.mypage.favor.FavorView;
import kr.me.sdam.mypage.favor.FavorView.OnLikeClickListener;
import kr.me.sdam.tabthree.TabThreeResult;

public class MyListView extends FrameLayout {

	public MyListView(Context context) {
		super(context);
		init();
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	// =======================================
	// ImageView backgroundView;
	TextView contentView;
	ImageView iconReportView;

	ImageView iconDistanceView;
	TextView distanceView;

	ImageView iconTimeView;
	TextView timeView;
	
	ImageView iconReplyView;
	TextView replyCountView;

	ImageView iconLikeView;
	TextView likeCountView;

	ImageView iconEmotionView;
	ImageView background;
	
	MyList2Result mItem;

	View frameDistanceView;
	View frameReplyView;
	View frameLikeView;
	View layoutView;

	ImageView lockedIcon;
	public interface OnLikeClickListener {
		public void onLikeClick(View v, MyList2Result item, int type);
	}

	OnLikeClickListener mListener;

	public void setOnLikeClickListener(OnLikeClickListener listener) {
		mListener = listener;
	}

	public OnClickListener viewListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.image_report:
				if (mListener != null) {
					mListener.onLikeClick(MyListView.this, mItem,
							CommonResultItem.TYPE_REPORT);
				}
				break;
			// case R.id.image_reply:
			// if (mListener != null) {
			// mListener.onLikeClick(TabTwoView.this, mItem,
			// TabTwoData.TYPE_REPLY);
			// }
			// break;
			// case R.id.image_like:
			// if (mListener != null) {
			// mListener.onLikeClick(TabTwoView.this, mItem,
			// TabTwoData.TYPE_LIKE);
			// }
			// break;
			case R.id.frame_distance:
				if (mListener != null) {
					mListener.onLikeClick(MyListView.this, mItem,
							CommonResultItem.TYPE_DISTANCE);
				}
				break;
			case R.id.frame_reply:
				if (mListener != null) {
					mListener.onLikeClick(MyListView.this, mItem,
							CommonResultItem.TYPE_REPLY);
				}
				break;
			case R.id.frame_like:
				if (mListener != null) {
					mListener.onLikeClick(MyListView.this, mItem,
							CommonResultItem.TYPE_LIKE);
				}
				break;
			default:
				break;
			}
		}
	};

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.item_common_list_layout,
				this);

		layoutView = (View) findViewById(R.id.frame_common_list);
		contentView = (TextView) findViewById(R.id.text_content);
		iconReportView = (ImageView) findViewById(R.id.image_report);
		iconDistanceView = (ImageView) findViewById(R.id.image_distance);
		distanceView = (TextView) findViewById(R.id.text_distance);
		
		iconTimeView = (ImageView)findViewById(R.id.image_time);
		timeView = (TextView)findViewById(R.id.text_time);
		
		iconReplyView = (ImageView) findViewById(R.id.image_reply);
		replyCountView = (TextView) findViewById(R.id.text_reply_count);
		iconLikeView = (ImageView) findViewById(R.id.image_like);
		likeCountView = (TextView) findViewById(R.id.text_like_count);

		iconEmotionView = (ImageView) findViewById(R.id.image_emoticon);
		background = (ImageView)findViewById(R.id.image_bg_);
		
		frameDistanceView = (View) findViewById(R.id.frame_distance);
		frameReplyView = (View) findViewById(R.id.frame_reply);
		frameLikeView = (View) findViewById(R.id.frame_like);

		iconReportView.setOnClickListener(viewListener);
		// iconLikeView.setOnClickListener(viewListener);
		// iconReplyView.setOnClickListener(viewListener);
		frameDistanceView.setOnClickListener(viewListener);
		frameReplyView.setOnClickListener(viewListener);
		frameLikeView.setOnClickListener(viewListener);
		
		lockedIcon = (ImageView)findViewById(R.id.image_locked);
	}

	public void setItemData(MyList2Result item) {
		lockedIcon.setVisibility(View.GONE);
		mItem = item;

		if(item.locate==99999 ){	
			distanceView.setText("어딘가");
		} else {
			distanceView.setText("" + item.locate + "km");// int	
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
			} else if (item.timeStamp.time.equals("months")) {
				timeStr = "달 전";
			} else if (item.timeStamp.time.equals("years")){
				timeStr = "년 전";
			}
			timeView.setText(item.timeStamp.value + timeStr);
		}
		
		contentView.setText(item.content);
		
		replyCountView.setText("" + item.repNum);// int
		likeCountView.setText("" + item.goodNum);// int
		
		iconLikeView.setImageResource(R.drawable.b_main_view_contents_icon_05_off);
		if (item.myGood == 0 ) {
			iconLikeView .setImageResource(R.drawable.b_main_view_contents_icon_05_off);
		} else if(item.myGood >= 1 ){
			iconLikeView .setImageResource(R.drawable.b_main_view_contents_icon_05_on);
		} else {
			iconLikeView .setImageResource(R.drawable.b_main_view_contents_icon_05_on);
		}

		iconEmotionView.setImageResource( BgImage.getEmotionIcon(item.emotion) );
		int bgResource = BgImage.DEFAULT_IMAGE;
		bgResource = BgImage.getBgImage(Integer.parseInt(item.category) , Integer.parseInt(item.image));
		background.setImageResource(bgResource);
		if(item.locked==1){
			lockedIcon.setVisibility(View.VISIBLE);
		}
	}

}
