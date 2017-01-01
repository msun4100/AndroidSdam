package kr.me.sdam.mypage.mylist;

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
import kr.me.sdam.common.CommonResultItem;
import kr.me.sdam.detail.DetailActivity;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.dialogs.MyPageMenuDialogFragment;
import kr.me.sdam.dialogs.WaitingDialogFragment;
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
import kr.me.sdam.mypage.mylist.MyListAdapter.OnAdapterItemClickListener;
import kr.me.sdam.tabthree.TabThreeInfo;
import okhttp3.Request;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MyListFragment extends PagerFragment{
	Tracker t;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		t = ((MyApplication)((MypageActivity)getActivity())
				.getApplication()).getTracker (MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("MyListFragment");
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
	MyListAdapter mAdapter;
	
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
	String myDate = "";
	String myDateDisplay ="";
	
	ArrayList<MyCalendarResult> myCalResult;
//	==============
	boolean usingCalendar = true; //캘린더일때와 날짜 눌러서 리스트 볼때 구분.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_my_list, container, false);

		
		initCategories();
		bv = (View)view.findViewById(R.id.behindScrollView);
		behindDateView = (TextView)view.findViewById(R.id.text_behind_date);
		behindEmotionIcon = (ImageView)view.findViewById(R.id.image_behind_emotion);
		
		scrollView = (View)view.findViewById(R.id.horizontalScrollView1);
		calendarView = (View)view.findViewById(R.id.my_list_calendar);
		
		listView = (ListView) view.findViewById(R.id.list_mywriting);
		mAdapter = new MyListAdapter(getActivity());
		listView.setAdapter(mAdapter);
		mAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {

			@Override
			public void onAdapterItemClick(MyListAdapter adapter, View view,
					MyList2Result item, int type) {
				switch (type) {
				case CommonResultItem.TYPE_INVALID:
					break;
				case CommonResultItem.TYPE_DISTANCE:
					break;
				case CommonResultItem.TYPE_REPORT:
					MyList2Result clickedItem = item;
					if(clickedItem.writer.equals( PropertyManager.getInstance().getUserId() )){
//						if(clickedItem.locked == 1){
//							clickedItem.locked =0;
//						} else {
//							clickedItem.locked =1;
//						}
						Bundle b = new Bundle();
						b.putSerializable("mypageItem", clickedItem);
						b.putSerializable("mypageAdapter", mAdapter);
						b.putInt("mypageNum", clickedItem.num);
						b.putInt("mypageType", 5); // 0==댓글, 1,2,3,4==탭게시물
//						b.putString("mypageLockedText", value);
						MyPageMenuDialogFragment f = new MyPageMenuDialogFragment();
						f.setArguments(b);
						f.show(getFragmentManager(), "mypagedialog");
					}
					break;
				case CommonResultItem.TYPE_REPLY:
					break;
				case CommonResultItem.TYPE_LIKE:
					setLikeDisplay(item);
					break;
					default:
						Toast.makeText(getActivity(), "INVALID TYPE", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyList2Result longClickedItem = (MyList2Result)listView.getItemAtPosition(position);
//				if(longClickedItem.writer == PropertyManager.getInstance().getUserId()){
				if(longClickedItem.writer.equals( PropertyManager.getInstance().getUserId() )){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", longClickedItem);
					b.putSerializable("deletedadapter", mAdapter);
					b.putInt("responseNum", longClickedItem.num);
					b.putInt("activityType", 5); // 0==댓글, 1,2,3,4==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getActivity().getSupportFragmentManager(), "deletereplydialog");	
				}
				
				
				return true;
			}
		});
		listView.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int currentPosition = view.getFirstVisiblePosition();
				if( currentPosition > lastPosition){
					((MypageActivity) getActivity()).setMenuInvisibility();
				}
				if(currentPosition < lastPosition){
					((MypageActivity) getActivity()).setMenuVisibility();
				}
				lastPosition=currentPosition;
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
				MyList2Result item = (MyList2Result) data;
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
				
				myDate="";
				myDate+=""+data.year;
				myDate+=getMonthString(data.month);
				
				myDateDisplay="";
				myDateDisplay+=""+data.year+". ";
				myDateDisplay+=getMonthString(data.month)+". ";
				
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
				
				myDate="";
				myDate+=""+data.year;
				myDate+=getMonthString(data.month);
				
				myDateDisplay="";
				myDateDisplay+=""+data.year+". ";
				myDateDisplay+=getMonthString(data.month)+". ";
				
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
		
		myDate="";
		myDate+=""+data.year;
		myDate+=getMonthString(data.month);
		
		myDateDisplay="";
		myDateDisplay+=""+data.year+". ";
		myDateDisplay+=getMonthString(data.month)+". ";
//		====onSuccess라 치고====
		
		gridView.setAdapter(mCalendarAdapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final CalendarItem item = (CalendarItem)mCalendarAdapter.getItem(position);
				
				bv.setVisibility(View.VISIBLE);
				scrollView.setVisibility(View.GONE);
				behindEmotionIcon.setVisibility(View.GONE); //나왔다 사라지는 잔버그 수정
				behindDateView.setText(""); //나왔다 사라지는 잔버그 수정
				NetworkManager.getInstance().getSdamMyDay(getActivity(), 
						myDate + item.dayOfMonth,
						new OnResultListener<MyList1Info>() {

							@Override
							public void onSuccess(Request request, MyList1Info result) {
								if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
									if(result.result != null) {
										calendarView.setVisibility(View.GONE);
										listView.setVisibility(View.VISIBLE);
										behindEmotionIcon.setVisibility(View.VISIBLE);
										behindDateView.setText(myDateDisplay + item.dayOfMonth);
										listView.setAdapter(mAdapter);
										mAdapter.clear();
										mAdapter.setTotalCount(1000);
										for(MyList2Result r : result.result){
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
								listView.setVisibility(View.GONE);
								calendarView.setVisibility(View.VISIBLE);
							}
						});
			}
		});

		getSdamCalendar(fullDate);
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
//									CalendarItem tempItem = (CalendarItem)mCalendarAdapter .getItem( (Integer.parseInt(r.date) - 1) );
//									tempItem.emotionIcon = r.emotion;
//									mCalendarAdapter.notifyDataSetChanged();
								}
							} else {
//								Toast.makeText(getActivity(), "/mycal : result == 0", Toast.LENGTH_SHORT).show();
							}
						} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
