package kr.me.sdam.tabtwo;

import java.util.List;

import kr.me.sdam.MainActivity;
import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PagerFragment;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResultItem;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.dialogs.WaitingDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.tabtwo.TabTwoAdapter.OnAdapterItemClickListener;
import okhttp3.Request;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class TabTwoFragment extends PagerFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((MyApplication)((MainActivity)getActivity())
				.getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("TabTwoFragment");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(getActivity()).reportActivityStart (getActivity());
	}
	@Override
	public void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(getActivity()).reportActivityStop (getActivity());
	}

	private static final String TAG = TabTwoFragment.class.getSimpleName();
	Handler mHandler = new Handler();
	long startTime;
	
	ListView listView;
	TabTwoAdapter mAdapter;
	GoodInfo goodInfo;
	GoodCancelInfo goodCancelInfo;
	int userId=0;
	
	private int lastPosition;
	private int requestPage = 1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_tab_two, container, false);
		listView = (ListView) view.findViewById(R.id.list_tabtwo);
		mAdapter = new TabTwoAdapter(getActivity());
		listView.setAdapter(mAdapter);
		mAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {

			@Override
			public void onAdapterItemClick(TabTwoAdapter adapter, View view,
					TabTwoResult item, int type) {
				switch (type) {
				case CommonResultItem.TYPE_INVALID:
					Toast.makeText(getActivity(), "INVALID TYPE", Toast.LENGTH_SHORT).show();
					break;
				case CommonResultItem.TYPE_DISTANCE:
					break;
				case CommonResultItem.TYPE_REPORT:
					Bundle b = new Bundle();
					b.putSerializable("reporteditem", item);
					b.putSerializable("reportedadapter", adapter);
					b.putInt("curruenttab", 2);
					ReportMenuDialogFragment f = new ReportMenuDialogFragment();
					f.setArguments(b);
					f.show(getFragmentManager(), "dialog");
					break;
				case CommonResultItem.TYPE_REPLY:
					break;
				case CommonResultItem.TYPE_LIKE:
					setLikeDisplay(item);
					break;
				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object data = listView.getItemAtPosition(position);
				TabTwoResult item = (TabTwoResult) data;
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(CommonResultItem.REQUEST_NUMBER, ""+item.num);
				// Bundle b = new Bundle();
				// b.putString("backgroundId", ""+item.backgroundId);
				// b.putString("content", ""+item.content);
//				intent.putExtra(TAB_TWO_ITEM, item);
				// // intent.putExtras(new Bundle());
//				startActivityForResult(intent, 0);
				startActivity(intent);
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TabTwoResult longClickedItem = (TabTwoResult)listView.getItemAtPosition(position);
				if(longClickedItem.writer.equals(PropertyManager.getInstance().getUserId())){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", longClickedItem);
					b.putSerializable("deletedadapter", mAdapter);
					b.putInt("responseNum", longClickedItem.num);
					b.putInt("activityType", 2); // 0==댓글, 1,2,3==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getActivity().getSupportFragmentManager(), "deletereplydialog");	
				}
				return true;
			}
		});
		listView.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int currentPosition = view.getFirstVisiblePosition();
				if( currentPosition > lastPosition){
					((MainActivity) getActivity()).setMenuInvisibility();
				}
				if(currentPosition < lastPosition){
					((MainActivity) getActivity()).setMenuVisibility();
				}
				lastPosition=currentPosition;
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
		});
	
//		initData();
		return view;
	}
	private void initData() {
		List<TabTwoResult> items = TabTwoDataManager.getInstance()
				.getTabTwoResultList();
		for (TabTwoResult id : items) {
			mAdapter.add(id);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
//		requestPage=1;
		NetworkManager.getInstance().getSdamIssue(getActivity(), 
				String.valueOf(requestPage),
				new OnResultListener<TabTwoInfo>() {

					@Override
					public void onSuccess(Request request, TabTwoInfo result) {
//						listView.setAdapter(mAdapter);
						if(result.success==CommonInfo.COMMON_INFO_SUCCESS){
							if(result.result != null){
								mAdapter.clear();
								mAdapter.setTotalCount(1000);
		//**********Clear()할지 말지******
								for (TabTwoResult r : result.result) {
									mAdapter.add(r);
//									NewTabTwoDataManager.getInstance() .addToTabTwoDataManagerList(r);
								}
								if(lastPosition != 0){
									listView.setSelection(lastPosition);	
								}
//								requestPage++;
							} else if(result.result == null){
//								Toast.makeText(getActivity(), "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
								Log.e(TAG, "onSuccess: result.result == null");
							}
						} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
							Log.e(TAG, "onSuccess: result.success == 0");
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다. " + result.work , Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),
									"issue/# : Unexpected Network Error..", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(getActivity(), TAG+ "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
					}
				});
	}

	@Override
	public void onPageCurrent() {
		super.onPageCurrent();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			SlidingMenu sm = ((MainActivity) getActivity()).getSlidingMenu();
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			((MainActivity)getActivity()).buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_off_3notice);
			((MainActivity)getActivity()).buttons[0].setBackgroundResource(R.drawable.b_main_navigationbar_on_1home);
		}

	}// setUser~
	
	public void setLikeDisplay(TabTwoResult item){
		final TabTwoResult tempItem = item;
		if (tempItem.myGood == 0 ) {
			NetworkManager.getInstance().putSdamGood(getActivity(), 
					String.valueOf(item.num),
					item.writer,
					
					new OnResultListener<GoodInfo>() {

						@Override
						public void onSuccess(Request request, GoodInfo result) {
							goodInfo = result;
							if (goodInfo.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem); //마이 굿 반대로, 카운트 증가/증감
							} else {
//								Toast.makeText(getActivity(), "/good onSuccess:0", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {

						}
					});

		} else if(tempItem.myGood >= 1){
			NetworkManager.getInstance().putSdamGoodCancel(getActivity(), String.valueOf(item.num),
					new OnResultListener<GoodCancelInfo>() {

						@Override
						public void onSuccess(Request request, GoodCancelInfo result) {
							goodCancelInfo = result;
							if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem);
							} else {
//								Toast.makeText( getActivity(), "/goodcancel onSuccess:0" , Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {

						}
					});
		}

	}//setLikeDisplay


}
