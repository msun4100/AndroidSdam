package kr.me.sdam.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.alarm.CustomDialogFragment;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResult;
import kr.me.sdam.common.CommonResultAdapter;
import kr.me.sdam.common.PreLoadLayoutManager;
import kr.me.sdam.common.event.EventBus;
import kr.me.sdam.common.event.EventInfo;
import kr.me.sdam.database.DBManager;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.mypage.MypageActivity;
import kr.me.sdam.write.WriteActivity;
import okhttp3.Request;
//public class SearchActivity extends ActionBarActivity {
public class SearchActivity extends FragmentActivity {
		
	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}	
	private static final String TAG = SearchActivity.class.getSimpleName();
	public static final int RC_DETAIL = 104;
	MySearchAdapter mAdapter;
	RecyclerView recyclerView;
	PreLoadLayoutManager layoutManager;
	SwipeRefreshLayout refreshLayout;
	boolean isLast = false;
	Handler mHandler = new Handler(Looper.getMainLooper());

	EditText inputView;
	
	InputMethodManager imm;
	public Button[] buttons = new Button[4];
	ImageView writeView;
	View menuView; // 애니메이션 메뉴 뷰
	TextView alarmCountView;
	ImageView searchBg;

	String keyword = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		menuView = (View)findViewById(R.id.menu_anim);
		alarmCountView = (TextView)findViewById(R.id.text_alarm_count);
		buttons[0] = (Button) findViewById(R.id.btn_home);
		buttons[0].setOnClickListener(buttonListener);
		buttons[1] = (Button) findViewById(R.id.btn_search);
		buttons[1].setOnClickListener(buttonListener);
		writeView = (ImageView) findViewById(R.id.image_write);
		writeView.setOnClickListener(buttonListener);
		buttons[2] = (Button) findViewById(R.id.btn_alarm);
		buttons[2].setOnClickListener(buttonListener);
		buttons[3] = (Button) findViewById(R.id.btn_my);
		buttons[3].setOnClickListener(buttonListener);
		
		searchBg = (ImageView)findViewById(R.id.image_search_bg);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		inputView = (EditText)findViewById(R.id.edit_search);
		inputView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					keyword= inputView.getText().toString();
					if ((keyword != null && !keyword.equals(""))) {
						searchData(keyword);
					} else {
						Toast.makeText(SearchActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
					}
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return true;
			}
		});
		
		
		Button btn = (Button)findViewById(R.id.btn_action_bar_search);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				keyword= inputView.getText().toString();
				if ((keyword != null && !keyword.equals(""))) {
					searchData(keyword);
				} else {
					Toast.makeText(SearchActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
				}
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				
			}
		});

//		===========================
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
		refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
			@Override
			public void onRefresh() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						start = 1;
						keyword= inputView.getText().toString();
						if ((keyword != null && !keyword.equals(""))) {
							searchData(keyword);
						} else {
							Toast.makeText(SearchActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
						}
					}
				}, 1000);
			}
		});
		recyclerView = (RecyclerView)findViewById(R.id.recycler);
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
					setMenuInvisible();
				} else if (dy < 0) { // Scroll Up
					setMenuVisible();
				}


			}
		});
//		mAdapter = new CommonResultAdapter();
		mAdapter = new MySearchAdapter();
		mAdapter.setOnItemClickListener(new kr.me.sdam.common.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Log.e(TAG, "onItemClick: " );
				CommonResult item = mAdapter.getItem(position);
				Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
				intent.putExtra(CommonResult.REQUEST_NUMBER, ""+item.num);
//				startActivity(intent);
				startActivityForResult(intent, RC_DETAIL);
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
						b.putInt("curruenttab", 4);	//search
						ReportMenuDialogFragment f = new ReportMenuDialogFragment();
						f.setArguments(b);
						f.show(getSupportFragmentManager(), "dialog");
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
					b.putInt("activityType", 4); // 0==댓글, 1,2,3==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getSupportFragmentManager(), "deletereplydialog");
				}
			}
		});
		recyclerView.setAdapter(mAdapter);
