package kr.me.sdam.detail.viewpager;

import kr.me.sdam.R;
import kr.me.sdam.detail.DetailActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TextFragment extends Fragment {
	int position;
	public static final String EXTRA_POSITION = "position";
	
	Context mContext;
	public TextFragment(){}
//	public TextFragment(Context context) {
//		mContext = context;
//	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		if( b != null) {
			position = b.getInt(EXTRA_POSITION, 0);
		}
	}
	
	TextView vpDescView;
	String content = null;
	int scrollamount;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
		vpDescView = (TextView)view.findViewById(R.id.text_vp_desc);
		
		
		//==========텍스트뷰 스크롤
		content = ((DetailActivity)getActivity()).getContent();
		
//		vpDescView.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//
//				scrollamount = vpDescView.getLayout().getLineTop(vpDescView.getLineCount()) - vpDescView.getHeight();
////				Log.i("on linetop:", ""+vpDescView.getLayout().getLineTop(vpDescView.getLineCount()));
////				scrollamount = vpDescView.getLineCount();
////				Log.i("amount", ""+scrollamount);
//				int lineCount = vpDescView.getLineCount();
//				if( lineCount > 12){
//					vpDescView.scrollTo(0, scrollamount);
////					DetailActivity.headerPager.requestDisallowInterceptTouchEvent(false);
////					Log.i("on T:", ""+scrollamount);
//					return true;
//				} 
//				vpDescView.scrollTo(0, 0);
////				DetailActivity.headerPager.requestDisallowInterceptTouchEvent(true);
////				Log.i("on F:", ""+scrollamount);
//				return false;
//			}
//		});
		
		
//		vpDescView.setMaxLines(100);
		vpDescView.setVerticalScrollBarEnabled(true);
//		vpDescView.setHorizontalScrollBarEnabled(false);
		vpDescView.setMovementMethod(new ScrollingMovementMethod());
		
		vpDescView.setText(((DetailActivity)getActivity()).strArr[position]);

//		scrollamount = vpDescView.getLayout().getLineTop(vpDescView.getLineCount()) - vpDescView.getHeight();
//		if(scrollamount > vpDescView.getHeight()){
//			vpDescView.scrollTo(0, scrollamount);
//			
//		}
		
		
		
		
		return view;
	}
}
