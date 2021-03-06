package ui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.passchange.passchangeapp.R;

import account.Account;
import account.AccountExpiredListener;
import account.AccountExportListener;
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
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemLongClickListener,
		android.widget.PopupMenu.OnMenuItemClickListener,
		android.content.DialogInterface.OnClickListener, OnItemClickListener,
		AccountExpiredListener, AccountExportListener {

	public final static boolean DEBUG_ACTIVATED = false;

	private LoginManager loginManager;
	private AccountListAdapter accountListAdapter;
	private PopupMenu popupMenu;
	private Account selectedAccount;
	private Timer expirationTimer;
	private boolean childWindowActive;
	private boolean webViewActive;
	private boolean showInfoToast;
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
				loginManager.OnAppStopped();
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
		loginManager.OnAppPaused();
		super.onStop();
	}

	@Override
	protected void onRestart() {
		loginManager.OnAppStarted();
		super.onRestart();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		childWindowActive = false;
		loginManager = new LoginManager(this);
		loginManager.OnAppStarted();
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.add_account) {

			childWindowActive = true;
			setContentView(R.layout.addaccount);
			new AddAccountWindow(loginManager.getAccountManager(), this);
			return true;
		}
		if (id == R.id.settings) {
			childWindowActive = true;
			setContentView(R.layout.settings);
			new SettingsWindow(this, loginManager.getAccountManager()
					.getConfiguration(), loginManager.getAccountManager());
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
		accountListAdapter = loginManager.getAccountListAdapter();
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
			builder.setTitle(getResources().getString(R.string.delete_account))
					.setMessage(
							this.getResources()
									.getString(R.string.are_you_sure))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(getResources().getString(R.string.yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									loginManager.getAccountManager()
											.removeAccount(selectedAccount);
									refreshAccountList();
								}
							})
					.setNegativeButton(getResources().getString(R.string.no),
							null).show();
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

		case R.id.action_export_password: {
			loginManager.getAccountManager().exportAccount(this,
					selectedAccount);
			break;
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
		selectedAccount = loginManager.getAccountManager().getAccount(position);
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
		int i = 0;
		for (final Account account : loginManager.getAccountManager()
				.getAccounts()) {
			selectedAccount = account;
			if (account.isExpired()) {
				i++;
				if (active) {
					final MainActivity activity = this;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									activity);
							builder.setTitle(
									getResources().getString(
											R.string.account_expired))
									.setMessage(
											getResources().getString(
													R.string.password_for)
													+ " "
													+ account.getUserName()
													+ " "
													+ getResources().getString(
															R.string.on)
													+ " "
													+ account.getWebsite()
															.getName()
													+ " "
													+ getResources()
															.getString(
																	R.string.pass_expire_sentence2))
									.setIcon(android.R.drawable.ic_dialog_alert)
									.setPositiveButton(
											getResources().getString(
													R.string.yes), activity)
									.setNegativeButton(
											getResources().getString(
													R.string.no), null) // Do
																		// nothing
																		// on no
									.show();

						}
					});

					return;
				} else {

				}

			}
		}
		if (!active && i > 0) {
			int mId = 0;
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this)
					.setSmallIcon(R.drawable.ic_passchange)
					.setContentTitle(
							getResources().getString(R.string.account_expired))
					.setContentText(
							getResources().getString(R.string.there_are)
									+ " "
									+ Integer.toString(i)
									+ " "
									+ getResources().getString(
											R.string.pass_expire_sentence));

			Intent resultIntent = new Intent(this, MainActivity.class);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(mId, mBuilder.build());
		}
	}

	// public void startExpirationTimer() {
	// if(expirationTimer!=null)
	// expirationTimer.cancel();
	// expirationTimer = new Timer();
	// expirationTimer.schedule(new TimerTask() {
	//
	// @Override
	// public void run() {
	// if(MainActivity.DEBUG_ACTIVATED){
	// Log.e("debug","run task");
	// }
	// if(loginManager.getAccountManager()!=null){
	// checkExpired();
	// if(MainActivity.DEBUG_ACTIVATED){
	// Log.e("debug","run task: check expire");
	// }
	// }
	//
	// }
	// }, 60000 *
	// loginManager.getAccountManager().getConfiguration().getRememberTimeMinmutes(),60000*loginManager.getAccountManager().getConfiguration().getRememberTimeMinmutes());
	// }

	@Override
	public void accountsExpired(ArrayList<Account> accounts) {
		for (final Account account : accounts) {
			selectedAccount = account;
			if (active) {
				final MainActivity activity = this;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								activity);
						builder.setTitle(
								getResources().getString(
										R.string.account_expired))
								.setMessage(
										getResources().getString(
												R.string.password_for)
												+ " "
												+ account.getUserName()
												+ " "
												+ getResources().getString(
														R.string.on)
												+ " "
												+ account.getWebsite()
														.getName()
												+ " "
												+ getResources()
														.getString(
																R.string.pass_expire_sentence2))
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setPositiveButton(
										getResources().getString(R.string.yes),
										activity)
								.setNegativeButton(
										getResources().getString(R.string.no),
										null) // Do nothing on no
								.show();

					}
				});

				return;
			} else {

			}

		}

		if (!active) {
			int mId = 0;
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this)
					.setSmallIcon(R.drawable.ic_passchange)
					.setContentTitle(
							getResources().getString(R.string.account_expired))
					.setContentText(
							getResources().getString(R.string.there_are)
									+ " "
									+ Integer.toString(accounts.size())
									+ " "
									+ getResources().getString(
											R.string.pass_expire_sentence));

			Intent resultIntent = new Intent(this, MainActivity.class);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(mId, mBuilder.build());
		}

	}

	@Override
	public void exportSuccessful(final String hash) {
		final MainActivity mainActivity=this;
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				LinearLayout layout = new LinearLayout(mainActivity);
				layout.setOrientation(LinearLayout.VERTICAL);
				EditText editHash = new EditText(mainActivity);
				if(DEBUG_ACTIVATED)
					Log.e("Hash","hash:"+hash);
				editHash.setText(hash);
				TextView textViewInfo = new TextView(mainActivity);
				textViewInfo.setText(getResources().getString(
						R.string.account_export_info));
				layout.addView(textViewInfo);
				layout.addView(editHash);

				AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity)
						.setMessage("Account exported").setPositiveButton("OK", null)
						.setView(layout);
				builder.create().show();
				
			}
		});

	}

	@Override
	public void exportFailed() {
		// TODO Auto-generated method stub

	}
}
