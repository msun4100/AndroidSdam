package kr.me.sdam.autologin;

import java.util.Arrays;
import java.util.List;

import kr.me.sdam.FontManager;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.autologin.popupwindow.MySignupPopup;
import kr.me.sdam.autologin.popupwindow.MySignupPopup.OnPopupListener;
import kr.me.sdam.dialogs.SignUpDialogFragment;
import okhttp3.Request;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SignUpActivity extends FragmentActivity{
	
	EditText idView;
	EditText passwordView;
	
	InputMethodManager imm;
	private static final String TAG = SignUpActivity.class.getSimpleName();
	
	RegisterInfoData userData;
	
	Spinner gender, birth;
	MyGenderAdapter genderAdapter;
	MyBirthAdapter birthAdapter;
	
	CheckBox checkBox,checkBoxPrivacy;
	TextView privacyText;
	TextView termText;
	boolean isTermBoxChecked = false;
	boolean isPrivacyBoxChecked = false;
	
	public Button btn_gender, btn_birth;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		userData = new RegisterInfoData();
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//		gender = (Spinner)findViewById(R.id.spinner_gender);
//		birth = (Spinner)findViewById(R.id.spinner_birth);
//	
//		genderAdapter = new MyGenderAdapter(this);
//		gender.setAdapter(genderAdapter);
//		
//		gender.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				String temp=(String)gender.getItemAtPosition(position).toString();
//				if(!(gender.getItemAtPosition(position).equals("성별"))){
//					if((gender.getItemAtPosition(position).equals("남자"))){
//						userData.sex=1;
//					} else userData.sex=2;
//				}
////				Toast.makeText(SignUpActivity.this, ""+userData.sex, Toast.LENGTH_SHORT).show();				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {

//			}
//		});
//		birthAdapter = new MyBirthAdapter(this);
//		birth.setAdapter(birthAdapter);
//		birth.setSelection(39);
//		birth.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				birth.setBackgroundResource(R.drawable.a_launcher_box_dropdown_blue_01_02);
//			}
//		});
//		birth.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				
//				if(!(birth.getItemAtPosition(position).equals("생일년도"))){
//					userData.birth=Integer.parseInt((String)birth.getItemAtPosition(position)) ;	
//				}
////				Toast.makeText(SignUpActivity.this, ""+userData.birth , Toast.LENGTH_SHORT).show();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//			}
//		});
		
		
//		==============
		btn_gender = (Button)findViewById(R.id.btn_gender);
		btn_gender.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				String [] array = getResources().getStringArray(R.array.spinner_gender_item); 
				List<String> items = Arrays.asList(array);
				
				MySignupPopup popup = new MySignupPopup(SignUpActivity.this, items);
				popup.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						btn_gender.setBackgroundResource(R.drawable.a_launcher_button);
					}
				});
				popup.setOnPopupListener(new OnPopupListener() {
					
					@Override
					public void onButtonClick(int type, String text) {
						if (type == 0){
//							btn_gender.setBackgroundResource(R.drawable.a_launcher_button);
							btn_gender.setTextColor(0xFFFFFFFF);
							btn_gender.setText(text);
						}
						String temp=text;
						if(!(temp.equals("성별"))){
							if((temp.equals("남자"))){
								userData.sex=1;
							} else userData.sex=2;
						}
					}
				});
				//set하는 순서 중요!!!!!!!!!!!!!!!!!!!!!!!!바깥 클릭해서 창닫히게 하려면
				popup.setFocusable(true);
				popup.setOutsideTouchable(true);
				Drawable d = getResources().getDrawable(R.drawable.a_launcher_box_dropdown_white);
				popup.setBackgroundDrawable(d);
				popup.showAsDropDown(v,0,0);
				btn_gender.setBackgroundResource(R.drawable.a_launcher_box_dropdown_blue_02);
			}
		});
		btn_birth = (Button)findViewById(R.id.btn_birth);
		btn_birth.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				String [] array = getResources().getStringArray(R.array.spinner_year_item); 
				List<String> items = Arrays.asList(array);
				
				MySignupPopup popup = new MySignupPopup(SignUpActivity.this, items);
				popup.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						btn_birth.setBackgroundResource(R.drawable.a_launcher_button);
					}
				});
				popup.setOnPopupListener(new OnPopupListener() {
					
					@Override
					public void onButtonClick(int type, String text) {
						if (type == 0){
							btn_birth.setTextColor(0xFFFFFFFF);
							btn_birth.setText(text);
						}
						String temp=text;
						if(!(temp.equals("생일년도"))){
							userData.birth=Integer.parseInt(temp) ;	
						}
						
//						Toast.makeText(SignUpActivity.this, ""+userData.birth, Toast.LENGTH_SHORT).show();
					}
				});
				//set하는 순서 중요!!!!!!!!!!!!!!!!!!!!!!!!바깥 클릭해서 창닫히게 하려면
				popup.setFocusable(true);
				popup.setOutsideTouchable(true);
				Drawable d = getResources().getDrawable(R.drawable.a_launcher_box_dropdown_white);
				popup.setBackgroundDrawable(d);
				popup.showAsDropDown(v,0,0);
				btn_birth.setBackgroundResource(R.drawable.a_launcher_box_dropdown_blue_02);
			}
		});
		
