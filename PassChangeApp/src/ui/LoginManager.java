package ui;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import plugins.Amazon;
import plugins.Ebay;
import plugins.Facebook;
import plugins.Google;
import plugins.LeagueOfLegends;
import plugins.Twitter;
import account.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.passchange.passchangeapp.R;

import core.Configuration;
import core.Website;

public class LoginManager {

	private boolean loggedIn;
	private MainActivity activity;
	private boolean resetPassword;
	private boolean active;
	protected AccountManager accountManager;
	protected AccountListAdapter accountListAdapter;

	public LoginManager(MainActivity activity) {
		this.activity = activity;
		active = false;
	}

	public void OnAppPaused() {
		try {
			active = false;
			accountManager.writeToFile();
			if (accountManager.getConfiguration().isLogoutWhenAppIsPaused())
				loggedIn = false;
			else {
				startLogoutTimer();
			}
		} catch (Exception e) {

			if (MainActivity.DEBUG_ACTIVATED)
				e.printStackTrace();
		}
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public AccountListAdapter getAccountListAdapter() {
		return accountListAdapter;
	}

	public void OnAppStopped(){
		if(isLoggedIn()){
			active=false;
			try {
				accountManager.writeToFile();
			} catch (Exception e) {
				if (MainActivity.DEBUG_ACTIVATED)
					e.printStackTrace();
			}
		}
	}
	
	public void OnAppStarted() {
		if (isLoggedIn()) {

		} else {
			login();
		}
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void startLogoutTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				loggedIn = false;
				if (MainActivity.DEBUG_ACTIVATED)
					System.out.println("Login falsed");
			}
		}, 60000 * accountManager.getConfiguration().getLogoutTimeMinutes());
	}

	private void login() {
		loggedIn = false;
		LayoutInflater factory = LayoutInflater.from(activity);

		final View textEntryView = factory.inflate(R.layout.dialog_login, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(activity);
		alert.setTitle("Login");
		alert.setCancelable(false);
		alert.setMessage("Please enter your Masterpassword:");
		alert.setView(textEntryView);
		resetPassword = false;

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// String value = input.getText().toString();
				EditText mUserText = (EditText) textEntryView
						.findViewById(R.id.txt_password);
				String password = mUserText.getText().toString();
				if (password.length() > 0) {

					loggedIn = true;

					HashMap<String, Website> websites = new HashMap<String, Website>();
					websites.put("Facebook", new Facebook(activity));
					websites.put("Twitter", new Twitter(activity));
					websites.put("Google", new Google(activity));
					websites.put("League of Legends", new LeagueOfLegends(
							activity));
					websites.put("Amazon", new Amazon(activity));
					websites.put("Ebay", new Ebay(activity));
					accountManager = new AccountManager("/sdcard/accounts.xml",
							password, websites);
					if (MainActivity.DEBUG_ACTIVATED)
						Log.e("file", "/sdcard/accounts.xml");
					File file = new File("/sdcard/accounts.xml");
					if (file.exists() && !resetPassword) {
						try {
							accountManager.loadFromFile();
						} catch (Exception e) {
							// if (DEBUG_ACTIVATED)
							// Log.e("Error", e.getMessage());
							if (MainActivity.DEBUG_ACTIVATED)
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
					activity.setContentView(R.layout.activity_main);
					accountListAdapter = new AccountListAdapter(accountManager);
					active = true;
					((MainActivity) activity).refreshAccountList();
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
						activity.finish();
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

}
