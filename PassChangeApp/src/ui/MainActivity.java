package ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import plugins.Facebook;
import plugins.Google;
import plugins.Twitter;

import com.passchange.passchangeapp.R;

import core.Website;
import account.Account;
import account.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
		android.widget.PopupMenu.OnMenuItemClickListener, android.content.DialogInterface.OnClickListener {

	@Override
	protected void onRestart() {
		for(Account account:accountManager.getAccounts()){
			selectedAccount=account;
			if(account.isExpired()){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Password expired")
						.setMessage("Your Password for: "+account.getUserName()+"@"+account.getWebsite().getName()+" is expired do you want to change it now?")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("Yes",
								this
								).setNegativeButton("No", null) // Do nothing on no
						.show();
			}
			}

		super.onRestart();
	}

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

		login();
	}
	
	private void login(){
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog_login, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Login");
		alert.setMessage("Enter Pin :");
		alert.setView(textEntryView);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				 //String value = input.getText().toString();
				EditText mUserText = (EditText) textEntryView
						.findViewById(R.id.txt_password);
				password = mUserText.getText().toString();

				websites = new HashMap<String, Website>();
				websites.put("Facebook", new Facebook());
				websites.put("Twitter", new Twitter());
				websites.put("Google", new Google());
				accountManager = new AccountManager(getApplicationContext().getFileStreamPath("accounts.xml")
						   .getPath(),password, websites);
				Log.e("file",getApplicationContext().getFileStreamPath("accounts.xml").getPath());
				try {
					accountManager.loadFromFile();
				} catch (Exception e) {
					Log.e("Error",e.getMessage());
					e.printStackTrace();
				    AlertDialog ad = new AlertDialog.Builder(getBaseContext()).create();  
				    ad.setCancelable(false); // This blocks the 'BACK' button  
				    ad.setMessage("An error occured, maybe you entered the wrong password try it again!");  
				    ad.setButton("OK", new DialogInterface.OnClickListener() {  
				        @Override  
				        public void onClick(DialogInterface dialog, int which) {  
				            dialog.dismiss();                      
				        }  
				    });  
				    ad.show(); 
				    login();
					return;
				}
				setContentView(R.layout.activity_main);
				accountListAdapter = new AccountListAdapter(accountManager);
				accountTextViews = new ArrayList<TextView>();
				refreshAccountList();
				if (password.length() != 0) {

				} else {
					finish();
				}
				return;
			}
		});


		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						return;
					}
				});
		alert.create().show();
	}

	@Override
	protected void onStop() {
		Log.e("Debug","onStop");
		try {
			accountManager.writeToFile();
		} catch (Exception e) {
			Log.e("Error",e.getMessage());
			e.printStackTrace();
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
			ChangePasswordWindow window=new ChangePasswordWindow(selectedAccount, this);
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
									accountManager.removeAccount(selectedAccount);
									refreshAccountList();
								}
							}).setNegativeButton("No", null) 
					.show();
			break;
		}
		case R.id.action_change_account: {
			setContentView(R.layout.changeaccount);
			ChangeAccountWindow window=new ChangeAccountWindow(selectedAccount,this);
			break;
		}
		case R.id.action_copy_password:{
			ClipboardManager clipboard = (ClipboardManager)
			        getSystemService(this.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("password",selectedAccount.getActualPassword());
			clipboard.setPrimaryClip(clip);
			break;
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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		setContentView(R.layout.changepassword);
		ChangePasswordWindow window=new ChangePasswordWindow(selectedAccount, this);
		
	}
}