//		layoutManager = new LinearLayoutManager(getActivity());
		layoutManager = new PreLoadLayoutManager(this);
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
		recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new BoardDecoration(getActivity()));


		Tracker t = ((MyApplication)getApplication()).getTracker
				(MyApplication.TrackerName.APP_TRACKER);
		t.enableAdvertisingIdCollection(true);
		t.setScreenName("SearchActivity");
		t.send(new HitBuilders.AppViewBuilder().build());

		EventBus.getInstance().register(this);
	}

	private void searchData(final String keyword){
		NetworkManager.getInstance().getSdamSearch(SearchActivity.this, keyword, start,
				new OnResultListener<SearchInfo>() {

					@Override
					public void onSuccess(Request request, SearchInfo result) {
						if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
							if(result.result != null){
								searchBg.setVisibility(View.GONE);
								recyclerView.setVisibility(View.VISIBLE);
								mAdapter.clear();
								mAdapter.setTotalCount(1000000);
								for (CommonResult r : result.result) {
									mAdapter.add(r);// 클리어하고 애드->리스트 안겹치게
								}
								start++;
								isMoreData = true;
							} else if(result.result == null){
								mAdapter.clear();
								recyclerView.setVisibility(View.GONE);
								searchBg.setVisibility(View.VISIBLE);
								Toast.makeText(SearchActivity.this, "\""+keyword+"\""+"에 대한 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
							}
						} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
							Toast.makeText(SearchActivity.this, "result.success:zero\nwork:"+result.work, Toast.LENGTH_SHORT).show();
						}
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
					}
				});
		dialog = new ProgressDialog(SearchActivity.this);
		dialog.setMessage("\""+keyword+"\""+"에 대한 담을 검색 중입니다...");
		dialog.setCancelable(false);
		dialog.show();
	}

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
			NetworkManager.getInstance().getSdamSearch(SearchActivity.this, keyword, start,
					new OnResultListener<SearchInfo>() {

						@Override
						public void onSuccess(Request request, SearchInfo result) {
							if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
								if(result.result != null){
									searchBg.setVisibility(View.GONE);
									recyclerView.setVisibility(View.VISIBLE);
									mAdapter.addAll(result.result);
									start++;
								} else if(result.result == null){
									Log.e(TAG, "onSuccess: result.result == null" );
									isMoreData = false;	// getMoreItem function variable
								}
							} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
								Log.e(TAG, "onSuccess: result.success == 0 "+ result.work );
							}
							refreshLayout.setRefreshing(false);
							dialog.dismiss();
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							refreshLayout.setRefreshing(false);
							dialog.dismiss();
						}
					});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		showLayout();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	GoodInfo goodInfo;
	GoodCancelInfo goodCancelInfo;
	public void setLikeDisplay(CommonResult item) {
		final CommonResult tempItem = item;
		if (tempItem.myGood == 0) {
			NetworkManager.getInstance().putSdamGood(SearchActivity.this, String.valueOf(item.num), item.writer,
					new OnResultListener<GoodInfo>() {

						@Override
						public void onSuccess(Request request, GoodInfo result) {
							goodInfo = result;
							if (goodInfo.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem); // 마이 굿 반대로, 카운트
								EventInfo eventInfo = new EventInfo(tempItem, EventInfo.MODE_UPDATE);
								postEvent(tempItem, EventInfo.MODE_UPDATE);
							} else {
//								Toast.makeText(getActivity(), "/good onSuccess:0", Toast.LENGTH_SHORT) .show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {

						}
					});

		} else if (tempItem.myGood >= 1) {
			NetworkManager.getInstance().putSdamGoodCancel(SearchActivity.this, String.valueOf(item.num),
					new OnResultListener<GoodCancelInfo>() {

						@Override
						public void onSuccess(Request request, GoodCancelInfo result) {
							goodCancelInfo = result;
							if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem);
								postEvent(tempItem, EventInfo.MODE_UPDATE);
							} else {
//								Toast.makeText(getActivity(), "/goodcancel onSuccess:zero", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(SearchActivity.this, "/goodcancel onFail" + code, Toast.LENGTH_SHORT).show();
						}
					});
		}

	}// setlikedisplay
	private void showLayout(){
		if(mAdapter.getItemCount()==0){
			recyclerView.setVisibility(View.GONE);
			searchBg.setVisibility(View.VISIBLE);
		} else {
			recyclerView.setVisibility(View.VISIBLE);
			searchBg.setVisibility(View.GONE);
		}
		currentSearchAction=1;
		setMenuImage();
		setAlarmCount();
	}
	private void initData() {

	}
	
	public void setAlarmCount(){
		int count  = DBManager.getInstance().getPushCount();
		if(count == 0){
			alarmCountView.setVisibility(View.GONE);
		}
		else {
			alarmCountView.setVisibility(View.VISIBLE);
		}
		alarmCountView.setText(" "+count);
	}
	
	public OnClickListener buttonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_home:
