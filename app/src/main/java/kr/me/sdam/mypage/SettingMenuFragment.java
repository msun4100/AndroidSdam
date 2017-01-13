package kr.me.sdam.mypage;


import kr.me.sdam.MyApplication;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.dialogs.LockDialogFragment;
import kr.me.sdam.dialogs.LogoutDialogFragment;
import kr.me.sdam.dialogs.UnsignConfirmDialogFragment;

import kr.me.sdam.seekbars.RangeSeekBar;
import kr.me.sdam.seekbars.RangeSeekBar.OnRangeSeekBarChangeListener;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SettingMenuFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((MyApplication)((MypageActivity)getActivity())
				.getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.enableAdvertisingIdCollection(true);
		t.setScreenName("SettingMenuFragment");
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
	
	public static final String TAG_RANGE = "rangeseekbar";
	public static final int ALARM_ON = 1;
	public static final int ALARM_OFF = 0;
	// ====================
	public static final int MENU_INVALID = -1;
	// public static final int MENU_MAIN = 0;
	public static final int MENU_SETTING = 0;

	public interface MenuCallback {
		public void selectMenu(int menuId);
	}

	MenuCallback callback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MenuCallback) {
			callback = (MenuCallback) activity;
		} else {
			// Throws...
		}
	}

	View pwView;
	View pwChangeView;
	View pwLockChangeView;
	SwitchCompat swLock;
	
	View alarmView;
	View feedView;
	
	View inviteView;
	View layoutInviteKakao;
	View layoutInviteFacebook;
	
	View supportView;
	View layoutSupportReport;
	View layoutSupportTermchk;
	TextView textSupportReport;
	TextView textSupportTermchk;

	
	View logoutView;
	View unsignView;

	View view;
	TextView min;
	TextView max;
	SwitchCompat swAlarm;
	SeekBar distSeekBar;
	TextView distTextView;
	
	ImageView backView;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.setting_menu_layout, container, false);
		pwView = (View) view.findViewById(R.id.set_pw_layout);
		pwView.setOnClickListener(settingListener);
		pwChangeView = (View)view.findViewById(R.id.set_pw_change_layout);
		pwChangeView.setOnClickListener(settingListener);
		pwLockChangeView = (View)view.findViewById(R.id.set_pw_lock_change_layout);
		pwLockChangeView.setOnClickListener(settingListener);
		swLock = (SwitchCompat)view.findViewById(R.id.set_lock_switch);
		
		
		alarmView = (View) view.findViewById(R.id.set_alarm_layout);
		alarmView.setOnClickListener(settingListener);
		feedView = (View) view.findViewById(R.id.set_feed_layout);
		feedView.setOnClickListener(settingListener);
		
		inviteView = (View) view.findViewById(R.id.set_invite_layout);
		inviteView.setOnClickListener(settingListener);
		layoutInviteKakao = (View)view.findViewById(R.id.set_invite_kakao_layout);
		layoutInviteKakao.setOnClickListener(settingListener);
		layoutInviteFacebook = (View)view.findViewById(R.id.set_invite_facebook_layout);
		layoutInviteFacebook.setOnClickListener(settingListener);
		
		supportView = (View) view.findViewById(R.id.set_support_layout);
		supportView.setOnClickListener(settingListener);
		layoutSupportReport = (View) view.findViewById(R.id.set_support_report_layout);
		layoutSupportReport.setOnClickListener(settingListener);
		layoutSupportTermchk = (View) view.findViewById(R.id.set_support_termchk_layout);
		layoutSupportTermchk.setOnClickListener(settingListener);
