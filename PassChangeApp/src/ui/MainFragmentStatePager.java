package ui;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainFragmentStatePager extends FragmentStatePagerAdapter {

	private ArrayList<CustomFragment> fragments;

	@Override
	public CharSequence getPageTitle(int position) {
		return fragments.get(position).getTitle();
	}

	public MainFragmentStatePager(FragmentManager fm,
			ArrayList<CustomFragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	public CustomFragment getCustomItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