//				Toast.makeText(MainActivity.this, 
//						"btn_home click\n"
//						+"userId:"+PropertyManager.getInstance().getUserId()
//						+"\nlatitute:"+PropertyManager.getInstance().getLatitude()
//						+"\nlongitude:"+PropertyManager.getInstance().getLongitude()
//						+"\nDistance:"+PropertyManager.getInstance().getDistance(), Toast.LENGTH_SHORT).show();
				setCurrentSearchAction(0);
				setMenuImage();
				finish();
				break;
			case R.id.btn_search:
				setCurrentSearchAction(1);
				setMenuImage();
				break;
			case R.id.image_write:
				Intent writeIntent = new Intent(SearchActivity.this, WriteActivity.class);
				startActivity(writeIntent);
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);

				break;
			case R.id.btn_alarm:
				setCurrentSearchAction(2);
				setMenuImage();
				setAlarmCount();
				CustomDialogFragment f = new CustomDialogFragment();
				f.show(getSupportFragmentManager(), "dialog");
				break;
			case R.id.btn_my:
				setCurrentSearchAction(3);
				setMenuImage();
				Intent intent = new Intent(SearchActivity.this, MypageActivity.class);
				startActivity(intent);
				finish();
				break;
			default:
			}
		}
	};
	
	
	
	public void setMenuVisible() {
		Animation anim = AnimationUtils.loadAnimation(SearchActivity.this,
				R.anim.show_anim);
		if (menuView.getVisibility() == View.GONE) {
			setMenuImage();
			menuView.setVisibility(View.VISIBLE);
			menuView.startAnimation(anim);
		}
	}

	public void setMenuInvisible() {
		Animation anim = AnimationUtils.loadAnimation(SearchActivity.this,
				R.anim.hide_anim);
		if (menuView.getVisibility() == View.VISIBLE) {
			menuView.setVisibility(View.GONE);
			menuView.startAnimation(anim);
		}
	}
	
	
	public int currentSearchAction=0;
	public void setCurrentSearchAction(int num){
		this.currentSearchAction = num;
	}
	public void setMenuImage(){
		switch(currentSearchAction){
		case 0:
			buttons[0].setBackgroundResource(R.drawable.b_main_navigationbar_on_1home);
			buttons[1].setBackgroundResource(R.drawable.b_main_navigationbar_off_2search);
			buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_off_3notice);
			buttons[3].setBackgroundResource(R.drawable.b_main_navigationbar_off_4my);
			break;
		case 1:
			buttons[0].setBackgroundResource(R.drawable.b_main_navigationbar_off_1home);
			buttons[1].setBackgroundResource(R.drawable.b_main_navigationbar_on_2search);
			buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_off_3notice);
			buttons[3].setBackgroundResource(R.drawable.b_main_navigationbar_off_4my);
			break;
		case 2:
			buttons[0].setBackgroundResource(R.drawable.b_main_navigationbar_off_1home);
			buttons[1].setBackgroundResource(R.drawable.b_main_navigationbar_off_2search);
			buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_on_3notice);
			buttons[3].setBackgroundResource(R.drawable.b_main_navigationbar_off_4my);
			break;
		case 3:
			buttons[0].setBackgroundResource(R.drawable.b_main_navigationbar_off_1home);
			buttons[1].setBackgroundResource(R.drawable.b_main_navigationbar_off_2search);
			buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_off_3notice);
			buttons[3].setBackgroundResource(R.drawable.b_main_navigationbar_on_4my);
			break;
			default:
				break;
		}
		
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy: " );
		EventBus.getInstance().unregister(this);
		super.onDestroy();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle extraBundle;
		switch (requestCode) {
			case RC_DETAIL:
				if (resultCode == RESULT_OK) {
					extraBundle = data.getExtras();
					CommonResult cr = (CommonResult) extraBundle.getSerializable("_OBJ_");
					postEvent(cr, EventInfo.MODE_UPDATE);
				}
				break;
		}
	}
}
