package kr.me.sdam.write.viewpager;

import kr.me.sdam.R;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.write.WriteActivity;
import kr.me.sdam.write.WriteTwoFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WriteTwoImageFragment extends Fragment {
	int position;
	public static final String EXTRA_POSITION = "position";
	
	Context mContext;
	public WriteTwoImageFragment(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		if( b != null) {
			position = b.getInt(EXTRA_POSITION, 0);
		}
	}
	
	TextView vpDescView;
//	ImageView vpPageIcon;
	String content = null;
	
	ImageView odd;
	ImageView even;
	int oddNum, evenNum;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_write_two_viewpager, container, false);
		odd = (ImageView)view.findViewById(R.id.image_odd);
		even = (ImageView)view.findViewById(R.id.image_even);
		even.setOnClickListener(pagerItemClicklistener);
		odd.setOnClickListener(pagerItemClicklistener);
		oddNum = (position*2) + 1 ;
		evenNum = (position*2) + 0 ;
		even.setImageResource( ((WriteActivity)getActivity()).getImageResIds()[ evenNum ] );
		odd.setImageResource( ((WriteActivity)getActivity()).getImageResIds()[ oddNum ] );
		
		
//		content = ((DetailActivity)getActivity()).getContent();
//		vpDescView.setText(((DetailActivity)getActivity()).strArr[position]);
		return view;
	}
	public OnClickListener pagerItemClicklistener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.image_odd:
				((WriteActivity)getActivity()).setSendImage( oddNum );
				((WriteActivity) getActivity()).layoutView .setBackgroundResource(
						((WriteActivity)getActivity()).getImageResIds()[ oddNum ]	);
				break;
			case R.id.image_even:
				((WriteActivity)getActivity()).setSendImage( evenNum );
				((WriteActivity) getActivity()).layoutView .setBackgroundResource(
						((WriteActivity)getActivity()).getImageResIds()[ evenNum ]	);
				break;
				default:
					break;
			}
			
		}
	};
}
