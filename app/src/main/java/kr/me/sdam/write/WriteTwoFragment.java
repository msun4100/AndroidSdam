package kr.me.sdam.write;

import kr.me.sdam.BgImage;
import kr.me.sdam.MainActivity;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.mypage.MypageActivity;
import kr.me.sdam.write.viewpager.WriteTwoPagerAdapter;
import okhttp3.Request;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class WriteTwoFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	ActionBar actionBar;
	Gallery gallery;
	MyGalleryAdapter mAdapter;
//	public int[] imageResIds = new int[BgImage.BG_IMAGE_SIZE];

	int receivedCid;
	
	WriteTwoPagerAdapter mPagerAdapter;
	ViewPager pager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_write_two, container, false);
		((WriteActivity)getActivity()).selectedIcon.setVisibility(View.VISIBLE);
		
		// 선택된 카테고리에 대한 값
		Bundle b = new Bundle();
		b = getArguments();
		receivedCid = Integer.parseInt(b.getString(WriteActivity.BUNDLE_ARG_CATEGORY));

//		gallery = (Gallery) view.findViewById(R.id.gallery1);
//		mAdapter = new MyGalleryAdapter(getActivity());
//		gallery.setAdapter(mAdapter);
		
		
		initData();
		pager = (ViewPager) view.findViewById(R.id.pager);
		
		mPagerAdapter = new WriteTwoPagerAdapter(getFragmentManager(),
				(((WriteActivity)getActivity()).getImageResIds().length / 2) );
		pager.setAdapter(mPagerAdapter);
		pager.setPageMargin(-1);
//		pager.setPageMargin(getResources().getDisplayMetrics().widthPixels / -9);
		pager.setOffscreenPageLimit(5);
		pager.setCurrentItem(0);
		
//==========갤러리로 했던 부분===================
//		int position = Integer.MAX_VALUE / 2;
//		position = (position / imageResIds.length) * imageResIds.length;
//		gallery.setSelection(position);
//
//		gallery.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
////				Toast.makeText(getActivity(), "Item Position # : " + position, Toast.LENGTH_SHORT).show();
//				sendImage = position % imageResIds.length;
//				((WriteActivity) getActivity()).layoutView .setBackgroundResource(imageResIds[position
//								% imageResIds.length]);
//			}
//		});

		return view;
	}

	private void initData() {
		if (receivedCid == BgImage.CATEGORY_ONE_DELIGHT) {
			((WriteActivity)getActivity()).setImageResIds(BgImage.resOne);  
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_01);
		}
		else if (receivedCid == BgImage.CATEGORY_TWO_SAD) {
//			imageResIds = BgImage.resTwo;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resTwo);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_02);
		}
		else if (receivedCid == BgImage.CATEGORY_THREE_SURPRISE) {
//			imageResIds = BgImage.resThree;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resThree);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_03);
		}
		else if (receivedCid == BgImage.CATEGORY_FOUR_ANGRY) {
//			imageResIds = BgImage.resFour;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resFour);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_04);
		}
		else if (receivedCid == BgImage.CATEGORY_FIVE_SHY) {
//			imageResIds = BgImage.resFive;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resFive);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_05);
		}
		else if (receivedCid == BgImage.CATEGORY_SIX_SORRY) {
//			imageResIds = BgImage.resSix;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resSix);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_06);
		}
		else if (receivedCid == BgImage.CATEGORY_SEVEN_TIRED) {
//			imageResIds = BgImage.resSeven;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resSeven);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_07);
		}
		else if (receivedCid == BgImage.CATEGORY_EIGHT_FUN) {
//			imageResIds = BgImage.resEight;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resEight);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_08);
		}
		else if (receivedCid == BgImage.CATEGORY_NINE_NERVOUS) {
//			imageResIds = BgImage.resNine;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resNine);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_09);
		}
		else if (receivedCid == BgImage.CATEGORY_TEN_PITAPAT) {
//			imageResIds = BgImage.resTen;
			((WriteActivity)getActivity()).setImageResIds(BgImage.resTen);
			((WriteActivity)getActivity()).selectedIcon.setImageResource(R.drawable.d_write_3_emoticon_10);
		}
//		for (int i = 0; i < imageResIds.length; i++) {
//			mAdapter.add(imageResIds[i]);
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
		actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("");
	}

	
	MenuItem menuUpload;
	ImageView imageUpload;
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.f2_menu, menu);
		
		menuUpload = menu.findItem(R.id.upload);
		imageUpload = new ImageView(getActivity());
		imageUpload.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		imageUpload.setPadding(16, 0, 16, 0);
		imageUpload.setImageResource(R.drawable.d_write_next_button_ok);
		imageUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendParams.category = "" + receivedCid;
//				sendParams.content = text;
				sendParams.content = ((WriteActivity)getActivity()).getInputString();
//				sendParams.image = "" + sendImage;
				sendParams.image = "" + ((WriteActivity)getActivity()).getSendImage();
				sendParams.emotion = "" + receivedCid;
				sendParams.lock = ((WriteActivity)getActivity()).getLock();
				sendParams.unlocate = ((WriteActivity)getActivity()).getUnlocate();
				//닉네임을 글 쓸 때 클라이언트에서 처리해야 될 듯?
				// =========================
				final ProgressDialog dialog = new ProgressDialog(getActivity());
				dialog.setMessage("글쓰기 중입니다...");
//	            dialog.setIcon(R.drawable.ic_launcher);
//	            dialog.setCancelable(false);
//	            dialog.setTitle("progress...");
	            dialog.show();

				NetworkManager.getInstance().putSdamArticle(getActivity(),
						sendParams, new OnResultListener<ArticleInfo>() {

							@Override
							public void onSuccess(Request request, ArticleInfo result) {
								receivedInfo = result;
								if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
									if(result.result != null){
										
									}
//									Toast.makeText(getActivity(), "onSuccess:" + receivedInfo.success, Toast.LENGTH_SHORT).show();
								}
								
								if(sendParams.lock == 1){
									Intent intent = new Intent(((WriteActivity)getActivity()), MypageActivity.class);
									startActivity(intent);
									MainActivity.setAfterWriting(true);
									MainActivity.setResumeTabNum(2);
									((WriteActivity) getActivity()).finish();
								} else {
									MainActivity.setAfterWriting(true);	//onResume시 initData() 할지 말지
									MainActivity.setResumeTabNum(2);	//탭이동
									((WriteActivity) getActivity()).finish();	
								}
								dialog.dismiss();
							}

							@Override
							public void onFailure(Request request, int code, Throwable cause) {
								Toast.makeText(getActivity(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
								dialog.setCancelable(true);
								dialog.dismiss();
							}
						});
			}
		});
		menuUpload.setActionView(imageUpload);
		
	}

	ArticleInfo receivedInfo = new ArticleInfo();
	ArticleParams sendParams = new ArticleParams();
	int sendImage = 0;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == android.R.id.home) {
			((WriteActivity) getActivity()).callWriteOneFragment();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public ActionBar getActionBar() {
		if (actionBar == null) {
			actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		}
		return actionBar;
	}

}
