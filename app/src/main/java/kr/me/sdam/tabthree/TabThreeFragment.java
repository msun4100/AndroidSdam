package kr.me.sdam.tabthree;

import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.MainActivity;
import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PagerFragment;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResultItem;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.tabthree.TabThreeAdapter.OnAdapterItemClickListener;
import okhttp3.Request;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class TabThreeFragment extends PagerFragment {

	private  static final String TAG = TabThreeFragment.class.getSimpleName();
	Handler mHandler = new Handler();
	long startTime;
	
	ListView listView;
	TabThreeAdapter mAdapter;
	GoodInfo goodInfo;
	GoodCancelInfo goodCancelInfo;
	int userId = 0;
	
	
	public int lastPosition=0;
	Handler readHandler = new Handler();
	public int newRequestPage =1;
	public int currentNewRequestPage=1;
	private TabThreeResult clickedItem;
//	=============
	public int getLastPosition(){
		return lastPosition;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tracker t = ((MyApplication)((MainActivity)getActivity()).getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.enableAdvertisingIdCollection(true);
		t.setScreenName("TabThreeFragment");
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
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_tab_three, container, false);

		listView = (ListView) view.findViewById(R.id.list_tabthree);

		mAdapter = new TabThreeAdapter(getActivity());
		listView.setAdapter(mAdapter);
//		mAdapter.clear();
		TabThreeDataManager.getInstance().clearDataManagerList();
//		newRequestPage =1;
//		currentNewRequestPage=1;
		mAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {
			
			@Override
			public void onAdapterItemClick(TabThreeAdapter adapter, View view,
					TabThreeResult item, int type) {
				switch (type) {
				case CommonResultItem.TYPE_INVALID:
					Toast.makeText(getActivity(), "INVALID TYPE", Toast.LENGTH_SHORT).show();
					break;
				case CommonResultItem.TYPE_DISTANCE:
					break;
				case CommonResultItem.TYPE_REPORT:
					Bundle b = new Bundle();
					b.putSerializable("reporteditem", item);
					b.putSerializable("reportedadapter", adapter);
					b.putInt("curruenttab", 3);
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
	
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int currentPosition = view.getFirstVisiblePosition();
				if (currentPosition > lastPosition) {
					((MainActivity) getActivity()).setMenuInvisibility();
				}
				if (currentPosition < lastPosition) {
					((MainActivity) getActivity()).setMenuVisibility();
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
				TabThreeResult item = (TabThreeResult) data;
				clickedItem = (TabThreeResult)item;		//리스트뷰에서 현재 클릭된 아이템 임시저장, 라이크 동기화 위해
				Intent intent = new Intent(getActivity(), DetailActivity.class);
				intent.putExtra(CommonResultItem.REQUEST_NUMBER, ""+item.num);
				// Bundle b = new Bundle();
				// b.putString("backgroundId", ""+item.backgroundId);
				// b.putString("content", ""+item.content);
//				intent.putExtra(TAB_TWO_ITEM, item);
				// // intent.putExtras(new Bundle());
//				startActivityForResult(intent, 0);
				startActivity(intent);
			}
		});
		
//		====글삭제 임시로 롱클릭 처리해서 시연======================
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
//				final int currentPosition = position;
//				CommonResultItem ttr = (CommonResultItem)listView.getItemAtPosition(position);
				
				TabThreeResult longClickedItem = (TabThreeResult)listView.getItemAtPosition(position);
				
//				if(longClickedItem.writer == PropertyManager.getInstance().getUserId()){
				if(longClickedItem.writer.equals( PropertyManager.getInstance().getUserId() )){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", longClickedItem);
					b.putSerializable("deletedadapter", mAdapter);
					b.putInt("responseNum", longClickedItem.num);
					b.putInt("activityType", 3); // 0==댓글, 1,2,3==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getActivity().getSupportFragmentManager(), "deletereplydialog");	
				}
				
				return true;
			}
		});
//		==========================
		
		return view;
	}

	private void initData() {
		mAdapter.clear();
		List<TabThreeResult> items = TabThreeDataManager.getInstance()
				.getDataManagerList();
		for (TabThreeResult id : items) {
			mAdapter.add(id);
		}
		if(lastPosition != 0){
			listView.setSelection(lastPosition);
		}
	}

