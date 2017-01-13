package kr.me.sdam.detail;

import java.util.ArrayList;

import kr.me.sdam.BgImage;
import kr.me.sdam.MyApplication;
import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.PropertyManager;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResult;
import kr.me.sdam.detail.MyDetailReplyAdapter.OnAdapterItemClickListener;
import kr.me.sdam.detail.autocomplete.MyAdapter;
import kr.me.sdam.detail.autocomplete.Person;
import kr.me.sdam.detail.good.CancelGReplyInfo;
import kr.me.sdam.detail.good.GoodReplyInfo;
import kr.me.sdam.detail.reply.ReplyInfo;
import kr.me.sdam.detail.viewpager.CircleAnimIndicator;
import kr.me.sdam.detail.viewpager.MyViewPagerAdapter;
import kr.me.sdam.dialogs.DeleteDialogFragment;
import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.good.GoodCancelInfo;
import kr.me.sdam.good.GoodInfo;
import okhttp3.Request;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class DetailActivity extends ActionBarActivity  {
	private static final String TAG = DetailActivity.class.getSimpleName();
	public final static int DETAIL_CONTENT_LENGTH = 110;
	public final static int DETAIL_CONTENT_MAX_LENGTH = 330;
	private final static int DETAIL_BUTTON_CANCEL = 0;
	private final static int DETAIL_BUTTON_COMPLETE = 1;
	private final int TV_DEFAULT_COLOR = R.color.edit_text_default_color;
	private final int TV_TAG_COLOR = R.color.sdam_text_color;
	
	ActionBar actionBar;

	ListView listView;
	MyDetailReplyAdapter mDetailAdapter;
	MyViewPagerAdapter mViewPagerAdapter;
	TextView vpDescView;
	TextView vpPageView;

	String content = "default contents";
	// =======List Header=====
	View headerView;
	public static ViewPager headerPager;
	CircleAnimIndicator circleAnimIndicator;

	ImageView headerReportIcon;
	ImageView headerLocateIcon;
	ImageView headerTimeIcon;
	ImageView headerRepIcon;
	ImageView headerGoodIcon;
	TextView headerDistanceView;
	TextView headerTimeView;
	TextView headerRepNumView;
	TextView headerGoodNumView;
	ImageView headerEmotionIcon;
	ImageView headerBackground;
	
	LinearLayout frameHeaderLike;

	AutoCompleteTextView editReply;
	MyAdapter mAdapter;		//autoCompleteView용 어답터 package==kr.me.sdam.detail.autocomplete
//	EditText editReply;
	String editRepMessage = null;
	int originRepCount;
	int detailNum;
	
	ArrayList<String> nicks; //서버로 보내줄 리스트
	ArrayList<String> ids;
	// ============
	int pageCount;
	public String[] strArr;
	// =======
	public String tagString;
	boolean isFirst = true;

	// =================
//	ContactsCompletionView completionView;
//	Person[] people;
//	ArrayAdapter<Person> tokenAdapter;
	int responseNum = 1;
	Detail2Result responseResult;
	int selectButton = 0;
	InputMethodManager imm;
	private int dupNick=-1;
	private int[] usingNicks = new int[MyDetailReplyView.REPLY_NICKS_COUNT];

	private String[] nicknames; //nickname 리스트
	private int lastInputLength=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_detail);