//		==============
	
		
		checkBox = (CheckBox)findViewById(R.id.checkBox1);
		checkBox.setOnClickListener(immListener);
		checkBox.setChecked(true);
		isTermBoxChecked=checkBox.isChecked();
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				Toast.makeText(SignUpActivity.this, "checked changed : "+checkBox.isChecked(), Toast.LENGTH_SHORT).show();				
				isTermBoxChecked=checkBox.isChecked();
			}
		});
		
		termText = (TextView)findViewById(R.id.text_checkbox);
		String str = "자세히";
		termText.setText(Html.fromHtml("<u>" + str + "</u>"));
		termText.setOnClickListener(immListener);
	//==================================
		checkBoxPrivacy = (CheckBox)findViewById(R.id.checkBox_privacy);
		checkBoxPrivacy.setOnClickListener(immListener);
		checkBoxPrivacy.setChecked(true);
		isPrivacyBoxChecked=checkBoxPrivacy.isChecked();
		checkBoxPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				Toast.makeText(SignUpActivity.this, "checked changed : "+checkBox.isChecked(), Toast.LENGTH_SHORT).show();				
				isPrivacyBoxChecked=checkBoxPrivacy.isChecked();
			}
		});
		privacyText=(TextView)findViewById(R.id.text_checkbox_privacy);
		String str1 = "자세히";
		privacyText.setText(Html.fromHtml("<u>" + str1 + "</u>"));
		privacyText.setOnClickListener(immListener);
		
