package ui;

import java.util.ArrayList;

import com.passchange.passchangeapp.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class MainFragmentActivity extends FragmentActivity {
	MainFramentStatePager pagerAdapter;
    ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        ArrayList<Fragment> fragments=new ArrayList<Fragment>();
        fragments.add(new AccountOverviewFragment());
        fragments.add(new WebViewFragment());
        fragments.add(new SettingsFragment());
        pagerAdapter=new MainFramentStatePager(getSupportFragmentManager(), fragments);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
    }
}