//							Toast.makeText(getActivity(), "/mycal : success == 0", Toast.LENGTH_SHORT).show();
						} else {
//							Toast.makeText(getActivity(), "/mycal : Unexpected error...", Toast.LENGTH_SHORT).show();
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
	private void initData() {
		List<MyList2Result> items = MyListDataManager.getInstance()
				.getMyListResultList();
		mAdapter.clear();
		for (MyList2Result id : items) {
			mAdapter.add(id);
		}
	}
	
	private void initDummyData(int emotion) {
		List<MyList2Result> items = MyListDataManager.getInstance()
				.getMyListDummyList();
		mAdapter.clear();
		for (MyList2Result id : items) {
			id.emotion=emotion;
			mAdapter.add(id);
		}
	}
	public ActionBar getActionBar() {
		if (actionBar == null) {
			actionBar = ((AppCompatActivity) getActivity()) .getSupportActionBar();
		}
		return actionBar;
	}

	
	private int myListRequestPage = 1;
	private int currentMyListRequestPage = 1;

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
		getSdamMyList();
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
			listView.setVisibility(View.VISIBLE);

			((MypageActivity)getActivity()).getWriteView().setVisibility(View.VISIBLE);
			((MypageActivity)getActivity()).setMenuVisibility();
			return;
		} else if(v.getVisibility() == View.GONE){
			imageCal.setImageResource(R.drawable.f_my_selectbar_button_actionbar_list);
			menuCalendar.setActionView(imageCal);
			
			scrollView.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
			v.setVisibility(View.VISIBLE);
			((MypageActivity)getActivity()).getWriteView().setVisibility(View.GONE);
			((MypageActivity)getActivity()).setMenuInvisibility();
			return;
		} else {
			Toast.makeText(getActivity(), "Visiblility error...", Toast.LENGTH_SHORT).show();
			return ;
		}
	}
	
	
	private OnClickListener categoryListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			
			case R.id.mylist_all:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyList();
				break;
			case R.id.mylist_1:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(0, 1);
				break;
			case R.id.mylist_2:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(1, 1);
				break;
			case R.id.mylist_3:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(2, 1);
				break;
			case R.id.mylist_4:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(3, 1);
				break;
			case R.id.mylist_5:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(4, 1);
				break;
			case R.id.mylist_6:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(5, 1);
				break;
			case R.id.mylist_7:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(6, 1);
				break;
			case R.id.mylist_8:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(7, 1);
				break;
			case R.id.mylist_9:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(8, 1);
				break;
			case R.id.mylist_10:
				bv.setVisibility(View.GONE);
				scrollView.setVisibility(View.VISIBLE);
				calendarView.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
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
				getSdamMyEmotion(9, 1);
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
				listView.setVisibility(View.GONE);
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
	
	public void connectGetSdamCalendar(){
		
	}
	WaitingDialogFragment wf;
	public void getSdamMyList(){
		wf = new WaitingDialogFragment();
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.addToBackStack(null);
		wf.setCancelable(false); // 딴영역 눌러도 안닫히게 하는 옵션
		wf.show(ft, "dialog");
		NetworkManager.getInstance().getSdamMyList(getActivity(),
				myListRequestPage, 
				new OnResultListener<MyList1Info>() {
					
					@Override
					public void onSuccess(Request request, MyList1Info result) {
						if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
//							Toast.makeText(getActivity(), "/mylist result.success 1\nwork:"+result.work, Toast.LENGTH_SHORT).show();
							if(result.result != null) {
								listView.setAdapter(mAdapter);
								mAdapter.clear();
								mAdapter.setTotalCount(100);
								for(MyList2Result r : result.result){
									mAdapter.add(r);
								}
								if(lastPosition != 0){
									//새로고침한 경우 리스트 자리 맞추기 위해
									if(currentMyListRequestPage != myListRequestPage && (lastPosition<20) ){
										lastPosition=0;
										listView.setSelection(lastPosition);
									} else {//디폴트 리쥼 리스트 자리 맞추기
										listView.setSelection(lastPosition%MainActivity.COMMON_LIST_ITEM_SIZE);	
									}
									currentMyListRequestPage = myListRequestPage;
								}
							} else if (result.result == null) {
								initDummyData(0);
							}
							
						} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다."+result.work, Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다."+result.work, Toast.LENGTH_SHORT).show();
						}
						if(wf!=null){
							if(wf.isVisible()){
								wf.dismiss();
							}
						}
						
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(getActivity(), "서버에 연결할 수 없습니다.#"+code, Toast.LENGTH_SHORT).show();
						if(wf!=null){
							if(wf.isVisible()){
								wf.dismiss();
							}
						}
					}
				});
	}//getSdamMyList()
	
	public void getSdamMyEmotion(final int emotion, int requestPageNum){
		NetworkManager.getInstance().getSdamMyEmotion(getActivity(), ""+emotion, requestPageNum, new OnResultListener<MyList1Info>() {
			
			@Override
			public void onSuccess(Request request, MyList1Info result) {
				if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
					if(result.result != null) {
						listView.setAdapter(mAdapter);
						mAdapter.clear();
						mAdapter.setTotalCount(100);
						for(MyList2Result r : result.result){
							mAdapter.add(r);
						}
//						myListRequestPage++;
					} else if (result.result == null) {
						initDummyData(emotion);
					}
					
				} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
//					Toast.makeText(getActivity(), "/mylist result.success:0\nwork:"+result.work, Toast.LENGTH_SHORT).show();
				} else {
//					Toast.makeText(getActivity(), "/mylist Unexpected error.."+result.work, Toast.LENGTH_SHORT).show();
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
	public void setLikeDisplay(MyList2Result item) {
		final MyList2Result tempItem = item;
		
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
							Toast.makeText(getActivity(), "서버에 연결할 수 없습니다. #"+code , Toast.LENGTH_SHORT).show();
						}
					});
		}

	}// setlikedisplay
	
	
}
