package kr.me.sdam;

import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.alarm.CustomDialogFragment;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResult;
import kr.me.sdam.common.event.EventBus;
import kr.me.sdam.common.event.EventInfo;
import kr.me.sdam.database.DBManager;
import kr.me.sdam.dialogs.UsingLocationDialogFragment;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.mypage.MypageActivity;
import kr.me.sdam.search.SearchActivity;
import kr.me.sdam.tabone.TabOneFragment;
import kr.me.sdam.tabthree.TabThreeFragment;
import kr.me.sdam.tabtwo.TabTwoFragment;
import kr.me.sdam.write.WriteActivity;
import okhttp3.Request;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements
		MenuFragment.MenuCallback {
	private static final String TAG = MainActivity.class.getSimpleName();
	public static final String TAG_TAB_ONE="tab1";
	public static final String TAG_TAB_TWO="tab2";
	public static final String TAG_TAB_THREE="tab3";
	public static final int MESSAGE_TIME_OUT_BACK_KEY = 0;
	public static final int TIME_BACK_KEY = 2000;
	public static final int MENU_ANIM_SIZE = 5;
	public static final int COMMON_LIST_ITEM_SIZE = 20;
	
	View menuView; // 애니메이션 메뉴 뷰
	public TabHost tabHost;
	ViewPager pager;
	TabsAdapter mAdapter;
	ActionBar actionBar;
	ImageView writeView; // 글쓰기 이미지 뷰
	public Button[] buttons = new Button[MENU_ANIM_SIZE];
	// ==============
	public SlidingMenu sm;
	private static final String TAG_MAIN = "main";
	private String currentTag;
	// ==============
	public TextView alarmCountView;
	
//	public WaitingDialogFragment wf;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.fragment_sliding_container);
		if (savedInstanceState == null) {
			// getSupportFragmentManager().beginTransaction()
			// .add(R.id.container, new TabOneFragment()).commit();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container_slide, new MenuFragment()).commit();
			currentTag = TAG_MAIN;
		} else {
			String tag = savedInstanceState.getString("tag");
			currentTag = TAG_MAIN;
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("");
		
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
		
		// ============================================
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.b_main_actionbar_slide_selector);
		sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);
		// sm.setSecondaryMenu(R.layout.secondary_menu);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setFadeDegree(0.5f);
		sm.setOnClosedListener(smClosedListener);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);	
//		setSlidingActionBarEnabled(false);
		
		menuView = (View) findViewById(R.id.menu_anim);
		alarmCountView = (TextView)findViewById(R.id.text_alarm_count);
		ImageView iv1 = new ImageView(this);
		iv1.setImageResource(R.drawable.tab_one_selector);
		ImageView iv2 = new ImageView(this);
		iv2.setImageResource(R.drawable.tab_two_selector);
		ImageView iv3 = new ImageView(this);
		iv3.setImageResource(R.drawable.tab_three_selector);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOffscreenPageLimit(2);
		mAdapter = new TabsAdapter(this, getSupportFragmentManager(), tabHost, pager);
		mAdapter.addTab(tabHost.newTabSpec("tab1").setIndicator(iv1), TabOneFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab2").setIndicator(iv2), TabTwoFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab3").setIndicator(iv3), TabThreeFragment.class, null);
		mAdapter.setOnTabChangedListener(tabChangeListener);
//		=======google analitics==
		Tracker t = ((MyApplication)getApplication()).getTracker
				(MyApplication.TrackerName.APP_TRACKER);
		t.enableAdvertisingIdCollection(true);
		t.setScreenName("MainActivity");
		t.send(new HitBuilders.AppViewBuilder().build());
//		====================
	}//oncreate
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
//	LocationManager mLM;
//	String mProvider;
	
	private void initData() {
		currentMainAction=0;
		setMenuImage();
		setAlarmCount();
		//|| !mLM.isProviderEnabled(mProvider))
		if(PropertyManager.getInstance().getUsingLocation() == 0 ) {
			UsingLocationDialogFragment f = new UsingLocationDialogFragment();
			f.show(getFragmentManager(), "usinglocationdialog");
		}
	}
	
	public void setAlarmCount(){
		int count  = DBManager.getInstance().getPushCount();
		if(count == 0){
			alarmCountView.setVisibility(View.GONE);
		}
		else {
			alarmCountView.setVisibility(View.VISIBLE);
		}
		if( count < 10){
			alarmCountView.setText(" "+count);
		} else {
			alarmCountView.setText(""+count);	
		}
		
	}
	
	public OnTabChangeListener tabChangeListener = new OnTabChangeListener() {
		
		@Override
		public void onTabChanged(String tabId) {
			switch(tabId){
			case "tab1":
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setTitle("");
				sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				break;
			case "tab2":
				getSupportActionBar().setDisplayHomeAsUpEnabled(false);
				getSupportActionBar().setTitle("");
				sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				break;
			case "tab3":
				getSupportActionBar().setDisplayHomeAsUpEnabled(false);
				getSupportActionBar().setTitle("");
				sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				break;
			}
		}
	};
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
				currentMainAction=0;
				setMenuImage();
				gotoMainActivity(0);
				break;
			case R.id.btn_search:
				currentMainAction=1;
				setMenuImage();
				gotoSearchActivity();
