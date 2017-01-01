package kr.me.sdam.write;

import kr.me.sdam.BgImage;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import kr.me.sdam.R;
import kr.me.sdam.mypage.MypageActivity;

public class WriteOneFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	ImageView[] categories = new ImageView[BgImage.BG_CATEGORY_SIZE];
	ActionBar actionBar;
	MenuItem menuItem;
	
	View view;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_write_one, container, false);
		
		menuItem = (MenuItem)view.findViewById(R.id.complete);
//		default image설정
		((WriteActivity)getActivity()).layoutView.setBackgroundResource(R.drawable.d_write_bg);
		((WriteActivity)getActivity()).selectedIcon.setVisibility(View.GONE);
		initData();
		
		
		return view;
	}
	@Override
	public void onResume() {
		super.onResume();
		actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");
	}
	private void initData(){
		int index = R.id.category_1;
		int bgResource = R.drawable.d_write_bg;
		for(int i=0; i< BgImage.BG_CATEGORY_SIZE; i++) {
			categories[i] = (ImageView)view.findViewById(index);
			categories[i].setOnClickListener(categoryListener);//리스너 설정
			
//			categories[i].setImageResource(bgResource);//카테고리 이미지 설정
			index++;
		}
	}

	public ActionBar getActionBar() {
		if (actionBar == null) {
			actionBar = ((AppCompatActivity) getActivity
			()).getSupportActionBar();
		}
		return actionBar;
	}

	MenuItem menuNext;
	ImageView imageNext;
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.f1_menu, menu);
		
		menuNext = menu.findItem(R.id.complete);
		imageNext = new ImageView(getActivity());
		imageNext.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageNext.setPadding(16, 0, 16, 0);
		imageNext.setImageResource(R.drawable.d_write_next_button);
		imageNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "카테고리를 선택하세요", Toast.LENGTH_SHORT).show();
				((WriteActivity)getActivity()).callWriteOneFragment();
				((WriteActivity)getActivity()).imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		});
		menuNext.setActionView(imageNext);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
//		if (id == R.id.complete) {
//			Toast.makeText(getActivity(), "카테고리를 선택하세요", Toast.LENGTH_SHORT).show();
//			
//			((WriteActivity)getActivity()).callWriteOneFragment();
//			((WriteActivity)getActivity()).imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//			return true;
//		}
		
		if( id == android.R.id.home){
			((WriteActivity)getActivity()).finish();
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	public OnClickListener categoryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.category_1:
//				((WriteActivity)getActivity()).setMessage(""+BgImage.CATEGORY_ONE_DELIGHT);
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_ONE_DELIGHT);
				break;
			case R.id.category_2:
//				((WriteActivity)getActivity()).setMessage(""+BgImage.CATEGORY_TWO_SAD);
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_TWO_SAD);
				break;
			case R.id.category_3:
//				((WriteActivity)getActivity()).setMessage(""+BgImage.CATEGORY_THREE_SURPRISE);
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_THREE_SURPRISE);
				break;
			case R.id.category_4:
//				((WriteActivity)getActivity()).setMessage(""+BgImage.CATEGORY_FOUR_ANGRY);
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_FOUR_ANGRY);
				break;
			case R.id.category_5:
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_FIVE_SHY);
				break;
			case R.id.category_6:
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_SIX_SORRY);
				break;
			case R.id.category_7:
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_SEVEN_TIRED);
				break;
			case R.id.category_8:
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_EIGHT_FUN);
				break;
			case R.id.category_9:
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_NINE_NERVOUS);
				break;
			case R.id.category_10:
				((WriteActivity)getActivity()).callWriteTwoFragment(BgImage.CATEGORY_TEN_PITAPAT);
				break;
			default:
				break;
			}
		}
	};//OnClickListener
}
