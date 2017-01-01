package kr.me.sdam.dialogs;

import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.R;
import kr.me.sdam.detail.Detail3Article;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.mypage.favor.Favor2Result;
import kr.me.sdam.mypage.favor.FavorAdapter;
import kr.me.sdam.report.ReportInfo;
import kr.me.sdam.search.MySearchAdapter;
import kr.me.sdam.search.SearchResult;
import kr.me.sdam.tabone.TabOneAdapter;
import kr.me.sdam.tabone.TabOneResult;
import kr.me.sdam.tabthree.TabThreeAdapter;
import kr.me.sdam.tabthree.TabThreeResult;
import kr.me.sdam.tabtwo.TabTwoAdapter;
import kr.me.sdam.tabtwo.TabTwoResult;
import okhttp3.Request;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class ReportOneDialogFragment extends DialogFragment {
	
	public ReportOneDialogFragment(){}
	
	TabThreeAdapter mThreeAdapter;
	TabThreeResult mThreeItem;
	
	TabTwoAdapter mTwoAdapter;
	TabTwoResult mTwoItem;
	
	TabOneAdapter mOneAdapter;
	TabOneResult mOneItem;
	
	MySearchAdapter mSearchAdapter;
	SearchResult mSearchItem;
	
	FavorAdapter mFavorAdapter;
	Favor2Result mFavorItem;
	
	Detail3Article mDetailItem;
	int type;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
		Bundle b = getArguments();
		if(b != null){
			type = b.getInt("curruenttab");
			switch(type){
			case 0:
				mDetailItem = (Detail3Article)b.getSerializable("reporteditem");
				break;
			case 1:
				mOneAdapter = (TabOneAdapter)b.getSerializable("reportedadapter");
				mOneItem = (TabOneResult)b.getSerializable("reporteditem");
				break;
			case 2:
				mTwoAdapter = (TabTwoAdapter)b.getSerializable("reportedadapter");
				mTwoItem = (TabTwoResult)b.getSerializable("reporteditem");
				break;
			case 3:
				mThreeAdapter = (TabThreeAdapter)b.getSerializable("reportedadapter");
				mThreeItem = (TabThreeResult)b.getSerializable("reporteditem");
				break;
			case 4:
				mSearchAdapter = (MySearchAdapter)b.getSerializable("reportedadapter");
				mSearchItem = (SearchResult)b.getSerializable("reporteditem");
				break;
			case 6:
				mFavorAdapter = (FavorAdapter)b.getSerializable("reportedadapter");
				mFavorItem = (Favor2Result)b.getSerializable("reporteditem");
				break;
			}
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
		
		View view = inflater.inflate(R.layout.dialog_report_one_layout, container, false);
		Button btn = (Button)view.findViewById(R.id.btn_dialog_cancel);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btn = (Button)view.findViewById(R.id.btn_dialog_ok);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(type == 1){
					NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
					mOneItem.num, Integer.parseInt(mOneItem.category), mOneItem.writer,
					new OnResultListener<ReportInfo>() {
						
						@Override
						public void onSuccess(Request request, ReportInfo result) {
							mOneAdapter.remove(mOneItem);
							Toast.makeText(getActivity(), "신고 처리가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
							dismiss();
						}
						
						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							dismiss();
						}
					});
				} else if( type == 2){
					NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
					mTwoItem.num, Integer.parseInt(mTwoItem.category), mTwoItem.writer,
					new OnResultListener<ReportInfo>() {
						
						@Override
						public void onSuccess(Request request, ReportInfo result) {
							mTwoAdapter.remove(mTwoItem);
							Toast.makeText(getActivity(), "신고 처리가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
							dismiss();
						}
						
						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							dismiss();
						}
					});
				}
				
				else if(type == 3){
					NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
					mThreeItem.num, Integer.parseInt(mThreeItem.category), mThreeItem.writer,
					new OnResultListener<ReportInfo>() {
						
						@Override
						public void onSuccess(Request request, ReportInfo result) {
							mThreeAdapter.remove(mThreeItem);
							Toast.makeText(getActivity(), "신고 처리가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
							dismiss();
						}
						
						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							dismiss();
						}
					});
				}//type == 3
				
				else if(type == 4){
					NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
					mSearchItem.num, Integer.parseInt(mSearchItem.category), mSearchItem.writer,
					new OnResultListener<ReportInfo>() {
						
						@Override
						public void onSuccess(Request request, ReportInfo result) {
							mSearchAdapter.remove(mSearchItem);
							Toast.makeText(getActivity(), "신고 처리가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
							dismiss();
						}
						
						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							dismiss();
						}
					});
				}//type == 4
				
				else if(type == 6){
					NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
					mFavorItem.num, Integer.parseInt(mFavorItem.category), mFavorItem.writer,
					new OnResultListener<ReportInfo>() {
						
						@Override
						public void onSuccess(Request request, ReportInfo result) {
							mFavorAdapter.remove(mFavorItem);
							Toast.makeText(getActivity(), "신고 처리가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
							dismiss();
						}
						
						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							dismiss();
						}
					});
				}//type == 6
				
				else if(type == 0){
					NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
							mDetailItem.num, Integer.parseInt(mDetailItem.category), mDetailItem.writer,
							new OnResultListener<ReportInfo>() {

								@Override
								public void onSuccess(Request request, ReportInfo result) {
									Toast.makeText(getActivity(), "신고 처리가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
									((DetailActivity)getActivity()).finish();
									dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									dismiss();
								}
							});
				}
			}//onClick
		});
//		initData(); //여기에서 알람 데이터목록 추가함
//		setCancelable(true);
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
