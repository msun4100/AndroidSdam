package kr.me.sdam.mypage.mylist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import kr.me.sdam.BgImage;
import kr.me.sdam.MainActivity;
import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResult;
import kr.me.sdam.common.CommonResultAdapter;
import kr.me.sdam.common.PreLoadLayoutManager;
import kr.me.sdam.common.event.EventBus;
import kr.me.sdam.common.event.EventInfo;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import kr.me.sdam.mypage.MypageActivity;
import kr.me.sdam.mypage.PagerFragment;
import kr.me.sdam.mypage.calendar.CalendarAdapter;
import kr.me.sdam.mypage.calendar.CalendarData;
import kr.me.sdam.mypage.calendar.CalendarItem;
import kr.me.sdam.mypage.calendar.CalendarManager;
import kr.me.sdam.mypage.calendar.CalendarManager.NoComparableObjectException;
import kr.me.sdam.mypage.calendar.ItemData;
import kr.me.sdam.mypage.calendar.MyCalendarInfo;
import kr.me.sdam.mypage.calendar.MyCalendarResult;
import okhttp3.Request;

public class MyListFragment extends PagerFragment{
	private static final String TAG = MyListFragment.class.getSimpleName();

	Tracker t;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		t = ((MyApplication)((MypageActivity)getActivity())
				.getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("MyListFragment");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	ActionBar actionBar;

	public static final int RC_DETAIL = 105;

	RecyclerView recyclerView;
	MyListAdapter mAdapter;
	PreLoadLayoutManager layoutManager;
	SwipeRefreshLayout refreshLayout;
	boolean isLast = false;
	Handler mHandler = new Handler(Looper.getMainLooper());


	ImageView[] categories = new ImageView[BgImage.BG_CATEGORY_SIZE];
	ImageView allIcon;
	ImageView e1Icon, e2Icon, e3Icon, e4Icon, e5Icon,e6Icon,e7Icon,e8Icon,e9Icon,e10Icon;
	View scrollView;
	View bv ;
	TextView behindDateView;
	ImageView behindEmotionIcon;
	int categoryId = 0;
	View view;
	
//	=============
	TextView yearView;
	TextView monthView;
	GridView gridView;
	CalendarAdapter mCalendarAdapter;
	View calendarView;
	ArrayList<ItemData> mItemdata = new ArrayList<ItemData>();
//	ArrayList<CalendarData> mItemdata = new ArrayList<CalendarData>();
//	ImageView lastMonth,nextMonth;
	View frameLastMonth, frameNextMonth;
	private int lastPosition;
	Handler readHandler = new Handler();
	String fullDate="";
	String days="";
	String firstDay="01";
	
	ArrayList<MyCalendarResult> myCalResult;
//	==============
	boolean usingCalendar = true; //캘린더일때와 날짜 눌러서 리스트 볼때 구분.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_my_list, container, false);

		initCategories();
		bv = (View)view.findViewById(R.id.behindScrollView);
		behindDateView = (TextView)view.findViewById(R.id.text_behind_date);
		behindEmotionIcon = (ImageView)view.findViewById(R.id.image_behind_emotion);
		
		scrollView = (View)view.findViewById(R.id.horizontalScrollView1);
		calendarView = (View)view.findViewById(R.id.my_list_calendar);

		//===================================
		refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
		refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
			@Override
			public void onRefresh() {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						start = 1;
						isEmotionSearching = false;
						initData();	//getSdamMyList()
					}
				}, 1000);
			}
		});
		recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (isLast && newState == RecyclerView.SCROLL_STATE_IDLE) {
					if(isEmotionSearching == true){
						getMoreEmotion(emotion);
					} else {
						getMoreMyList();
					}


				}
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int totalItemCount = mAdapter.getItemCount();
				int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
				if (totalItemCount > 0 && lastVisibleItemPosition != RecyclerView.NO_POSITION && (totalItemCount - 1 <= lastVisibleItemPosition)) {
					isLast = true;
				} else {
					isLast = false;
				}
				if (dy > 0) { // Scroll Down
					((MypageActivity) getActivity()).setMenuInvisible();
				} else if (dy < 0) { // Scroll Up
					((MypageActivity) getActivity()).setMenuVisible();
				}
			}
		});

		mAdapter = new MyListAdapter();
		mAdapter.setOnItemClickListener(new kr.me.sdam.common.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
				Log.e(TAG, "onItemClick: " );
				CommonResult item = mAdapter.getItem(position);
				if(item.num != -1){	// If it's not a dummy data, shows details
					Intent intent = new Intent(getActivity(), DetailActivity.class);
					intent.putExtra(CommonResult.REQUEST_NUMBER, ""+item.num);
					getActivity().startActivityForResult(intent, RC_DETAIL);
				}
			}
		});
		mAdapter.setOnAdapterItemClickListener(new CommonResultAdapter.OnAdapterItemClickListener() {
			@Override
			public void onAdapterItemClick(CommonResultAdapter adapter, View view, int position, CommonResult item, int type) {
				switch (type) {
					case CommonResult.TYPE_INVALID:
						Log.e(TAG, "onAdapterItemClick: INVALID TYPE" );
						break;
					case CommonResult.TYPE_DISTANCE:
						break;
					case CommonResult.TYPE_REPORT:
						break;
					case CommonResult.TYPE_REPLY:
						break;
					case CommonResult.TYPE_LIKE:
						setLikeDisplay(item);
						break;
				}
			}
		});
		mAdapter.setOnItemLongClickListener(new kr.me.sdam.common.OnItemLongClickListener() {
			@Override
			public void onItemLongClick(View view, int position) {
				CommonResult longClickedItem = mAdapter.getItem(position);
				Log.e(TAG, "onItemLongClick: " + longClickedItem);
				if(longClickedItem.writer.equals( PropertyManager.getInstance().getUserId() )){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", longClickedItem);
					b.putSerializable("deletedadapter", mAdapter);
					b.putInt("responseNum", longClickedItem.num);
					b.putInt("activityType", 5); // 0==댓글, 1,2,3==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getActivity().getSupportFragmentManager(), "deletereplydialog");
				}
			}
		});
		recyclerView.setAdapter(mAdapter);
