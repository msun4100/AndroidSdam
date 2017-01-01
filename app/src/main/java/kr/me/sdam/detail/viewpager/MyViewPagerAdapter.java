package kr.me.sdam.detail.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyViewPagerAdapter extends FragmentStatePagerAdapter{

	public MyViewPagerAdapter(FragmentManager fm,int count) {
		super(fm);
		setCount(count);
	}

	public int pageCount;
	@Override
	public Fragment getItem(int position) {

		TextFragment tf = new TextFragment();
		Bundle b = new Bundle();
		b.putInt(TextFragment.EXTRA_POSITION, position);
		tf.setArguments(b);
		
		return tf;
	}

	@Override
	public int getCount() {
		return pageCount;
//		return 5;
	}
	
	public void setCount(int count){
		this.pageCount=count;
	}
	
	public void setViewPagerItem(){
		
	}
}
