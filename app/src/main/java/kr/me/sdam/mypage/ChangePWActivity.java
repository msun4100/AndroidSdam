package kr.me.sdam.mypage;

import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.autologin.FindPWInfo;
import kr.me.sdam.autologin.SplashActivity;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.database.DBManager;
import kr.me.sdam.dialogs.FindPWDialogFragment;
import okhttp3.Request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class ChangePWActivity extends Activity {

	/** Called when the activity is first created. */
	
	EditText edit1, edit2, edit3;
	TextView text1,text2,text3,findPWView,finishView;
	InputMethodManager imm;
	
	private String password;
	private String temp1;
	private boolean [] flags;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_change_pw);
	    flags = new boolean[3];
	    password = (String)PropertyManager.getInstance().getPassword();
	    imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    
	    text1 = (TextView)findViewById(R.id.text_change_pw_1);
	    text2 = (TextView)findViewById(R.id.text_change_pw_2);
	    text3 = (TextView)findViewById(R.id.text_change_pw_3);
	    finishView = (TextView)findViewById(R.id.text_change_cancel);
	    finishView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	    findPWView = (TextView)findViewById(R.id.text_change_find_pw);
		findPWView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Bundle b = new Bundle();
//				b.putString("userId", PropertyManager.getInstance().getUserId());
				FindPWDialogFragment f = new FindPWDialogFragment();
//				f.setArguments(b);				
				f.show(getFragmentManager(), "findpwdialog");
			}
		});
	    
		edit1 = (EditText)findViewById(R.id.edit_change_pw_1);
		edit1.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
				if(text.length() != password.length() || !text.equals(password) ){
					text1.setVisibility(View.VISIBLE);
					text1.setTextColor(0xfff14249);
					text1.setText("기존 비밀번호가 일치하지 않습니다.");
					flags[0]=false;
//					edit1.setTextColor(0xfff14249);
				} else {
					text1.setVisibility(View.VISIBLE);
					text1.setTextColor(0xffffffff);
					text1.setText("기존 비밀번호가 일치합니다.");
//					edit1.setTextColor(0xffffffff);
					flags[0]=true;
				}	
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	    edit2 = (EditText)findViewById(R.id.edit_change_pw_2);
	    edit2.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
				if(text.length() < 8  ){
					text2.setVisibility(View.VISIBLE);
					text2.setTextColor(0xfff14249);
					text2.setText("8자리 이상 입력해주세요.");
					flags[1]=false;
				} else {
					text2.setVisibility(View.VISIBLE);
					text2.setTextColor(0xffffffff);
					text2.setText("유효한 비밀번호입니다.");
					flags[1]=true;
					temp1 = edit2.getText().toString();
				}		
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	    edit3 = (EditText)findViewById(R.id.edit_change_pw_3);
	    edit3.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
				if(!text.equals(temp1)){
					text3.setVisibility(View.VISIBLE);
					text3.setTextColor(0xfff14249);
					text3.setText("비밀번호가 일치하지 않습니다.");
					flags[2]=false;
				} else {
					text3.setVisibility(View.VISIBLE);
					text3.setTextColor(0xffffffff);
					text3.setText("비밀번호가 일치합니다.");
					flags[2]=true;
				}	
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	    edit3.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND){
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					if(flags[0] && flags[1] && flags[2]){
//						Toast.makeText(ChangePWActivity.this, "플래그셋다. 네트워크 요청", Toast.LENGTH_SHORT).show();
						NetworkManager.getInstance().putSdamChangePW(ChangePWActivity.this, temp1, new OnResultListener<FindPWInfo>() {
							
							@Override
							public void onSuccess(Request request, FindPWInfo result) {
								if(result.success == CommonInfo.COMMON_INFO_SUCCESS && result.result.equals("ok")){
//									Toast.makeText(ChangePWActivity.this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
//									finish();
									doLogout();
								} else {
									Toast.makeText(ChangePWActivity.this, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void onFailure(Request request, int code, Throwable cause) {
								Toast.makeText(ChangePWActivity.this, "비밀번호가  변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
							}

						});
					} else {
						Toast.makeText(ChangePWActivity.this, "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
					}
					
					return true;	
				}
				return false;
			}
		});
	    
	    Button btn = (Button)findViewById(R.id.btn_change_pw);
	    btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(flags[0] && flags[1] && flags[2]){
//					Toast.makeText(ChangePWActivity.this, "플래그셋다. 네트워크 요청", Toast.LENGTH_SHORT).show();
					NetworkManager.getInstance().putSdamChangePW(ChangePWActivity.this, temp1, new OnResultListener<FindPWInfo>() {
						
						@Override
						public void onSuccess(Request request, FindPWInfo result) {
							if(result.success == CommonInfo.COMMON_INFO_SUCCESS && result.result.equals("ok")){
//								Toast.makeText(ChangePWActivity.this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
//								finish();
								doLogout();
							} else {
								Toast.makeText(ChangePWActivity.this, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(ChangePWActivity.this, "비밀번호가  변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					Toast.makeText(ChangePWActivity.this, "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	    
	}//onCreate

	private void doLogout(){
		Toast.makeText(ChangePWActivity.this, "비밀번호가 성공적으로 변경되었습니다.\n다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
		PropertyManager.getInstance().setUserId("");
		PropertyManager.getInstance().setPassword("");
		PropertyManager.getInstance().setLockPassword("");
		PropertyManager.getInstance().setFirstVisit(0);
		DBManager.getInstance().clearPushTable();
//		Toast.makeText(getActivity(), "로그아웃 됩니다.", Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(ChangePWActivity.this, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
}