//		layoutManager = new LinearLayoutManager(getActivity());
		layoutManager = new PreLoadLayoutManager(getActivity());
//        layoutManager.scrollToPosition(5);
//        layoutManager.smoothScrollToPosition(recyclerView, null, 5);
		recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new BoardDecoration(getActivity()));

		initData();

//		=calendar========================================
		
		mItemdata.add(new ItemData(2014,2,01,"A",1));	//감정 이모티콘== 숫자 변환
		mItemdata.add(new ItemData(2014,2,02,"B",2));
		mItemdata.add(new ItemData(2014,2,03,"C",3));
		mItemdata.add(new ItemData(2014,2,04,"D",4));
		mItemdata.add(new ItemData(2014,2,05,"E",5));
		mItemdata.add(new ItemData(2014,2,06,"F",6));
		
		yearView = (TextView)view.findViewById(R.id.title);
		monthView = (TextView)view.findViewById(R.id.title_month);
		frameNextMonth = (View)view.findViewById(R.id.frame_next_month);
		frameNextMonth.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CalendarData data = CalendarManager.getInstance().getNextMonthCalendarData();
				yearView.setText("" + (data.year) );
				monthView.setText(getMonthString(data.month));
				mCalendarAdapter.setCalendarData(data);
				String nextDate="";
				nextDate+=""+data.year;
				nextDate+=getMonthString(data.month);
				nextDate+=firstDay;

				getSdamCalendar(nextDate);
			}
		});
		
		frameLastMonth = (View)view.findViewById(R.id.frame_last_month);
		frameLastMonth.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				CalendarData data = CalendarManager.getInstance().getPrevWeekCalendarData(); 
				CalendarData data = CalendarManager.getInstance().getLastMonthCalendarData();
				
//				yearView.setText("" + data.year + " Year " + (data.month + 1) );
				yearView.setText("" + (data.year) );
				monthView.setText(getMonthString(data.month));
				mCalendarAdapter.setCalendarData(data);
				String lastDate="";
				lastDate+=""+data.year;
				lastDate+=getMonthString(data.month);
				lastDate+=firstDay;
				
				getSdamCalendar(lastDate);
			}
		});
		gridView = (GridView)view.findViewById(R.id.gridView1);
		try{
			CalendarManager.getInstance().setDataObject(mItemdata);
		} catch(NoComparableObjectException e){
			e.printStackTrace();
		}
//      CalendarData data = CalendarManager.getInstance().getWeekCalendarData(); 
		final CalendarData data = CalendarManager.getInstance().getCalendarData();
