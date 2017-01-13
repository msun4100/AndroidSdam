package kr.me.sdam.tabthree;

import kr.me.sdam.MainActivity;
import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PagerFragment;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResult;
import kr.me.sdam.common.CommonResultAdapter;
import kr.me.sdam.common.OnItemClickListener;
import kr.me.sdam.common.OnItemLongClickListener;
import kr.me.sdam.common.PreLoadLayoutManager;
import kr.me.sdam.common.event.EventBus;
import kr.me.sdam.common.event.EventInfo;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.dialogs.WaitingDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import okhttp3.Request;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.otto.Subscribe;

public class TabThreeFragment extends PagerFragment {

	private static final String TAG = TabThreeFragment.class.getSimpleName();
	public static final int RC_DETAIL = 103;
	GoodInfo goodInfo;
	GoodCancelInfo goodCancelInfo;

	public int lastPosition=0;

	RecyclerView recyclerView;
//	CommonResultAdapter mAdapter;
	TabThreeAdapter mAdapter;
//	LinearLayoutManager layoutManager;
	PreLoadLayoutManager layoutManager;
	SwipeRefreshLayout refreshLayout;
	boolean isLast = false;
	Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((MyApplication)((MainActivity)getActivity()).getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.enableAdvertisingIdCollection(true);
		t.setScreenName("TabThreeFragment");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_tab_three, container, false);

		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
		refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
			@Override
			public void onRefresh() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						start = 1;
						initData();
					}
				}, 1000);
			}
		});
		recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
					getMoreItem();
				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int totalItemCount = mAdapter.getItemCount();
				int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
				if (totalItemCount > 0 && lastVisibleItemPosition != RecyclerView.NO_POSITION && (totalItemCount - 1 <= lastVisibleItemPosition)) {
					isLast = true;
				} else {
					isLast = false;
				}
				if (dy > 0) { // Scroll Down
					((MainActivity) getActivity()).setMenuInvisible();
				} else if (dy < 0) { // Scroll Up
					((MainActivity) getActivity()).setMenuVisible();
				}


			}
		});
//		mAdapter = new CommonResultAdapter();
		mAdapter = new TabThreeAdapter();
		mAdapter.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Log.e(TAG, "onItemClick: " );
				CommonResult item = mAdapter.getItem(position);
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(CommonResult.REQUEST_NUMBER, ""+item.num);
//				startActivity(intent);
				getActivity().startActivityForResult(intent, RC_DETAIL);
			}
		});
		mAdapter.setOnAdapterItemClickListener(new CommonResultAdapter.OnAdapterItemClickListener() {
			@Override
			public void onAdapterItemClick(CommonResultAdapter adapter, View view, int position, CommonResult item, int type) {
				switch (type) {
					case CommonResult.TYPE_INVALID:
						Log.e(TAG, "onAdapterItemClick: INVALID TYPE" );
						break;
					case CommonResult.TYPE_DISTANCE:
						break;
					case CommonResult.TYPE_REPORT:
						Log.e(TAG, "onAdapterItemClick: report click");
						Bundle b = new Bundle();
						b.putSerializable("reporteditem", item);
						b.putSerializable("reportedadapter", mAdapter);
						b.putInt("curruenttab", 3);
						ReportMenuDialogFragment f = new ReportMenuDialogFragment();
						f.setArguments(b);
						f.show(getFragmentManager(), "dialog");
						break;
					case CommonResult.TYPE_REPLY:
						break;
					case CommonResult.TYPE_LIKE:
						setLikeDisplay(item);
						break;
				}
			}
		});
		mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public void onItemLongClick(View view, int position) {
				CommonResult longClickedItem = mAdapter.getItem(position);
				Log.e(TAG, "onItemLongClick: " + longClickedItem);
				if(longClickedItem.writer.equals( PropertyManager.getInstance().getUserId() )){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", longClickedItem);
					b.putSerializable("deletedadapter", mAdapter);
					b.putInt("responseNum", longClickedItem.num);
					b.putInt("activityType", 3); // 0==댓글, 1,2,3==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getActivity().getSupportFragmentManager(), "deletereplydialog");
				}
			}
		});
		recyclerView.setAdapter(mAdapter);
