package kr.me.sdam.mypage.favor;

import java.util.List;

import kr.me.sdam.MainActivity;
import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResultItem;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.mypage.MypageActivity;
import kr.me.sdam.mypage.PagerFragment;
import kr.me.sdam.mypage.favor.FavorAdapter.OnAdapterItemClickListener;
import okhttp3.Request;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class FavorFragment extends PagerFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((MyApplication)((MypageActivity)getActivity ())
				.getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("FavorFragment");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	public void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(getActivity()).reportActivityStart (getActivity());
	}
	@Override
	public void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(getActivity()).reportActivityStop (getActivity());
	}

	Handler mHandler = new Handler();
	long startTime;
	
	ActionBar actionBar;
	ListView listView;
	// ArrayAdapter<Result> mAdapter;
	FavorAdapter mAdapter;
	private int lastPosition;
	private int myFavorRequestPage = 1;
	private int currentMyFavorRequestPage = 1;
	Handler readHandler =new Handler();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_my_favor, container,
				false);

		
		listView = (ListView) view.findViewById(R.id.list_interest);

		mAdapter = new FavorAdapter(getActivity());
		listView.setAdapter(mAdapter);
		mAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {

			@Override
			public void onAdapterItemClick(FavorAdapter adapter, View view,
					Favor2Result item, int type) {
				switch (type) {
				case CommonResultItem.TYPE_INVALID:
					break;
				case CommonResultItem.TYPE_DISTANCE:
					break;
				case CommonResultItem.TYPE_REPORT:
					Bundle b = new Bundle();
					b.putSerializable("reporteditem", item);
					b.putSerializable("reportedadapter", adapter);
					b.putInt("curruenttab", 6);
					ReportMenuDialogFragment f = new ReportMenuDialogFragment();
					f.setArguments(b);
					f.show(getFragmentManager(), "dialog");
					break;
				case CommonResultItem.TYPE_REPLY:
					break;
				case CommonResultItem.TYPE_LIKE:
					setLikeDisplay(item);
					break;
				}
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Favor2Result longClickedItem = (Favor2Result)listView.getItemAtPosition(position);
				if(longClickedItem.writer.equals( PropertyManager.getInstance().getUserId() )){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", longClickedItem);
					b.putSerializable("deletedadapter", mAdapter);
					b.putInt("responseNum", longClickedItem.num);
					b.putInt("activityType", 6); // 0==댓글, 1,2,3,4==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getActivity().getSupportFragmentManager(), "deletereplydialog");	
				}
				
				return true;
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int currentPosition = view.getFirstVisiblePosition();
				if (currentPosition > lastPosition) {
					((MypageActivity) getActivity()).setMenuInvisibility();
				}
				if (currentPosition < lastPosition) {
					((MypageActivity) getActivity()).setMenuVisibility();
				}
				lastPosition = currentPosition;
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object data = listView.getItemAtPosition(position);
				Favor2Result item = (Favor2Result) data;
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(CommonResultItem.REQUEST_NUMBER, "" + item.num);
				// Bundle b = new Bundle();
				// b.putString("backgroundId", ""+item.backgroundId);
				// b.putString("content", ""+item.content);
				// intent.putExtra(TAB_ONE_ITEM, item);
				// // intent.putExtras(new Bundle());
//				startActivityForResult(intent, 0);
				startActivity(intent);
			}
		});
		
		// ========================
		
		// =========================
		return view;
	}

	private void initData() {
		List<Favor2Result> items = FavorDataManager.getInstance()
				.getFavorResultList();
		mAdapter.clear();
		for (Favor2Result id : items) {
			mAdapter.add(id);
		}
	}

	private void initDummyData() {
		List<Favor2Result> items = FavorDataManager.getInstance()
				.getFavorDummyList();
		mAdapter.clear();
		for (Favor2Result id : items) {
			mAdapter.add(id);
		}
	}

	public ActionBar getActionBar() {
		if (actionBar == null) {
			actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		}
		return actionBar;
	}

	@Override
	public void onResume() {
		super.onResume();
		setHasOptionsMenu(true);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Drawable d = getResources().getDrawable(R.drawable.f_my_actionbar);
		actionBar.setBackgroundDrawable(d);
		actionBar.setTitle("");
//		=================
		NetworkManager.getInstance().getSdamMyFavor(getActivity(),
				myFavorRequestPage, new OnResultListener<Favor1Info>() {

					@Override
					public void onSuccess(Request request, Favor1Info result) {
						if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
							if (result.result != null) {
								listView.setAdapter(mAdapter);
								mAdapter.clear();
								mAdapter.setTotalCount(1000);
								for (Favor2Result r : result.result) {
									mAdapter.add(r);
								}
								if(lastPosition != 0){
									//새로고침한 경우 리스트 자리 맞추기 위해
									if(currentMyFavorRequestPage != myFavorRequestPage && (lastPosition<20) ){
										lastPosition=0;
										listView.setSelection(lastPosition);
									} else {//디폴트 리쥼 리스트 자리 맞추기
										listView.setSelection(lastPosition%MainActivity.COMMON_LIST_ITEM_SIZE);	
									}
									currentMyFavorRequestPage = myFavorRequestPage;
								}
							} else if (result.result == null) {
								initDummyData();
//								Toast.makeText( getActivity(), "/favor result:null\nwork:" + result.work, Toast.LENGTH_SHORT).show();
							}

						} else if (result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO) {
							Toast.makeText(
									getActivity(), "서버에 연결할 수 없습니다." + result.work, Toast.LENGTH_SHORT) .show();
						} else {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다." + result.work, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(getActivity(), "서버에 연결할 수 없습니다.#" + code, Toast.LENGTH_SHORT).show();
					}
				});

	}

	@Override
	public void onPageCurrent() {
		super.onPageCurrent();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			((MypageActivity)getActivity()).buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_off_3notice);
			((MypageActivity)getActivity()).buttons[3].setBackgroundResource(R.drawable.b_main_navigationbar_on_4my);
		}

	}// setUser~

	
	MenuItem menuSetting;
	ImageView imageSet;
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.favor, menu);
		
		menuSetting = menu.findItem(R.id.my_setting);
		imageSet = new ImageView(getActivity());
		imageSet.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		imageSet.setPadding(16, 0, 16, 0);
		imageSet.setImageResource(R.drawable.f_my_actionbar_icon_settings_selector);
		imageSet.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((MypageActivity)getActivity()).toggle();
			}
		});
		menuSetting.setActionView(imageSet);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

