package kr.me.sdam.autologin.popupwindow;

import java.util.Arrays;
import java.util.List;

import kr.me.sdam.MyApplication;
import kr.me.sdam.R;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MySignupPopup extends PopupWindow{
	public interface OnPopupListener {
		public void onButtonClick(int type, String text) ;
	}
	OnPopupListener mListener;
	public void setOnPopupListener(OnPopupListener listener) {
		mListener = listener;
	}
	MySignupPopupAdapter mAdapter;
	ListView listView;
	List<String> mItems;
	public MySignupPopup(Context context, List<String> items) {
		super(context);
		final View view = LayoutInflater.from(context).inflate(R.layout.popup_gender, null);
		setContentView(view);
		mItems=items;
		listView=(ListView)view.findViewById(R.id.listView_gender);
		
		mAdapter = new  MySignupPopupAdapter(MyApplication.getContext());
		listView.setFocusable(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String text = (String)listView.getItemAtPosition(position);
				if(mListener != null){
					mListener.onButtonClick(0, text);
				}
				dismiss();
				
			}
		});
		listView.setAdapter(mAdapter);
		initData();
	}
	private void initData(){
//		String [] genderArray = MyApplication.getContext().getResources().getStringArray(R.array.spinner_gender_item); 
//		List<String> items = Arrays.asList(genderArray);
		for(String did : mItems) {
			mAdapter.add(did);
		}
		if(mAdapter.getCount()>2){
			listView.setSelection(38); //생일년도인경우 1988터보이기
			setHeight(370);
			setWidth(270);
		} else { 
			listView.setSelection(0); //성별인 경우 남자부터
			setHeight(LayoutParams.WRAP_CONTENT);
			setWidth(270);
		}
		
	}
}
