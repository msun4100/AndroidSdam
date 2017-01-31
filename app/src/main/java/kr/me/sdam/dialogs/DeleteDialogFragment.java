package kr.me.sdam.dialogs;

import kr.me.sdam.NetworkManager;
import kr.me.sdam.NetworkManager.OnResultListener;
import kr.me.sdam.R;
import kr.me.sdam.common.CommonInfo;
import kr.me.sdam.common.CommonResult;
import kr.me.sdam.common.event.EventBus;
import kr.me.sdam.common.event.EventInfo;
import kr.me.sdam.detail.Detail5Replies;
import kr.me.sdam.detail.MyDetailReplyAdapter;
import kr.me.sdam.detail.reply.DelReplyInfo;
import kr.me.sdam.mypage.favor.Favor2Result;
import kr.me.sdam.mypage.favor.FavorAdapter;
import kr.me.sdam.mypage.mylist.MyList2Result;
import kr.me.sdam.mypage.mylist.MyListAdapter;
import kr.me.sdam.search.MySearchAdapter;
import kr.me.sdam.tabone.TabOneAdapter;
import kr.me.sdam.tabone.TabOneResult;
import kr.me.sdam.tabthree.TabThreeAdapter;
import kr.me.sdam.tabthree.TabThreeResult;
import kr.me.sdam.tabtwo.TabTwoAdapter;
import kr.me.sdam.tabtwo.TabTwoResult;
import kr.me.sdam.write.DelArticleInfo;
import okhttp3.Request;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class DeleteDialogFragment extends DialogFragment {
	
	
	MyDetailReplyAdapter mReplyAdapter;
	Detail5Replies mReplyItem;
	
	TabOneAdapter tabOneAdapter;
	CommonResult tabOneItem;
	
	TabTwoAdapter tabTwoAdapter;
	CommonResult tabTwoItem;
	
	TabThreeAdapter tabThreeAdapter;
//	TabThreeResult tabThreeItem;
	CommonResult tabThreeItem;

	MySearchAdapter mSearchAdapter;
	CommonResult mSearchItem;
	
	MyListAdapter myListAdapter;
	CommonResult myListItem;
	
	FavorAdapter favorAdapter;
	CommonResult favorItem;
	
	int responseNum;
	int type;
//	Bundle b = new Bundle();
//	b.putSerializable("deletedItem", rep);
//	b.putSerializable("deletedadapter", mDetailAdapter);
//	b.putInt("responseNum", responseNum);
//	DeleteDialogFragment f = new DeleteDialogFragment();
//	f.setArguments(b);
//	f.show(getSupportFragmentManager(), "deletereplydialog");
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
		Bundle b = getArguments();
		if(b != null){
			type= b.getInt("activityType");
			if(type == 0){
				mReplyAdapter = (MyDetailReplyAdapter)b.getSerializable("deletedadapter");
				mReplyItem = (Detail5Replies)b.getSerializable("deletedItem");
				responseNum = b.getInt("responseNum");	
			} else if(type == 1){
				tabOneAdapter = (TabOneAdapter)b.getSerializable("deletedadapter");
				tabOneItem = (CommonResult)b.getSerializable("deletedItem");
				responseNum=b.getInt("responseNum");
			} else if(type == 2){
				tabTwoAdapter = (TabTwoAdapter)b.getSerializable("deletedadapter");
				tabTwoItem = (CommonResult)b.getSerializable("deletedItem");
				responseNum=b.getInt("responseNum");
			} else if(type == 3){
				tabThreeAdapter = (TabThreeAdapter)b.getSerializable("deletedadapter");
				tabThreeItem = (CommonResult)b.getSerializable("deletedItem");
				responseNum=b.getInt("responseNum");
			} else if(type == 4){ //search..
				mSearchAdapter = (MySearchAdapter)b.getSerializable("deletedadapter");
				mSearchItem = (CommonResult)b.getSerializable("deletedItem");
				responseNum=b.getInt("responseNum");
			} else if(type == 5){
				myListAdapter = (MyListAdapter)b.getSerializable("deletedadapter");
				myListItem = (CommonResult)b.getSerializable("deletedItem");
				responseNum=b.getInt("responseNum");
			} else if(type == 6){
				favorAdapter = (FavorAdapter)b.getSerializable("deletedadapter");
				favorItem = (CommonResult)b.getSerializable("deletedItem");
				responseNum=b.getInt("responseNum");
			}			
		}
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		getDialog().getWindow().setGravity(Gravity.CENTER_VERTICAL);
//		getDialog().getWindow().setLayout(300,300);
//		getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
//		getDialog().getWindow().getAttributes().x = 120;
//		getDialog().getWindow().getAttributes().y = -18;
		
		View view = inflater.inflate(R.layout.dialog_delete_layout, container, false);
		Button btn = (Button)view.findViewById(R.id.btn_dialog_cancel);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btn = (Button)view.findViewById(R.id.btn_dialog_ok);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(type == 0){
					NetworkManager.getInstance().putSdamDelReply(
							getActivity(), responseNum, mReplyItem.repNum,
							new OnResultListener<DelReplyInfo>() {

								@Override
								public void onSuccess(Request request, DelReplyInfo result) {
									if(result.success== CommonInfo.COMMON_INFO_SUCCESS){
										mReplyAdapter.remove(mReplyItem);
									} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
//										Toast.makeText(getActivity(),
//												"/delreply onSuccess:0 ", Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(getActivity(),
												"unexpected error.. ", Toast.LENGTH_SHORT).show();
									}
									dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									Toast.makeText(getActivity(),
											"서버에 연결할 수 없습니다. #"+ code, Toast.LENGTH_SHORT).show();
									dismiss();
								}
							});	
				} else if(type == 1){
					NetworkManager.getInstance().putSdamDelArticle(getActivity(), responseNum, 
							new OnResultListener<DelArticleInfo>() {

								@Override
								public void onSuccess(Request request, DelArticleInfo result) {
									if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
										tabOneAdapter.remove(tabOneItem);
										EventInfo eventInfo = new EventInfo(tabOneItem, EventInfo.MODE_DELETE);
										EventBus.getInstance().post(eventInfo);
									} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
										Toast.makeText(getActivity(), "내 게시글이 아닙니다.(글 삭제 실패)\nwork:"+result.work, Toast.LENGTH_SHORT).show();
									}
									dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									Toast.makeText(getActivity(), "Unexpected error.."+code, Toast.LENGTH_SHORT).show();
									dismiss();
								}
							});
				} else if(type == 2){
					NetworkManager.getInstance().putSdamDelArticle(getActivity(), responseNum, 
							new OnResultListener<DelArticleInfo>() {

								@Override
								public void onSuccess(Request request, DelArticleInfo result) {
									if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
										tabTwoAdapter.remove(tabTwoItem);
										EventInfo eventInfo = new EventInfo(tabTwoItem, EventInfo.MODE_DELETE);
										EventBus.getInstance().post(eventInfo);
									} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
										Toast.makeText(getActivity(), "내 게시글이 아닙니다.(글 삭제 실패)\nwork:"+result.work, Toast.LENGTH_SHORT).show();
									}
									dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									Toast.makeText(getActivity(), "Unexpected error.."+code, Toast.LENGTH_SHORT).show();
									dismiss();
								}
							});
				}//else if
				else if(type == 3){
					NetworkManager.getInstance().putSdamDelArticle(getActivity(), responseNum, 
							new OnResultListener<DelArticleInfo>() {

								@Override
								public void onSuccess(Request request, DelArticleInfo result) {
									if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
										tabThreeAdapter.remove(tabThreeItem);
										EventInfo eventInfo = new EventInfo(tabThreeItem, EventInfo.MODE_DELETE);
										EventBus.getInstance().post(eventInfo);
									} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
										Toast.makeText(getActivity(), "내 게시글이 아닙니다.(글 삭제 실패)\nwork:"+result.work, Toast.LENGTH_SHORT).show();
									}
									dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									Toast.makeText(getActivity(), "Unexpected error.."+code, Toast.LENGTH_SHORT).show();
									dismiss();
								}
							});
				}//else if
				else if(type == 4){
					NetworkManager.getInstance().putSdamDelArticle(getActivity(), responseNum,
							new OnResultListener<DelArticleInfo>() {

								@Override
								public void onSuccess(Request request, DelArticleInfo result) {
									if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
										mSearchAdapter.remove(mSearchItem);
										EventInfo eventInfo = new EventInfo(mSearchItem, EventInfo.MODE_DELETE);
										EventBus.getInstance().post(eventInfo);
									} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
										Toast.makeText(getActivity(), "내 게시글이 아닙니다.(글 삭제 실패)\nwork:"+result.work, Toast.LENGTH_SHORT).show();
									}
									dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									Toast.makeText(getActivity(), "Unexpected error.."+code, Toast.LENGTH_SHORT).show();
									dismiss();
								}
							});
				}
				else if(type == 5){
					NetworkManager.getInstance().putSdamDelArticle(getActivity(), responseNum, 
							new OnResultListener<DelArticleInfo>() {

								@Override
								public void onSuccess(Request request, DelArticleInfo result) {
									if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
										myListAdapter.remove(myListItem);
										EventInfo eventInfo = new EventInfo(myListItem, EventInfo.MODE_DELETE);
										EventBus.getInstance().post(eventInfo);
									} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
										Toast.makeText(getActivity(), "내 게시글이 아닙니다.(마이페이지 글 삭제 실패)\nwork:"+result.work, Toast.LENGTH_SHORT).show();
									}
									dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									Toast.makeText(getActivity(), "Unexpected error.."+code, Toast.LENGTH_SHORT).show();
									dismiss();
								}
							});
				}//else if
				else if(type == 6){
					NetworkManager.getInstance().putSdamDelArticle(getActivity(), responseNum, 
							new OnResultListener<DelArticleInfo>() {

								@Override
								public void onSuccess(Request request, DelArticleInfo result) {
									if(result.success == CommonInfo.COMMON_INFO_SUCCESS){
										favorAdapter.remove(favorItem);
										EventInfo eventInfo = new EventInfo(favorItem, EventInfo.MODE_DELETE);
										EventBus.getInstance().post(eventInfo);
									} else if(result.success == CommonInfo.COMMON_INFO_SUCCESS_ZERO){
										Toast.makeText(getActivity(), "내 게시글이 아닙니다.(글 삭제 실패)\nwork:"+result.work, Toast.LENGTH_SHORT).show();
									}
									dismiss();
								}

								@Override
								public void onFailure(Request request, int code, Throwable cause) {
									Toast.makeText(getActivity(), "Unexpected error.."+code, Toast.LENGTH_SHORT).show();
									dismiss();
								}
							});
				}//else if
				
				
			}//onClick
		});
		
//		initData(); //여기에서 알람 데이터목록 추가함
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
//		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
//		getDialog().setTitle("Custom Dialog");
	}
	private void initData(){
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	@Override
	public void onResume() {
		super.onResume();
		initData();
	}
}
