package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeAccountWindow implements OnClickListener, OnLongClickListener {

	private Account selectedAccount;
	private MainFragmentActivity mainActivity;
	private EditText password,user,email,expire;
	
	public ChangeAccountWindow(Account selectedAccount,
			MainFragmentActivity mainFragmentActivity,View mainView) {
		this.selectedAccount=selectedAccount;
		this.mainActivity=mainFragmentActivity;
		Button button=(Button)mainView.findViewById(R.id.buttonChangeSubmit);
		button.setOnClickListener(this);
		password=(EditText)mainView.findViewById(R.id.editChangePass);
		password.setOnLongClickListener(this);
		user=(EditText)mainView.findViewById(R.id.editChangeUserName);
		email=(EditText)mainView.findViewById(R.id.editChangeEmail);
		expire=(EditText)mainView.findViewById(R.id.editChangeExpire);
		user.setText(selectedAccount.getUserName());
		email.setText(selectedAccount.getEmail());
		password.setText(selectedAccount.getActualPassword());
		expire.setText(Integer.toString(selectedAccount.getExpire()));
		selectedAccount.setUserName(user.getText().toString());
		
	}


	@Override
	public void onClick(View v) {
		selectedAccount.setUserName(user.getText().toString());
		selectedAccount.setActualPassword(password.getText().toString());	
		selectedAccount.setEmail(email.getText().toString());
		selectedAccount.setExpire(Integer.parseInt(expire.getText().toString()));
		mainActivity.setContentView(R.layout.activity_main);
	}


	@Override
	public boolean onLongClick(View v) {
		if(password.getInputType()==android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD){
			password.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
		} else {
			password.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
		return false;
	}
	
	

}