//		titleView.setText("" + data.year + " Year " + (data.weekOfYear) + " Week");
//		titleView.setText("" + data.year + " Year " + (data.month + 1) );
		yearView.setText("" + data.year );
		monthView.setText(getMonthString(data.month));
		mCalendarAdapter = new CalendarAdapter(getActivity(), data);
		
//		===================
		fullDate+=""+data.year;
		fullDate+=getMonthString(data.month);
		fullDate+=firstDay;
		days = ""+data.days.size();
//		====onSuccess라 치고====
		
		gridView.setAdapter(mCalendarAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final CalendarItem item = (CalendarItem)mCalendarAdapter.getItem(position);
				bv.setVisibility(View.VISIBLE);
				scrollView.setVisibility(View.GONE);
				behindEmotionIcon.setVisibility(View.GONE); //나왔다 사라지는 잔버그 수정
				behindDateView.setText(""); //나왔다 사라지는 잔버그 수정
				final String dayOfMonth;
				if(item.dayOfMonth < 10){
					dayOfMonth = "0"+item.dayOfMonth;
				} else {
					dayOfMonth = ""+item.dayOfMonth;
				}
				String str = "";
				str += item.year;
				str += getMonthString(item.month);
				str += dayOfMonth;

				final String myDateDisplay = item.year+"." + getMonthString(item.month) + "." + dayOfMonth;
//				Log.e(TAG, "onItemClick: "+item.inMonth );
				if(item.emotionIcon == 99999)
					return;
				NetworkManager.getInstance().getSdamMyDay(getActivity(), str,
						new OnResultListener<MyList1Info>() {
							@Override
							public void onSuccess(Request request, MyList1Info result) {
								if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
									if(result.result != null) {
										calendarView.setVisibility(View.GONE);
										refreshLayout.setVisibility(View.VISIBLE);
										behindEmotionIcon.setVisibility(View.VISIBLE);
										behindDateView.setText(myDateDisplay);
										mAdapter.clear();
										mAdapter.setTotalCount(100000);
										for(CommonResult r : result.result){
											mAdapter.add(r);
										}
										behindEmotionIcon.setImageResource(getEmotionIcon(item.emotionIcon));
									} else if (result.result == null) {
										behindEmotionIcon.setVisibility(View.GONE);
										behindDateView.setText("");
										initDummyData(0);
									}
									usingCalendar = false;
								}
							}

							@Override
							public void onFailure(Request request, int code, Throwable cause) {
								refreshLayout.setVisibility(View.GONE);
								calendarView.setVisibility(View.VISIBLE);
							}
						});
			}
		});

		getSdamCalendar(fullDate);
		Log.e(TAG, "onCreateView: fullDate: "+fullDate );