//		layoutManager = new LinearLayoutManager(getActivity());
		layoutManager = new PreLoadLayoutManager(getActivity());
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
		recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new BoardDecoration(getActivity()));



		initData();

		return view;
	}

	private void initData() {

		NetworkManager.getInstance().getSdamNew(getActivity(),
				""+start,
				new OnResultListener<TabThreeInfo>() {

					@Override
					public void onSuccess(Request request, TabThreeInfo result) {
						if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
							if (result.result != null) {
								mAdapter.clear();
								mAdapter.setTotalCount(Integer.MAX_VALUE - DISPLAY_NUM);	// sdam 서버는 total 변수 안씀, DisplayNum을 빼서 오버플로 방지
								mAdapter.addAll(result.result);
								start++;
								isMoreData = true;	// getMoreItem function variable
							} else {
								Log.e(TAG, "onSuccess: result.result == null" );
							}
						} else if (result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO) {
							Log.e(TAG, "onSuccess: result.success == 0 "+ result.work );
						}
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText( getActivity(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
					}
				});
		dialog = new ProgressDialog(getActivity());
		dialog.setTitle("타이틀");
		dialog.setMessage("데이터 로딩중..");
		dialog.show();
	}	//init

	boolean isMoreData = true;
	ProgressDialog dialog = null;
	private static final int DISPLAY_NUM = 10;
	private int start=1;
	private String reqDate = null;

	private void getMoreItem(){
		Log.e(TAG, "getMoreItem: page: " +start);
		if (isMoreData == false) {
			Log.e(TAG, "getMoreItem: has no more items" );
			return;
		}
		if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount() ) {
            NetworkManager.getInstance().getSdamNew(getActivity(), "" + start,
					new OnResultListener<TabThreeInfo>() {
                        @Override
                        public void onSuccess(Request request, TabThreeInfo result) {
                            if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
                                if (result.result != null) {
                                    mAdapter.addAll(result.result);
                                    start++;
                                } else { // success == 1 && result == null
                                    Log.e(TAG, "onSuccess: result.result == null " + result.work );
									isMoreData = false;	// Never call this function
                                }
                            } else if (result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO) {
                                Log.e(TAG, "onSuccess: result.success == 0 "+ result.work );
                            }
                            refreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onFailure(Request request, int code, Throwable cause) {
                            Toast.makeText( getActivity(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
                            refreshLayout.setRefreshing(false);
                        }
                    });
		}
	}

//	WaitingDialogFragment wf3;
	@Override
	public void onResume() {
		super.onResume();
		if(MainActivity.getAfterWriting() == true){
			MainActivity.setAfterWriting(false);
			start = 1;
			initData();
		}
//        Log.e(TAG, "onResume: "+MyApplication.getCurrentTimeStampString() );
//        initData();
//		testSSL();
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

	}

	public void setLikeDisplay(CommonResult item) {
		final CommonResult tempItem = item;
		if (tempItem.myGood == 0 ) {
			NetworkManager.getInstance().putSdamGood(getActivity(), String.valueOf(item.num), item.writer,
					new OnResultListener<GoodInfo>() {

						@Override
						public void onSuccess(Request request, GoodInfo result) {
							goodInfo = result;
							if (goodInfo.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem); //마이 굿 반대로, 카운트 증가/증감
								postEvent(tempItem, EventInfo.MODE_UPDATE);
							} else {
								Log.e(TAG, "onSuccess: LIKE error" );
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Log.e(TAG, "onFailure: LIKE error" + cause );
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
								postEvent(tempItem, EventInfo.MODE_UPDATE);
							} else {
								Log.e(TAG, "onSuccess: DISLIKE error" );
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Log.e(TAG, "onFailure: DISLIKE error" + cause );
						}
					});
		}

	}// setlikedisplay
	private void testSSL(){
		NetworkManager.getInstance().testSSL(getActivity(), new NetworkManager.OnResultListener<String>() {
			@Override
			public void onSuccess(Request request, String result) {
				Log.e(TAG, "onSuccess: testSSL: "+result  );
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Log.e(TAG, "onFailure: testSSL: error" );
			}
		});
	}

	@Subscribe
	public void onEvent(EventInfo eventInfo){
		Log.e(TAG, "onEvent: "+eventInfo.mode );
		if(mAdapter == null || mAdapter.getItemCount() < 1) {
			return;
		}
		mAdapter.findOneAndModify(eventInfo.commonResult, eventInfo.mode);
	}

	private void postEvent(CommonResult commonResult, String mode){
		EventInfo eventInfo = new EventInfo(commonResult, mode);
		EventBus.getInstance().post(eventInfo);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle extraBundle;
		Log.i(TAG, "onActivityResult: " );
		if(data != null){
			Log.e(TAG, "onActivityResult: " +data.toString() );
		}
		switch (requestCode) {
			case RC_DETAIL:
				if (resultCode == getActivity().RESULT_OK) {
					extraBundle = data.getExtras();
					String next =  extraBundle.getString("_NEXT_");
				}
				break;
		}
	}
}
