package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class WebViewFragment extends CustomFragment {
	private WebView webView;
	private View mainView;
	private Account account;
	private ProgressBar spinner;
	private boolean firstLoad;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// webView=new WebView(getActivity());

		mainView = inflater.inflate(R.layout.webview, container, false);
		spinner = (ProgressBar) mainView.findViewById(R.id.progressBar1);
		spinner.setVisibility(View.VISIBLE);
		webView = (WebView) mainView.findViewById(R.id.webView1);
		webView.setVisibility(View.GONE);
		if (!firstLoad) {
			Log.e("DEBUG", "onCreateView");
			account.openBrowser(webView, getActivity(), this);
		}
		firstLoad = false;
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
			webView.invalidate();
			webView.refreshDrawableState();
			webView.reload();
		}

	}

	public void removeProgressBar() {
		spinner.setVisibility(View.GONE);

	}

}
