package kr.me.sdam.alarm;

import kr.me.sdam.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmView extends FrameLayout{

	public AlarmView(Context context) {
		super(context);
		init();
	}

	public AlarmView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	

	public interface OnLikeClickListener {
		public void onLikeClick(View v, AlarmData item, int type);
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
			case R.id.frame_alarm_icon:
				if (mListener != null) {
					mListener.onLikeClick(AlarmView.this, mItem, AlarmData.TYPE_ICON);
				}
				break;
			default:
				break;
			}
		}
	};
	
	ImageView iconAlarmView;
	TextView alarmDescView;
	
	AlarmData mItem;
	
	View frameAlarmIcon;
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.item_alarm_content_layout, this);
		iconAlarmView = (ImageView)findViewById(R.id.image_alarm_icon);
		alarmDescView=(TextView)findViewById(R.id.text_alarm_desc);
		
		frameAlarmIcon = (View)findViewById(R.id.frame_alarm_icon);
		frameAlarmIcon.setOnClickListener(viewListener);
	}
	
	public void setItemData(AlarmData item) {
		mItem = item;
		alarmDescView.setText(item.alarmDesc);
		
		
//		public static final int ALARM_LIKE = 1;
//		public static final int ALARM_REPLY_LIKE = 2;
//		public static final int ALARM_REPLY = 3;
//		public static final int ALARM_TAG = 4;
//		public static final int ALARM_NOTI = 5;//공지
//		1=좋아요 2=댓글좋아요 3=댓글을남겼습니다 4=태그
		switch(item.alarmType){
		case AlarmData.ALARM_LIKE://1
			if(item.clicked == 1){
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_02_off);
			} else {
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_02_on);
			}
			alarmDescView.setText("누군가 나의 담을 좋아합니다.");
			break;
		case AlarmData.ALARM_REPLY_LIKE://2
			if(item.clicked == 1){
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_02_off);
			} else {
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_02_on);
			}
			alarmDescView.setText("누군가 나의 댓글을 좋아합니다.");
			break;
		case AlarmData.ALARM_REPLY://3
			if(item.clicked == 1){
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_01_off);
			} else{
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_01_on);
			}
			alarmDescView.setText("누군가 나의 담에 댓글을 남겼습니다.");
			break;
		case AlarmData.ALARM_TAG: //4
			if(item.clicked== 1){
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_04_off);
			} else {
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_04_on);
			}
			alarmDescView.setText("누군가 당신에게 이야기합니다.");
			break;
		case AlarmData.ALARM_NOTI://5
			if(item.clicked== 1){
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_03_off);
			} else {
				iconAlarmView.setImageResource(R.drawable.e_notice_contents_icon_03_on);
			}
			alarmDescView.setText("공지사항이 있습니다.");
			break;

		}
		
	}
}