//		getSdamMyList();
//		========================================
		return view;
	}

	private void getSdamCalendar(String date){
		NetworkManager.getInstance().getSdamCalendar(getActivity(), date,
				new OnResultListener<MyCalendarInfo>() {
					@Override
					public void onSuccess(Request request, MyCalendarInfo result) {
						if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
							if(result.result != null){
								gridView.setAdapter(mCalendarAdapter);
								mCalendarAdapter.clear();
								int dateIndex = 0;
								Log.e(TAG, "onSuccess: "+result.result );
//								try{
//									CalendarManager.getInstance().setDataObject(result.result);
//									mCalendarAdapter.notifyDataSetChanged();
//								} catch(NoComparableObjectException e){
//									e.printStackTrace();
//								}

								for(MyCalendarResult r : result.result){
									dateIndex = Integer.parseInt(r.date);
									for(int i = 0; i < 31; i++){
										CalendarItem tempItem = (CalendarItem)mCalendarAdapter.getItem(i);
										if(tempItem.dayOfMonth == dateIndex){
											tempItem.emotionIcon = r.emotion;
											mCalendarAdapter.notifyDataSetChanged();
											break;
										}
									}
								}
							} else {
								Log.e(TAG, "onSuccess: result,result == null" );
							}
						} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
							Log.e(TAG, "onSuccess: result,result == 0" );
						}
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(getActivity(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
					}
				});
	}
	private void initCategories(){
		int index = R.id.mylist_1;
		for(int i=0; i<BgImage.BG_CATEGORY_SIZE; i++){
			categories[i] = (ImageView)view.findViewById(index);
			categories[i].setOnClickListener(categoryListener);
			index++;
		}
		
		allIcon = (ImageView)view.findViewById(R.id.mylist_all);
		allIcon.setOnClickListener(categoryListener);
		
		e1Icon = (ImageView)view.findViewById(R.id.mylist_1);
		e1Icon.setOnClickListener(categoryListener);
		
		e2Icon = (ImageView)view.findViewById(R.id.mylist_2);
		e2Icon.setOnClickListener(categoryListener);
		
		e3Icon = (ImageView)view.findViewById(R.id.mylist_3);
		e3Icon.setOnClickListener(categoryListener);
		
		e4Icon = (ImageView)view.findViewById(R.id.mylist_4);
		e4Icon.setOnClickListener(categoryListener);
		
		e5Icon = (ImageView)view.findViewById(R.id.mylist_5);
		e5Icon.setOnClickListener(categoryListener);
		
		e6Icon = (ImageView)view.findViewById(R.id.mylist_6);
		e6Icon.setOnClickListener(categoryListener);
		
		e7Icon = (ImageView)view.findViewById(R.id.mylist_7);
		e7Icon.setOnClickListener(categoryListener);
		
		e8Icon = (ImageView)view.findViewById(R.id.mylist_8);
		e8Icon.setOnClickListener(categoryListener);
		
		e9Icon = (ImageView)view.findViewById(R.id.mylist_9);
		e9Icon.setOnClickListener(categoryListener);
		
		e10Icon = (ImageView)view.findViewById(R.id.mylist_10);
		e10Icon.setOnClickListener(categoryListener);
	}


	public ActionBar getActionBar() {
		if (actionBar == null) {
			actionBar = ((AppCompatActivity) getActivity()) .getSupportActionBar();
		}
		return actionBar;
	}

	private void initData(){
		start = 1;
		getSdamMyList();
	}

	private void initDummyData(int emotion) {
		List<MyList2Result> items = MyListDataManager.getInstance().getMyListDummyList();
		mAdapter.clear();
		for (CommonResult id : items) {
			id.emotion=emotion;
			mAdapter.add(id);
		}
	}


	boolean isMoreData = true;
	ProgressDialog dialog = null;
	private static final int DISPLAY_NUM = 10;
	private int start=1;
	private String reqDate = null;

	boolean isEmotionSearching = false;
	int emotion = 0;

	private void getMoreEmotion(int emotion){
		Log.e(TAG, "getMoreEmotion: page: " +start);
		if (isMoreData == false) {
			Log.e(TAG, "getMoreEmotion: has no more items" );
			return;
		}
		if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount() ) {
			NetworkManager.getInstance().getSdamMyEmotion(getActivity(), ""+emotion, start, new OnResultListener<MyList1Info>() {

				@Override
				public void onSuccess(Request request, MyList1Info result) {
					if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
						if(result.result != null) {
							mAdapter.addAll(result.result);
							start++;
						} else if (result.result == null) {
							isMoreData = false;
							Log.e(TAG, "onSuccess: result.result == null"  );
						}
					} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
						Log.e(TAG, "onSuccess: result.result == 0"  );
					}
				}

				@Override
				public void onFailure(Request request, int code, Throwable cause) {
					Toast.makeText(getActivity(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
				}

			});
		}
	}

	private void getMoreMyList(){
		Log.e(TAG, "getMoreMyList: page: " +start);
		if (isMoreData == false) {
			Log.e(TAG, "getMoreMyList: has no more items" );
			return;
		}
		if (mAdapter.getTotalCount() > 0 && mAdapter.getTotalCount() + DISPLAY_NUM > mAdapter.getItemCount() ) {
			NetworkManager.getInstance().getSdamMyList(getActivity(), start,
					new OnResultListener<MyList1Info>() {

						@Override
						public void onSuccess(Request request, MyList1Info result) {
							if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
								if(result.result != null) {
									mAdapter.setTotalCount(1000000);
									mAdapter.addAll(result.result);
									start++;
								} else if (result.result == null) {
									isMoreData = false;
									Log.e(TAG, "onSuccess: result.result == null" );
								}

							} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
								Log.e(TAG, "onSuccess: result.result == 0" );
							}
							refreshLayout.setRefreshing(false);
							dialog.dismiss();

						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다.#"+code, Toast.LENGTH_SHORT).show();
							refreshLayout.setRefreshing(false);
							dialog.dismiss();
						}
					});
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("데이터 로딩중..");
			dialog.show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setHasOptionsMenu(true);
		actionBar=getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Drawable d = getResources().getDrawable(R.drawable.f_my_actionbar);
		actionBar.setBackgroundDrawable(d);
		actionBar.setTitle("");
