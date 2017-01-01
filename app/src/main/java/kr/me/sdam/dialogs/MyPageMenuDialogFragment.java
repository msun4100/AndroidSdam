package kr.me.sdam.dialogs;

import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.mypage.mylist.MyList0Hide;
import kr.me.sdam.mypage.mylist.MyList2Result;
import kr.me.sdam.mypage.mylist.MyListAdapter;
import okhttp3.Request;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class MyPageMenuDialogFragment extends DialogFragment {
	Bundle tb;

	MyListAdapter mMyListAdapter;
	MyList2Result mMyListItem;

	int type;
	int responseNum;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
		Bundle b = getArguments();
		if (b != null) {
			mMyListAdapter = (MyListAdapter)b.getSerializable("mypageAdapter");
			mMyListItem = (MyList2Result)b.getSerializable("mypageItem");
			type = b.getInt("mypageType");
			responseNum = b.getInt("mypageNum");
		}
	}

	View report1, report2;
	TextView textReport1;
	Message msg;

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

		View view = inflater.inflate(R.layout.dialog_mypage_menu_layout,
				container, false);

		report1 = (View) view.findViewById(R.id.layout_report1);
		report1.setOnClickListener(reportListener);
		report2 = (View) view.findViewById(R.id.layout_report2);
		report2.setOnClickListener(reportListener);
		textReport1 = (TextView)view.findViewById(R.id.text_report1);
		if(mMyListItem.locked==1){ //클릭시 이미 1된 상태로 넘어오니까 
			textReport1.setText("비밀글 해제하기");
//			mMyListItem.locked=0;
		} else {
			textReport1.setText("비밀글 설정하기");
//			mMyListItem.locked=1;
		}
		return view;
	}

	public OnClickListener reportListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layout_report1:
				if(mMyListItem.locked==0){
					mMyListItem.locked=1;
				} else {
					mMyListItem.locked=0;
				}
				NetworkManager.getInstance().putSdamHide(getActivity(),
						mMyListItem.num, mMyListItem.locked, 
						new OnResultListener<MyList0Hide>() {
							
							@Override
							public void onSuccess(Request request, MyList0Hide result) {
								if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
									if(mMyListItem.locked==1){
										mMyListAdapter.notifyDataSetChanged();
										Toast.makeText(MyApplication.getContext(), "비밀글이 설정되었습니다.", Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(MyApplication.getContext(), "비밀글이 해제되었습니다.", Toast.LENGTH_SHORT).show();
										mMyListAdapter.notifyDataSetChanged();
									}
									
								}
							}
							
							@Override
							public void onFailure(Request request, int code, Throwable cause) {
								Toast.makeText(MyApplication.getContext(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
							}
						});
				
				dismiss();
				break;
			case R.id.layout_report2:
				Bundle b = new Bundle();
				b.putSerializable("deletedItem", mMyListItem);
				b.putSerializable("deletedadapter", mMyListAdapter);
				b.putInt("responseNum", responseNum);
				b.putInt("activityType", type); // 0==댓글, 1,2,3==탭게시물
				DeleteDialogFragment f = new DeleteDialogFragment();
				f.setArguments(b);
				f.show(getActivity().getSupportFragmentManager(), "deletereplydialog");
				dismiss();
				break;
			default:
				break;
			}

		}
	};

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
