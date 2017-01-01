package kr.me.sdam.write.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class WriteTwoPagerAdapter extends FragmentStatePagerAdapter {

	public WriteTwoPagerAdapter(FragmentManager fm,int count) {
		super(fm);
		setCount(count);
	}

	public int pageCount;
	@Override
	public Fragment getItem(int position) {

		WriteTwoImageFragment tf = new WriteTwoImageFragment();
		Bundle b = new Bundle();
		b.putInt(WriteTwoImageFragment.EXTRA_POSITION, position);
		tf.setArguments(b);
		
		return tf;
	}

	@Override
	public int getCount() {
		return pageCount;
//		return 5;
	}
	@Override
	public float getPageWidth(int position) {
		return 0.3f;
	}
	
	
	public void setCount(int count){
		this.pageCount=count;
	}
	
	public void setViewPagerItem(){
		
	}

}
