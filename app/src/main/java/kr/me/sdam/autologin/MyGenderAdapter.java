package kr.me.sdam.autologin;

import java.util.ArrayList;
import java.util.List;
import kr.me.sdam.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
deprecated
deprecated
deprecated
deprecated
deprecated
deprecated
deprecated
deprecated
kr.me.sdam.autologin.popupwindow 여기꺼 씀 성별 나이 모두.
*/
@SuppressLint("ResourceAsColor")
public class MyGenderAdapter extends BaseAdapter {
	ArrayList<String> items = new ArrayList<String>();
	Context mContext;

	public MyGenderAdapter(Context context) {
		mContext = context;
	}

	public void add(String item) {
		items.add(item);
		notifyDataSetChanged();
	}

	public void addAll(List<String> items) {
		this.items.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;
		if (convertView == null) {
			tv = new TextView(mContext);
		} else {
			tv = (TextView) convertView;
		}
		if (position == 0) {
			tv.setTextColor(R.color.sign_up_font);
		} else {
			tv.setTextColor(R.color.a_launcher_spinner);
		}
		tv.setText(items.get(position));
		// tv.setHeight(R.dimen.signup_gender_height);
		// tv.setWidth(R.dimen.signup_gender_width);
		// tv.setBackgroundResource(R.drawable.a_launcher_box_dropdown);
		//
		return tv;
	}
}
