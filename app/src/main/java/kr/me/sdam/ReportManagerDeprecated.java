//package kr.me.sdam;
//
//import kr.me.sdam.NetworkManager.OnResultListener;
//import kr.me.sdam.alarm.CustomDialogFragment;
//import kr.me.sdam.common.CommonResultAdapter;
//import kr.me.sdam.common.CommonResultItem;
//import kr.me.sdam.dialogs.ReportOneDialogFragment;
//import kr.me.sdam.report.ABanInfo;
//import kr.me.sdam.report.ReportInfo;
//import kr.me.sdam.report.WBanInfo;
//import kr.me.sdam.tabone.TabOneAdapter;
//import kr.me.sdam.tabone.TabOneResult;
//import kr.me.sdam.tabthree.TabThreeAdapter;
//import kr.me.sdam.tabthree.TabThreeResult;
//import kr.me.sdam.tabtwo.TabTwoAdapter;
//import kr.me.sdam.tabtwo.TabTwoResult;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.support.v4.app.FragmentManager;
//import android.view.View;
//import android.widget.Toast;
//
//public class ReportManagerDeprecated {
//	private static ReportManagerDeprecated instance;
//	public static ReportManagerDeprecated getInstance(){
//		if(instance == null) {
//			instance = new ReportManagerDeprecated();
//		}
//		return instance;
//	}
//	private ReportManagerDeprecated(){
//
//	}
//
//	final String[] alertItems = {
//			"                      신고하기",
//			"                 더 이상 보지 않기" ,
//			"                      차단하기"}; //for Dialog
//	public static AlertDialog.Builder builder;
//
//	public AlertDialog.Builder getTabAlertDialogBuilder(Context context,
//			final int num, final int category, final String writer){
//
//		if(builder == null){
//			builder = new AlertDialog.Builder(context);
////			builder.setIcon(R.drawable.a_launcher_1_icon_512x512);
////			builder.setTitle("Report Dialog..");
////			builder.setView(R.layout.dialog_report_layout);
//			builder.setItems(alertItems, new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Toast.makeText(MyApplication.getContext(), "items:"+alertItems[which], Toast.LENGTH_SHORT).show();
//					switch(which){
//					case 0:
//						Toast.makeText(MyApplication.getContext(), "case 0:report\n"
//								+"num:"+""+num +"\ncategory: "+""+category+"\nwriter:"+writer , Toast.LENGTH_SHORT).show();
//						NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
//								num, category, writer,
//								new OnResultListener<ReportInfo>() {
//
//									@Override
//									public void onSuccess(ReportInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/report onSuccess", Toast.LENGTH_SHORT).show();
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						break;
//					case 1:
//						NetworkManager.getInstance().putSdamABan(MyApplication.getContext()
//								, num, writer,
//								new OnResultListener<ABanInfo>() {
//
//									@Override
//									public void onSuccess(ABanInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/aban onSuccess", Toast.LENGTH_SHORT).show();
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						break;
//					case 2:
//						NetworkManager.getInstance().putSdamWBan(MyApplication.getContext()
//								, num, writer,
//								new OnResultListener<WBanInfo>(){
//
//									@Override
//									public void onSuccess(WBanInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/wban onSuccess", Toast.LENGTH_SHORT).show();
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//
//								});
//						break;
//						default:
//							builder.setCancelable(false);
//							builder.create().show();
//							break;
//					}
//				}
//			});
//		}
//
//		return builder;
//	}
//
//
//	public AlertDialog.Builder getTabOneAlertDialogBuilder(Context context,
//			final TabOneAdapter adapter, final TabOneResult item){
//
//		if(builder == null){
//			builder = new AlertDialog.Builder(context);
////			builder.setIcon(R.drawable.a_launcher_1_icon_512x512);
////			builder.setTitle("Report Dialog..");
////			builder.setView(R.layout.dialog_report_layout);
//			builder.setCancelable(true);
//			builder.setItems(alertItems, new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Toast.makeText(MyApplication.getContext(), "items:"+alertItems[which], Toast.LENGTH_SHORT).show();
//					switch(which){
//					case 0:
//						NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
//								item.num, Integer.parseInt(item.category), item.writer,
//								new OnResultListener<ReportInfo>() {
//
//									@Override
//									public void onSuccess(ReportInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/report onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						break;
//					case 1:
//						NetworkManager.getInstance().putSdamABan(MyApplication.getContext()
//								, item.num, item.writer,
//								new OnResultListener<ABanInfo>() {
//
//									@Override
//									public void onSuccess(ABanInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/aban onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						break;
//					case 2:
//						NetworkManager.getInstance().putSdamWBan(MyApplication.getContext()
//								, item.num, item.writer,
//								new OnResultListener<WBanInfo>(){
//
//									@Override
//									public void onSuccess(WBanInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/wban onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//
//								});
//						break;
//						default:
//							builder.setCancelable(false);
//							builder.create().show();
//							break;
//					}
//				}
//			});
//		}
//
//		return builder;
//	}
//
//	public AlertDialog.Builder getTabTwoAlertDialogBuilder(Context context,
//			final TabTwoAdapter adapter, final TabTwoResult item){
//
//		if(builder == null){
//			builder = new AlertDialog.Builder(context);
//			builder.setItems(alertItems, new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Toast.makeText(MyApplication.getContext(), "items:"+alertItems[which], Toast.LENGTH_SHORT).show();
//					switch(which){
//					case 0:
//						NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
//								item.num, Integer.parseInt(item.category), item.writer,
//								new OnResultListener<ReportInfo>() {
//
//									@Override
//									public void onSuccess(ReportInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/report onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						break;
//					case 1:
//						NetworkManager.getInstance().putSdamABan(MyApplication.getContext()
//								, item.num, item.writer,
//								new OnResultListener<ABanInfo>() {
//
//									@Override
//									public void onSuccess(ABanInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/aban onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						break;
//					case 2:
//						NetworkManager.getInstance().putSdamWBan(MyApplication.getContext()
//								, item.num, item.writer,
//								new OnResultListener<WBanInfo>(){
//
//									@Override
//									public void onSuccess(WBanInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/wban onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//
//								});
//						break;
//						default:
//							builder.setCancelable(false);
//							builder.create().show();
//							break;
//					}
//				}
//			});
//		}
//
//		return builder;
//	}
//
//	public AlertDialog.Builder getTabThreeAlertDialogBuilder(Context context,
//			final TabThreeAdapter adapter, final TabThreeResult item){
//
//		if(builder == null){
//			builder = new AlertDialog.Builder(context);
////			builder.setIcon(R.drawable.a_launcher_1_icon_512x512);
////			builder.setTitle("Report Dialog..");
////			builder.setView(R.layout.dialog_report_layout);
//			builder.setItems(alertItems, new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Toast.makeText(MyApplication.getContext(), "items:"+alertItems[which], Toast.LENGTH_SHORT).show();
//					switch(which){
//					case 0:
//
////						Toast.makeText(MyApplication.getContext(), "case 0:report\n"
////								+"num:"+""+num +"\ncategory: "+""+category+"\nwriter:"+writer , Toast.LENGTH_SHORT).show();
//						NetworkManager.getInstance().putSdamReport(MyApplication.getContext(),
//								item.num, Integer.parseInt(item.category), item.writer,
//								new OnResultListener<ReportInfo>() {
//
//									@Override
//									public void onSuccess(ReportInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/report onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						break;
//					case 1:
//						NetworkManager.getInstance().putSdamABan(MyApplication.getContext()
//								, item.num, item.writer,
//								new OnResultListener<ABanInfo>() {
//
//									@Override
//									public void onSuccess(ABanInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/aban onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//								});
//						break;
//					case 2:
//						NetworkManager.getInstance().putSdamWBan(MyApplication.getContext()
//								, item.num, item.writer,
//								new OnResultListener<WBanInfo>(){
//
//									@Override
//									public void onSuccess(WBanInfo result) {
//										Toast.makeText(MyApplication.getContext(), "/wban onSuccess", Toast.LENGTH_SHORT).show();
//										adapter.remove(item);
//									}
//
//									@Override
//									public void onFail(int code) {
//										// TODO Auto-generated method stub
//
//									}
//
//								});
//						break;
//						default:
//							builder.setCancelable(false);
//							builder.create().show();
//							break;
//					}
//				}
//			});
//		}
//
//		return builder;
//	}
//}
