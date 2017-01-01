package kr.me.sdam.mypage;

import kr.me.sdam.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends Activity {

	
	ImageView num1,num2,num3,num4,num5,num6,num7,num8,num9,num0;
	ImageView closeIcon, countIcon, deleteIcon;
	TextView descView;
	
	EditText countView;
	int type;
	int activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);
		
		Intent intent = getIntent();
		if(intent != null){
			if(intent.getIntExtra("activity", 0)==1){ 
				type=3; //디폴트 잠금암호입력 출력되게
				activity=1; //mypage일때
			} else if(intent.getIntExtra("activity", 0)==0){ //세팅메뉴에서 호출시
				type=intent.getIntExtra("type", 0);
				activity=0; //세팅메뉴에서 호출시
			}
		}
//		type = intent.getIntExtra("type", 0);
//		String message = intent.getStringExtra("message");
		
		deleteIcon = (ImageView)findViewById(R.id.image_pw_delete);
		deleteIcon.setOnClickListener(mListener);
		
		descView = (TextView)findViewById(R.id.text_pw_desc);
		descView.setOnClickListener(mListener);
		setDescViewMessage(type);
		
		closeIcon = (ImageView)findViewById(R.id.image_pw_close);
		closeIcon.setOnClickListener(mListener);
		
		countIcon = (ImageView)findViewById(R.id.image_pw_count);
		countView = new EditText(PasswordActivity.this);
		countView.setText("");
		
		countView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
				if(text.length()<5){
					switch(text.length()){
					case 0:
						countIcon.setImageResource(R.drawable.g_setting_01password_circle_00);
						break;
					case 1:
						countIcon.setImageResource(R.drawable.g_setting_01password_circle_01);
						break;
					case 2:
						countIcon.setImageResource(R.drawable.g_setting_01password_circle_02);
						break;
					case 3:
						countIcon.setImageResource(R.drawable.g_setting_01password_circle_03);
						break;
					case 4:
						countIcon.setImageResource(R.drawable.g_setting_01password_circle_04);
						break;
						default:
							countIcon.setImageResource(R.drawable.g_setting_01password_circle_00);
							break;
					}	
				} else {
					countView.setText("");
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
		
		
		num0 = (ImageView)findViewById(R.id.image_pw_0);
		num0.setOnClickListener(mListener);
		
		num1 = (ImageView)findViewById(R.id.image_pw_1);
		num1.setOnClickListener(mListener);
		
		num2 = (ImageView)findViewById(R.id.image_pw_2);
		num2.setOnClickListener(mListener);
		
		num3 = (ImageView)findViewById(R.id.image_pw_3);
		num3.setOnClickListener(mListener);
		
		num4 = (ImageView)findViewById(R.id.image_pw_4);
		num4.setOnClickListener(mListener);
		
		num5 = (ImageView)findViewById(R.id.image_pw_5);
		num5.setOnClickListener(mListener);
		
		num6 = (ImageView)findViewById(R.id.image_pw_6);
		num6.setOnClickListener(mListener);
		
		num7 = (ImageView)findViewById(R.id.image_pw_7);
		num7.setOnClickListener(mListener);
		
		num8 = (ImageView)findViewById(R.id.image_pw_8);
		num8.setOnClickListener(mListener);
		
		num9 = (ImageView)findViewById(R.id.image_pw_9);
		num9.setOnClickListener(mListener);
		
		
	}

	private void setDescViewMessage(int type) {
		String str = "잠금 암호 입력";
		switch(type){
		case 0:
			str = "기존 암호 입력";
			break;
		case 1:
			str = "새 암호 입력";
			break;
		case 2:
			str = "새 암호 재입력";
			break;
			default:
				str = "잠금 암호 입력";
				break;
		}
		descView.setText(Html.fromHtml("<b>" + str + "</b>"));
	}

	public OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			switch(v.getId()){
			case R.id.image_pw_delete:
				String str = countView.getText().toString();
				int strLen = str.length();
				if(strLen == 0){
					countView.setText("");
				} else {
					countView.setText(str.substring(0, strLen-1));	
				}
//				Toast.makeText(PasswordActivity.this, ""+countView.getText().toString(), Toast.LENGTH_SHORT).show();
				break;
			case R.id.text_pw_desc:
				break;
			case R.id.image_pw_close:
				if(activity==0){
					MypageActivity.lockRequestCnt=1;
					finish();	
				} else if(activity == 1){
					Intent data = new Intent();
					data.putExtra(MypageActivity.RESULT_MYPAGE, ""+99999);
					setResult(Activity.RESULT_OK, data);
					finish();
				}
				
				break;
			case R.id.image_pw_0:
				countView.setText(countView.getText().toString()+""+0);
				break;
			case R.id.image_pw_1:
				countView.setText(countView.getText().toString()+""+1);
				break;
			case R.id.image_pw_2:
				countView.setText(countView.getText().toString()+""+2);
				break;
			case R.id.image_pw_3:
				countView.setText(countView.getText().toString()+""+3);
				break;
			case R.id.image_pw_4:
				countView.setText(countView.getText().toString()+""+4);
				break;
			case R.id.image_pw_5:
				countView.setText(countView.getText().toString()+""+5);
				break;
			case R.id.image_pw_6:
				countView.setText(countView.getText().toString()+""+6);
				break;
			case R.id.image_pw_7:
				countView.setText(countView.getText().toString()+""+7);
				break;
			case R.id.image_pw_8:
				countView.setText(countView.getText().toString()+""+8);
				break;
			case R.id.image_pw_9:
				countView.setText(countView.getText().toString()+""+9);
				break;
			}
			if(countView.getText().toString().length() == 4){
				if(activity == 0){ //menufragment일때
					Intent data = new Intent();
					data.putExtra(SettingMenuFragment.RESULT_LOCK, ""+countView.getText().toString());
					setResult(Activity.RESULT_OK, data);
					finish();	
				} else if(activity == 1){ //mypage일때
					Intent data = new Intent();
					data.putExtra(MypageActivity.RESULT_MYPAGE, ""+countView.getText().toString());
					setResult(Activity.RESULT_OK, data);
					finish();	
				}
				
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.password, menu);
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
