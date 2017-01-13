package kr.me.sdam;

import kr.me.sdam.seekbars.VerticalSeekBar_Reverse;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

@SuppressLint("ResourceAsColor")
public class MenuFragment extends Fragment {
	public static final int MENU_INVALID = -1;
	public static final int MENU_MAIN = 0;

	public interface MenuCallback {
		public void selectMenu(int menuId);
	}

	MenuCallback callback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof MenuCallback) {
			callback = (MenuCallback) activity;
		} else {
			// Throws...
		}
	}

	private static final String TAG = MenuFragment.class.getSimpleName();
	VerticalSeekBar_Reverse verticalSeekBar = null;
	TextView vsProgress = null;

	private static final int MAX_PROGRESS = 200;
	boolean bSeekStart = false;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sliding_distance, container, false);
		vsProgress = (TextView) view.findViewById(R.id.text_progress);
		verticalSeekBar = (VerticalSeekBar_Reverse) view.findViewById(R.id.vertical_seekbar);
		//setMax할 때 reverse 클래스 onTouchEvent값 수정해야 됨
		setVerticalSeekBar();	// onResume에서 호출--> 세팅에서 수정한 값도 반영 되도록
		verticalSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//					private static final int NOT_CHANGED = -1;
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// if(progress != NOT_CHANGED) {
						// Toast.makeText(getActivity(), "progress"+progress,
						// Toast.LENGTH_SHORT).show();
						// progressView.setProgress(seekBar.getProgress());
						// Toast.makeText(getActivity(),
						// "progress"+seekBar.getProgress(),
						// Toast.LENGTH_SHORT).show();
						// Toast.makeText(getActivity(), "stop:inner",
						// Toast.LENGTH_SHORT).show();
						//
						// }
						// Toast.makeText(getActivity(), "stop:outer",
						// Toast.LENGTH_SHORT).show();
						// bSeekStart = false;
						// progressView.setProgress(seekBar.getProgress());
						// setMessage(""+seekBar.getProgress()); //여기 함수 안먹음.
						// setMessage(""+progress);
						Log.e(TAG, "onStopTrackingTouch: " + seekBar.getProgress() );
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						Log.e(TAG, "onStartTrackingTouch: " + seekBar.getProgress());
						PropertyManager.getInstance().setDistance(seekBar.getProgress());	//리버스로 돌렸으니까 스타트가 stop 함수
						// Toast.makeText(getActivity(), "onstart",
						// Toast.LENGTH_SHORT).show();
						// progress = NOT_CHANGED;
						//
						// bSeekStart = true;
					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if(progress>=100){
							vsProgress.setText("  "+progress );
						} else if(progress < 100){
							vsProgress.setText("   "+progress );
						} 
						if(progress >= MAX_PROGRESS){
							vsProgress.setText("담너머");
						}
//						PropertyManager.getInstance().setDistance(progress);

					}
				});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		setVerticalSeekBar();
	}

	private void setVerticalSeekBar(){
		verticalSeekBar.setMax(MAX_PROGRESS);  
		int initNum = (PropertyManager.getInstance().getDistance());
		verticalSeekBar.setProgress(initNum);
		
		if(initNum >= 100){
			vsProgress.setText("  "+initNum );
		} else if(initNum < 100){
			vsProgress.setText("   "+initNum);
		} 
		if(verticalSeekBar.getProgress() >= MAX_PROGRESS){
			vsProgress.setText("담너머");
		}
		
//		verticalSeekBar.setBackgroundColor(R.color.b_main_seekbar_bg);
		
	}
	public void setMessage(String str) {
		vsProgress.setText(str);
	}

	public int getDistance() {
		return Integer.parseInt(vsProgress.getText().toString());
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((MyApplication)((MainActivity)getActivity()).getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.enableAdvertisingIdCollection(true);
		t.setScreenName("MenuFragment");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
	}
	
	@Override
	public void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
	}
	
}