//				Intent widgetIntent = new Intent(MainActivity.this, WidgetUpdateService.class );
//				startService(widgetIntent);
				break;
			case R.id.image_write:
				gotoWriteActivity();
				break;
			case R.id.btn_alarm:
				currentMainAction=2;
				setMenuImage();
				setAlarmCount();
				CustomDialogFragment f = new CustomDialogFragment();
				f.show(getSupportFragmentManager(), "dialog");
//				MyPopup popup = new MyPopup(MainActivity.this);
//				popup.setOnPopupListener(new OnPopupListener() {
//
//					@Override
//					public void onButtonClick(int index) {
//						if(index==0){
//							Toast.makeText(MainActivity.this, "popup image click",
//									Toast.LENGTH_SHORT).show();	
//						} else if(index == 1){
//							Toast.makeText(MainActivity.this, "popup layout click",
//									Toast.LENGTH_SHORT).show();
//						}
//						
//					}
//				});
//				
//				popup.setFocusable(true);
////				popup.showAsDropDown(v,-300,0); //G4 
//				popup.showAsDropDown(v,0,0);  

				break;
			case R.id.btn_my:
				currentMainAction=3;
				setMenuImage();
				Intent intent = new Intent(MainActivity.this, MypageActivity.class);
				startActivity(intent);
				break;
			default:
			}
		}
	};
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tag", currentTag);	//기존코드
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			toggle();
			return true;
		}
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void setMenuVisible() {
		Animation anim = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.show_anim);
		if (menuView.getVisibility() == View.GONE) {
			setMenuImage();
			menuView.setVisibility(View.VISIBLE);
			menuView.startAnimation(anim);
		}
	}

	public void setMenuInvisible() {
		Animation anim = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.hide_anim);
		if (menuView.getVisibility() == View.VISIBLE) {
			menuView.setVisibility(View.GONE);
			menuView.startAnimation(anim);
		}
	}

	@Override
	public void selectMenu(int menuId) {
		switch (menuId) {
		case MenuFragment.MENU_MAIN:
			if (currentTag != TAG_MAIN) {
				// getSupportFragmentManager().popBackStack(null,
				// FragmentManager.POP_BACK_STACK_INCLUSIVE);
				tabHost.setCurrentTab(0);
				currentTag = TAG_MAIN;
			}
			break;
		}
		Toast.makeText(MainActivity.this, "showContent(", Toast.LENGTH_SHORT).show();	
		showContent();
	}// SlidingMenu

	public void gotoSearchActivity() {
		Intent searchIntent = new Intent(MainActivity.this,
				SearchActivity.class);
		startActivity(searchIntent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
	}

	public void gotoWriteActivity() {
		Intent intent = new Intent(MainActivity.this, WriteActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);

	}

	public void gotoMainActivity(int tabNum) {
		// 홈버튼 눌렸을 때 메인으로 돌아가기
		// if(is메인액티비티)return;
		Intent intent = new Intent(MainActivity.this, MainActivity.class);
		tabHost.setCurrentTab(tabNum);
		startActivity(intent);
		finish();

	}
//	========BackPress======

	boolean isBackPressed = false;
	Handler mBackHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case MESSAGE_TIME_OUT_BACK_KEY:
				isBackPressed = false;
				break;
			}
		}
	};
	
	
	
	@Override
	public void onBackPressed() {
		if( !isBackPressed ){
			isBackPressed = true;
			Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
			mBackHandler.sendEmptyMessageDelayed(MESSAGE_TIME_OUT_BACK_KEY, TIME_BACK_KEY);
		} else {
			mBackHandler.removeMessages(MESSAGE_TIME_OUT_BACK_KEY);
			super.onBackPressed();
		}
	}
	public static boolean isTabChangeEnable=false;
	public static int tabNum=0;
	@Override
	protected void onResume() {
		super.onResume();
		if(isTabChangeEnable){
			tabHost.setCurrentTab(tabNum);
			isTabChangeEnable = false;
		}
		initData();
	}
	
	public static void setResumeTabNum(int num){ //호출하는 액티비티에서 보고싶은 Tab # 를 넣어서 호출하면 onResume 시 적용
		isTabChangeEnable = true;
		tabNum = num;
	}
	
	public static boolean afterWriting = false;
	public static void setAfterWriting(boolean bool){
		afterWriting = bool;
	}
	public static boolean getAfterWriting(){
		return afterWriting;
	}
	
	Handler distanceHandler = new Handler();
	public OnClosedListener smClosedListener = new OnClosedListener() {
		
		@Override
		public void onClosed() {
			final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
//            dialog.setIcon(R.drawable.ic_launcher);
//            dialog.setTitle("progress...");
			if(PropertyManager.getInstance().getDistance() == 200){
				dialog.setMessage("담너머 주변담을 불러옵니다...");
			} else {
				dialog.setMessage(""+PropertyManager.getInstance().getDistance()+"km"+""+" 이내의 주변담을 불러옵니다...");	
			}
            
            dialog.setCancelable(false);
            dialog.show();
			NetworkManager.getInstance().putSdamDistance(MainActivity.this,
					PropertyManager.getInstance().getDistance(),
					new OnResultListener<DistanceInfo>() {
						
						@Override
						public void onSuccess(Request request, DistanceInfo result) {
							if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
//								Toast.makeText(MainActivity.this, "/distance onSuccess:1\nwork:"+result.work, Toast.LENGTH_SHORT).show();
								distanceHandler.postDelayed(
										new Runnable() {

											@Override
											public void run() {
												Intent intent = new Intent(MainActivity.this, MainActivity.class);
												startActivity(intent);
												overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
												finish();
												dialog.dismiss();
											}

										}, 1000);
//								onResumeFragments();
//								tabHost.setCurrentTab(0);
//								runOnUiThread(nextAction);
								
								
							} else if(result.success ==CommonInfo.COMMON_INFO_SUCCESS_ZERO){
								Toast.makeText(MainActivity.this, "연결에 실패했습니다."+result.work, Toast.LENGTH_SHORT).show();
								dialog.setCancelable(true);					
								dialog.dismiss();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(MainActivity.this, "연결에 실패했습니다.#"+code, Toast.LENGTH_SHORT).show();
							dialog.setCancelable(true);
							dialog.dismiss();
						}

					});
		}
	};
	
	@Override
	public void onResumeFragments() {
		super.onResumeFragments();
//		((TabOneFragment)tabHost.getCurrentTab()).getTabOneAdapter().clear();
//		((new TabOneFragment())).getTabOneAdapter().notifyDataSetChanged();
	}
	Runnable nextAction = new Runnable() {
		
		@Override
		public void run() {
			Toast.makeText(MainActivity.this, "설정한 거리값의 주변담을 불러옴", Toast.LENGTH_SHORT).show();
//			tabHost.setCurrentTab(0);
//			listView.setSelection(0);
		}
	};
	
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		NetworkManager.getInstance().putSdamExit(MainActivity.this, new OnResultListener<GoodInfo>() {
//
//			@Override
//			public void onSuccess(GoodInfo result) {
////				Toast.makeText(MainActivity.this, "onDestroy()-/exit", Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onFail(int code) {
//
//			}
//		});
//	}
	public int currentMainAction=0;
	public void setCurrentMainAction(int num){
		this.currentMainAction = num;
	}
	public int getCurrentMainAction(){
		return currentMainAction;
	}
	public void setMenuImage(){
		switch(currentMainAction){
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null){
			Log.e(TAG, "onActivityResult: 1" +data.toString() );
		} else {
			Log.e(TAG, "onActivityResult: 2" );
		}
		Bundle extraBundle;
		switch (requestCode) {
			case TabOneFragment.RC_DETAIL:
			case TabTwoFragment.RC_DETAIL:
			case TabThreeFragment.RC_DETAIL:
				if (resultCode == RESULT_OK) {
					extraBundle = data.getExtras();
					CommonResult cr = (CommonResult) extraBundle.getSerializable("_OBJ_");
					EventInfo eventInfo = new EventInfo(cr, EventInfo.MODE_UPDATE);
					EventBus.getInstance().post(eventInfo);
				}
				break;
		}
	}
}//main