//		===========================
//		getSdamMyList();
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
	public void setCalendarVisibility(View v) {
		if(v.getVisibility() == View.VISIBLE){
			imageCal.setImageResource(R.drawable.f_my_selectbar_button_actionbar_calender);
			menuCalendar.setActionView(imageCal);
			
			v.setVisibility(View.GONE);
			
			scrollView.setVisibility(View.VISIBLE);
			refreshLayout.setVisibility(View.VISIBLE);

			((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
			((MypageActivity)getActivity()).setMenuVisible();
			return;
		} else if(v.getVisibility() == View.GONE){
			imageCal.setImageResource(R.drawable.f_my_selectbar_button_actionbar_list);
			menuCalendar.setActionView(imageCal);
			
			scrollView.setVisibility(View.GONE);
			refreshLayout.setVisibility(View.GONE);
			v.setVisibility(View.VISIBLE);
			((MypageActivity)getActivity()).getWriteView().setVisibility(View.GONE);
			((MypageActivity)getActivity()).setMenuInvisible();
			return;
		} else {
			Toast.makeText(getActivity(), "Visiblility error...", Toast.LENGTH_SHORT).show();
			return ;
		}
	}
	
	
	private OnClickListener categoryListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			start = 1;	//어떤 요청을 하든 start = 1
			switch(v.getId()){
			case R.id.mylist_all:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_on);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = false;
				getSdamMyList();
				break;
			case R.id.mylist_1:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
//				==================
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_on);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 0;
				getSdamMyEmotion(0);
				break;
			case R.id.mylist_2:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_on);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 1;
				getSdamMyEmotion(1);
				break;
			case R.id.mylist_3:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_on);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 2;
				getSdamMyEmotion(2);
				break;
			case R.id.mylist_4:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_on);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 3;
				getSdamMyEmotion(3);
				break;
			case R.id.mylist_5:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_on);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 4;
				getSdamMyEmotion(4);
				break;
			case R.id.mylist_6:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_on);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 5;
				getSdamMyEmotion(5);
				break;
			case R.id.mylist_7:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_on);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 6;
				getSdamMyEmotion(6);
				break;
			case R.id.mylist_8:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_on);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 7;
				getSdamMyEmotion(7);
				break;
			case R.id.mylist_9:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_on);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_off);
				isEmotionSearching = true;
				emotion = 8;
				getSdamMyEmotion(8);
				break;
			case R.id.mylist_10:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.VISIBLE);
				((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
				allIcon.setImageResource(R.drawable.f_my_selectbar_emoticon_00all_off);
				e1Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_01_off);
				e2Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_02_off);
				e3Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_03_off);
				e4Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_04_off);
				e5Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_05_off);
				e6Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_06_off);
				e7Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_07_off);
				e8Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_08_off);
				e9Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_09_off);
				e10Icon.setImageResource(R.drawable.f_my_selectbar_emoticon_10_on);
				isEmotionSearching = true;
				emotion = 9;
				getSdamMyEmotion(9);
				break;
				default:
					break;
				
			}
		}
	};
	MenuItem menuCalendar, menuSetting;
	ImageView imageCal, imageSet ;
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.my_list, menu);
		
		menuCalendar = menu.findItem(R.id.my_calendar);
		menuCalendar.setTitle("calendar");
		imageCal = new ImageView(getActivity());
		imageCal.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		imageCal.setPadding(0, 0, -16, 0); // 이미지 받으면 이거 주석 살려서
		imageCal.setImageResource(R.drawable.f_my_selectbar_button_actionbar_calender);
		imageCal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bv.setVisibility(View.GONE);
				setCalendarVisibility(calendarView);
				usingCalendar = true;
				t.send(new HitBuilders.EventBuilder().setCategory("MyListFragment").setAction("Pressed Menu").setLabel("Calendar Click").build());
				
			}
		});
		menuCalendar.setActionView(imageCal);
