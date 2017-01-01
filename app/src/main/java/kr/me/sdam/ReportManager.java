package kr.me.sdam;

import kr.me.sdam.dialogs.ReportMenuDialogFragment;
import kr.me.sdam.search.MySearchAdapter;
import kr.me.sdam.search.SearchResult;
import kr.me.sdam.tabone.TabOneAdapter;
import kr.me.sdam.tabone.TabOneResult;
import kr.me.sdam.tabthree.TabThreeAdapter;
import kr.me.sdam.tabthree.TabThreeResult;
import kr.me.sdam.tabtwo.TabTwoAdapter;
import kr.me.sdam.tabtwo.TabTwoResult;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ReportManager extends Fragment{
	
	public static final int TAB_TYPE_ONE = 1;
	public static final int TAB_TYPE_TWO = 2;
	public static final int TAB_TYPE_THREE = 3;
	public static final int TAB_TYPE_SEARCH = 4;
	public static final String KEY_CURRENT_TAB = "curruenttab";
	public static final String KEY_REPORTED_ITEM ="reporteditem";
	public static final String KEY_REPORTED_ADAPTER ="reportedadapter";
	
	private static ReportManager instance;
	public static ReportManager getInstance(){
		if(instance == null) {
			instance = new ReportManager();
		}
		return instance;
	}
	private ReportManager(){
		
	}
	
	public void showReportMenu(TabOneAdapter adapter, TabOneResult item, int tabtype){
		Bundle b = new Bundle();
		b.putSerializable("reporteditem", item);
		b.putSerializable("reportedadapter", adapter);
		b.putInt("curruenttab", 1);
		ReportMenuDialogFragment f = new ReportMenuDialogFragment();
		f.setArguments(b);
		f.show(getFragmentManager(), "dialog");
	}
	public void showReportMenu(TabTwoAdapter adapter, TabTwoResult item, int tabtype){
		Bundle b = new Bundle();
		b.putSerializable(KEY_REPORTED_ITEM, item);
		b.putSerializable(KEY_REPORTED_ADAPTER, adapter);
		b.putInt(KEY_CURRENT_TAB, tabtype);
		ReportMenuDialogFragment f = new ReportMenuDialogFragment();
		f.setArguments(b);
		f.show(getFragmentManager(), "dialog");
	}
	public void showReportMenu(TabThreeAdapter adapter, TabThreeResult item, int tabtype){
		Bundle b = new Bundle();
		b.putSerializable(KEY_REPORTED_ITEM, item);
		b.putSerializable(KEY_REPORTED_ADAPTER, adapter);
		b.putInt(KEY_CURRENT_TAB, tabtype);
		ReportMenuDialogFragment f = new ReportMenuDialogFragment();
		f.setArguments(b);
		f.show(getFragmentManager(), "dialog");
	}
	public void showReportMenu(MySearchAdapter adapter, SearchResult item, int tabtype){
		Bundle b = new Bundle();
		b.putSerializable(KEY_REPORTED_ITEM, item);
		b.putSerializable(KEY_REPORTED_ADAPTER, adapter);
		b.putInt(KEY_CURRENT_TAB, tabtype);
		ReportMenuDialogFragment f = new ReportMenuDialogFragment();
		f.setArguments(b);
		f.show(getFragmentManager(), "dialog");
	}
	
	
}