//===================================		
		
		idView = (EditText)findViewById(R.id.sign_edit_id);
		idView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
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
		passwordView = (EditText)findViewById(R.id.sign_edit_pw);
		passwordView.setOnEditorActionListener(mEditorListener);
		passwordView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
				if(text.length() < 8){
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
		
//		group = (RadioGroup)findViewById(R.id.radioGroup_sex);
////		picker = (DatePicker)findViewById(R.id.datePicker1);
//		findViewById(R.id.radio_male).setOnClickListener(immListener);
//		findViewById(R.id.radio_female).setOnClickListener(immListener);
//		group.setOnClickListener(immListener);
		
		
		Button btn = (Button)findViewById(R.id.btn_send);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String id = idView.getText().toString();
				final String password = passwordView.getText().toString();
				userData.userId = idView.getText().toString();
				userData.pw = passwordView.getText().toString();
				if(!isTermBoxChecked){
					Toast.makeText(SignUpActivity.this, "쓰담 이용 약관 동의를 체크해주세요", Toast.LENGTH_SHORT).show();
				}else if(!isPrivacyBoxChecked){
					Toast.makeText(SignUpActivity.this, "개인정보 수집 및 사용 동의를 체크해주세요", Toast.LENGTH_SHORT).show();
				}else if(!userData.userId.contains("@")){
					Toast.makeText(SignUpActivity.this, "이메일 형식을 확인해주세요", Toast.LENGTH_SHORT).show();
				}else if(userData.pw.length()<8){
					Toast.makeText(SignUpActivity.this, "비밀번호를 8자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
				} else if(userData.sex == 0){
					Toast.makeText(SignUpActivity.this, "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
				} else if(userData.birth == 0 ){
					Toast.makeText(SignUpActivity.this, "생일년도를 선택해주세요", Toast.LENGTH_SHORT).show();
				} else {

					NetworkManager.getInstance().putSdamRegister(SignUpActivity.this, userData, 
							new OnResultListener<RegisterInfo>() {
								@Override
								public void onSuccess(Request request, RegisterInfo result) {
									if(result.success == RegisterInfo.COMMON_INFO_SUCCESS){
										PropertyManager.getInstance().setUserId(userData.userId);
										PropertyManager.getInstance().setPassword(userData.pw);
										PropertyManager.getInstance().setSex(userData.sex);
										PropertyManager.getInstance().setBirth(userData.birth);
										PropertyManager.getInstance().setAlarm(1);
										Bundle b = new Bundle();
										b.putSerializable("userData", userData);
										SignUpDialogFragment f = new SignUpDialogFragment();
										f.setArguments(b);
										f.show(getSupportFragmentManager(), "dialog");
									} else { // success == zero
										Toast.makeText(SignUpActivity.this, "회원가입에 실패하였습니다.\n아이디 및 패스워드를 확인해주세요.", Toast.LENGTH_SHORT).show();
									}
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									Toast.makeText(SignUpActivity.this, "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
								}
					});
					
					
//					Bundle b = new Bundle();
//					b.putSerializable("userData", userData);
//					SignUpDialogFragment f = new SignUpDialogFragment();
//					f.setArguments(b);
//					f.show(getSupportFragmentManager(), "dialog");
					
//					userData.birth = picker.getYear();
					
					
//					final String[] alertItems = {"쓰담 시작하기", "Goto LoginActivity", "Show Google"};
//					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
//					builder.setIcon(R.drawable.a_launcher_1_icon_notification);
//					builder.setTitle("이메일 인증 후 회원가입");
//					builder.setItems(alertItems, new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							switch(which){
//							case 0:
//								//회원가입 처리 후 로그인
//								Toast.makeText(SignUpActivity.this, "case 0", Toast.LENGTH_SHORT ).show();
//								NetworkManager.getInstance().putSdamRegister(SignUpActivity.this, userData,
//										new OnResultListener<RegisterInfo>() {
//											
//											@Override
//											public void onSuccess(RegisterInfo result) {
//												if(result.success == RegisterInfo.REGISTER_SUCCESS){
//													Toast.makeText(SignUpActivity.this, "/register :onSuccess", Toast.LENGTH_SHORT ).show();
//													PropertyManager.getInstance().setUserId(userData.userId);
//													PropertyManager.getInstance().setPassword(userData.pw);
//													PropertyManager.getInstance().setSex(userData.sex);
//													PropertyManager.getInstance().setBirth(userData.birth);
//													
//													NetworkManager.getInstance().putSdamLogin(SignUpActivity.this, userData.userId, userData.pw,
////															"37.4762397",
////															"126.9583907",
//															PropertyManager.getInstance().getLatitude(),
//															PropertyManager.getInstance().getLongitude(),
//															new OnResultListener<LoginInfo>() {
//
//																@Override
//																public void onSuccess(LoginInfo result) {
//																	Toast.makeText(SignUpActivity.this, "/login onSuccess", Toast.LENGTH_SHORT).show();
//																	Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//																	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//																	startActivity(intent);
//																	finish();
//																}
//
//																@Override
//																public void onFail(int code) {
//																	Toast.makeText(SignUpActivity.this, "/login onFail:"+code, Toast.LENGTH_SHORT).show();											
//																}
//															});
//												} else {
//													Toast.makeText(SignUpActivity.this, "case 0:userEmail_duplicated", Toast.LENGTH_SHORT).show();
//												}
//									
//											}
//											
//											@Override
//											public void onFail(int code) {
//												Toast.makeText(SignUpActivity.this, "/register :onFail", Toast.LENGTH_SHORT ).show();
//											}
//										});
//		
//								break;
//							case 1:
//								Toast.makeText(SignUpActivity.this, "case 1", Toast.LENGTH_SHORT ).show();
//								NetworkManager.getInstance().putSdamRegister(SignUpActivity.this, userData,
//										new OnResultListener<RegisterInfo>() {
//											
//											@Override
//											public void onSuccess(RegisterInfo result) {
//												if(result.success==RegisterInfo.REGISTER_SUCCESS){
//													Toast.makeText(SignUpActivity.this, "/register :onSuccess", Toast.LENGTH_SHORT ).show();
//													PropertyManager.getInstance().setUserId(userData.userId);
//													PropertyManager.getInstance().setPassword(userData.pw);
//													PropertyManager.getInstance().setSex(userData.sex);
//													PropertyManager.getInstance().setBirth(userData.birth);
//													Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//													intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//													startActivity(intent);
//													finish();
//												} else {
//													Toast.makeText(SignUpActivity.this, "case 1 : userEmail_duplicated", Toast.LENGTH_SHORT).show();
//												}
//												
//											}
//											
//											@Override
//											public void onFail(int code) {
//												Toast.makeText(SignUpActivity.this, "/register :onFail"+code, Toast.LENGTH_SHORT ).show();
//											}
//										});
//								break;
//						
//							case 2:
//								Toast.makeText(SignUpActivity.this, "case 2", Toast.LENGTH_SHORT ).show();
//								NetworkManager.getInstance().putSdamRegister(SignUpActivity.this, userData,
//										new OnResultListener<RegisterInfo>() {
//											
//											@Override
//											public void onSuccess(RegisterInfo result) {
//												Toast.makeText(SignUpActivity.this, "/register :onSuccess", Toast.LENGTH_SHORT ).show();
//												PropertyManager.getInstance().setUserId(userData.userId);
//												PropertyManager.getInstance().setPassword(userData.pw);
//												PropertyManager.getInstance().setSex(userData.sex);
//												PropertyManager.getInstance().setBirth(userData.birth);
//												Intent intentWebView = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
//												intentWebView.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//												startActivity(intentWebView);
//												finish();
//											}
//											
//											@Override
//											public void onFail(int code) {
//												Toast.makeText(SignUpActivity.this, "/register :onFail"+code, Toast.LENGTH_SHORT ).show();
//											}
//										});
//								break;
//							default:
//								break;
//
//							}
//						}
//						
//					});
//					builder.create().show();
				

				}
								
				
			}//onClick
		});
		
//		int selectedId = group.getCheckedRadioButtonId();
//		showSelectedRadie(selectedId);
//		group.check(R.id.radio_male);
//		
//		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				showSelectedRadie(checkedId);
//			}
//		});
//		
//		picker.init(1988, 3, 12, new OnDateChangedListener() {
//			
//			@Override
//			public void onDateChanged(DatePicker view, int year, int monthOfYear,
//					int dayOfMonth) {
//				userData.birth=year;
//				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//				Log.i(TAG,"" + year + "-" + monthOfYear + "-" + dayOfMonth);				
//			}
//		});
//		
	
		
	}
