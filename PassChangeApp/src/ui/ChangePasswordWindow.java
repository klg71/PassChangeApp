package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ChangePasswordWindow implements OnClickListener {
	
	private Account account;
	private MainActivity mainActivity;
	
	
	public ChangePasswordWindow(Account account,MainActivity mainActivity){
		this.account=account;
		this.mainActivity=mainActivity;
		Button submit=(Button) mainActivity.findViewById(R.id.buttonChangeSubmit);
		submit.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View v) {

		EditText pass;
		pass=(EditText)mainActivity.findViewById(R.id.editNewPass);
		try {
			account.getWebsite().initialize(account.getUserName(), account.getActualPassword());
			account.getWebsite().changePassword(pass.getText().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
