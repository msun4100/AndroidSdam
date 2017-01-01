package kr.me.sdam.write;

import kr.me.sdam.BgImage;
import kr.me.sdam.MainActivity;
import kr.me.sdam.MyApplication;
import kr.me.sdam.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
public class WriteActivity extends ActionBarActivity {

	public static final String BUNDLE_ARG_CATEGORY = "category";
	public static final String BUNDLE_ARG_TEXT = "text";
	private static final String F1_TAG = "tab1";
	private static final String F2_TAG = "tab2";
	
	ActionBar actionBar;
	ImageView[] categories;
	WriteOneFragment writeOneFragment;
	WriteTwoFragment writeTwoFragment;

	public EditText inputView;

	int categoryId = 0;
	TextView writingCount;
	public View layoutView;
	public ImageView selectedIcon;
	public ImageView lockIcon;

	public ImageView locateIcon;
	public int lock=0, unlocate=0;
	public InputMethodManager imm;
	
	public int[] imageResIds = new int[BgImage.BG_IMAGE_SIZE];
	
	public int sendImage ;
	public int getSendImage() {
		return sendImage;
	}

	public void setSendImage(int sendImage) {
		this.sendImage = sendImage;
	}

	public int[] getImageResIds() {
		return imageResIds;
	}

	public void setImageResIds(int[] imageResIds) {
		this.imageResIds = imageResIds;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write);
		// initData();
		selectedIcon = (ImageView)findViewById(R.id.image_selected_emotion);
		selectedIcon.setVisibility(View.GONE);
		
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		writingCount = (TextView) findViewById(R.id.text_writing_count);
		inputView = (EditText) findViewById(R.id.edit_input);
		inputView.addTextChangedListener(mTextEditorWatcher);
		inputView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND){
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					return true;	
				}
				return false;
			}
		});
		layoutView = (View) findViewById(R.id.frame_write);
		layoutView.setBackgroundResource(R.drawable.d_write_bg);
		lockIcon = (ImageView)findViewById(R.id.image_lock);
		lockIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(lock == 0){
					lockIcon.setImageResource(R.drawable.d_write_option_secret_lock);
					setLock(1);
				} else if(lock == 1){
					lockIcon.setImageResource(R.drawable.d_write_option_secret_unlock);
					setLock(0);
				}
			}
		});
		locateIcon = (ImageView)findViewById(R.id.image_location);
		locateIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(unlocate == 0){
					locateIcon.setImageResource(R.drawable.d_write_option_location_lock);
					setUnlocate(1);
				} else if(unlocate == 1){
					locateIcon.setImageResource(R.drawable.d_write_option_location_unlock);
					setUnlocate(0);
				}
			}
		});
		
		callWriteOneFragment();
		Tracker t = ((MyApplication)getApplication()).getTracker
				(MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("WriteActivity");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

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

	private final TextWatcher mTextEditorWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			writingCount.setText(String.valueOf(s.length())+"/330" );
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}
		
		@Override
		public void afterTextChanged(Editable s) {

		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		actionBar = getSupportActionBar();
		actionBar.setTitle("");
		Drawable d = getResources().getDrawable(
				R.drawable.d_write_actionbar);
		actionBar.setBackgroundDrawable(d);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.z_actionbar_back_button);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.write, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void callWriteOneFragment() {
		CURRENT_FRAGMENT=FRAGMENT_ONE;
		Fragment f = getSupportFragmentManager().findFragmentByTag(F1_TAG);
		if (f == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			writeOneFragment = new WriteOneFragment();
			// --프래그먼트 전환효과
			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
			// --리플레이스나 애드전에 지정
			ft.replace(R.id.containerforwrite, writeOneFragment, F1_TAG);
			ft.commit();
		}
	}

	public void callWriteTwoFragment(int categoryId) {
		CURRENT_FRAGMENT=FRAGMENT_TWO;
		Fragment f = getSupportFragmentManager().findFragmentByTag(F2_TAG);
		if (f == null) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			Bundle b = new Bundle();
			b.putString(BUNDLE_ARG_CATEGORY, "" + categoryId);
			b.putString(BUNDLE_ARG_TEXT, inputView.getText().toString());
			writeTwoFragment = new WriteTwoFragment();
			writeTwoFragment.setArguments(b);
			// --프래그먼트 전환효과
			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
			// --리플레이스나 애드전에 지정
			ft.replace(R.id.containerforwrite, writeTwoFragment, F2_TAG);
			ft.commit();
		}
	}
	public String getInputString(){
		return inputView.getText().toString();
	}
	public void setMessage(String message) {
		if (writingCount != null) {
			writingCount.setText("Category # : " + message);
		}
	}

	public void gotoMainActivity() {
		Intent intent = new Intent(WriteActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//		===글쓰고 탭 선택하기(1회성)==========
		MainActivity.setResumeTabNum(2);
//		=============================
		startActivity(intent);
		finish();
	}
	private int CURRENT_FRAGMENT;
	private static final int FRAGMENT_ONE=1;
	private static final int FRAGMENT_TWO=2;
	boolean isBackPressd = false;
	Handler mWriteBackHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case MainActivity.MESSAGE_TIME_OUT_BACK_KEY:
				isBackPressd = false;
				break;
			}
		}
	};
	
	@Override
	public void onBackPressed() {
		if(CURRENT_FRAGMENT == FRAGMENT_TWO){
			callWriteOneFragment();
		} else {
			if( !isBackPressd ){
				isBackPressd = true;
				Toast.makeText(this, "한번 더 누르시면 글쓰기가 종료됩니다.", Toast.LENGTH_SHORT).show();
				mWriteBackHandler.sendEmptyMessageDelayed(MainActivity.MESSAGE_TIME_OUT_BACK_KEY, MainActivity.TIME_BACK_KEY);
			} else  {
				mWriteBackHandler.removeMessages(MainActivity.MESSAGE_TIME_OUT_BACK_KEY);
				super.onBackPressed();	
			}
			
		}
	}
	
	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public int getUnlocate() {
		return unlocate;
	}

	public void setUnlocate(int unlocate) {
		this.unlocate = unlocate;
	}
	
}