//		====================
		for(int i=0; i<usingNicks.length; i++){
			usingNicks[i]=0;
		}
		
		nicknames = DetailDataManager.getInstance().getNicksArray();
		
		nicks=new ArrayList<String>();
		ids = new ArrayList<String>();
		tokenNicks = new ArrayList<String>();
		
		Intent intent = getIntent();
		responseResult = new Detail2Result();
		if (intent != null) {
			responseNum = Integer.parseInt(intent
					.getStringExtra(CommonResult.REQUEST_NUMBER));
		} else {
			responseNum = 1;
		}
		// ===================

		// ==========================
		
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		listView = (ListView) findViewById(R.id.list_detail_reply);
		headerView = LayoutInflater.from(this).inflate(R.layout.item_detail_header_layout, null, false);

		headerPager = (ViewPager) headerView.findViewById(R.id.pager2);
		circleAnimIndicator = (CircleAnimIndicator) headerView.findViewById(R.id.circleAnimIndicator);

		listView.addHeaderView(headerView, "header", true);

		headerDistanceView = (TextView) headerView .findViewById(R.id.text_detail_distance2);
		headerTimeView = (TextView) headerView .findViewById(R.id.text_detail_time);
		headerRepNumView = (TextView) headerView .findViewById(R.id.text_detail_reply_count2);
		headerGoodNumView = (TextView) headerView .findViewById(R.id.text_detail_like_count2);

		headerReportIcon = (ImageView) headerView .findViewById(R.id.image_detail_report);
		headerReportIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(DetailActivity.this, "report click", Toast.LENGTH_SHORT).show();
				Bundle b = new Bundle();
				b.putSerializable("reporteditem", responseResult.article);
				b.putInt("curruenttab", 0);
				ReportMenuDialogFragment f = new ReportMenuDialogFragment();
				f.setArguments(b);
				f.show(getSupportFragmentManager(), "dialog");
			}
		});
		headerLocateIcon = (ImageView) headerView .findViewById(R.id.image_detail_distance);
		headerTimeIcon = (ImageView) headerView .findViewById(R.id.image_detail_time);
		headerRepIcon = (ImageView) headerView .findViewById(R.id.image_detail_reply);
		headerEmotionIcon = (ImageView) headerView .findViewById(R.id.image_detail_emoticon);

		headerGoodIcon = (ImageView) headerView .findViewById(R.id.image_detail_like);
		frameHeaderLike = (LinearLayout)headerView.findViewById(R.id.frame_detail_like);
		frameHeaderLike.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setLikeDisplay(responseResult);
			}
		});
		headerBackground = (ImageView)findViewById(R.id.image_header_bg);

		mDetailAdapter = new MyDetailReplyAdapter(this);
		mDetailAdapter.setOnAdapterItemClickListener(new OnAdapterItemClickListener() {

					@Override
					public void onAdapterItemClick(
							MyDetailReplyAdapter adapter, View view,
							Detail5Replies item, int type) {
						switch (type) {
						case Detail1Info.TYPE_REPLY_INVALID:
							break;
						case Detail1Info.TYPE_REPLY_TAG:
							if(lastInputLength != editReply.getText().toString().length()){
								isFirst = true;
							}
							if(editReply.getText().toString().length() == 0){
								if(isFirst==false){
									ids.remove(ids.size()-1);
									nicks.remove(nicks.size()-1);	
								}
								isFirst = true; //true로 왜 안바뀌지
							}
							StringBuilder sb = new StringBuilder();
							if (!isFirst) {
								sb.append(", ");
							}
							if(item.master==1){
//								sb.append("\""+"담쟁이"+"\"");
								sb.append("담쟁이");
							} else if(item.master==0){
//								sb.append("\"" + nicknames[item.nickname] + "\"");
								sb.append( nicknames[item.nickname] );
							}
							isFirst = false;
							// ===========
//							completionView.addObject(new Person(sb.toString()));
							// ==========
							nicks.add(""+item.nickname);
							ids.add(item.repAuthor);
							
							SpannableString text = new SpannableString( editReply.getText().toString() + sb );
							text.setSpan(new ForegroundColorSpan(0xFF3E3E3E), 0, text.length(), 0);
							if(nicks != null){
								for(int i=0; i<nicks.size(); i++){
									String tagname = nicknames[ (Integer.parseInt(nicks.get(i))) ] ;
									String tempText = text.toString();
									int lastLen = 0;
									for(;;){
										if(tempText.contains(tagname) ){ 
											int start = tempText.indexOf( tagname.substring(0) ); //substring(0) 고정
											int end = start + nicknames[Integer.parseInt(nicks.get(i))].length() ;
											if(start != -1){
												text.setSpan(new ForegroundColorSpan(0xFF26A7C7), lastLen+start, lastLen+end, 0);	
											}
											tempText = tempText.substring(end);
//											Log.i("DetailActivity_tempText", tempText);
											lastLen = lastLen+end ;
										} else {
											lastLen = 0;
											break;
										}
									}
								}
							}
							editReply.setText(text, BufferType.SPANNABLE);
							lastInputLength = editReply.getText().length();
							editReply.setSelection(lastInputLength);
							break;
						case Detail1Info.TYPE_REPLY_LIKE:
							setRepGoodNumDisplay(item);
							break;
						default:
							break;
						}

					}
				});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Detail5Replies rep = (Detail5Replies) listView .getItemAtPosition(position);
				if(rep.me==1){
					Bundle b = new Bundle();
					b.putSerializable("deletedItem", rep);
					b.putSerializable("deletedadapter", mDetailAdapter);
					b.putInt("responseNum", responseNum);
					b.putInt("activityType", 0); // 0==댓글, 1,2,3==탭게시물
					DeleteDialogFragment f = new DeleteDialogFragment();
					f.setArguments(b);
					f.show(getSupportFragmentManager(), "deletereplydialog");
				} 
				return true;
			}
		});
		
		//========네트워크 중 캔슬러블 false주기
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIcon(R.drawable.a_launcher_1_icon_512x512);
		dialog.setTitle("Loading...");
		dialog.setMessage("상세담을 불러오는 중입니다...");
		dialog.setCancelable(false);
		dialog.show();
		//init detailactivity header view
		NetworkManager.getInstance().getSdamRead(this, responseNum,
				new OnResultListener<Detail1Info>() {

					@Override
					public void onSuccess(Request request, Detail1Info result) {
						
						if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
							// *********리플처리***********
							listView.setAdapter(mDetailAdapter);
							responseResult=result.result;
							mDetailAdapter.clear();
							// **********Clear()할지 말지******
							if (result.result.bestReply != null) {
								if(result.result.bestReply.me == 1){
									dupNick=result.result.bestReply.nickname;
								}
								usingNicks[result.result.bestReply.nickname]=1;
								Detail5Replies best = result.result.bestReply;
								best.isBest=true;
								mDetailAdapter.add(best);
							}
							if (result.result.replies != null) {
								for (Detail5Replies r : result.result.replies) {
									if(r.me == 1){
										dupNick=r.nickname;
									}
									if(usingNicks[r.nickname] != 1){
										tokenNicks.add( nicknames[ r.nickname ]);	
									}
									usingNicks[r.nickname]=1;
									mDetailAdapter.add(r);
								}
								initAutoCompleteData();
							} else {
//								리플이 널인 경우
							}
							setHeaderStatus(result.result.article);
							setHeaderString();
							setHeaderPager();
						} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
//							Toast.makeText(DetailActivity.this, "Success:zero", Toast.LENGTH_SHORT).show();
						} else {
//							Toast.makeText(DetailActivity.this, "Unexpected error..", Toast.LENGTH_SHORT).show();
						}
						dialog.dismiss();
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(DetailActivity.this, "서버요청에 실패하였습니다. #", Toast.LENGTH_SHORT).show();
						Log.e(TAG, "onFailure: "+cause );
						dialog.setCancelable(true);
						dialog.dismiss();
					}
				});

		// listView.setAdapter(mDetailAdapter);
		// ===============================

		editReply = (AutoCompleteTextView) findViewById(R.id.edit_reply_input);
		editReply.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
