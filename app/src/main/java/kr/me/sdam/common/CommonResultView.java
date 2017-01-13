package kr.me.sdam.common;

import kr.me.sdam.BgImage;
import kr.me.sdam.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CommonResultView extends FrameLayout{
	public CommonResultView(Context context) {
		super(context);
		init();
	}


	public CommonResultView(Context context, AttributeSet attrs) {
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
	CommonResult mItem;

	View frameDistanceView;
	View frameReplyView;
	View frameLikeView;
	View layoutView;

	public interface OnLikeClickListener {
		public void onLikeClick(View v, CommonResult item, int type);
	}

	OnLikeClickListener mListener;

	public void setOnLikeClickListener(OnLikeClickListener listener) {
		mListener = listener;
	}

	// ==================================================
	public OnClickListener viewListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.image_report:
				if (mListener != null) {
					mListener.onLikeClick(CommonResultView.this, mItem,
							CommonResult.TYPE_REPORT);
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
					mListener.onLikeClick(CommonResultView.this, mItem,
							CommonResult.TYPE_DISTANCE);
				}
				break;
			case R.id.frame_reply:
				if (mListener != null) {
					mListener.onLikeClick(CommonResultView.this, mItem,
							CommonResult.TYPE_REPLY);
				}
				break;
			case R.id.frame_like:
				if (mListener != null) {
					mListener.onLikeClick(CommonResultView.this, mItem,
							CommonResult.TYPE_LIKE);
				}
				break;
			default:
				break;
			}
		}
	};

	private void init() {
		LayoutInflater.from(getContext()).inflate( R.layout.item_common_list_layout, this);

		layoutView = (View) findViewById(R.id.frame_common_list);
		contentView = (TextView) findViewById(R.id.text_content);
		iconReportView = (ImageView) findViewById(R.id.image_report);
		iconDistanceView = (ImageView) findViewById(R.id.image_distance);
		distanceView = (TextView) findViewById(R.id.text_distance);
		iconTimeView = (ImageView) findViewById(R.id.image_time);
		timeView = (TextView) findViewById(R.id.text_time);
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

	}

	public void setItemData(CommonResult item) {
		mItem = item;

		if(item.locate==99999){	
			distanceView.setText("어딘가");
		} else {
			distanceView.setText("" + item.locate + "km");// int	
		}
		if(item.timeStamp != null){
			timeView.setText(item.timeStamp.value + item.timeStamp.time);
		}
		
		contentView.setText(item.content);
		replyCountView.setText("" + item.repNum);// int
		likeCountView.setText("" + item.goodNum);// int
		
		iconLikeView.setImageResource(R.drawable.b_main_view_contents_icon_05_off);
				
		if (item.goodNum == 0) {
			iconLikeView .setImageResource(R.drawable.b_main_view_contents_icon_05_off);
		} else {
			iconLikeView .setImageResource(R.drawable.b_main_view_contents_icon_05_on);
		}

		iconEmotionView.setImageResource(getEmotionIcon(item.emotion));
		int bgResource = BgImage.DEFAULT_IMAGE;
		switch (Integer.parseInt(item.category)) {
		case BgImage.CATEGORY_ONE_DELIGHT:
			bgResource = BgImage.resOne[Integer.parseInt(item.image)];
			break;
		case BgImage.CATEGORY_TWO_SAD:
			bgResource = BgImage.resTwo[Integer.parseInt(item.image)];
			break;
		case BgImage.CATEGORY_THREE_SURPRISE:
			bgResource = BgImage.resThree[Integer.parseInt(item.image)];
			break;
		default:
			bgResource = BgImage.DEFAULT_IMAGE;
			break;
		}
		background.setImageResource(bgResource);
	}
	public int getEmotionIcon(int emotion){
		int num=0;
		switch(emotion){
		case 0:
			num=R.drawable.b_main_view_1_emoticon_01happy;
			break;
		case 1:
			num=R.drawable.b_main_view_1_emoticon_02sad;
			break;
		case 2:
			num=R.drawable.b_main_view_1_emoticon_03surprised;
			break;
		case 3:
			num=R.drawable.b_main_view_1_emoticon_04angry;
			break;
		case 4:
			num=R.drawable.b_main_view_1_emoticon_05shy;
			break;
		case 5:
			num=R.drawable.b_main_view_1_emoticon_06sorry;
			break;
		case 6:
			num=R.drawable.b_main_view_1_emoticon_07tired;
			break;
		case 7:
			num=R.drawable.b_main_view_1_emoticon_08funny;
			break;
		case 8:
			num=R.drawable.b_main_view_1_emoticon_09nervous;
			break;
		case 9:
			num=R.drawable.b_main_view_1_emoticon_10love;
			break;
		}
		
		return num;
	}
	
	
}
