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
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class AccountOverviewFragment extends CustomFragment implements
		OnItemLongClickListener, OnItemClickListener, AccountExpiredListener,
		AccountExportListener, OnLongClickListener, OnClickListener {
	public final static boolean DEBUG_ACTIVATED = false;

	private View mainView;
	private LoginManager loginManager;
	private AccountListAdapter accountListAdapter;
	private Account selectedAccount;
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

	}

	public void refreshAccountList() {
		accountListAdapter = loginManager.getAccountListAdapter();
		if (!accountListAdapter.isEmpty()) {
			ListView listViewAccounts = (ListView) mainView
					.findViewById(R.id.listViewAccounts);
			listViewAccounts.setAdapter(accountListAdapter);
			listViewAccounts.setOnItemLongClickListener(this);
			listViewAccounts.setOnItemClickListener(this);

			showInfoToast = true;
		}

		checkExpired();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.empty_activity_main, container,
				false);
		accountListAdapter = loginManager.getAccountListAdapter();
		if (accountListAdapter.getCount() > 0) {
			mainView = inflater.inflate(R.layout.activity_main, container,
					false);
			refreshAccountList();
		}
		mainView.setOnLongClickListener(this);

		return mainView;

	}

	public AccountOverviewFragment(LoginManager loginManager) {
		super();
		this.loginManager = loginManager;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		longClicked = true;
		selectedAccount = loginManager.getAccountManager().getAccount(position);
		((MainFragmentActivity) getActivity())
				.accountInteraction(selectedAccount);
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (showInfoToast && !longClicked) {

			Toast.makeText(getActivity(),
					getResources().getString(R.string.hold_item_string),
					Toast.LENGTH_SHORT).show();
			showInfoToast = false;
			Timer timer = new Timer();
			final AccountOverviewFragment activity = this;
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					activity.setShowInfoToast(true);
				}
			}, 5000);
		}
		longClicked = false;

	}

	private void checkExpired() {
		int i = 0;
		for (final Account account : loginManager.getAccountManager()
				.getAccounts()) {
			selectedAccount = account;
			if (account.isExpired()) {
				i++;
				if (active) {
					final AccountOverviewFragment fragment = this;
					final Activity activity = getActivity();
					activity.runOnUiThread(new Runnable() {

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
													R.string.yes), fragment)
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
					getActivity())
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

			Intent resultIntent = new Intent(getActivity(),
					AccountOverviewFragment.class);

			TaskStackBuilder stackBuilder = TaskStackBuilder
					.create(getActivity());
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) getActivity()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(mId, mBuilder.build());
		}
	}

	@Override
	public void accountsExpired(ArrayList<Account> accounts) {
		for (final Account account : accounts) {
			selectedAccount = account;
			if (active) {
				final AccountOverviewFragment fragment = this;
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
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
										fragment)
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
					getActivity())
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

			Intent resultIntent = new Intent(getActivity(),
					AccountOverviewFragment.class);

			TaskStackBuilder stackBuilder = TaskStackBuilder
					.create(getActivity());
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) getActivity()
					.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(mId, mBuilder.build());
		}

	}

	@Override
	public void exportSuccessful(final String hash) {
		final AccountOverviewFragment fragment = this;
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LinearLayout layout = new LinearLayout(getActivity());
				layout.setOrientation(LinearLayout.VERTICAL);
				EditText editHash = new EditText(getActivity());
				if (DEBUG_ACTIVATED)
					Log.e("Hash", "hash:" + hash);
				editHash.setText(hash);
				TextView textViewInfo = new TextView(getActivity());
				textViewInfo.setText(getResources().getString(
						R.string.account_export_info));
				layout.addView(textViewInfo);
				layout.addView(editHash);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity()).setMessage("Account exported")
						.setPositiveButton("OK", null).setView(layout);
				builder.create().show();

			}
		});

	}

	@Override
	public void exportFailed() {
		// TODO Auto-generated method stub

	}

	@Override
	public CharSequence getTitle() {
		return "Home";
	}

	@Override
	public boolean onLongClick(View v) {
		if(v.equals(mainView)){
			((MainFragmentActivity) getActivity()).justAddAction();
		}
		return false;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		refreshAccountList();
	}
}