//				Toast.makeText(DetailActivity.this, editReply.getText().toString(), Toast.LENGTH_SHORT).show();
				String clickedNick=editReply.getText().toString();
				
//				if(lastInputLength != editReply.getText().toString().length()){
//					isFirst = true;
//				} //이부분 주석처리한 이유는 어차피 지금 글 중간에 자동완성 구현은 무리고, 닉네임클릭해서 태그 입력하는 기존 태그 기능시 lastInputLength가 이미 에딧텍스트의 렝스와 같게 되어서 isFirst가 true로 초기화가 안됨.
				isFirst = true; //이렇게해도 버그 없을라나...
				if(editReply.getText().toString().length() == 0){ //아마 여긴 무조건 안들어가(리스트중 클릭했을때니까), 클릭한거 지우면 들어가긴 하겠네
					if(isFirst==false){
						ids.remove(ids.size()-1);
						nicks.remove(nicks.size()-1);	
					}
					isFirst = true; //true로 왜 안바뀌지
				}
				StringBuilder sb = new StringBuilder();
				if (!isFirst) {
					sb.append(", ");
				}
				sb.append(clickedNick);
				isFirst = false;
				int nick = -1;
				String idd="";
				for(int i=0; i<mDetailAdapter.getCount(); i++){
					Detail5Replies r = (Detail5Replies)mDetailAdapter.getItem(i);
					if(nicknames[r.nickname].equals(clickedNick)){
						nick=r.nickname;
						idd=r.repAuthor;
						break;
					}
				}
