package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class WebViewFragment extends CustomFragment {
	private WebView webView;
	private LinearLayout mainView;
	private Account account;
	private ProgressBar spinner;
	private boolean firstLoad;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// webView=new WebView(getActivity());
		mainView = new LinearLayout(getActivity());
		mainView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mainView.setOrientation(LinearLayout.VERTICAL);
		// mainView = inflater.inflate(R.layout.webview, container, false);
		// spinner = (ProgressBar) mainView.findViewById(R.id.progressBar1);
		spinner = new ProgressBar(getActivity());
		spinner.setVisibility(View.VISIBLE);
		// LayoutParams params=new LayoutParams(Gravity.CENTER);
		// spinner.setLayoutParams(params);

		// webView = (WebView) mainView.findViewById(R.id.webView1);
		webView = new WebView(getActivity());
		webView.setVisibility(View.GONE);
		mainView.addView(spinner);
		mainView.addView(webView);
		account.openBrowser(webView, getActivity(), this);
		// refresh();
		return mainView;
	}

	public WebViewFragment(Account account) {
		super();
		firstLoad = true;
		this.account = account;
	}

	@Override
	public void onBackPressed() {
		webView.goBack();
	}

	@Override
	public CharSequence getTitle() {
		return account.getUserName() + " @ " + account.getWebsite().getName();
	}

	@Override
	public void refresh() {
		if (webView != null) {
			webView.setWebViewClient(new WebViewClient());
			webView.getSettings()
					.setJavaScriptEnabled(true);
			webView.getSettings().setBuiltInZoomControls(
					true);
			webView.getSettings().setDisplayZoomControls(
					false);
			webView.invalidate();
			webView.refreshDrawableState();
			webView.loadUrl(account.getWebsite().getWebsiteUrl());
		}

	}

	public void removeProgressBar() {
		spinner.setVisibility(View.GONE);

	}

}
