package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import plugins.Amazon;
import plugins.Ebay;
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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemLongClickListener,
		android.widget.PopupMenu.OnMenuItemClickListener,
		android.content.DialogInterface.OnClickListener, OnItemClickListener {

	public final static boolean DEBUG_ACTIVATED = true;

	private AccountManager accountManager;
	private HashMap<String, Website> websites;
	private AccountListAdapter accountListAdapter;
	private PopupMenu popupMenu;
	private Account selectedAccount;
	private String password;
	private boolean childWindowActive;
	private boolean webViewActive;
	private boolean showInfoToast;
	private boolean resetPassword;
	private boolean longClicked;
	private boolean active;

	public boolean isShowInfoToast() {
		return showInfoToast;
	}

	public void setShowInfoToast(boolean showInfoToast) {
		this.showInfoToast = showInfoToast;
	}

	private boolean loaded;

	@Override
	public void onBackPressed() {
		if (childWindowActive) {
			if (webViewActive) {
				WebView webView = (WebView) findViewById(R.id.webView1);
				if (webView.canGoBack()) {
					webView.goBack();
				} else
					handleChildBackButton();
			} else {
				handleChildBackButton();
			}
		} else {
			super.onBackPressed();
			try {
				accountManager.writeToFile();
			} catch (Exception e) {
				if (DEBUG_ACTIVATED)
					e.printStackTrace();
			}
			active = false;
			finish();
		}
	}

	@Override
	protected void onPause() {
		if (loaded) {
			try {
				active = false;
				accountManager.writeToFile();
				if (accountManager.getConfiguration().isLogoutWhenAppIsPaused())
					loaded = false;
			} catch (Exception e) {

				if (DEBUG_ACTIVATED)
					// Log.e("Error", e.getMessage());
					e.printStackTrace();
			}
		}
		super.onStop();
	}

	@Override
	protected void onRestart() {
		if (loaded) {
			active = true;
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
		startExpirationTimer();
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
		resetPassword = false;

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
					websites.put("Ebay", new Ebay(activity));
					accountManager = new AccountManager("/sdcard/accounts.xml",
							password, websites);
					if (DEBUG_ACTIVATED)
						Log.e("file", "/sdcard/accounts.xml");
					File file = new File("/sdcard/accounts.xml");
					if (file.exists() && !resetPassword) {
						try {
							accountManager.loadFromFile();
							if (!accountManager.getConfiguration()
									.isLogoutWhenAppIsPaused()) {
								Timer timer = new Timer();
								timer.schedule(new TimerTask() {

									@Override
									public void run() {
										loaded = false;

									}
								}, accountManager.getConfiguration()
										.getLogoutTimeMinutes() * 60000);
							}
						} catch (Exception e) {
							// if (DEBUG_ACTIVATED)
							// Log.e("Error", e.getMessage());
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
					active = true;
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
		Button button = (Button) textEntryView
				.findViewById(R.id.button_reset_password);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder ad = new AlertDialog.Builder(activity);
				ad.setMessage("Do you really want to reset your password an loose all of your account data?");
				ad.setPositiveButton("yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								resetPassword = true;
							}
						});
				ad.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				ad.create().show();

			}
		});
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
			new SettingsWindow(this, accountManager.getConfiguration());
		}
		if (id == R.id.main_page) {

			handleChildBackButton();
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
		if (!accountListAdapter.isEmpty()) {
			setContentView(R.layout.activity_main);
			ListView listViewAccounts = (ListView) findViewById(R.id.listViewAccounts);
			listViewAccounts.setAdapter(accountListAdapter);
			listViewAccounts.setOnItemLongClickListener(this);
			listViewAccounts.setOnItemClickListener(this);

			showInfoToast = true;
		} else {
			setContentView(R.layout.empty_activity_main);
		}

		checkExpired();
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

		case R.id.action_show_browser: {
			childWindowActive = true;
			webViewActive = true;
			setContentView(R.layout.webview);
			selectedAccount.openBrowser((WebView) findViewById(R.id.webView1),
					this);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
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
		longClicked = true;
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (showInfoToast && !longClicked) {

			Toast.makeText(this,
					getResources().getString(R.string.hold_item_string),
					Toast.LENGTH_SHORT).show();
			showInfoToast = false;
			Timer timer = new Timer();
			final MainActivity activity = this;
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.setShowInfoToast(true);
				}
			}, 5000);
		}
		longClicked = false;

	}

	private void handleChildBackButton() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		childWindowActive = false;
		webViewActive = false;
		refreshAccountList();
	}

	private void checkExpired() {
		int i=0;
		for (final Account account : accountManager.getAccounts()) {
			selectedAccount = account;
			if (account.isExpired()) {
				i++;
				if (active) {
					final MainActivity activity=this;
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(activity);
							builder.setTitle("Password expired")
									.setMessage(
											"Your Password for: "
													+ account.getUserName()
													+ " on "
													+ account.getWebsite().getName()
													+ " is expired do you want to change it now?")
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setPositiveButton("Yes", activity)
									.setNegativeButton("No", null) // Do nothing on no
									.show();
							
						}
					});

					return;
				} else {

				}

			}
		}
		if(!active){
			int mId = 0;
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.ic_passchange)
			        .setContentTitle("Account Expired")
			        .setContentText("There are "+Integer.toString(i)+" passwords expired in PassChange please change them in time.");

			Intent resultIntent = new Intent(this, MainActivity.class);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(mId, mBuilder.build());
		}
	}

	public void startLogoutTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				loaded = false;
				if (DEBUG_ACTIVATED)
					System.out.println("Login falsed");
			}
		}, 60000 * accountManager.getConfiguration().getLogoutTimeMinutes());
	}

	public void startExpirationTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if(MainActivity.DEBUG_ACTIVATED){
					Log.e("debug","run task");
				}
				if(accountManager!=null){
					checkExpired();
					if(MainActivity.DEBUG_ACTIVATED){
						Log.e("debug","run task: check expire");
					}
				}

			}
		}, 60000 * 10,60000*10);
	}
}
