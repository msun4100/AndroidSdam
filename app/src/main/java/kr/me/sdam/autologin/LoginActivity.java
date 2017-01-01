package kr.me.sdam.autologin;


import kr.me.sdam.FontManager;
import kr.me.sdam.MainActivity;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.dialogs.FindPWDialogFragment;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.dialogs.UsingLocationDialogFragment;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import okhttp3.Request;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private static final String TAG = LoginActivity.class.getSimpleName();
	EditText idView;
	EditText passwordView;
	TextView signupView;
	TextView findPWView;
	InputMethodManager imm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		findPWView = (TextView)findViewById(R.id.text_login_find_pw);
		findPWView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(LoginActivity.this, "서비스 준비중입니다..", Toast.LENGTH_SHORT).show();
//				Bundle b = new Bundle();
//				b.putString("userId", PropertyManager.getInstance().getUserId());
				FindPWDialogFragment f = new FindPWDialogFragment();
//				f.setArguments(b);				
				f.show(getFragmentManager(), "findpwdialog");
				
			}
		});
		signupView = (TextView)findViewById(R.id.text_signup);
		signupView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);				
			}
		});
		idView = (EditText)findViewById(R.id.login_editText1);
		idView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
//				if(text.length()<8){
				if(!text.contains("@")){
					idView.setTextColor(0xfff14249);
				} else {
					idView.setTextColor(0xffffffff);
				}				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		passwordView = (EditText)findViewById(R.id.login_editText2);
		passwordView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
				if(text.length()<8){
					passwordView.setTextColor(0xfff14249);
				} else {
					passwordView.setTextColor(0xffffffff);
				}				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		passwordView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND){
					//....
					doLogin(idView.getText().toString(),passwordView.getText().toString());
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					return true;	
				}
				return false;
			}
		});
		Button btn = (Button)findViewById(R.id.btn_login);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//				startActivity(intent);
//				finish();
				//===========
				final String id = idView.getText().toString();
				final String password = passwordView.getText().toString();
				Log.i("LoginActivity", id+" "+ password);
				doLogin(id,password);
			}
		});
		
	}
	public void doLogin(final String id, final String password){
		NetworkManager.getInstance().putSdamLogin(LoginActivity.this, id, password,
//				"37.4762397",
//				"126.9583907",
				PropertyManager.getInstance().getLatitude(),
				PropertyManager.getInstance().getLongitude(),
				new NetworkManager.OnResultListener<LoginInfo>() {

					@Override
					public void onSuccess(Request request, LoginInfo result) {
						if(result != null){
							
//							Toast.makeText(LoginActivity.this, "lastLogin"+result.result.lastLogin, Toast.LENGTH_SHORT).show();	
							if(result.success==LoginInfo.LOGIN_SUCCESS){
								Log.e(TAG, "onSuccess: "+result.work );
								PropertyManager.getInstance().setUserId(id);
								PropertyManager.getInstance().setPassword(password);
//								Toast.makeText(LoginActivity.this, "/login onSuccess", Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								finish();
							} else if(result.success == LoginInfo.COMMON_INFO_SUCCESS_ZERO) {
								Log.e(TAG, "onSuccess: "+result.work );
								if(result.work.equals("Recently_withdraw")){
									Toast.makeText(LoginActivity.this, "최근에 회원탈퇴한 계정입니다.\n관리자에게 문의해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(LoginActivity.this, "아이디 및 패스워드를 확인해주세요.\n"+result.work, Toast.LENGTH_SHORT).show();
								}
								
							}	
						} else {
							Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
						}
						
					}
					
					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(LoginActivity.this, "등록되지 않은 유저입니다.\n아이디&패스워드를 확인하세요.", Toast.LENGTH_SHORT).show();
					}
				});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.login, menu);
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

//	========BackPress======
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(LoginActivity.this, SplashBActivity.class);
		startActivity(intent);
		finish();
	}
	
//	@SuppressLint("InlinedApi")
//	@Override
//	public View onCreateView(String name, @NonNull Context context, 
//			@NonNull AttributeSet attrs) {
//		
//		if(name.equals("TextView")){
//			int[] ids = {android.R.attr.fontFamily};
//			TypedArray ta = context.obtainStyledAttributes(attrs, ids);
//			String fontName = ta.getString(0);
//			ta.recycle();
//			Typeface tf = FontManager.getInstance().getTypeface(context, fontName);
//			if( tf != null) {
//				TextView tv = new TextView(context, attrs);
//				tv.setTypeface(tf);
//				return tv;
//			}
//		}
//		if(name.equals("Button")){
//			int[] ids = {android.R.attr.fontFamily};
//			TypedArray ta = context.obtainStyledAttributes(attrs, ids);
//			String fontName = ta.getString(0);
//			ta.recycle();
//			Typeface tf = FontManager.getInstance().getTypeface(context, fontName);
//			if( tf != null) {
//				Button btn = new Button(context, attrs);
//				btn.setTypeface(tf);
//				return btn;
//			}
//		}
//		return super.onCreateView(name, context, attrs);
//	}
	
}