//	private void showSelectedRadie(int id){
//		switch(id){
//		case R.id.radio_male :
//			userData.sex=1;
//			Toast.makeText(this, "select : 남", Toast.LENGTH_SHORT).show();
//			break;
//		case R.id.radio_female :
//			userData.sex=2;
//			Toast.makeText(this, "select : 여", Toast.LENGTH_SHORT).show();
//			break;
//		}
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.sign_up, menu);
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
	public OnEditorActionListener mEditorListener = new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			switch(v.getId()){
//			case R.id.sign_edit_id:
//				if(actionId == EditorInfo.IME_ACTION_SEND){
//					///....
//					imm.ne
//					return true;	
//				}
//				return false;
			case R.id.sign_edit_pw:
				if(actionId == EditorInfo.IME_ACTION_SEND){
					///....
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					return true;	
				}
				return false;
				default:
					break;
			}
			return false;
		}
	};
	public OnClickListener immListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.spinner_birth:
			case R.id.spinner_gender:
			case R.id.checkBox1:
			case R.id.checkBox_privacy:
//			case R.id.datePicker1:
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				break;
			case R.id.text_checkbox:
				Intent intentWebView = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.naver.com/sdamkorea/220310795077"));
				startActivity(intentWebView);
				break;
			case R.id.text_checkbox_privacy:
				Intent intentWebView2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.naver.com/sdamkorea/220315869559"));
				startActivity(intentWebView2);
				break;
			}
		}
	};
//	private void initData(){
//		String [] array = getResources().getStringArray(R.array.spinner_gender_item);
//		genderAdapter.addAll(Arrays.asList(array));
//		
//		array = getResources().getStringArray(R.array.spinner_year_item);
//		birthAdapter.addAll(Arrays.asList(array));
//		
//	}
	
	
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
//		if(name.equals("EditText")){
//			int[] ids = {android.R.attr.fontFamily};
//			TypedArray ta = context.obtainStyledAttributes(attrs, ids);
//			String fontName = ta.getString(0);
//			ta.recycle();
//			Typeface tf = FontManager.getInstance().getTypeface(context, fontName);
//			if( tf != null) {
//				EditText et = new EditText(context, attrs);
//				et.setTypeface(tf);
//				return et;
//			}
//		}
////		if(name.equals("Spinner")){
////			int[] ids = {android.R.attr.fontFamily};
////			TypedArray ta = context.obtainStyledAttributes(attrs, ids);
////			String fontName = ta.getString(0);
////			ta.recycle();
////			Typeface tf = FontManager.getInstance().getTypeface(context, fontName);
////			if( tf != null) {
////				Spinner et = new Spinner(context, attrs);
////				
////				et.setTypeface(tf);
////				return et;
////			}
////		}
//		return super.onCreateView(name, context, attrs);
//	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(SignUpActivity.this, SplashBActivity.class);
		startActivity(intent);
		finish();
	}
}