//	WaitingDialogFragment wf3;
	@Override
	public void onResume() {
		super.onResume();
		if(MainActivity.getAfterWriting()){
			lastPosition=0;
			MainActivity.setAfterWriting(false);
		}
//		wf3 = new WaitingDialogFragment();
//		FragmentManager fm = getActivity().getSupportFragmentManager();
//		FragmentTransaction ft = fm.beginTransaction();
//		ft.addToBackStack(null);
//		wf3.setCancelable(false); // 딴영역 눌러도 안닫히게 하는 옵션
//		wf3.show(ft, "dialog3");
		
//		issueRequestPage = 1;		
		NetworkManager.getInstance().getSdamNew(getActivity(), 
				String.valueOf(newRequestPage),
				new OnResultListener<TabThreeInfo>() {

					@Override
					public void onSuccess(Request request, TabThreeInfo result) {
						
						if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
							if (result.result != null) {
								listView.setAdapter(mAdapter);
								// **********Clear()할지 말지******
								mAdapter.clear();
								mAdapter.setTotalCount(1000);
								if(TabThreeDataManager.getInstance().getDataManagerList().size() > 0){
									mAdapter.addAll((ArrayList<TabThreeResult>) TabThreeDataManager.getInstance().getDataManagerList());
								}
								mAdapter.addAll(result.result);
//								for (TabThreeResult r : result.result) {
//									mAdapter.add(r);
//								}
								listView.setSelection(lastPosition);
//								if(lastPosition != 0){
//									//새로고침한 경우 리스트 자리 맞추기 위해
//									if(currentNewRequestPage != newRequestPage && (lastPosition<20) ){
//										lastPosition=0;
//										listView.setSelection(lastPosition);
//									} else {//디폴트 리쥼 리스트 자리 맞추기
//										listView.setSelection(lastPosition%MainActivity.COMMON_LIST_ITEM_SIZE);	
//									}
//									currentNewRequestPage = newRequestPage;
//								}
							} else if (result.result == null) {
								Log.e(TAG, "onSuccess: result.result == null" );
//								Toast.makeText(getActivity(), "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
							}
						} else if (result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO) {
							Log.e(TAG, "onSuccess: result.success == 0 "+ result.work );
//							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다." + result.work , Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(),
									"new/# : Unexpected Network Error..", Toast.LENGTH_SHORT).show();
						}
						
//						if(wf3!=null){
//							if(wf3.isVisible()){
//								wf3.dismiss();
//							}
//						}

					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText( getActivity(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
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
			SlidingMenu sm = ((MainActivity) getActivity()).getSlidingMenu();
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			((MainActivity)getActivity()).buttons[2].setBackgroundResource(R.drawable.b_main_navigationbar_off_3notice);
			((MainActivity)getActivity()).buttons[0].setBackgroundResource(R.drawable.b_main_navigationbar_on_1home);
		}

	}// setUser~
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		//수행을 제대로한 경우
//		
//		if( resultCode == getActivity().RESULT_OK && data != null ){
//			
////			Detail3Article temp = (Detail3Article) data.getSerializableExtra("modifiedItem");
//			clickedItem.myGood = data.getIntExtra("myGood", 10);
//			clickedItem.goodNum = data.getIntExtra("goodNum", 10);
//			clickedItem.repNum = data.getIntExtra("repNum", 10);
//			mAdapter.notifyDataSetChanged();
//			Toast.makeText(getActivity(), "RESULT_OK:"+data.getIntExtra("goodNum", 10)
//					+"clicked_goodnum:"+clickedItem.goodNum, Toast.LENGTH_SHORT).show();
//			
//		} else if(resultCode == getActivity().RESULT_CANCELED) {
//			Toast.makeText(getActivity(), "RESULT_CANCELED", Toast.LENGTH_SHORT).show();
//		}
//		
//	}
	
	
	public void setLikeDisplay(TabThreeResult item) {
		final TabThreeResult tempItem = item;
		
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

						}
					});
		}

	}// setlikedisplay
	
}
