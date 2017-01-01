package kr.me.sdam.dialogs;

import kr.me.sdam.R;
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

public class ReportMenuDialogFragment extends DialogFragment {
	
	Bundle tb;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
		Bundle b = getArguments();
		if(b != null){
			tb = new Bundle();
			tb = b;
		}
	}
	
	
	View report1, report2, report3;
	Message msg;
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
		
		View view = inflater.inflate(R.layout.dialog_report_menu_layout, container, false);
		
		
		report1 = (View)view.findViewById(R.id.layout_report1);
		report1.setOnClickListener(reportListener);
		report2 = (View)view.findViewById(R.id.layout_report2);
		report2.setOnClickListener(reportListener);
		report3 = (View)view.findViewById(R.id.layout_report3);
		report3.setOnClickListener(reportListener);
		
//		initData(); //여기에서 알람 데이터목록 추가함
		return view;
	}
	public OnClickListener reportListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.layout_report1:
				ReportOneDialogFragment f = new ReportOneDialogFragment();
				f.setArguments(tb);
				f.show(getFragmentManager(), "dialog");
				dismiss();
				break;
			case R.id.layout_report2:
				ReportTwoDialogFragment f2 = new ReportTwoDialogFragment();
				f2.setArguments(tb);
				f2.show(getFragmentManager(), "dialog");
				dismiss();
				break;
			case R.id.layout_report3:
				ReportThreeDialogFragment f3 = new ReportThreeDialogFragment();
				f3.setArguments(tb);
				f3.show(getFragmentManager(), "dialog");
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
