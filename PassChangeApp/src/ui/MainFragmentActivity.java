package ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.passchange.passchangeapp.R;

import core.Configuration;
import account.Account;
import account.AccountExpiredListener;
import account.AccountExportListener;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainFragmentActivity extends FragmentActivity implements
		AccountExpiredListener, OnClickListener, AccountExportListener, android.content.DialogInterface.OnClickListener {
	private MainFragmentStatePager pagerAdapter;
	private ViewPager mViewPager;
	public final static boolean DEBUG_ACTIVATED = false;
	private Account selectedAccount;
	private LinearLayout changePasswordLayout, editAccountLayout,
			testLoginLayout, copyPasswordLayout, exportAccountLayout,
			addAccountLayout, deleteAccountLayout;
	private LoginManager loginManager;
	private AlertDialog actionAlert;
	private boolean active;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_collection);

	}

	@Override
	protected void onStart() {
		active=true;
		loginManager = new LoginManager(this);
		loginManager.OnAppStarted();

		super.onStart();
	}

	public void onLoggedIn() {
		ArrayList<CustomFragment> fragments = new ArrayList<CustomFragment>();
		fragments.add(new AccountOverviewFragment(loginManager));
		fragments.add(new WebViewFragment());
		fragments.add(new SettingsFragment(this, loginManager
				.getAccountManager().getConfiguration(), loginManager
				.getAccountManager()));
		pagerAdapter = new MainFragmentStatePager(getSupportFragmentManager(),
				fragments);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(pagerAdapter);
	}

	@Override
	public void onBackPressed() {
		pagerAdapter.getCustomItem(mViewPager.getCurrentItem()).onBackPressed();
		if (mViewPager.getCurrentItem() == 0) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onPause() {
		active=false;
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
		active=false;
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

	}

	public void dataSetChanged() {
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
	}

	@Override
	public void accountsExpired(ArrayList<Account> accounts) {
		for (final Account account : accounts) {
			selectedAccount = account;
			if (active) {
				final MainFragmentActivity activity=this;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								activity);
						builder.setTitle(
								getResources().getString(
										R.string.account_expired))
								.setMessage(										getResources().getString(
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

			TaskStackBuilder stackBuilder = TaskStackBuilder
					.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
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
	
	public void justAddAction(){
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

		changePasswordLayout = (LinearLayout) textEntryView
				.findViewById(R.id.change_password_action);
		changePasswordLayout.setEnabled(false);
		editAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.edit_account_action);
		editAccountLayout.setEnabled(false);
		testLoginLayout = (LinearLayout) textEntryView
				.findViewById(R.id.test_login_action);
		testLoginLayout.setEnabled(false);
		copyPasswordLayout = (LinearLayout) textEntryView
				.findViewById(R.id.copy_password_action);
		copyPasswordLayout.setEnabled(false);
		exportAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.export_account_icon);
		exportAccountLayout.setEnabled(false);
		addAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.add_account_action);
		addAccountLayout.setOnClickListener(this);
		deleteAccountLayout = (LinearLayout) textEntryView
				.findViewById(R.id.delete_account_icon);
		deleteAccountLayout.setEnabled(false);

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
}
