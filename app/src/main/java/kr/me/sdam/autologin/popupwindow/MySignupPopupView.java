package kr.me.sdam.autologin.popupwindow;

import kr.me.sdam.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MySignupPopupView extends FrameLayout{
	public MySignupPopupView(Context context) {
		super(context);
		init();
	}
	public MySignupPopupView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	TextView textView;

	private void init() {
		LayoutInflater.from(getContext()).inflate( R.layout.popup_gender_item, this);
		textView = (TextView) findViewById(R.id.text_gender);
		
	}

	public void setItemData(String item) {
		textView.setText(item);
	}
}