//		if (id == R.id.my_setting) {
//			((MypageActivity) getActivity()).toggle();
//			return true;
//		}

		if (id == android.R.id.home) {
			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			((MypageActivity) getActivity()).overridePendingTransition(
					R.anim.slide_right_in, R.anim.slide_right_out);
			((MypageActivity) getActivity()).finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private GoodInfo goodInfo;
	private GoodCancelInfo goodCancelInfo;
	public void setLikeDisplay(Favor2Result item) {
		final Favor2Result tempItem = item;
		
		if (tempItem.myGood == 0 ) {
			NetworkManager.getInstance().putSdamGood(getActivity(), 
					String.valueOf(item.num),
					item.writer,
					
					new OnResultListener<GoodInfo>() {

						@Override
						public void onSuccess(Request request, GoodInfo result) {
							goodInfo = result;
							if (goodInfo.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem); //마이 굿 반대로, 카운트 증가/증감
							} else {
//								Toast.makeText(getActivity(), "/good onSuccess:0", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다."+code , Toast.LENGTH_SHORT).show();
						}
					});

		} else if(tempItem.myGood >= 1){
			NetworkManager.getInstance().putSdamGoodCancel(getActivity(), String.valueOf(item.num),
					new OnResultListener<GoodCancelInfo>() {

						@Override
						public void onSuccess(Request request, GoodCancelInfo result) {
							goodCancelInfo = result;
							if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
								mAdapter.setLikeNum(tempItem);
							} else {
//								Toast.makeText( getActivity(), "/goodcancel onSuccess:0" , Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다."+code , Toast.LENGTH_SHORT).show();
						}
					});
		}

	}// setlikedisplay
	

}
