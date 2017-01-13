package kr.me.sdam.mypage;

import kr.me.sdam.MainActivity;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.TabsAdapter;
import kr.me.sdam.alarm.CustomDialogFragment;
import kr.me.sdam.alarm.MyPopup;
import kr.me.sdam.alarm.MyPopup.OnPopupListener;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResult;
import kr.me.sdam.common.event.EventBus;
import kr.me.sdam.common.event.EventInfo;
import kr.me.sdam.database.DBManager;
import kr.me.sdam.dialogs.LockDialogFragment;
import kr.me.sdam.mypage.favor.FavorFragment;
import kr.me.sdam.mypage.mylist.MyListFragment;
import kr.me.sdam.search.SearchActivity;
import kr.me.sdam.tabone.TabOneFragment;
import kr.me.sdam.tabthree.TabThreeFragment;
import kr.me.sdam.tabtwo.TabTwoFragment;
import kr.me.sdam.write.WriteActivity;
import okhttp3.Request;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
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

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MypageActivity extends SlidingFragmentActivity implements SettingMenuFragment.MenuCallback {

	private static final String TAG = MypageActivity.class.getSimpleName();

	SwitchCompat sw;
	TabHost tabHost;
	ViewPager pager;
	TabsAdapter mAdapter;
	ActionBar actionBar;
	SlidingMenu sm;
	private static final String TAG_SETTING = "setting";
	// private static final String TAG_ONE = "one";
	// private static final String TAG_TWO = "two";
	// private static final String TAG_SETTING = "setting";
	private String currentTag;
	public Button[] buttons = new Button[MainActivity.MENU_ANIM_SIZE];
	View menuView;
	public ImageView writeView;
	public TextView alarmCountView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mypage);
		setBehindContentView(R.layout.setting_menu_container_layout);
		menuView = (View)findViewById(R.id.menu_anim);
		alarmCountView = (TextView)findViewById(R.id.text_alarm_count);
		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.setting_menu_container, new SettingMenuFragment())
					.commit();
			currentTag = TAG_SETTING;
			// currentTag = TAG_SETTING;
		} else {
			String tag = savedInstanceState.getString("tag");
			currentTag = TAG_SETTING;
			// if (tag.equals(TAG_MAIN)) {
			// currentTag = TAG_MAIN;
			// } else if (tag.equals(TAG_ONE)) {
			// currentTag = TAG_ONE;
			// } else if (tag.equals(TAG_TWO)) {
			// currentTag = TAG_TWO;
			// } else {
			// currentTag = TAG_MAIN;
			// }
		}
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
		
		sm = getSlidingMenu();
		sm.setMode(SlidingMenu.RIGHT);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_setting_offset);
		sm.setShadowWidthRes(R.dimen.shadow_setting_width);
		sm.setShadowDrawable(R.drawable.shadow_setting);
//		sm.setFadeDegree(0.5f);
		// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// setSlidingActionBarEnabled(false);
		sm.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