//				Log.i("nick", ""+nick);
//				Log.i("idd", idd);
				
				nicks.add(""+nick);
				ids.add(idd);
				
				SpannableString text = new SpannableString( ""+sb );
				text.setSpan(new ForegroundColorSpan(0xFF3E3E3E), 0, text.length(), 0);
				if(nicks != null){
					for(int i=0; i<nicks.size(); i++){
						String tagname = nicknames[ (Integer.parseInt(nicks.get(i))) ] ;
						String tempText = text.toString();
						int lastLen = 0;
						for(;;){
							if(tempText.contains(tagname) ){ 
								int start = tempText.indexOf( tagname.substring(0) ); //substring(0) 고정
								int end = start + nicknames[Integer.parseInt(nicks.get(i))].length() ;
								if(start != -1){
									text.setSpan(new ForegroundColorSpan(0xFF26A7C7), lastLen+start, lastLen+end, 0);	
								}
								tempText = tempText.substring(end);
//								Log.i("DetailActivity_tempText", tempText);
								lastLen = lastLen+end ;
							} else {
								lastLen = 0;
								break;
							}
						}
					}
				}
				editReply.setText(text, BufferType.SPANNABLE);
				lastInputLength = editReply.getText().length();
				editReply.setSelection(lastInputLength);
			}
		});
		mAdapter = new MyAdapter(this);
		editReply.setAdapter(mAdapter);