//		=============
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
//			((MypageActivity)getActivity()).toggle();
//			return true;
//		}
		
		if( id == android.R.id.home){
			if(usingCalendar == false){
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.GONE);
				refreshLayout.setVisibility(View.GONE);
				calendarView.setVisibility(View.VISIBLE);
				usingCalendar = true;
			}  
			else {
				((MypageActivity)getActivity()).onBackPressed();	
			}
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	public String getMonthString(int month){
		String str="12";
		if(month+1 < 10){
			str= "0" + ( month + 1);
		} else {
			str= "" + ( month + 1);
		}
		return str;
	}
	
	public void getSdamMyList(){
		isEmotionSearching = false;
		NetworkManager.getInstance().getSdamMyList(getActivity(),
				start,
				new OnResultListener<MyList1Info>() {
					
					@Override
					public void onSuccess(Request request, MyList1Info result) {
						if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
							if(result.result != null) {
								mAdapter.clear();
								mAdapter.setTotalCount(1000000);
								mAdapter.addAll(result.result);
								start++;
								isMoreData = true;
							} else if (result.result == null) {
								Log.e(TAG, "onSuccess: result.result == null" );
							}
							
						} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
							Log.e(TAG, "onSuccess: result.result == 0" );
						}
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(getActivity(), "서버에 연결할 수 없습니다.#"+code, Toast.LENGTH_SHORT).show();
						refreshLayout.setRefreshing(false);
						dialog.dismiss();
					}
				});
		dialog = new ProgressDialog(getActivity());
		dialog.setMessage("데이터 로딩중..");
		dialog.show();
	}//getSdamMyList()
	
	public void getSdamMyEmotion(final int emotion){
		NetworkManager.getInstance().getSdamMyEmotion(getActivity(), ""+emotion, start, new OnResultListener<MyList1Info>() {
			
			@Override
			public void onSuccess(Request request, MyList1Info result) {
				if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
					if(result.result != null) {
						mAdapter.clear();
						mAdapter.setTotalCount(1000000);
						mAdapter.addAll(result.result);
						start++;
///////////////////////////////////////////////////////////////////////
 						isMoreData = false;		//지금 서버단에서 page ==2 요청할때 500에러가나서 앱이 죽는 버그가 있음. 수정하기 전까지 false로
					} else if (result.result == null) {
						mAdapter.clear();
						initDummyData(emotion);
						isMoreData = false;
						Log.e(TAG, "onSuccess: result.result == null"  );
					}
				} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
					Log.e(TAG, "onSuccess: result.result == 0"  );
				}
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(getActivity(), "서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT).show();
			}

		});
	}
	
	private int getEmotionIcon(int emotion){
		int bg=R.drawable.f_my_selectbar_emoticon_01_on;
		switch(emotion){
		case 0:
			bg=R.drawable.f_my_selectbar_emoticon_01_on;
			break;
		case 1:
			bg=R.drawable.f_my_selectbar_emoticon_02_on;
			break;
		case 2:
			bg=R.drawable.f_my_selectbar_emoticon_03_on;
			break;
		case 3:
			bg=R.drawable.f_my_selectbar_emoticon_04_on;
			break;
		case 4:
			bg=R.drawable.f_my_selectbar_emoticon_05_on;
			break;
		case 5:
			bg=R.drawable.f_my_selectbar_emoticon_06_on;
			break;
		case 6:
			bg=R.drawable.f_my_selectbar_emoticon_07_on;
			break;
		case 7:
			bg=R.drawable.f_my_selectbar_emoticon_08_on;
			break;
		case 8:
			bg=R.drawable.f_my_selectbar_emoticon_09_on;
			break;
		case 9:
			bg=R.drawable.f_my_selectbar_emoticon_10_on;
			break;
		}
		return bg;
	}
	
	private GoodInfo goodInfo;
	private GoodCancelInfo goodCancelInfo;
	public void setLikeDisplay(CommonResult item) {
		final CommonResult tempItem = item;
		
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
								postEvent(tempItem, EventInfo.MODE_UPDATE);
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
								postEvent(tempItem, EventInfo.MODE_UPDATE);
							} else {
//								Toast.makeText( getActivity(), "/goodcancel onSuccess:0" , Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다. #"+code , Toast.LENGTH_SHORT).show();
						}
					});
		}

	}// setlikedisplay
	@Subscribe
	public void onEvent(EventInfo eventInfo){
		Log.e(TAG, "onEvent: "+eventInfo.mode );
		if(mAdapter == null || mAdapter.getItemCount() < 1) {
			return;
		}
		mAdapter.findOneAndModify(eventInfo.commonResult, eventInfo.mode);
	}

	private void postEvent(CommonResult commonResult, String mode){
		EventInfo eventInfo = new EventInfo(commonResult, mode);
		EventBus.getInstance().post(eventInfo);
	}
	
}