//				Toast.makeText(MypageActivity.this, "setting menu onclosed listener", Toast.LENGTH_SHORT).show();
				NetworkManager.getInstance().putSdamSet(MypageActivity.this,
						""+PropertyManager.getInstance().getMinAge(),
						""+PropertyManager.getInstance().getMaxAge(), 
						""+PropertyManager.getInstance().getDistance(), 
						new OnResultListener<SettingInfo>() {
							
							@Override
							public void onSuccess(Request request, SettingInfo result) {
								if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
									Toast.makeText(MypageActivity.this, "설정 값이 저장 되었습니다.", Toast.LENGTH_SHORT).show();
									Log.e(TAG, "onSuccess: set feed distance from user: " + PropertyManager.getInstance().getDistance() );
								} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
									Toast.makeText(MypageActivity.this, "서버에 연결할 수 없습니다."+result.work, Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(MypageActivity.this, "서버에 연결할 수 없습니다."+result.work, Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onFailure(Request request, int code, Throwable cause) {
								Toast.makeText(MypageActivity.this, "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
							}
						});
			}
		});
		// =================
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOffscreenPageLimit(1);
		ImageView iv1 = new ImageView(this);
		iv1.setImageResource(R.drawable.f_my_selector);
		ImageView iv2 = new ImageView(this);
		iv2.setImageResource(R.drawable.f_favor_selector);
		
		mAdapter = new TabsAdapter(this, getSupportFragmentManager(), tabHost, pager);
		mAdapter.addTab(tabHost.newTabSpec("tab1").setIndicator(iv1), MyListFragment.class, null);
		mAdapter.addTab(tabHost.newTabSpec("tab2").setIndicator(iv2), FavorFragment.class, null);

		initData();
		//=======onresume에서 아래 코드 처리하면 비번변경시 튕기게됨==========
		if(PropertyManager.getInstance().getFirstVisit() == 0) {
			if(PropertyManager.getInstance().getLockPassword().equals("") ){
				Bundle b = new Bundle();
				LockDialogFragment f = new LockDialogFragment();
				f.setArguments(b);
				f.show(getSupportFragmentManager(), "lockfeedbackdialog");
			}
		}
		if(lockPassword != null ){
			if( lockPassword.equals(PropertyManager.getInstance().getLockPassword())){
				return ;
			} else {
				if(lockPassword.equals("99999")){ //비밀번호 입력안하고 닫기 눌렀을 때
					Toast.makeText(MypageActivity.this, "잠금 비밀번호가 일치하지않습니다.9", Toast.LENGTH_SHORT).show();	
				} else {
					Toast.makeText(MypageActivity.this, "잠금 비밀번호가 일치하지않습니다.", Toast.LENGTH_SHORT).show();
				}
				finish();
			}
		} 
		if(PropertyManager.getInstance().getLock()==1 && PropertyManager.getInstance().getLockPassword()!=""){
			Intent intent = new Intent(MypageActivity.this, PasswordActivity.class);
			intent.putExtra("activity", 1);
			intent.putExtra("type", 0);
			startActivityForResult(intent, MYPAGE_REQUEST_LOCK_PW);
		}
		
	}//oncreate

	private void initData() {

		currentMypageAction=3;
		setMenuImage();
		setAlarmCount();
	}

	public void setMenuVisible() {
		Animation anim = AnimationUtils.loadAnimation(MypageActivity.this, R.anim.show_anim);
		if (menuView.getVisibility() == View.GONE) {
			menuView.setVisibility(View.VISIBLE);
			menuView.startAnimation(anim);
		}
	}

	public void setMenuInvisible() {
		Animation anim = AnimationUtils.loadAnimation(MypageActivity.this, R.anim.hide_anim);
		if (menuView.getVisibility() == View.VISIBLE) {
			menuView.setVisibility(View.GONE);
			menuView.startAnimation(anim);
		}
	}

	public OnTabChangeListener tabChangeListener = new OnTabChangeListener() {

		@Override
		public void onTabChanged(String tabId) {
			switch (tabId) {
			case "tab1":
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setTitle("");
				sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				break;
			case "tab2":
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setTitle("");
				sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				break;

			}
		}
	};
	public OnClickListener buttonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_home:
				setCurrentMypageAction(0);
				setMenuImage();
				finish();
				break;
			case R.id.btn_search:
				setCurrentMypageAction(1);
				setMenuImage();
				Intent searchIntent = new Intent(MypageActivity.this,
						SearchActivity.class);
				startActivity(searchIntent);
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_right_out);
				break;
			case R.id.image_write:
				Intent intent = new Intent(MypageActivity.this,
						WriteActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_right_out);
				break;
			case R.id.btn_alarm:
				setCurrentMypageAction(2);
				setMenuImage();
				setAlarmCount();
				CustomDialogFragment f = new CustomDialogFragment();
				f.show(getSupportFragmentManager(), "dialog");
				break;
			case R.id.btn_my:
