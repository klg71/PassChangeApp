package ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import com.passchange.passchangeapp.R;

import core.Website;
import account.Account;
import account.AccountManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddAccountWindow implements OnClickListener {

	private AccountManager accountManager;
	private MainActivity mainActivity;
	private ArrayList<Website> websites;

	public AddAccountWindow(AccountManager accountManager,
			MainActivity mainActivity) {
		this.accountManager = accountManager;
		this.mainActivity = mainActivity;
		Button submit = (Button) mainActivity.findViewById(R.id.buttonSubmit);
		submit.setOnClickListener(this);
		websites = new ArrayList<Website>();
		for (Map.Entry<String, Website> entry : accountManager.getWebsites()
				.entrySet()) {
			websites.add(entry.getValue());
		}
		Spinner website = (Spinner) mainActivity.findViewById(R.id.spinner1);
		WebsiteSpinnerAdapter dataAdapter = new WebsiteSpinnerAdapter(
				mainActivity, android.R.layout.simple_spinner_item, websites);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		website.setAdapter(dataAdapter);
		EditText expire = (EditText) mainActivity.findViewById(R.id.editExpire);
		expire.setText("10");

	}

	@Override
	public void onClick(View v) {
		EditText user, email, pass,expire;
		Spinner website;
		user = (EditText) mainActivity.findViewById(R.id.editUserName);
		email = (EditText) mainActivity.findViewById(R.id.editEmail);
		pass = (EditText) mainActivity.findViewById(R.id.editPass);
		website = (Spinner) mainActivity.findViewById(R.id.spinner1);
		expire = (EditText) mainActivity.findViewById(R.id.editExpire);
		Calendar temp = Calendar.getInstance();
		Account tempAcc=accountManager.addAccount(new Account(user.getText().toString(), email
				.getText().toString(), pass.getText().toString(), temp,
				websites.get((int) website.getSelectedItemId()), Integer.parseInt(expire.getText().toString())));
		tempAcc.testLogin(mainActivity);
		// Hide Keyboard
		InputMethodManager im = (InputMethodManager) mainActivity
				.getApplicationContext().getSystemService(
						MainActivity.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(mainActivity.getWindow().getDecorView()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		mainActivity.setContentView(R.layout.activity_main);
		mainActivity.refreshAccountList();
		mainActivity.setChildWindowActive(false);
	}
}
