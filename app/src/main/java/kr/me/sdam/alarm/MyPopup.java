package kr.me.sdam.alarm;

import java.util.List;

import kr.me.sdam.MyApplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import kr.me.sdam.R;
//Deprecated
//Deprecated
//Deprecated
//Deprecated
//Deprecated
//Deprecated
//Deprecated
//Deprecated
//Deprecated
//Deprecated
//Deprecated
public class MyPopup extends PopupWindow {
	public interface OnPopupListener {
		public void onButtonClick(int index) ;
	}
	OnPopupListener mListener;
	public void setOnPopupListener(OnPopupListener listener) {
		mListener = listener;
	}
	MyAlarmAdapter mAdapter;
	ListView listView;
	ImageView closeIcon;
	View popupLayout;
	public MyPopup(Context context) {
		super(context);
		final View view = LayoutInflater.from(context).inflate(R.layout.item_alarm_popup_layout, null);
		setContentView(view);

		listView=(ListView)view.findViewById(R.id.list_popup);
//		TextView headerView = new TextView(MyApplication.getContext());
//		headerView.setText("알림");
//		listView.addHeaderView(headerView, "알림data", true);
		
		mAdapter = new MyAlarmAdapter(context);
		listView.setFocusable(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Object data = listView.getItemAtPosition(position);
				if (data instanceof String) {
					String text = (String) data;
					Toast.makeText(MyApplication.getContext(), "header click : " + text,
							Toast.LENGTH_SHORT).show();
				} else {
					AlarmData item = (AlarmData) data;
					mAdapter.setDisplay(item);
					Toast.makeText(MyApplication.getContext(), "popup item click:" + item.alarmDesc, Toast.LENGTH_SHORT).show();
				}
			}
		});
		listView.setAdapter(mAdapter);
		closeIcon = (ImageView)view.findViewById(R.id.image_popup_close);
		closeIcon.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mListener != null){
					mListener.onButtonClick(0);
				}
				dismiss();
			}
			
		});
		popupLayout = (View)view.findViewById(R.id.frame_popup_layout);
		popupLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mListener != null){
					mListener.onButtonClick(1);
				}
				dismiss();
			}
		});
		initData();
//		setHeight(LayoutParams.MATCH_PARENT);
//		setWidth(LayoutParams.MATCH_PARENT);
	}
	private void initData(){
		
		List<AlarmData> items = AlarmDataManager.getInstance().getItemDataList();
		for(AlarmData did : items) {
			mAdapter.add(did);
		}
//		setHeight((mAdapter.getCount())*100);
		setHeight(1710);
		setWidth(1100);
//		setWidth(LayoutParams.MATCH_PARENT);
//		setHeight(LayoutParams.MATCH_PARENT);
//		setHeight(1150);
//		setWidth(770);
	}
	
	
}
