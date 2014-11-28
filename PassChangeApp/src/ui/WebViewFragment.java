package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class WebViewFragment extends CustomFragment {
	private WebView webView;
	private View mainView;
	private Account account;
	private boolean firstLoad;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.webview, container,
				false);
		account.openBrowser((WebView) mainView.findViewById(R.id.webView1),
				getActivity());
		webView=(WebView) mainView.findViewById(R.id.webView1);
		webView.invalidate();
		return mainView;	
	}

	public WebViewFragment(Account account) {
		super();
		firstLoad=true;
		this.account = account;
	}

	@Override
	public void onBackPressed() {
		webView.goBack();
	}

	@Override
	public CharSequence getTitle() {
		return account.getUserName()+" @ "+account.getWebsite().getName();
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

}
