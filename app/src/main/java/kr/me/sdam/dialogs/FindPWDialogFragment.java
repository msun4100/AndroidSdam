package kr.me.sdam.dialogs;

import kr.me.sdam.NetworkManager;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.autologin.FindPWInfo;
import kr.me.sdam.autologin.SplashActivity;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.database.DBManager;
import kr.me.sdam.mypage.WithdrawInfo;
import okhttp3.Request;

import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindPWDialogFragment extends android.app.DialogFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
		Bundle b = getArguments();
		if (b != null) {
//			userId = b.getString("userId");
		}

	}

	String userId;
	EditText editpw;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);

		View view = inflater.inflate(R.layout.dialog_find_pw_layout, container,
				false);

		editpw = (EditText) view.findViewById(R.id.edit_dialog_pw);

		Button btn = (Button) view.findViewById(R.id.btn_dialog_cancel);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btn = (Button) view.findViewById(R.id.btn_dialog_ok);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String email = editpw.getText().toString();
				final ProgressDialog dialog = new ProgressDialog(getActivity());
				dialog.setMessage("이메일 전송중입니다...");
				dialog.setCancelable(false);
				dialog.show();
				NetworkManager.getInstance().putSdamFindPW(getActivity(),
						email, new OnResultListener<FindPWInfo>() {

							@Override
							public void onSuccess(Request request, FindPWInfo result) {
								dialog.setCancelable(true);
								if (result != null && result.success == CommonInfo.COMMON_INFO_SUCCESS) {
									Toast.makeText(getActivity(), "이메일 전송이 완료되었습니다.", Toast.LENGTH_SHORT).show();
									dialog.dismiss();
								} else {
									Toast.makeText(getActivity(), "이메일 전송이 실패하였습니다.\n쓰담아이디와 동일한 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
//									dialog.setCancelable(true);
									dialog.dismiss();
								}
								dismiss();
							}

							@Override
							public void onFailure(Request request, int code, Throwable cause) {
								Toast.makeText(getActivity(), "이메일 전송에 실패하였습니다.#"+code,
										Toast.LENGTH_SHORT).show();
								dialog.setCancelable(true);
								dialog.dismiss();
								dismiss();
							}
						});

			}// onClick
		});

		// initData(); //여기에서 알람 데이터목록 추가함
		return view;
	}

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
