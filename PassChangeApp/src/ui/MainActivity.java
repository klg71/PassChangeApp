package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import plugins.Amazon;
import plugins.Facebook;
import plugins.Google;
import plugins.LeagueOfLegends;
import plugins.Twitter;

import com.passchange.passchangeapp.R;

import core.Website;
import account.Account;
import account.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemLongClickListener,
		android.widget.PopupMenu.OnMenuItemClickListener,
		android.content.DialogInterface.OnClickListener {

	public final static boolean DEBUG_ACTIVATED = true;

	private AccountManager accountManager;
	private HashMap<String, Website> websites;
	private AccountListAdapter accountListAdapter;
	private PopupMenu popupMenu;
	private Account selectedAccount;
	private String password;
	private boolean childWindowActive;
	private WebView webView;

	private boolean loaded;

	@Override
	public void onBackPressed() {
		if (childWindowActive) {
			setContentView(R.layout.activity_main);
			childWindowActive=false;
			refreshAccountList();
		} else {
			super.onBackPressed();
			finish();
		}
	}

	@Override
	protected void onPause() {
		if (loaded) {
			try {
				accountManager.writeToFile();
				loaded = false;
			} catch (Exception e) {

				if (DEBUG_ACTIVATED)
					Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
		}
		super.onStop();
	}

	@Override
	protected void onRestart() {
		if (loaded) {
			for (Account account : accountManager.getAccounts()) {
				selectedAccount = account;
				if (account.isExpired()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Password expired")
							.setMessage(
									"Your Password for: "
											+ account.getUserName()
											+ "@"
											+ account.getWebsite().getName()
											+ " is expired do you want to change it now?")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setPositiveButton("Yes", this)
							.setNegativeButton("No", null) // Do nothing on no
							.show();
				}
			}
		} else {
			login();
		}
		super.onRestart();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		childWindowActive = false;
		login();
	}

	private void login() {
		loaded = false;
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog_login, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Login");
		alert.setCancelable(false);
		alert.setMessage("Please enter your Masterpassword:");
		alert.setView(textEntryView);
		final Activity activity = this;
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// String value = input.getText().toString();
				EditText mUserText = (EditText) textEntryView
						.findViewById(R.id.txt_password);
				password = mUserText.getText().toString();
				if (password.length() > 0) {

					loaded = true;
					websites = new HashMap<String, Website>();
					websites.put("Facebook", new Facebook(activity));
					websites.put("Twitter", new Twitter(activity));
					websites.put("Google", new Google(activity));
					websites.put("League of Legends", new LeagueOfLegends(
							activity));
					websites.put("Amazon", new Amazon(activity));
					accountManager = new AccountManager("/sdcard/accounts.xml",
							password, websites);
					if (DEBUG_ACTIVATED)
						Log.e("file", "/sdcard/accounts.xml");
					File file = new File("/sdcard/accounts.xml");
					if (file.exists()) {
						try {
							accountManager.loadFromFile();
						} catch (Exception e) {
							if (DEBUG_ACTIVATED)
								Log.e("Error", e.getMessage());
							if (DEBUG_ACTIVATED)
								e.printStackTrace();
							AlertDialog.Builder ad = new AlertDialog.Builder(
									activity);
							ad.setCancelable(false); // This blocks the 'BACK'
														// button
							ad.setMessage("An error occured, maybe you entered the wrong password try it again!");
							ad.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											login();
										}
									});
							ad.create().show();
							return;
						}
					}
					setContentView(R.layout.activity_main);
					accountListAdapter = new AccountListAdapter(accountManager);
					refreshAccountList();
					return;
				} else {
					AlertDialog.Builder ad = new AlertDialog.Builder(activity);
					ad.setCancelable(false); // This blocks the 'BACK'
												// button
					ad.setMessage("An error occured, maybe you entered the wrong password try it again!");
					ad.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									login();
								}
							});
					ad.create().show();
				}

			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
						return;
					}
				});
		alert.create().show();
		
	}

	@Override
	protected void onStop() {
		if (loaded) {
			try {
				accountManager.writeToFile();
			} catch (Exception e) {

				if (DEBUG_ACTIVATED)
					Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
		}
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.add_account) {

			childWindowActive = true;
			setContentView(R.layout.addaccount);
			new AddAccountWindow(accountManager, this);
			return true;
		}
		if (id == R.id.settings) {
			childWindowActive = true;
			setContentView(R.layout.settings);
			new SettingsWindow(this);
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean isChildWindowActive() {
		return childWindowActive;
	}

	public void setChildWindowActive(boolean childWindowActive) {
		this.childWindowActive = childWindowActive;
	}

	public void refreshAccountList() {
		ListView listViewAccounts = (ListView) findViewById(R.id.listViewAccounts);
		listViewAccounts.setAdapter(accountListAdapter);
		listViewAccounts.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_change_password: {

			childWindowActive = true;
			setContentView(R.layout.changepassword);
			new ChangePasswordWindow(selectedAccount, this);
			break;
		}
		
		case R.id.action_show_browser:{
			childWindowActive=true;

			setContentView(R.layout.webview);
			selectedAccount.openBrowser((WebView) findViewById(R.id.webView1),this);
			break;
		}
		
		case R.id.action_delete_account: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Delete Account")
					.setMessage("Are you sure?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									accountManager
											.removeAccount(selectedAccount);
									refreshAccountList();
								}
							}).setNegativeButton("No", null).show();
			break;
		}
		case R.id.action_change_account: {
			childWindowActive = true;
			setContentView(R.layout.changeaccount);
			new ChangeAccountWindow(selectedAccount, this);
			break;
		}
		case R.id.action_copy_password: {
			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("password",
					selectedAccount.getActualPassword());
			clipboard.setPrimaryClip(clip);
			break;
		}
		case R.id.action_test_login: {
			selectedAccount.testLogin(this);
		}
		}
		return false;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		popupMenu = new PopupMenu(view.getContext(), view);
		popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(this);
		popupMenu.show();
		selectedAccount = accountManager.getAccount(position);
		return false;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		childWindowActive = true;
		setContentView(R.layout.changepassword);
		new ChangePasswordWindow(selectedAccount, this);

	}
}
