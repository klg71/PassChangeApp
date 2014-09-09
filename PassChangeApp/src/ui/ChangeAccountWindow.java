package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChangeAccountWindow implements OnClickListener, OnLongClickListener {

	private Account selectedAccount;
	private MainActivity mainActivity;
	private TextView password;
	
	public ChangeAccountWindow(Account selectedAccount,
			MainActivity mainActivity) {
		this.selectedAccount=selectedAccount;
		this.mainActivity=mainActivity;
		Button button=(Button)mainActivity.findViewById(R.id.buttonChangeSubmit);
		button.setOnClickListener(this);
		password=(TextView)mainActivity.findViewById(R.id.editChangePass);
		password.setOnLongClickListener(this);
		
	}


	@Override
	public void onClick(View v) {
		TextView user,email,expire;
		user=(TextView)mainActivity.findViewById(R.id.editChangeUserName);
		email=(TextView)mainActivity.findViewById(R.id.editChangeEmail);
		expire=(TextView)mainActivity.findViewById(R.id.editChangeExpire);
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
