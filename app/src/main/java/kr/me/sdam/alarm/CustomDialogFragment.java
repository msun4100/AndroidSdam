package kr.me.sdam.alarm;

import java.util.List;

import kr.me.sdam.MyApplication;
import kr.me.sdam.R;
import kr.me.sdam.alarm.MyAlarmAdapter.OnAdapterItemClickListener;
import kr.me.sdam.common.CommonResultItem;
import kr.me.sdam.database.DBManager;
import kr.me.sdam.detail.DetailActivity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class CustomDialogFragment extends DialogFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
//		Tracker t = ((MyApplication)((MainActivity)getActivity())
//				.getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
//		t.setScreenName("TabThreeFragment");
//		t.send(new HitBuilders.AppViewBuilder().build());
	}
//	@Override
//	public void onStart() {
//		super.onStart();
//		GoogleAnalytics.getInstance(getActivity()).reportActivityStart (getActivity());
//	}
//	@Override
//	public void onStop() {
//		super.onStop();
//		GoogleAnalytics.getInstance(getActivity()).reportActivityStop (getActivity());
//	}

	MyAlarmAdapter mAdapter;
	ListView listView;
	FrameLayout closeLayout;
	FrameLayout deleteLayout;

	// =======================
	// SimpleCursorAdapter mCursorAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);
		// getDialog().getWindow().setLayout(300,300);
		// getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
		// getDialog().getWindow().getAttributes().x = 120;
		getDialog().getWindow().getAttributes().y = -18;

		View view = inflater.inflate(R.layout.item_alarm_dialog_layout,
				container, false);

		listView = (ListView) view.findViewById(R.id.list_popup);
		// TextView headerView = new TextView(MyApplication.getContext());
		// headerView.setText("알림");
		// listView.addHeaderView(headerView, "알림data", true);
		// String[] from = {DBConstant.PushTable.FIELD_CASE,
		// DBConstant.PushTable.FIELD_NUM};
		// int[] to = { R.id.text_name, R.id.text_age };
		// mCursorAdapter = new SimpleCursorAdapter(getActivity(),
		// R.layout.item_alarm_content_layout, null, from, to, 0);//안씀
		mAdapter = new MyAlarmAdapter(getActivity());
		
		listView.setFocusable(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Object data = listView.getItemAtPosition(position);
				if (data instanceof String) {
					String text = (String) data;
					Toast.makeText(MyApplication.getContext(),
							"header click : " + text, Toast.LENGTH_SHORT)
							.show();
				} else {
					AlarmData item = (AlarmData) data;
					mAdapter.setDisplay(item);
					item.clicked = 1;
					DBManager.getInstance().updatePushData(item);
					Intent intent = new Intent(getActivity(),
							DetailActivity.class);
					intent.putExtra(CommonResultItem.REQUEST_NUMBER, ""
							+ item.num);
					// startActivityForResult(intent, 0);
					startActivity(intent);
					// Toast.makeText(MyApplication.getContext(),
					// "popup item click:" + position,
					// Toast.LENGTH_SHORT).show();
				}
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int currentPosition = position;
				AlarmData item = (AlarmData) listView
						.getItemAtPosition(position);
				DBManager.getInstance().deletePushData(item);
				mAdapter.remove(currentPosition);
				Toast.makeText(getActivity(),
						"" + currentPosition + " 알림이 삭제 되었습니다.",
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		// listView.setAdapter(mCursorAdapter);
		
		mAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
			
			@Override
			public void onAdapterItemClick(MyAlarmAdapter adapter, View view,
					AlarmData item, int type) {
				switch (type) {
				case AlarmData.TYPE_ICON:
//					Toast.makeText(getActivity(), "Alarm icon", Toast.LENGTH_SHORT).show();
					AlarmData mItem = (AlarmData) item;
					mAdapter.setDisplay(mItem);
					mItem.clicked = 1;
					DBManager.getInstance().updatePushData(mItem);
					break;
					
					default:
						break;
				}
			}
		});
		
		
		listView.setAdapter(mAdapter);
		
		closeLayout = (FrameLayout) view.findViewById(R.id.frame_popup_close);
		closeLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});
		deleteLayout = (FrameLayout) view.findViewById(R.id.frame_popup_delete);
		deleteLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlarmData dbData = new AlarmData();
				// dbData = listView.getItemAtPosition(position)
				int cnt = DBManager.getInstance().getPushList().size() - DBManager.getInstance().getPushCount();
				if(cnt > 0){
					Toast.makeText(getActivity(),""+(cnt)+"개 알림이 삭제됩니다.", Toast.LENGTH_SHORT) .show();
					DBManager.getInstance().deletePushData();	
				} else {
					Toast.makeText(getActivity()," 삭제할 알림이 없습니다.\n알림을 확인해 주세요.", Toast.LENGTH_SHORT) .show();
				}
				dismiss();
				// mAdapter.clear();
			}
		});

		// initData(); //여기에서 알람 데이터목록 추가함
		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		// getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
		// R.drawable.ic_launcher);
		// getDialog().setTitle("Custom Dialog");
	}

	// private List<AlarmData> items;
	boolean isFirst = false;

	private void initData() {
		// if(!isFirst){
		// mAdapter.clear();
		// // items = DBManager.getInstance().getPushList();
		// AlarmDataManager.getInstance().setItemDataList(DBManager.getInstance().getPushList());
		// for(AlarmData did : AlarmDataManager.getInstance().getItemDataList())
		// {
		// mAdapter.add(did);
		// }
		// isFirst=!isFirst;
		// }
		mAdapter.clear();
		List<AlarmData> items = DBManager.getInstance().getPushList();
		for (AlarmData did : items) {
			mAdapter.add(did);
		}
		// isFirst=!isFirst;
		// Cursor c = DBManager.getInstance().getPushCursor();
		// mCursorAdapter.changeCursor(c);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// mCursorAdapter.changeCursor(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		// if(this.getActivity() == ((MainActivity)getActivity())){
		// ((MainActivity)getActivity()).setCurrentMainAction(0);
		// ((MainActivity)getActivity()).setMenuImage();
		// } else if(this.getActivity() == ((MypageActivity)getActivity())){
		// ((MypageActivity)getActivity()).setCurrentMypageAction(3);
		// ((MypageActivity)getActivity()).setMenuImage();
		// }

	}
}
