package kr.me.sdam.tabone;

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
import kr.me.sdam.tabone.TabOneAdapter.OnAdapterItemClickListener;
import okhttp3.Request;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
//kr.me.sdam.tabone.TabOneFragment
public class TabOneFragment extends PagerFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((MyApplication)((MainActivity)getActivity())
				.getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("TabOneFragment");
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
	
	private static final String TAG = TabOneFragment.class.getSimpleName();
	Handler mHandler = new Handler();
	long startTime;
	public static final String TAB_ONE_ITEM = "tabOneItem";
	ActionBar actionBar;
	ListView listView;
	TabOneAdapter mAdapter;
	boolean isSelected = false;

	GoodInfo goodInfo;
	GoodCancelInfo goodCancelInfo;
	int userId = 0;
	int lastPostion;
	private int requestNum = 1;
	private int currentRequestNum = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_tab_one, container, false);

		listView = (ListView) view.findViewById(R.id.list_tabone);
		mAdapter = new TabOneAdapter(getActivity());
		listView.setAdapter(mAdapter);
		mAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {

			@Override
			public void onAdapterItemClick(TabOneAdapter adapter, View view,
					TabOneResult item, int type) {

				switch (type) {
				case CommonResultItem.TYPE_INVALID:
					Toast.makeText(getActivity(), "INVALID TYPE",
							Toast.LENGTH_SHORT).show();
					break;
				case CommonResultItem.TYPE_DISTANCE:
					break;
				case CommonResultItem.TYPE_REPORT:
					Bundle b = new Bundle();
					b.putSerializable("reporteditem", item);
					b.putSerializable("reportedadapter", adapter);
					b.putInt("curruenttab", 1);
					ReportMenuDialogFragment f = new ReportMenuDialogFragment();
					f.setArguments(b);
					f.show(getFragmentManager(), "dialog");
					//======
//					ReportManager.getInstance().showReportMenu(adapter, item, ReportManager.TAB_TYPE_ONE);
					break;
				case CommonResultItem.TYPE_REPLY:
					break;
				case CommonResultItem.TYPE_LIKE:
					setLikeDisplay(item);
					break;
				}
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TabOneResult longClickedItem = (TabOneResult)listView.getItemAtPosition(position);
				if(longClickedItem.writer.equals(PropertyManager.getInstance().getUserId() )){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", longClickedItem);
					b.putSerializable("deletedadapter", mAdapter);
					b.putInt("responseNum", longClickedItem.num);
					b.putInt("activityType", 1); // 0==댓글, 1,2,3==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getActivity().getSupportFragmentManager(), "deletereplydialog");	
				}
				return true;
			}
		});
		
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int currentPostion = view.getFirstVisiblePosition();
				if (currentPostion > lastPostion) {
					((MainActivity) getActivity()).setMenuInvisibility();
				}
				if (currentPostion < lastPostion) {
					((MainActivity) getActivity()).setMenuVisibility();
				}
				lastPostion = currentPostion;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object data = listView.getItemAtPosition(position);
				TabOneResult item = (TabOneResult) data;
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(CommonResultItem.REQUEST_NUMBER, "" + item.num);
				startActivity(intent);
			}
		});

		// =====================
		return view;
	}

	private void initData() {
		List<TabOneResult> items = TabOneDataManager.getInstance()
				.getTabOneResultList();
		mAdapter.clear();
		for (TabOneResult id : items) {
			mAdapter.add(id);
		}
	}

	private void initDummyData() {
		List<TabOneResult> items = TabOneDataManager.getInstance()
				.getTabOneDummyList();
		mAdapter.clear();
		for (TabOneResult id : items) {
			mAdapter.add(id);
		}
	}

	public ActionBar getActionBar() {
		if (actionBar == null) {
			actionBar = ((ActionBarActivity) getActivity())
					.getSupportActionBar();
		}
		return actionBar;
	}

	WaitingDialogFragment wf;
	@Override
	public void onResume() {
		super.onResume();

		wf = new WaitingDialogFragment();
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(null);

		wf.setCancelable(false); // 딴영역 눌러도 안닫히게 하는 옵션
		wf.show(ft, "dialog");
		
//		((MainActivity) getActivity()).wf.show(ft, "dialog");
//		===================
		NetworkManager.getInstance().getSdamAround(getActivity(),
				String.valueOf(requestNum), new OnResultListener<TabOneInfo>() {
					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						initDummyData();
						Toast.makeText(getActivity(), TAG+ "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
						if(wf!=null){
							if(wf.isVisible()){
								wf.setCancelable(true);
								wf.dismiss();
							}
						}
					}

					@Override
					public void onSuccess(Request request, TabOneInfo result) {

						if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {

							if (result.result != null) {
								// **********Clear()할지 말지******
								listView.setAdapter(mAdapter);
								mAdapter.clear();
								mAdapter.setTotalCount(1000);
								for (TabOneResult r : result.result) {
									mAdapter.add(r);// 클리어하고 애드->리스트 안겹치게
								}
								if(lastPostion != 0){
									//새로고침한 경우 리스트 자리 맞추기 위해
									if(currentRequestNum != requestNum && (lastPostion<20) ){
										lastPostion=0;
										listView.setSelection(lastPostion);
									} else {//디폴트 리쥼 리스트 자리 맞추기
										listView.setSelection(lastPostion%MainActivity.COMMON_LIST_ITEM_SIZE);	
									}
									currentRequestNum = requestNum;
								}
							} else if (result.result == null) {
//								Toast.makeText( getActivity(), "Around - result:null " + result.result,
//										Toast.LENGTH_SHORT).show();
							}
						} else if (result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO) {
//							Toast.makeText(getActivity(),
//									"TabOne success:0 " + result.work, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),
									"around/# : Unexpected Network Error..",
									Toast.LENGTH_SHORT).show();
						}
						// ======dismiss()
						if(wf!=null){
							if(wf.isVisible()){
								wf.dismiss();
							}
						}
						
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
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			((MainActivity)getActivity()).buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_off_3notice);
			((MainActivity)getActivity()).buttons[0].setBackgroundResource(R.drawable.b_main_navigationbar_on_1home);
		}

	}// setUser~

	public void setLikeDisplay(TabOneResult item) {
		final TabOneResult tempItem = item;
		if (tempItem.myGood == 0) {
			NetworkManager.getInstance().putSdamGood(getActivity(),
					String.valueOf(item.num), item.writer,
					new OnResultListener<GoodInfo>() {

						@Override
						public void onSuccess(Request request, GoodInfo result) {
							goodInfo = result;
							if (goodInfo.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem); // 마이 굿 반대로, 카운트
								// 증가/증감
							} else {
//								Toast.makeText(getActivity(),
//										"/good onSuccess:0", Toast.LENGTH_SHORT)
//										.show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {

						}
					});

		} else if (tempItem.myGood >= 1) {
			NetworkManager.getInstance().putSdamGoodCancel(getActivity(),
					String.valueOf(item.num),
					new OnResultListener<GoodCancelInfo>() {

						@Override
						public void onSuccess(Request request, GoodCancelInfo result) {
							goodCancelInfo = result;
							if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem);
							} else {
//								Toast.makeText(getActivity(), "/goodcancel onSuccess:zero", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(getActivity(), TAG+" 서버에 연결할 수 없습니다. #" + code, Toast.LENGTH_SHORT).show();
						}
					});
		}

	}// setlikedisplay

	public TabOneAdapter getTabOneAdapter() {
		return mAdapter;
	}

}
