package kr.me.sdam.mypage.favor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import kr.me.sdam.MainActivity;
import kr.me.sdam.MyApplication;
import kr.me.sdam.MyConfig;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResult;
import kr.me.sdam.common.CommonResultAdapter;
import kr.me.sdam.common.PreLoadLayoutManager;
import kr.me.sdam.common.event.EventBus;
import kr.me.sdam.common.event.EventInfo;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.mypage.MypageActivity;
import kr.me.sdam.mypage.PagerFragment;
import okhttp3.Request;

public class FavorFragment extends PagerFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((MyApplication)((MypageActivity)getActivity ())
				.getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("FavorFragment");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	ActionBar actionBar;

	private static final String TAG = FavorFragment.class.getSimpleName();
	public static final int RC_DETAIL = 106;

	RecyclerView recyclerView;
	FavorAdapter mAdapter;
	//	LinearLayoutManager layoutManager;
	PreLoadLayoutManager layoutManager;
	SwipeRefreshLayout refreshLayout;
	boolean isLast = false;
	Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_my_favor, container, false);
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
					((MypageActivity) getActivity()).setMenuInvisible();
				} else if (dy < 0) { // Scroll Up
					((MypageActivity) getActivity()).setMenuVisible();
				}
			}
		});
//		mAdapter = new CommonResultAdapter();
		mAdapter = new FavorAdapter();
		mAdapter.setOnItemClickListener(new kr.me.sdam.common.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Log.e(TAG, "onItemClick: " );
				CommonResult item = mAdapter.getItem(position);
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(CommonResult.REQUEST_NUMBER, ""+item.num);
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
						b.putInt("curruenttab", 6);
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
		mAdapter.setOnItemLongClickListener(new kr.me.sdam.common.OnItemLongClickListener() {
			@Override
			public void onItemLongClick(View view, int position) {
				CommonResult longClickedItem = mAdapter.getItem(position);
				Log.e(TAG, "onItemLongClick: " + longClickedItem);
				if(longClickedItem.writer.equals( PropertyManager.getInstance().getUserId() )){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", longClickedItem);
					b.putSerializable("deletedadapter", mAdapter);
					b.putInt("responseNum", longClickedItem.num);
					b.putInt("activityType", 6); // 0==댓글, 1,2,3==탭게시물
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
		NetworkManager.getInstance().getSdamMyFavor(getActivity(),
				start, new OnResultListener<Favor1Info>() {
					@Override
					public void onSuccess(Request request, Favor1Info result) {
						if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
							if (result.result != null) {
								mAdapter.clear();
								mAdapter.setTotalCount(100000);
								mAdapter.addAll(result.result);
								start++;
								isMoreData = true;	// getMoreItem function variable
							} else if (result.result == null) {
								Log.e(TAG, "onSuccess: result.result == null");
							}
						} else if (result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO) {
							Log.e(TAG, "onSuccess: result.success == 0 " + result.work);
						}
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(getActivity(), "서버에 연결할 수 없습니다.#" + code, Toast.LENGTH_SHORT).show();
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
					}
				});
		dialog = new ProgressDialog(getActivity());
		dialog.setMessage("데이터 로딩중..");
		dialog.show();
	}

	public ActionBar getActionBar() {
		if (actionBar == null) {
			actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		}
		return actionBar;
	}

	boolean isMoreData = true;
	ProgressDialog dialog = null;
	private static final int DISPLAY_NUM = MyConfig.DISPLAY_NUM;
	private int start=1;
	private String reqDate = null;

	private void getMoreItem(){
		Log.e(TAG, "getMoreItem: page: " +start);
		if (isMoreData == false) {
			Log.e(TAG, "getMoreItem: has no more items" );
			return;
		}
		if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount() ) {
			NetworkManager.getInstance().getSdamMyFavor(getActivity(),
					start, new OnResultListener<Favor1Info>() {
						@Override
						public void onSuccess(Request request, Favor1Info result) {
							if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
								if (result.result != null) {
									mAdapter.addAll(result.result);
									start++;
								} else if (result.result == null) {
									Log.e(TAG, "onSuccess: result.result == null");
									isMoreData = false;
								}
							} else if (result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO) {
								Log.e(TAG, "onSuccess: result.success == 0 " + result.work);
							}
							refreshLayout.setRefreshing(false);
							dialog.dismiss();
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다.#" + code, Toast.LENGTH_SHORT).show();
							refreshLayout.setRefreshing(false);
							dialog.dismiss();
						}
					});
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("데이터 로딩중..");
			dialog.show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setHasOptionsMenu(true);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Drawable d = getResources().getDrawable(R.drawable.f_my_actionbar);
		actionBar.setBackgroundDrawable(d);
		actionBar.setTitle("");
//		=================


	}

	@Override
	public void onPageCurrent() {
		super.onPageCurrent();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			((MypageActivity)getActivity()).buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_off_3notice);
			((MypageActivity)getActivity()).buttons[3].setBackgroundResource(R.drawable.b_main_navigationbar_on_4my);
		}

	}// setUser~

	
	MenuItem menuSetting;
	ImageView imageSet;
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.favor, menu);
		
		menuSetting = menu.findItem(R.id.my_setting);
		imageSet = new ImageView(getActivity());
		imageSet.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		imageSet.setPadding(16, 0, 16, 0);
		imageSet.setImageResource(R.drawable.f_my_actionbar_icon_settings_selector);
		imageSet.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MypageActivity)getActivity()).toggle();
			}
		});
		menuSetting.setActionView(imageSet);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

//		if (id == R.id.my_setting) {
//			((MypageActivity) getActivity()).toggle();
//			return true;
//		}

		if (id == android.R.id.home) {
			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			((MypageActivity) getActivity()).overridePendingTransition(
					R.anim.slide_right_in, R.anim.slide_right_out);
			((MypageActivity) getActivity()).finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private GoodInfo goodInfo;
	private GoodCancelInfo goodCancelInfo;
	public void setLikeDisplay(CommonResult item) {
		final CommonResult tempItem = item;
		
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
								postEvent(tempItem, EventInfo.MODE_UPDATE);
							} else {
//								Toast.makeText(getActivity(), "/good onSuccess:0", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다."+code , Toast.LENGTH_SHORT).show();
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
//								Toast.makeText( getActivity(), "/goodcancel onSuccess:0" , Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다."+code , Toast.LENGTH_SHORT).show();
						}
					});
		}

	}// setlikedisplay

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

}