//		editReply = (EditText) findViewById(R.id.edit_reply_input);
//		editReply.setTextColor(TV_TAG_COLOR);
		final Button btn = (Button) findViewById(R.id.btn_add_reply);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				// 댓글리스트에 추가
				if(responseNum==-1){
					Toast.makeText(DetailActivity.this, "게시글이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					return;
				}
				isFirst = true;
				editRepMessage = editReply.getText().toString();
				if (selectButton == DETAIL_BUTTON_COMPLETE) {
					if ((editRepMessage != null && !editRepMessage.equals(""))) {
						// 댓글 서버에 씀
						dialog.setMessage("댓글을 쓰는 중입니다...");
						dialog.setCancelable(false);				
						dialog.show();
						
						int usingNickCount=0;
						
						int nickname;
						if(dupNick != -1){ //처음 read를 받아서 뿌릴 때 중복검사한 닉네임이 있으면 그 닉네임으로 
							nickname = dupNick;
						} else { //새로 닉네임을 만드는경우 == 처음 댓글 쓸 때 == 닉네임을 새로 만들어서 서버에 보냄
							if(PropertyManager.getInstance().getUserId().equals(responseResult.article.writer)){
								nickname = 0 ;
							} else {
								for(;;){
									if( (usingNickCount+1) == MyDetailReplyView.REPLY_NICKS_COUNT){
										// +1하는건 담쟁이인 경우 포함해서 전체 닉카운트랑 중복 닉카운트가 같아지면 아무거나 뽑아서 뿌리게.. 무한루프도는 잠재적 버그 없애기 위해
										nickname = 1 + (int)( Math.random() * (MyDetailReplyView.REPLY_NICKS_COUNT-1) );
										break;
									}
									int tempNick = 1 + (int)( Math.random() * (MyDetailReplyView.REPLY_NICKS_COUNT-1) );
//									Log.i("tempNickName:", ""+tempNick);
									if(usingNicks[tempNick]==1){ //중복인경우
										usingNickCount++;
										continue;
									} else {
										nickname = tempNick;
										break;
									}
								}
//								nickname =  1 + (int)( Math.random() * (MyDetailReplyView.REPLY_NICKS_COUNT-1) );	
							}
							dupNick=nickname;
							usingNicks[nickname]=1;
						}
						String [] nickArr = new String[nicks.size()];
						String [] idsArr = new String[ids.size()];
						nickArr = nicks.toArray(nickArr);
						idsArr = ids.toArray(idsArr);
						
						NetworkManager.getInstance().putSdamArticleReply(
								DetailActivity.this, 
								responseNum, // num
								editRepMessage, // content
								""+nickname, //새로만든 닉네임 or 내가썼던 닉네임
								writer,	//글쓴이
								nickArr,
								idsArr,
								new OnResultListener<ReplyInfo>() {

									@Override
									public void onSuccess(Request request, ReplyInfo result) {
										if (result.success == CommonInfo.COMMON_INFO_SUCCESS) {
											if (result.result != null) {
//												Toast.makeText( DetailActivity.this, "/Reply onSuccess:1\nwork:"+result.work, Toast.LENGTH_SHORT) .show();
												mDetailAdapter.clear();
												if(result.result.bestReply != null){
													Detail5Replies best = result.result.bestReply;
													best.isBest=true;								
													mDetailAdapter.add(best);
												}
												for (Detail5Replies r : result.result.replies) {
													mDetailAdapter.add(r);
												}
//												댓글등록이 성공했으면 헤더의 댓글 숫자 증가
												responseResult.article.repNum+=1;
												setHeaderStatus(responseResult.article);
												listView.setSelection(0);
												//===태그 배열 클리어
												
//												initAutoCompleteData(); //nicks clear()하기 전에
												nicks.clear();
												ids.clear();
												editReply.getEditableText().clear();
												imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
												
											} else if (result.result == null) {
//												Toast.makeText( DetailActivity.this,
//														"/Reply result:null\nwork"+result.work, Toast.LENGTH_SHORT) .show();
											}
										} else if (result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO) {
											Toast.makeText(DetailActivity.this, "댓글 등록에 실패하였습니다."+result.work, Toast.LENGTH_SHORT).show();
										} else {
//											Toast.makeText( DetailActivity.this, "/Reply Unexpected error..", Toast.LENGTH_SHORT).show();
										}
										dialog.setCancelable(true);
										dialog.dismiss();
										
									}//onSuccess

									@Override
									public void onFailure(Request request, int code, Throwable cause) {
										Toast.makeText(DetailActivity.this, "댓글 등록에 실패하였습니다. #" + code, Toast.LENGTH_SHORT).show();
										dialog.setCancelable(true);
										dialog.dismiss();
									}
								});
					} else {
						Toast.makeText(DetailActivity.this, "댓글을 입력하세요",
								Toast.LENGTH_SHORT).show();
					}
				} else if (selectButton == DETAIL_BUTTON_CANCEL) {
					// 키보드 hide
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}

			}
		});// btn listener

		editReply.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0) {
					btn.setBackgroundResource(R.drawable.b_main_view_2_detail_comments_next_button_blue_cancle);
					selectButton = DETAIL_BUTTON_CANCEL;
				} else {
					btn.setBackgroundResource(R.drawable.b_main_view_2_detail_comments_next_button_blue_ok);
					selectButton = DETAIL_BUTTON_COMPLETE;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				btn.setVisibility(View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				btn.setVisibility(View.VISIBLE);
//				btn.setBackgroundResource(R.drawable.b_main_view_2_detail_comments_next_button_blue_cancle);
			}
		});
