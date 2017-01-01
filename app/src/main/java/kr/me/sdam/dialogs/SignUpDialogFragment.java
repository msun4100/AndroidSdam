package kr.me.sdam.dialogs;

import kr.me.sdam.MainActivity;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.autologin.LoginInfo;
import kr.me.sdam.autologin.RegisterInfo;
import kr.me.sdam.autologin.RegisterInfoData;
import kr.me.sdam.autologin.SignUpActivity;
import kr.me.sdam.autologin.SplashBActivity;
import kr.me.sdam.common.CommonInfo;
import okhttp3.Request;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class SignUpDialogFragment extends DialogFragment{
	private static final String TAG = SignUpDialogFragment.class.getSimpleName();
	RegisterInfoData userData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
		Bundle b = getArguments();
		if(b != null){
			userData = (RegisterInfoData)b.getSerializable("userData");
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);
//		getDialog().getWindow().setLayout(300,300);
//		getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
//		getDialog().getWindow().getAttributes().x = 120;
//		getDialog().getWindow().getAttributes().y = -18;
		
		View view = inflater.inflate(R.layout.dialog_signup_layout, container, false);
	
		Button btn = (Button)view.findViewById(R.id.btn_dialog_ok);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NetworkManager.getInstance().putSdamLogin(getActivity(),
						userData.userId, userData.pw,
						PropertyManager.getInstance().getLatitude(),
						PropertyManager.getInstance().getLongitude(),
						new OnResultListener<LoginInfo>() {

							@Override
							public void onSuccess(Request request, LoginInfo result) {
								if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
									Log.e(TAG, "onSuccess: "+result.work );
									Intent intent = new Intent(getActivity(), MainActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
									dismiss();	
								} else {
									//login이 실패 할 경우 splashB로 이동
									Log.e(TAG, "onSuccess: "+result.work );
									Toast.makeText(getActivity(), "로그인에 실패하였습니다.\n다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
									Intent intent = new Intent(getActivity(), SplashBActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
									dismiss();
								}
								
							}

							@Override
							public void onFailure(Request request, int code, Throwable cause) {
								Toast.makeText(getActivity(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();											
								Intent intent = new Intent(getActivity(), SplashBActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
								dismiss();
							}
						});
				
			}//onClick
		});
//		initData(); //여기에서 알람 데이터목록 추가함
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
//		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
//		getDialog().setTitle("Custom Dialog");
	}
	private void initData(){
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