//				Toast.makeText(MypageActivity.this, "btn_my click",
//						Toast.LENGTH_SHORT).show();
//				Intent intentMy = new Intent(MypageActivity.this,
//						MypageActivity.class);
//				startActivity(intentMy);
//				finish();
//				setCurrentMypageAction(3);
//				setMenuImage();
				Intent intentmy = new Intent(MypageActivity.this, MypageActivity.class);
				startActivity(intentmy);
				finish();
				break;
			default:
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		actionBar = getSupportActionBar();
		actionBar.setTitle("");
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.z_actionbar_back_button);
		initData();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mypage, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tag", currentTag);
	}

	@Override
	public void selectMenu(int menuId) {
		switch (menuId) {
		case SettingMenuFragment.MENU_SETTING:
			if (currentTag != TAG_SETTING) {
				// getSupportFragmentManager().popBackStack(null,
				// FragmentManager.POP_BACK_STACK_INCLUSIVE);
				tabHost.setCurrentTab(0);
				currentTag = TAG_SETTING;
			}
			break;
		// case SettingMenuFragment.MENU_ONE:
		// if (currentTag != TAG_ONE) {
		// tabHost.setCurrentTab(1);
		// // getSupportFragmentManager().popBackStack(null,
		// // FragmentManager.POP_BACK_STACK_INCLUSIVE);
		// // getSupportFragmentManager().beginTransaction()
		// // .replace(R.id.container, new TabTwoFragment())
		// // .addToBackStack(null).commit();
		// currentTag = TAG_ONE;
		// }
		// break;
		// case SettingMenuFragment.MENU_TWO:
		// if (currentTag != TAG_TWO) {
		// tabHost.setCurrentTab(2);
		// // getSupportFragmentManager().popBackStack(null,
		// // FragmentManager.POP_BACK_STACK_INCLUSIVE);
		// // getSupportFragmentManager().beginTransaction()
		// // .replace(R.id.container, new TabThreeFragment())
		// // .addToBackStack(null).commit();
		// currentTag = TAG_TWO;
		// }
		// break;
		}
		showContent();
	}
	public ImageView getWriteView(){
		return this.writeView;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void setAlarmCount(){
//		int count = PropertyManager.getInstance().getAlarmDataList().size();
//		int count = PropertyManager.getInstance().getPushList().size();
		int count  = DBManager.getInstance().getPushCount();
		if(count == 0){
			alarmCountView.setVisibility(View.GONE);
		}
		else {
			alarmCountView.setVisibility(View.VISIBLE);
		}
		alarmCountView.setText(" "+count);
	}
	public void gotoMainActivity(int tabNum) {
		// 홈버튼 눌렸을 때 메인으로 돌아가기
		// if(is메인액티비티)return;
		Intent intent = new Intent(MypageActivity.this, MainActivity.class);
//		tabHost.setCurrentTab(tabNum);
		startActivity(intent);
		finish();

	}
	public int currentMypageAction;
	
	public void setCurrentMypageAction(int num){
		this.currentMypageAction = num;
	}
	public void setMenuImage(){
		switch(currentMypageAction){
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
	
	
	
	public static final int MYPAGE_REQUEST_LOCK_PW = 1;
	public static final int ACTIVITY_TYPE= 1;
	public static final String RESULT_MYPAGE = "resultmypage";
	public String lockPassword;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Bundle extraBundle;
		switch (requestCode) {
			case MyListFragment.RC_DETAIL:
			case FavorFragment.RC_DETAIL:
				if (resultCode == RESULT_OK) {
					extraBundle = data.getExtras();
					CommonResult cr = (CommonResult) extraBundle.getSerializable("_OBJ_");
					EventInfo eventInfo = new EventInfo(cr, EventInfo.MODE_UPDATE);
					EventBus.getInstance().post(eventInfo);
				}
				break;
			case MYPAGE_REQUEST_LOCK_PW:
				if (resultCode == RESULT_OK) {
					lockPassword = data.getStringExtra(RESULT_MYPAGE);
					if( lockPassword.equals(PropertyManager.getInstance().getLockPassword())){
//				Toast.makeText(MypageActivity.this, "잠금 비밀번호가 일치함.ㅎㅎㅎㅎ", Toast.LENGTH_SHORT).show();
						return;
					} else {
						Toast.makeText(MypageActivity.this, "잠금 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
						finish();
					}
				}
				break;
			case 0:	//settingMenuFragment랑 합쳐서 onActivityResult해보려고 시도했는데 안되는군
				if (resultCode == RESULT_OK) {
					lockPassword = data.getStringExtra(RESULT_LOCK);
					if(lockRequestCnt==0 && lockPassword.equals(PropertyManager.getInstance().getLockPassword())){ //기존암호랑 리턴받은 암호랑 같은지 여부 먼저 검사.
						if(!lockPassword.equals(PropertyManager.getInstance().getLockPassword())){
							Toast.makeText(MypageActivity.this, ""+lockPassword+"잠금 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
							return ;
						}
					}
					lockArr[lockRequestCnt] = lockPassword; //입력받은 패스워드를 0,1,2번째 배열에 저장
//			Toast.makeText(MypageActivity.this, "result : " + lockPassword, Toast.LENGTH_SHORT).show();
					if( lockRequestCnt < 2 ){
						lockRequestCnt++; // 0==기존번호가 있는 경우(->), 1==새암호입력(->) 2==새암호컨펌
						Intent intent = new Intent(MypageActivity.this, PasswordActivity.class);
						intent.putExtra("type", lockRequestCnt);
						startActivityForResult(intent, REQUEST_LOCK_PW);
						overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
					} else {
						if(lockArr[lockRequestCnt].equals(lockArr[lockRequestCnt-1])){
							PropertyManager.getInstance().setLockPassword(lockPassword);
							PropertyManager.getInstance().setLock(1);
							Toast.makeText(MypageActivity.this, "잠금 비밀번호가 설정되었습니다.", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MypageActivity.this, "비밀번호가 일치하지 않습니다.\n잠금 비밀번호를 재설정 해주세요", Toast.LENGTH_SHORT).show();
						}
						lockRequestCnt=0;
					}
				}
				break;
		}

	}
	//세팅메뉴 프래그먼트꺼 복사한거 (하단변수들)
	public static final int REQUEST_LOCK_PW = 0;
	public static final String RESULT_LOCK = "result";
	public static int lockRequestCnt=1; //type 넘버링
	public String[] lockArr = new String[3]; //



}