//		editReply.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				if(hasFocus){
//					btn.setVisibility(View.VISIBLE);
//					btn.setBackgroundResource(R.drawable.b_main_view_2_detail_comments_next_button_blue_cancle);	
//				} else {
//					btn.setVisibility(View.GONE);
//				}
//				listView.setSelection(0);
//			}
//		});
//=======================
		Tracker t = ((MyApplication)getApplication()).getTracker
				(MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName("DetailActivity");
		t.send(new HitBuilders.AppViewBuilder().build());
		
		
		
	} //onCreate
	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
	public String getContent() {
		return content;
	}

	private ArrayList<String> tokenNicks;
	private void initAutoCompleteData() {
//		nickArr = nicks.toArray(nickArr);
		String[] array = new String[tokenNicks.size()];
		array = tokenNicks.toArray(array);
		for(String s : array){
			Person p = new Person();
			p.resId = R.drawable.a_launcher_1_icon_notification;
			p.name = s;
			mAdapter.add(p);
		}	
	}

	@Override
	protected void onResume() {
		super.onResume();
		actionBar = getSupportActionBar();
		actionBar.setTitle("");
		Drawable d = getResources().getDrawable( R.drawable.b_main_view_2_actionbar_detail);
		actionBar.setBackgroundDrawable(d);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeAsUpIndicator(R.drawable.z_actionbar_back_button);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_detail, menu);
		// return true;
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == android.R.id.home) {
//			finish();
			finishAndReturnData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
	GoodReplyInfo goodReplyInfo;
	CancelGReplyInfo cancelGReplyInfo;
	public void setRepGoodNumDisplay(Detail5Replies item) {
		final Detail5Replies tempItem = item;
//		final Detail5Replies tempBestItem;
		if (tempItem.myGood == 0) { //myGood으로 바꿔서 탭에서 했던 거랑 똑같이 해
			NetworkManager.getInstance().putSdamGoodReply(DetailActivity.this,
					responseNum, item.repNum, item.repAuthor,
					
					new OnResultListener<GoodReplyInfo>() {

						@Override
						public void onSuccess(Request request, GoodReplyInfo result) {
							goodReplyInfo = result;
							if(goodReplyInfo.success == CommonInfo.COMMON_INFO_SUCCESS){
//								Toast.makeText(DetailActivity.this, "/goodreply onSuccess:1", Toast.LENGTH_SHORT).show();
//								mDetailAdapter.setLikeNum(tempItem);
								mDetailAdapter.dupExistSetLikeNum(tempItem);
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(DetailActivity.this,
									"서버에 연결할 수 없습니다."+code, Toast.LENGTH_SHORT) .show();
						}
					});
		}// if
		else if (tempItem.myGood >= 1) {
			NetworkManager.getInstance().putSdamCancelGoodReply( DetailActivity.this,
					responseNum, item.repNum, 
					new OnResultListener<CancelGReplyInfo>() {

						@Override
						public void onSuccess(Request request, CancelGReplyInfo result) {
							cancelGReplyInfo=result;
							if(cancelGReplyInfo.success == CommonInfo.COMMON_INFO_SUCCESS){
//								Toast.makeText(DetailActivity.this, "/cancelgreply onSuccess:1", Toast.LENGTH_SHORT).show();
//								mDetailAdapter.setLikeNum(tempItem);
								mDetailAdapter.dupExistSetLikeNum(tempItem);
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(DetailActivity.this,
									"서버에 연결할 수 없습니다. #"+code, Toast.LENGTH_SHORT) .show();
						}
					});
		}
	}

	private void initIndicator(){
		// 원사이의 간격
		circleAnimIndicator.setItemMargin(15);
		// 애니메이션 속도
		circleAnimIndicator.setAnimDuration(300);
		// indecator 생성
		circleAnimIndicator.createDotPanel(pageCount, R.drawable.b_main_view_2_page_non , R.drawable.b_main_view_2_page_on);
	}

	private void setHeaderPager() {
		mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), pageCount);
		headerPager.setAdapter(mViewPagerAdapter);
		headerPager.addOnPageChangeListener(mOnPageChangeListener);
		initIndicator();
		headerPager.setCurrentItem(0);
	}

	/**
	 * ViewPager 전환시 호출되는 메서드
	 */
	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
		@Override
		public void onPageSelected(int position) {
			circleAnimIndicator.selectDot(position);
		}
		@Override
		public void onPageScrollStateChanged(int state) { }
	};

	private String writer=null;
	private void setHeaderStatus(CommonResult article) {
		writer = article.writer;
		originRepCount = article.repNum; //원래 댓글수 저장
		if (article.locate == 99999) {
			headerDistanceView.setText("어딘가	");
		} else {
			headerDistanceView.setText("" + article.locate + "km");
		}
		if(article.myGood==0){
			headerGoodIcon.setImageResource(R.drawable.b_main_view_contents_icon_05_off);
		} else {
			headerGoodIcon.setImageResource(R.drawable.b_main_view_contents_icon_05_on);
		}
		
		if(article.timeStamp != null){
			String timeStr="time";
			if(article.timeStamp.time.equals("hours")){
				timeStr = "시간 전";
			} else if(article.timeStamp.time.equals("dates")){
				timeStr = "일 전";
			} else if(article.timeStamp.time.equals("minutes")){
				timeStr = "분 전";
			} else if(article.timeStamp.time.equals("second")){
				timeStr = "초 전";
			} else if (article.timeStamp.time.equals("months")) {
				timeStr = "달 전";
			} else if (article.timeStamp.time.equals("years")){
				timeStr = "년 전";
			}
			headerTimeView.setText(article.timeStamp.value + timeStr);
		}
//		if(article.myGood==0){
//			headerGoodNumView.setTextColor(0xb3ffffff);
//		} else {
//			headerGoodNumView.setTextColor(0xFF26A7C7);
//		}
		headerRepNumView.setText("" + article.repNum);
		headerGoodNumView.setText("" + article.goodNum);
		content = "" + article.content;// 이게 TextFragment랑 동기화되는 개념
		headerEmotionIcon
				.setImageResource( BgImage.getEmotionIcon(article.emotion) );
		headerBackground.setImageResource( BgImage.getBgImage(Integer.parseInt(article.category), Integer.parseInt(article.image)) );
	}

	public void setHeaderString() {
		pageCount = (int) (content.length() / DETAIL_CONTENT_LENGTH);
//		pageCount++;
		if(content.length() == DETAIL_CONTENT_MAX_LENGTH){ //maxlength임
			pageCount = 3;
		} else {
			pageCount++;
		}
		if(circleAnimIndicator != null && pageCount > 1 ){
			circleAnimIndicator.setVisibility(View.VISIBLE);
		}
		strArr = new String[pageCount];
		int end = content.length();
		
		for (int i = 0; i < pageCount; i++) {
			if (content.length() < DETAIL_CONTENT_LENGTH) {
				strArr[i] = content;
			} else {
				if (i + 1 == pageCount) { //마지막 페이지면
					strArr[i] = content.substring(i * DETAIL_CONTENT_LENGTH, end);
				} else {
					strArr[i] = content.substring(i * DETAIL_CONTENT_LENGTH, ((i + 1) * (DETAIL_CONTENT_LENGTH)) );
				}

			}
//			뷰페이저 페이지별 내용 확인
//			Toast.makeText(DetailActivity.this, "" + i + " " + strArr[i], Toast.LENGTH_SHORT).show();
		}
	}

	public String getHeaderString(int position) {
		return strArr[position];
	}

	public void setLikeDisplay(final Detail2Result item) {	//setting header like display 
		if(item.article.myGood >= 1 ){ //myGood == 1 이미 내가 라이크를 누른 상태
			NetworkManager.getInstance().putSdamGoodCancel(DetailActivity.this,
					String.valueOf(item.article.num),/*글번호 */
					new OnResultListener<GoodCancelInfo>() {

						@Override
						public void onSuccess(Request request, GoodCancelInfo result) {
							GoodCancelInfo goodCancelInfo = result;
							if (goodCancelInfo.success == CommonInfo.COMMON_INFO_SUCCESS) {
//								Toast.makeText( DetailActivity.this, "/goodcancel onSuccess:1" , Toast.LENGTH_SHORT).show();
								responseResult.article.myGood=0;
								responseResult.article.goodNum-=1;
								headerGoodIcon.setImageResource(R.drawable.b_main_view_contents_icon_05_off);
//								if(responseResult.article.myGood==0){
//									headerGoodNumView.setTextColor(0xb3ffffff);
//								} else {
//									headerGoodNumView.setTextColor(0xFF26A7C7);
//								}
								headerGoodNumView.setText("" + responseResult.article.goodNum);
								//==fragment의 라이크 동기화 처리==
//								Intent goodIntent = new Intent();
////								goodIntent.putExtra("modifiedItem", responseResult.article);
//								goodIntent.putExtra("myGood", responseResult.article.myGood);
//								goodIntent.putExtra("goodNum", responseResult.article.goodNum);
//								goodIntent.putExtra("repNum", responseResult.article.repNum);
//								setResult(RESULT_OK, goodIntent);
							} else {
//								Toast.makeText(
//										DetailActivity.this,
//										"/goodcancel onSuccess:fail " + goodCancelInfo.success+"\n" + goodCancelInfo.work , Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {

						}
					});
		}//if mygood==1
		
		else if(item.article.myGood== 0){ //if myGood==0이면
			NetworkManager.getInstance().putSdamGood(DetailActivity.this, 
					String.valueOf(item.article.num), 
					item.article.writer,
					new OnResultListener<GoodInfo>() {

						@Override
						public void onSuccess(Request request, GoodInfo result) {
							GoodInfo goodInfo = result;
							if (goodInfo.success == CommonInfo.COMMON_INFO_SUCCESS) {
								responseResult.article.myGood=1;
								responseResult.article.goodNum+=1;
								headerGoodIcon.setImageResource(R.drawable.b_main_view_contents_icon_05_on);
//								if(responseResult.article.myGood==0){
//									headerGoodNumView.setTextColor(0xb3ffffff);
//								} else {
//									headerGoodNumView.setTextColor(0xFF26A7C7);
//								}
								headerGoodNumView.setText("" + responseResult.article.goodNum);
								//==fragment의 라이크 동기화 처리==
//								Intent goodIntent = new Intent();
////								goodIntent.putExtra("modifiedItem", responseResult.article);
//								goodIntent.putExtra("myGood", responseResult.article.myGood);
//								goodIntent.putExtra("goodNum", responseResult.article.goodNum);
//								goodIntent.putExtra("repNum", responseResult.article.repNum);
//								setResult(RESULT_OK, goodIntent);
								
							} else {
//								Toast.makeText(DetailActivity.this,
//										"/good : success 0 ", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onFailure(Request request, int code, Throwable cause) {
							Toast.makeText(DetailActivity.this, "서버에 연결할 수 없습니다. #" + code, Toast.LENGTH_SHORT) .show();
						}
					});
			
		} //else if
	}// setLikeDisplay

	private void finishAndReturnData(){
		if(responseResult.article != null){
			Intent intent = new Intent();
			intent.putExtra("_OBJ_", responseResult.article);
			setResult(RESULT_OK, intent);
		}
		finish();
	}
	@Override
	public void onBackPressed() {
		finishAndReturnData();
		super.onBackPressed();
	}
}
