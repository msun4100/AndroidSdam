package kr.me.sdam.search;

import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.R;
import kr.me.sdam.alarm.CustomDialogFragment;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResultItem;
import kr.me.sdam.database.DBManager;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.mypage.MypageActivity;
import kr.me.sdam.search.MySearchAdapter.OnAdapterItemClickListener;
import kr.me.sdam.write.WriteActivity;
import okhttp3.Request;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
		
	ActionBar actionBar;
	
	ListView listView;
	MySearchAdapter mAdapter;
	
	EditText inputView;
	
	InputMethodManager imm;
	private int searchCount = 1;
	public Button[] buttons = new Button[4];
	ImageView writeView;
	View menuView; // 애니메이션 메뉴 뷰
	TextView alarmCountView;
	ImageView searchBg;
	private int lastPosition;
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
//		actionBar = getSupportActionBar();
//		actionBar.setDisplayShowHomeEnabled(false);
////		actionBar.setIcon(R.drawable.ic_launcher);
//		actionBar.setDisplayHomeAsUpEnabled(false);
////		actionBar.setTitle("ActionBar Test");
////		actionBar.setSubtitle("test...");
//		actionBar.setDisplayShowTitleEnabled(false);
//		actionBar.setDisplayShowCustomEnabled(true);
//		actionBar.setCustomView(R.layout.item_search_actionbar_layout);
		
		inputView = (EditText)findViewById(R.id.edit_search);
		inputView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					//....
					final String keyword= inputView.getText().toString();
					if ((keyword != null && !keyword.equals(""))) {	
						final ProgressDialog dialog = new ProgressDialog(SearchActivity.this);
						dialog.setMessage("\""+keyword+"\""+"에 대한 담을 검색 중입니다...");
						dialog.setCancelable(false);
						dialog.show();
						NetworkManager.getInstance().getSdamSearch(SearchActivity.this, keyword, searchCount, 
								new OnResultListener<SearchInfo>() {
									
									@Override
									public void onSuccess(Request request, SearchInfo result) {
										if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
											if(result.result != null){
												searchBg.setVisibility(View.GONE);
												listView.setVisibility(View.VISIBLE);
												listView.setAdapter(mAdapter);
												mAdapter.clear();
												mAdapter.setTotalCount(100);
												for (SearchResult r : result.result) {
													mAdapter.add(r);// 클리어하고 애드->리스트 안겹치게
												}
											} else if(result.result == null){
												mAdapter.clear();
												listView.setVisibility(View.GONE);
												searchBg.setVisibility(View.VISIBLE);
												Toast.makeText(SearchActivity.this, "\""+keyword+"\""+"에 대한 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
											}
										} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
											Toast.makeText(SearchActivity.this, "result.success:zero\nwork:"+result.work, Toast.LENGTH_SHORT).show();
										}
										
										imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
										dialog.dismiss();
									}

									@Override
									public void onFailure(Request request, int code, Throwable cause) {
										imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
										dialog.dismiss();
									}
								});
					} else {
						Toast.makeText(SearchActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
		});
		
		
		Button btn = (Button)findViewById(R.id.btn_action_bar_search);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				
				final String keyword= inputView.getText().toString();
				if ((keyword != null && !keyword.equals(""))) {	
					final ProgressDialog dialog = new ProgressDialog(SearchActivity.this);
//					dialog.setIcon(R.drawable.a_launcher_1_icon_notification);
//					dialog.setTitle("Progress...");
					dialog.setMessage("\""+keyword+"\""+"에 대한 담을 검색 중입니다...");
					dialog.setCancelable(false);
					dialog.show();
					NetworkManager.getInstance().getSdamSearch(SearchActivity.this, keyword, searchCount, 
							new OnResultListener<SearchInfo>() {
								
								@Override
								public void onSuccess(Request request, SearchInfo result) {
									if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
										if(result.result != null){
											searchBg.setVisibility(View.GONE);
											listView.setVisibility(View.VISIBLE);
											listView.setAdapter(mAdapter);
											mAdapter.clear();
											mAdapter.setTotalCount(100);
											for (SearchResult r : result.result) {
												mAdapter.add(r);// 클리어하고 애드->리스트 안겹치게
											}
										} else if(result.result == null){
											mAdapter.clear();
											listView.setVisibility(View.GONE);
											searchBg.setVisibility(View.VISIBLE);
											
											Toast.makeText(SearchActivity.this, "\""+keyword+"\""+"에 대한 검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
										}
									} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
										Toast.makeText(SearchActivity.this, "result.success:zero\nwork:"+result.work, Toast.LENGTH_SHORT).show();
									}
									
									imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
									dialog.dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
									dialog.dismiss();
								}
							});
				} else {
					Toast.makeText(SearchActivity.this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
//		===============================
		listView = (ListView)findViewById(R.id.list_search);
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int currentPostion = view.getFirstVisiblePosition();
				if (currentPostion > lastPosition) {
					setMenuInvisibility();
				}
				if (currentPostion < lastPosition) {
					setMenuVisibility();
				}
				lastPosition = currentPostion;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

		});
		mAdapter = new MySearchAdapter(this);
		mAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
			
			@Override
			public void onAdapterItemClick(MySearchAdapter adapter, View view,
					SearchResult item, int type) {
				switch (type) {
				case CommonResultItem.TYPE_INVALID:
					Toast.makeText(SearchActivity.this, "INVALID TYPE", Toast.LENGTH_SHORT).show();
					break;
				case CommonResultItem.TYPE_DISTANCE:
					break;
				case CommonResultItem.TYPE_REPORT:
					Bundle b = new Bundle();
					b.putSerializable("reporteditem", item);
					b.putSerializable("reportedadapter", adapter);
					b.putInt("curruenttab", 4);
					ReportMenuDialogFragment f = new ReportMenuDialogFragment();
					f.setArguments(b);
//					f.show(getFragmentManager(), "dialog");
					f.show(getSupportFragmentManager(),  "dialog");
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
				SearchResult item = (SearchResult) data;
				Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
				intent.putExtra(CommonResultItem.REQUEST_NUMBER, ""+item.num);
				startActivity(intent);
			}
		});
		
		Tracker t = ((MyApplication)getApplication()).getTracker
				(MyApplication.TrackerName.APP_TRACKER);
		t.enableAdvertisingIdCollection(true);
		t.setScreenName("SearchActivity");
		t.send(new HitBuilders.AppViewBuilder().build());
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
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
	public void setLikeDisplay(SearchResult item) {
		final SearchResult tempItem = item;
		if (tempItem.myGood == 0) {
			NetworkManager.getInstance().putSdamGood(SearchActivity.this,
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
			NetworkManager.getInstance().putSdamGoodCancel(SearchActivity.this,
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
							Toast.makeText(SearchActivity.this, "/goodcancel onFail" + code, Toast.LENGTH_SHORT).show();
						}
					});
		}

	}// setlikedisplay
	
	private void initData() {
		if(mAdapter.getCount()==0){
			listView.setVisibility(View.GONE);
			searchBg.setVisibility(View.VISIBLE);
		} else {
			listView.setVisibility(View.VISIBLE);
			searchBg.setVisibility(View.GONE);
		}
		currentSearchAction=1;
		setMenuImage();
		setAlarmCount();
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
	
	
	
	public void setMenuVisibility() {
		Animation anim = AnimationUtils.loadAnimation(SearchActivity.this,
				R.anim.show_anim);
		if (menuView.getVisibility() == View.GONE) {
			setMenuImage();
			menuView.setVisibility(View.VISIBLE);
			menuView.startAnimation(anim);
		}
	}

	public void setMenuInvisibility() {
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
	
	
}
