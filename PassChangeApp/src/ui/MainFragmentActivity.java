package ui;

import java.util.ArrayList;

import com.passchange.passchangeapp.R;

import core.Configuration;
import account.Account;
import account.AccountExpiredListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class MainFragmentActivity extends FragmentActivity implements AccountExpiredListener {
	MainFragmentStatePager pagerAdapter;
    ViewPager mViewPager;
    
	private LoginManager loginManager;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        
        setContentView(R.layout.activity_collection);

		loginManager = new LoginManager(this);
		loginManager.OnAppStarted();
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        ArrayList<CustomFragment> fragments=new ArrayList<CustomFragment>();
        fragments.add(new AccountOverviewFragment());
        fragments.add(new WebViewFragment());
        fragments.add(new SettingsFragment(this,loginManager.getAccountManager().getConfiguration(),loginManager.getAccountManager()));
        pagerAdapter=new MainFragmentStatePager(getSupportFragmentManager(), fragments);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);
    }

	@Override
	public void onBackPressed() {
		pagerAdapter.getCustomItem(mViewPager.getCurrentItem()).onBackPressed();
		if(mViewPager.getCurrentItem()==0){
			super.onBackPressed();
		}
	}
	

	@Override
	protected void onPause() {
		loginManager.OnAppPaused();
		super.onStop();
	}

	@Override
	protected void onRestart() {
		loginManager.OnAppStarted();
		super.onRestart();

	}

	@Override
	protected void onStop() {
		loginManager.OnAppStopped();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void changePassword(Account selectedAccount) {
		setContentView(R.layout.changepassword);
		new ChangePasswordWindow(selectedAccount, this);
		
	}

	public void dataSetChanged() {
		pagerAdapter.notifyDataSetChanged();		
	}

	public void accountInteraction(Account selectedAccount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accountsExpired(ArrayList<Account> accounts) {
		// TODO Auto-generated method stub
		
	}
}
