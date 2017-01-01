package kr.me.sdam.autologin;

import kr.me.sdam.FontManager;
import kr.me.sdam.MainActivity;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.PropertyManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import kr.me.sdam.R;
import kr.me.sdam.NetworkManager.OnResultListener;
import okhttp3.Request;

public class SplashBActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_b);
		
		Button btn = (Button)findViewById(R.id.btn_login);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SplashBActivity.this, LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});
		
		btn = (Button)findViewById(R.id.btn_signup);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SplashBActivity.this, SignUpActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});
		
		btn = (Button)findViewById(R.id.btn_temp);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				doLogin("asd@", "12341234");
				doLogin(PropertyManager.getInstance().getUserId(), PropertyManager.getInstance().getPassword());
				
			}
		});

	}
//	===========using for temp button================
	public void doLogin(String id, String password){
		NetworkManager.getInstance().putSdamLogin(SplashBActivity.this, id, password,
//				"37.4762397",
//				"126.9583907",
				PropertyManager.getInstance().getLatitude(),
				PropertyManager.getInstance().getLongitude(),
				new OnResultListener<LoginInfo>() {
					@Override
					public void onSuccess(Request request, LoginInfo result) {
						if(result.success==LoginInfo.LOGIN_SUCCESS){
//							Toast.makeText(SplashBActivity.this, "/login onSuccess", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(SplashBActivity.this, MainActivity.class);
							startActivity(intent);
							finish();
						}
						else {
							Toast.makeText(SplashBActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(SplashBActivity.this, "등록되지 않은 유저입니다.\n아이디&패스워드를 확인하세요.", Toast.LENGTH_SHORT).show();
					}

				});
	}
//	===========temp button================
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
//		
//		
//		return super.onCreateView(name, context, attrs);
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_b, menu);
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
}
