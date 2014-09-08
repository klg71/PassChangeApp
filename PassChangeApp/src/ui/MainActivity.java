package ui;

import java.util.ArrayList;
import java.util.HashMap;

import plugins.Facebook;
import plugins.Google;
import plugins.Twitter;

import com.passchange.passchangeapp.R;

import core.Website;
import account.Account;
import account.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemLongClickListener,
		android.widget.PopupMenu.OnMenuItemClickListener {

	private AccountManager accountManager;
	private HashMap<String, Website> websites;
	private ArrayList<TextView> accountTextViews;
	private AccountListAdapter accountListAdapter;
	private PopupMenu popupMenu;
	private Account selectedAccount;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog_login, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Login");
		alert.setMessage("Enter Pin :");
		alert.setView(textEntryView);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// String value = input.getText().toString();
				EditText mUserText;
				mUserText = (EditText) textEntryView
						.findViewById(R.id.txt_password);
				password = mUserText.getText().toString();
				return;
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						return;
					}
				});
		if (password.length() == 0) {
			alert.show();

			websites = new HashMap<String, Website>();
			websites.put("Facebook", new Facebook());
			websites.put("Twitter", new Twitter());
			websites.put("Google", new Google());
			accountManager = new AccountManager("accounts.xml",password, websites);
			accountManager.loadFromDatabase();
			setContentView(R.layout.activity_main);
			accountListAdapter = new AccountListAdapter(accountManager);
			accountTextViews = new ArrayList<TextView>();
		} else {
			finish();
		}
	}

	@Override
	protected void onStop() {
		accountManager.writeToFile();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.add_account) {
			// Intent intent=new Intent(this,AddAccountActivity.class);
			// startActivityForResult(intent,0);
			setContentView(R.layout.addaccount);
			new AddAccountWindow(accountManager, this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
			setContentView(R.layout.changepassword);
		}
		case R.id.action_delete_account: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Erase hard drive")
					.setMessage("Are you sure?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									accountManager.removeAccount(
											selectedAccount.getWebsite()
													.getName(), selectedAccount
													.getEmail());
								}
							}).setNegativeButton("No", null) // Do nothing on no
					.show();
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
		Log.e("PassChange", " " + position);
		return false;
	}
}