//		textSupportReport = (TextView)view.findViewById(R.id.text_set_support_repot);
//		textSupportReport.setOnClickListener(settingListener);
//		textSupportTermchk = (TextView)view.findViewById(R.id.text_set_support_termchk);
//		textSupportTermchk.setOnClickListener(settingListener);
		
		logoutView = (View)view.findViewById(R.id.set_logout_layout);
		logoutView.setOnClickListener(settingListener);
		unsignView = (View)view.findViewById(R.id.set_unsign_layout);
		unsignView.setOnClickListener(settingListener);
		
		backView = (ImageView)view.findViewById(R.id.image_set_back);
		backView.setOnClickListener(settingListener);
		// 대메뉴 선언 및 리스너 등록
		
		min = (TextView) view.findViewById(R.id.minValue);
		max = (TextView) view.findViewById(R.id.maxValue);
		// create RangeSeekBar as Integer range between 20 and 75
		RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(10, 100, getActivity());
		setFeedAge(seekBar);
		
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				// handle changed range values
				Log.i(TAG_RANGE, "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);

				min.setText(minValue.toString());
				max.setText(maxValue.toString());
				PropertyManager.getInstance().setMinAge(minValue);
				PropertyManager.getInstance().setMaxAge(maxValue);
			}
		});
		// add RangeSeekBar to pre-defined layout
		ViewGroup layout = (ViewGroup) view.findViewById(R.id.layout);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int size = Math.round( 3 * dm.density);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
		param.bottomMargin= size;
		param.width=Math.round( 196 * dm.density);
		param.height=Math.round( 52 * dm.density);
		param.leftMargin=Math.round( 60 * dm.density);
		layout.setLayoutParams(param);
		
		layout.addView(seekBar);
//		==========Range seekbar=========
		swAlarm=(SwitchCompat)view.findViewById(R.id.set_alarm_switch);
		if(PropertyManager.getInstance().getAlarm()==ALARM_ON){
			swAlarm.setChecked(true);	
		} else {
			swAlarm.setChecked(false);
		}
		swAlarm.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					PropertyManager.getInstance().setAlarm(ALARM_ON);
					Toast.makeText(getActivity(), "ALARM ON" , Toast.LENGTH_SHORT).show();
					
				} else {
					PropertyManager.getInstance().setAlarm(ALARM_OFF);
					Toast.makeText(getActivity(), "ALARM OFF" , Toast.LENGTH_SHORT).show();
				}
			}
			
		});
//		===========alarm===============
		if(PropertyManager.getInstance().getLock()==ALARM_ON){
			swLock.setChecked(true);
		} else {
			swLock.setChecked(false);
		}
		swLock.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked){
					PropertyManager.getInstance().setLock(ALARM_ON);
					Toast.makeText(getActivity(), "Lock ON" , Toast.LENGTH_SHORT).show();
					if(PropertyManager.getInstance().getLockPassword().equals("") ){
						Bundle b = new Bundle();
						LockDialogFragment f = new LockDialogFragment();
						f.setArguments(b);
						f.show(getActivity().getSupportFragmentManager(), "lockfeedbackdialog");
					}
					
				} else {
					PropertyManager.getInstance().setLock(ALARM_OFF);
					Toast.makeText(getActivity(), "Lock OFF" , Toast.LENGTH_SHORT).show();
				}
			}
			
		});
//		===========lock===============
		distTextView = (TextView)view.findViewById(R.id.set_feed_dist_text);
		distSeekBar = (SeekBar)view.findViewById(R.id.set_feed_dist);
		setFeedDistance();
		distSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				PropertyManager.getInstance().setDistance(distSeekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				distTextView.setText(""+distSeekBar.getProgress()+"km");
				if(distSeekBar.getProgress() > 199){
					distTextView.setText("담너머");
				}
			}
		});
//		===========distance==================
		
		return view;
	}
	private void setFeedDistance(){
		distTextView.setText(""+PropertyManager.getInstance().getDistance()+"km");
		distSeekBar.setMax(200);
		distSeekBar.setProgress(PropertyManager.getInstance().getDistance());
	}
	private void setFeedAge(RangeSeekBar<Integer> seekBar) {
		min.setText(""+PropertyManager.getInstance().getMinAge());
		max.setText(""+PropertyManager.getInstance().getMaxAge());
		seekBar.setSelectedMinValue(Integer.parseInt(min.getText().toString()));
		seekBar.setSelectedMaxValue(Integer.parseInt(max.getText().toString()));
	}
	public static final int REQUEST_LOCK_PW = 0;
	public static final String RESULT_LOCK = "result";
	
	
	public OnClickListener settingListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.set_pw_layout:
				setVisibility(((View) view.findViewById(R.id.set_pw_child)));
				break;
			case R.id.set_pw_change_layout:
				Intent i = new Intent(getActivity(), ChangePWActivity.class);
				startActivity(i);
