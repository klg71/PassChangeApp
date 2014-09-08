package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
			account.changePassword(pass.getText().toString());
		} catch (Exception e) {
			   new AlertDialog.Builder(mainActivity)
			      .setMessage("Unable to change password maybe youre actual one is wrong?")
			      .setTitle("error")
			      .setCancelable(true)
			      .setNeutralButton(android.R.string.cancel,
			         new DialogInterface.OnClickListener() {
			         public void onClick(DialogInterface dialog, int whichButton){}
			         })
			      .show();
			e.printStackTrace();
		}
		mainActivity.setContentView(R.layout.activity_main);
		mainActivity.refreshAccountList();

	}
}
