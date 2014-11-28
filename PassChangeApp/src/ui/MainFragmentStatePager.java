package ui;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainFragmentStatePager extends FragmentPagerAdapter{

	private ArrayList<CustomFragment> fragments;
	private Activity activity;

	@Override
	public CharSequence getPageTitle(int position) {
		return fragments.get(position).getTitle();
	}

	public MainFragmentStatePager(FragmentManager fm,
			ArrayList<CustomFragment> fragments,Activity activity) {
		super(fm);
		this.fragments = fragments;
		this.activity=activity;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	public CustomFragment getCustomItem(int arg0) {
		return fragments.get(arg0);
	}
	public int getItemPosition(Object object){
	     return POSITION_NONE;
	}
	
	public int getCustomItemPosition(CustomFragment fragment){
		return fragments.indexOf(fragment);
	}
	
	@Override
	public int getCount() {
		return fragments.size();
	}

}
