package kr.me.sdam.dialogs;

import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.mypage.MypageActivity;
import kr.me.sdam.mypage.PasswordActivity;
import kr.me.sdam.mypage.SettingMenuFragment;
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
import android.widget.TextView;

public class LockDialogFragment extends DialogFragment {

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

		View view = inflater.inflate(R.layout.dialog_lock_layout, container,
				false);
		titleView = (TextView) view.findViewById(R.id.text_dialog_title);
		subTitleView = (TextView) view.findViewById(R.id.text_dialog_sub_title);
		Button btn = (Button) view.findViewById(R.id.btn_dialog_cancel);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PropertyManager.getInstance().setFirstVisit(1);
				dismiss();
			}
		});
		btn = (Button) view.findViewById(R.id.btn_dialog_ok);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PropertyManager.getInstance().setFirstVisit(1);
				int lockRequestCnt;
				if (PropertyManager.getInstance().getLockPassword().length() == 4) {
					lockRequestCnt = 0;
				} else {
					lockRequestCnt = 1;
				}
				Intent intent = new Intent(getActivity(),
						PasswordActivity.class);
				intent.putExtra("activity", 0);
				intent.putExtra("type", lockRequestCnt);
//				getActivity().startActivityForResult(intent, 1);
				startActivityForResult(intent, 0);
//				dismiss();
				
			}// onClick
		});

		// initData(); //여기에서 알람 데이터목록 추가함
		return view;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		((MypageActivity)getActivity()).onActivityResult(requestCode, resultCode, data);
		dismiss();
	}
	public void setMessage(String title, String subTitle) {
		titleView.setText(title);
		subTitleView.setText(subTitle);
		
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
