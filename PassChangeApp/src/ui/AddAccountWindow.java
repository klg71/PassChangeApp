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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddAccountWindow implements OnClickListener {

	private AccountManager accountManager;
	private MainActivity mainActivity;
	private ArrayList<Website> websites;
	
	public AddAccountWindow(AccountManager accountManager,MainActivity mainActivity){
		this.accountManager=accountManager;
		this.mainActivity=mainActivity;
		Button submit=(Button) mainActivity.findViewById(R.id.buttonSubmit);
		submit.setOnClickListener(this);
		websites=new ArrayList<Website>();
		for(Map.Entry<String, Website> entry:accountManager.getWebsites().entrySet()){
			websites.add(entry.getValue());
		} 
		Spinner website = (Spinner) mainActivity.findViewById(R.id.spinner1);
		ArrayAdapter<Website> dataAdapter = new ArrayAdapter<Website>(mainActivity,
			android.R.layout.simple_spinner_item, websites);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		website.setAdapter(dataAdapter);
		
	}

	@Override
	public void onClick(View v) {
		EditText user,email,pass;
		Spinner website;
		user=(EditText)mainActivity.findViewById(R.id.editUserName);
		email=(EditText)mainActivity.findViewById(R.id.editEmail);
		pass=(EditText)mainActivity.findViewById(R.id.editPass);
		website=(Spinner)mainActivity.findViewById(R.id.spinner1);
		Calendar temp=Calendar.getInstance();
		accountManager.addAccount(new Account(user.getText().toString(),email.getText().toString(),pass.getText().toString(), temp, websites.get((int)website.getSelectedItemId()), 10));
		mainActivity.setContentView(R.layout.activity_main);
		mainActivity.refreshAccountList();
		mainActivity.setChildWindowActive(false);
	}
}
