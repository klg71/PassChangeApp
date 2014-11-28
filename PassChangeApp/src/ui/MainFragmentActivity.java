package ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.passchange.passchangeapp.R;

import core.Configuration;
import account.Account;
import account.AccountExpiredListener;
import account.AccountExportListener;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainFragmentActivity extends FragmentActivity implements
		AccountExpiredListener, OnClickListener, AccountExportListener,
		android.content.DialogInterface.OnClickListener, OnPageChangeListener {
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.settings) {
			setContentView(R.layout.settings);
			childWindowActive = true;
			new SettingsWindow(this, loginManager.getAccountManager()
					.getConfiguration(), loginManager.getAccountManager());
		} else if (item.getItemId() == R.id.close) {
			Account account = null;
			for (Map.Entry<Account, CustomFragment> entry : loadedWebsites
					.entrySet()) {
				if (entry.getValue().equals(
						pagerAdapter.getItem(mViewPager.getCurrentItem()))) {
					account = entry.getKey();
				}
			}
			loadedWebsites.remove(account);
			fragments.remove(mViewPager.getCurrentItem());
			pagerAdapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}

	private HashMap<Account, CustomFragment> loadedWebsites;
	private ArrayList<CustomFragment> fragments;
	private MainFragmentStatePager pagerAdapter;
	private ViewPager mViewPager;
	public final static boolean DEBUG_ACTIVATED = false;
	private Account selectedAccount;
	private LinearLayout changePasswordLayout, editAccountLayout,
			testLoginLayout, copyPasswordLayout, exportAccountLayout,
			addAccountLayout, deleteAccountLayout, webViewLayout;
	private LoginManager loginManager;
	private AlertDialog actionAlert;
	private boolean active;
	private boolean childWindowActive;
	private Menu optionsMenu;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadedWebsites = new HashMap<Account, CustomFragment>();
		loginManager = new LoginManager(this);
		setContentView(R.layout.activity_collection);
		childWindowActive = false;
		fragments = new ArrayList<CustomFragment>();
		// fragments.add(new SettingsFragment(this, loginManager
		// .getAccountManager().getConfiguration(), loginManager
		// .getAccountManager()));
		fragments.add(new AccountOverviewFragment(loginManager));
	}

	@Override
	protected void onStart() {
		active = true;
		loginManager.OnAppStarted();

		super.onStart();
	}

	public void onLoggedIn() {

		pagerAdapter = new MainFragmentStatePager(getSupportFragmentManager(),
				fragments, this);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setOnPageChangeListener(this);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

	}

	@Override
	public void onBackPressed() {
		if (childWindowActive) {
			childWindowActive = false;
			setContentView(R.layout.activity_collection);
			onLoggedIn();
			dataSetChanged();
		} else {
			pagerAdapter.getCustomItem(mViewPager.getCurrentItem())
					.onBackPressed();
			if (mViewPager.getCurrentItem() == 0) {
				super.onBackPressed();
			}
		}
	}

	@Override
	protected void onPause() {
		active = false;
		loginManager.OnAppPaused();
		super.onStop();
	}

	@Override
	protected void onRestart() {
		// loginManager.OnAppStarted();
		super.onRestart();

	}

	@Override
	protected void onStop() {
		active = false;
		loginManager.OnAppStopped();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		optionsMenu = menu;
		return true;
	}

	public void changePassword(Account selectedAccount) {
		setContentView(R.layout.changepassword);

	}

	public void dataSetChanged() {
		for (CustomFragment fragment : fragments) {
			getSupportFragmentManager().beginTransaction().detach(fragment)
					.attach(fragment).commit();
		}
		pagerAdapter.notifyDataSetChanged();
	}

	public void accountInteraction(Account selectedAccount) {

		this.selectedAccount = selectedAccount;
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.action_view, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Actions");
		alert.setView(textEntryView);
		ImageView imageView = (ImageView) textEntryView
				.findViewById(R.id.imageViewIcon);
		imageView.setImageResource(selectedAccount.getWebsite()
				.getImageSource());

		SimpleDateFormat format = (SimpleDateFormat) new SimpleDateFormat()
				.getDateInstance(SimpleDateFormat.MEDIUM);
		Calendar tempCal = (Calendar) selectedAccount.getLastChangedCalendar()
				.clone();
		tempCal.add(Calendar.DAY_OF_YEAR, selectedAccount.getExpire());
		TextView view = (TextView) textEntryView
				.findViewById(R.id.textViewInformation);
		view.setText(selectedAccount.getUserName() + " - "
				+ selectedAccount.getWebsite()
				+ System.getProperty("line.separator") + "Expires: "
				+ format.format(tempCal.getTime()));
		actionAlert = alert.create();
		actionAlert.show();

		changePasswordLayout = (LinearLayout) textEntryView
				.findViewById(R.id.change_password_action);
		changePasswordLayout.setOnClickListener(this);
		editAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.edit_account_action);
		editAccountLayout.setOnClickListener(this);
		testLoginLayout = (LinearLayout) textEntryView
				.findViewById(R.id.test_login_action);
		testLoginLayout.setOnClickListener(this);
		copyPasswordLayout = (LinearLayout) textEntryView
				.findViewById(R.id.copy_password_action);
		copyPasswordLayout.setOnClickListener(this);
		exportAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.export_account_icon);
		exportAccountLayout.setOnClickListener(this);
		addAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.add_account_action);
		addAccountLayout.setOnClickListener(this);
		deleteAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.delete_account_icon);
		deleteAccountLayout.setOnClickListener(this);
		webViewLayout = (LinearLayout) textEntryView
				.findViewById(R.id.open_browser_action);
		webViewLayout.setOnClickListener(this);
	}

	@Override
	public void accountsExpired(ArrayList<Account> accounts) {
		for (final Account account : accounts) {
			selectedAccount = account;
			if (active) {
				final MainFragmentActivity activity = this;
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

			Intent resultIntent = new Intent(this,
					AccountOverviewFragment.class);

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
	public void onClick(View v) {
		if (v.equals(changePasswordLayout)) {
			actionAlert.dismiss();
			View alertView = createAlert(R.layout.changepassword);
			new ChangePasswordWindow(selectedAccount, this, alertView);
		} else if (v.equals(editAccountLayout)) {
			actionAlert.dismiss();
			View alertView = createAlert(R.layout.changepassword);
			new ChangeAccountWindow(selectedAccount, this, alertView);
		} else if (v.equals(testLoginLayout)) {
			actionAlert.dismiss();
			selectedAccount.testLogin(this);
		} else if (v.equals(copyPasswordLayout)) {
			actionAlert.dismiss();
			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("password",
					selectedAccount.getActualPassword());
			clipboard.setPrimaryClip(clip);
		} else if (v.equals(exportAccountLayout)) {
			actionAlert.dismiss();
			loginManager.getAccountManager().exportAccount(this,
					selectedAccount);
		} else if (v.equals(addAccountLayout)) {
			actionAlert.dismiss();
			View alertView = createAlert(R.layout.addaccount);
			new AddAccountWindow(loginManager.getAccountManager(), this,
					alertView);
		} else if (v.equals(deleteAccountLayout)) {
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
								}
							})
					.setNegativeButton(getResources().getString(R.string.no),
							null).show();
		} else if (v.equals(webViewLayout)) {
			actionAlert.dismiss();
			if (loadedWebsites.containsKey(selectedAccount)) {
				mViewPager.setCurrentItem(pagerAdapter
						.getCustomItemPosition(loadedWebsites
								.get(selectedAccount)));
			} else {
				CustomFragment fragment = new WebViewFragment(selectedAccount);
				fragments.add(fragment);
				pagerAdapter.notifyDataSetChanged();
				mViewPager.setCurrentItem(pagerAdapter.getCount());
				loadedWebsites.put(selectedAccount, fragment);
			}
		}
		pagerAdapter.notifyDataSetChanged();
	}

	private View createAlert(int layoutId) {
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(layoutId, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Actions");
		alert.setView(textEntryView);
		alert.create().show();
		return textEntryView;
	}

	@Override
	public void exportSuccessful(final String hash) {
		final MainFragmentActivity mainActivity = this;
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LinearLayout layout = new LinearLayout(mainActivity);
				layout.setOrientation(LinearLayout.VERTICAL);
				EditText editHash = new EditText(mainActivity);
				if (MainFragmentActivity.DEBUG_ACTIVATED)
					Log.e("Hash", "hash:" + hash);
				editHash.setText(hash);
				TextView textViewInfo = new TextView(mainActivity);
				textViewInfo.setText(getResources().getString(
						R.string.account_export_info));
				layout.addView(textViewInfo);
				layout.addView(editHash);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						mainActivity).setMessage("Account exported")
						.setPositiveButton("OK", null).setView(layout);
				builder.create().show();

			}
		});

	}

	public void justAddAction() {
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.action_view, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Actions");
		alert.setView(textEntryView);
		// ImageView imageView = (ImageView) textEntryView
		// .findViewById(R.id.imageViewIcon);
		// imageView.setImageResource(selectedAccount.getWebsite()
		// .getImageSource());
		//
		// SimpleDateFormat format = (SimpleDateFormat) new SimpleDateFormat()
		// .getDateInstance(SimpleDateFormat.MEDIUM);
		// Calendar tempCal = (Calendar)
		// selectedAccount.getLastChangedCalendar()
		// .clone();
		// tempCal.add(Calendar.DAY_OF_YEAR, selectedAccount.getExpire());
		// TextView view = (TextView) textEntryView
		// .findViewById(R.id.textViewInformation);
		// view.setText(selectedAccount.getUserName() + " - "
		// + selectedAccount.getWebsite()
		// + System.getProperty("line.separator") + "Expires: "
		// + format.format(tempCal.getTime()));
		actionAlert = alert.create();
		((LinearLayout) textEntryView.findViewById(R.id.layoutInformation))
				.setVisibility(View.GONE);
		changePasswordLayout = (LinearLayout) textEntryView
				.findViewById(R.id.change_password_action);
		changePasswordLayout.setVisibility(View.GONE);
		editAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.edit_account_action);
		editAccountLayout.setVisibility(View.GONE);
		testLoginLayout = (LinearLayout) textEntryView
				.findViewById(R.id.test_login_action);
		testLoginLayout.setVisibility(View.GONE);
		copyPasswordLayout = (LinearLayout) textEntryView
				.findViewById(R.id.copy_password_action);
		copyPasswordLayout.setVisibility(View.GONE);
		exportAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.export_account_icon);
		exportAccountLayout.setVisibility(View.GONE);
		addAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.add_account_action);
		addAccountLayout.setOnClickListener(this);
		deleteAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.delete_account_icon);
		deleteAccountLayout.setVisibility(View.GONE);
		webViewLayout = (LinearLayout) textEntryView
				.findViewById(R.id.open_browser_action);
		webViewLayout.setVisibility(View.GONE);
		actionAlert.show();
	}

	@Override
	public void exportFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		View alertView = createAlert(R.layout.changepassword);
		new ChangePasswordWindow(selectedAccount, this, alertView);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		if (arg0 != 0) {
			optionsMenu.findItem(R.id.close).setVisible(true);
		} else {
			optionsMenu.findItem(R.id.close).setVisible(false);
		}

	}

}
