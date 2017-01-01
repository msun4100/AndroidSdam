package kr.me.sdam.dialogs;

import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import android.app.ActionBar.LayoutParams;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

public class UsingLocationDialogFragment extends DialogFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
		Bundle b = getArguments();
		if (b != null) {

		}
	}

	TextView titleView;
	TextView subTitleView;
	TextView refeatView;
	CheckBox checkBox;
	boolean isBoxChecked;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);
		// getDialog().getWindow().setLayout(300,300);
		// getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
		// getDialog().getWindow().getAttributes().x = 120;
		// getDialog().getWindow().getAttributes().y = -18;

		View view = inflater.inflate(R.layout.dialog_using_location_layout, container,
				false);
		titleView = (TextView) view.findViewById(R.id.text_dialog_title);
		subTitleView = (TextView) view.findViewById(R.id.text_dialog_sub_title);
		Button btn = (Button) view.findViewById(R.id.btn_dialog_cancel);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PropertyManager.getInstance().setUsingLocation(0);;
				dismiss();
			}
		});
		btn = (Button) view.findViewById(R.id.btn_dialog_ok);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				PropertyManager.getInstance().setFirstVisit(1);
//				int lockRequestCnt;
//				if (PropertyManager.getInstance().getLockPassword().length() == 4) {
//					lockRequestCnt = 0;
//				} else {
//					lockRequestCnt = 1;
//				}
////				dismiss();
				PropertyManager.getInstance().setUsingLocation(1);//위치 정보를 켠다고 가정. 설정창들어갓다가 껏을때 다이얼로그가 또뜨지 않게
//				if(isBoxChecked) {
//					PropertyManager.getInstance().setUsingLocation(2);//다시 보지않기
//				} 
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
				dismiss();
				
			}// onClick
		});
		String str = "다시 보지 않기";
		refeatView = (TextView)view.findViewById(R.id.text_dialog_refeat);
		refeatView.setText(Html.fromHtml("<u>" + str + "</u>"));
		refeatView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				checkBox.setChecked(!checkBox.isChecked());
				PropertyManager.getInstance().setUsingLocation(2);
				dismiss();
			}
		});
		checkBox = (CheckBox)view.findViewById(R.id.chk_location); //gone처리됨
		checkBox.setChecked(false);
		isBoxChecked=checkBox.isChecked();
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				Toast.makeText(SignUpActivity.this, "checked changed : "+checkBox.isChecked(), Toast.LENGTH_SHORT).show();				
				isBoxChecked=checkBox.isChecked();
				if(isChecked){
//					PropertyManager.getInstance().setUsingLocation(1);
					isBoxChecked = true;
//					Toast.makeText(getActivity(), "1111111111", Toast.LENGTH_SHORT).show();
				} else {
//					PropertyManager.getInstance().setUsingLocation(0);
					isBoxChecked = false;
//					Toast.makeText(getActivity(), "0000000000", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
//		checkText=(TextView)findViewById(R.id.text_checkbox);
//		String str = "자세히";
//		checkText.setText(Html.fromHtml("<u>" + str + "</u>"));
//		checkText.setOnClickListener(immListener);
		
		
		// initData(); //여기에서 알람 데이터목록 추가함
		return view;
	}
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		((MypageActivity)getActivity()).onActivityResult(requestCode, resultCode, data);
//		dismiss();
//	}
//	public void setMessage(String title, String subTitle) {
//		titleView.setText(title);
//		subTitleView.setText(subTitle);
//		
//	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		// getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
		// R.drawable.ic_launcher);
		// getDialog().setTitle("Custom Dialog");
	}

	private void initData() {
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}
}