//				Toast.makeText(getActivity(), "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();
				break;
			case R.id.set_pw_lock_change_layout:
				if(PropertyManager.getInstance().getLockPassword().length()==4){
					lockRequestCnt = 0;
				} else {
					lockRequestCnt = 1;
				}
				Intent intent = new Intent(getActivity(), PasswordActivity.class);
				intent.putExtra("activity", 0);
				intent.putExtra("type", lockRequestCnt);
				startActivityForResult(intent, REQUEST_LOCK_PW);
				break;
			case R.id.set_alarm_layout:
				setVisibility(((View) view.findViewById(R.id.set_alarm_child)));
				break;
			case R.id.set_feed_layout:
				setVisibility(((View) view.findViewById(R.id.set_feed_child)));
				break;
			case R.id.set_invite_layout:
				setVisibility(((View) view.findViewById(R.id.set_invite_child)));
				break;
			case R.id.set_support_layout:
				setVisibility(((View) view.findViewById(R.id.set_support_child)));
				break;
			case R.id.set_support_report_layout:
				Toast.makeText(getActivity(), "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();
				break;
			case R.id.set_support_termchk_layout:
				Intent intentWebView = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.naver.com/sdamkorea/220310795077"));
//				intentWebView.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentWebView);
//				finish();
				break;
			case R.id.set_invite_kakao_layout:
//				Intent kakaoIntent = new Intent(getActivity(), KakaoLinkActivity.class);
//				startActivity(kakaoIntent);
				break;
			case R.id.set_invite_facebook_layout:
//				Intent mFBIntent = new Intent(getActivity(), HelloFacebookSampleActivity.class);
//				startActivity(mFBIntent);
				break;
			case R.id.set_logout_layout:
				Bundle b = new Bundle();
				LogoutDialogFragment f = new LogoutDialogFragment();
				f.setArguments(b);
				f.show(getActivity().getSupportFragmentManager(), "logoutdialog");
				break;
			case R.id.set_unsign_layout:
				Bundle bb = new Bundle();
				UnsignConfirmDialogFragment unsingFragment = new UnsignConfirmDialogFragment();
				unsingFragment.setArguments(bb);
				unsingFragment.show(getActivity().getSupportFragmentManager(), "unsignconfirmdialog");
				break;
			case R.id.image_set_back:
				((MypageActivity)getActivity()).toggle();
			}
		}
	};
	public void setVisibility(View v) {
		// Animation anim;
		if (v.getVisibility() == View.VISIBLE) {
			v.setVisibility(View.GONE);
			return;
		} else if (v.getVisibility() == View.GONE) {
			v.setVisibility(View.VISIBLE);
			return;
		} else {
			Toast.makeText(getActivity(), "Visibility error..",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	public int getMenuId(int position) {
		switch (position) {
		case 0:
			return MENU_SETTING;
		}
		return MENU_INVALID;
	}
	
	public String lockPassword="0000"; //임시 패스워드저장 스트링변수
	public int lockRequestCnt=0; //type 넘버링
	public String[] lockArr = new String[3]; //
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			lockPassword = data.getStringExtra(RESULT_LOCK);
			if(lockRequestCnt==0){ //기존암호랑 리턴받은 암호랑 같은지 여부 먼저 검사.
				if(!lockPassword.equals(PropertyManager.getInstance().getLockPassword())){
					Toast.makeText(getActivity(), "잠금 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
					return ;
				}
			}
			lockArr[lockRequestCnt] = lockPassword; //입력받은 패스워드를 0,1,2번째 배열에 저장
//			Toast.makeText(getActivity(), "result : " + lockPassword, Toast.LENGTH_SHORT).show();
			if( lockRequestCnt < 2 ){ 
				lockRequestCnt++; // 0==기존번호가 있는 경우(->), 1==새암호입력(->) 2==새암호컨펌 
				Intent intent = new Intent(getActivity(), PasswordActivity.class);
				intent.putExtra("type", lockRequestCnt);
				startActivityForResult(intent, REQUEST_LOCK_PW);
				getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
			} else {
				if(lockArr[lockRequestCnt].equals(lockArr[lockRequestCnt-1])){
					PropertyManager.getInstance().setLockPassword(lockPassword);	
					PropertyManager.getInstance().setLock(1);
					Toast.makeText(getActivity(), "잠금 비밀번호가 설정되었습니다.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "비밀번호가 일치하지 않습니다.\n잠금 비밀번호를 재설정 해주세요", Toast.LENGTH_SHORT).show();
				}
				lockRequestCnt=0;
			}
		}
	}
}
