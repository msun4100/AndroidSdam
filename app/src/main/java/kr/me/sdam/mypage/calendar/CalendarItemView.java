package kr.me.sdam.mypage.calendar;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import kr.me.sdam.R;
public class CalendarItemView extends LinearLayout {

	TextView numberView;
//	TextView contentView;
	ImageView contentView;
	CalendarItem mItem;
//	public final static int NUMBER_COLOR = Color.BLACK;
//	public final static int SAT_COLOR = Color.BLUE;
//	public final static int SUN_COLOR = Color.RED;
	public final static int NUMBER_COLOR = R.color.calendar_day;
	public final static int SAT_COLOR = R.color.calendar_day;
	public final static int SUN_COLOR = R.color.sunday;
	
	public final static float IN_MONTH_TEXT_SIZE_SP = 14.0f;
//	public final static int OUT_MONTH_TEXT_COLOR = Color.GRAY;
	public final static int OUT_MONTH_TEXT_COLOR = R.color.calendar_out_day;
	public final static float OUT_MONTH_TEXT_SIZE_SP = 14.0f;
//	public final static float OUT_MONTH_TEXT_SIZE_SP = 10.0f;
	
	public CalendarItemView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.calendar_item, this);
		numberView = (TextView)findViewById(R.id.number);
		contentView = (ImageView)findViewById(R.id.image_calendar_emotion);
//		contentView = (TextView)findViewById(R.id.content);
	}
	
	public void setData(CalendarItem item) {
		mItem = item;
		float textsize = IN_MONTH_TEXT_SIZE_SP;
		int textColor = NUMBER_COLOR;
		if (!item.inMonth) {
			textsize = OUT_MONTH_TEXT_SIZE_SP;
			textColor = OUT_MONTH_TEXT_COLOR;
		} else {
			textsize = IN_MONTH_TEXT_SIZE_SP;
			switch (item.dayOfWeek) {
//				case Calendar.SUNDAY :
//					textColor = SUN_COLOR;
//					break;
				case Calendar.SATURDAY :
					textColor = SAT_COLOR;
					break;
				case Calendar.SUNDAY :
				default :
					textColor = NUMBER_COLOR;
					break;
			}
		}
		numberView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textsize);
		if(item.dayOfWeek == Calendar.SUNDAY){
			numberView.setTextColor(0xFFF14249);
//			numberView.setTextColor(0xFFFff249);
		} else {
			numberView.setTextColor(0xff626262);	
		}
		if(!item.inMonth){
			numberView.setTextColor(0xffacacac);
		}
//		numberView.setTextColor(textColor);
		numberView.setText("" + item.dayOfMonth);
		// contentView setting
		
		ArrayList<ItemData> items = item.items;
		int size = items.size();
//		StringBuilder sb = new StringBuilder();
//		sb.append(size + "��");
//		contentView.setText(sb.toString());
		
		if(item.emotionIcon != 99999){
			contentView.setVisibility(View.VISIBLE);
			contentView.setImageResource(getEmotionIcon(item.emotionIcon));	
		} else {
			contentView.setVisibility(View.INVISIBLE);
		}
		

	}
	private int getEmotionIcon(int emotion){
		int bg=R.drawable.f_my_calender_emoticon_01;
		switch(emotion){
		case 0:
			bg=R.drawable.f_my_calender_emoticon_01;
			break;
		case 1:
			bg=R.drawable.f_my_calender_emoticon_02;
			break;
		case 2:
			bg=R.drawable.f_my_calender_emoticon_03;
			break;
		case 3:
			bg=R.drawable.f_my_calender_emoticon_04;
			break;
		case 4:
			bg=R.drawable.f_my_calender_emoticon_05;
			break;
		case 5:
			bg=R.drawable.f_my_calender_emoticon_06;
			break;
		case 6:
			bg=R.drawable.f_my_calender_emoticon_07;
			break;
		case 7:
			bg=R.drawable.f_my_calender_emoticon_08;
			break;
		case 8:
			bg=R.drawable.f_my_calender_emoticon_09;
			break;
		case 9:
			bg=R.drawable.f_my_calender_emoticon_10;
			break;
		}
		return bg;
	}

}
