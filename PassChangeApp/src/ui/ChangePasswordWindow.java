package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ChangePasswordWindow implements OnClickListener {

	private Account account;
	private MainActivity mainActivity;

	public ChangePasswordWindow(Account account, MainActivity mainActivity) {
		this.account = account;
		this.mainActivity = mainActivity;
		Button submit = (Button) mainActivity
				.findViewById(R.id.buttonChangeSubmit);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		EditText pass;
		pass = (EditText) mainActivity.findViewById(R.id.editNewPass);
		if (account.getWebsite().validatePassword(pass.getText().toString())) {
			account.changePassword(pass.getText().toString());

			// Hide Keyboard
			InputMethodManager im = (InputMethodManager) mainActivity
					.getApplicationContext().getSystemService(
							MainActivity.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(mainActivity.getWindow().getDecorView()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			mainActivity.setContentView(R.layout.activity_main);
			mainActivity.refreshAccountList();
			mainActivity.setChildWindowActive(false);
		} else {
			new AlertDialog.Builder(mainActivity)
					.setMessage(
							"Pls enter a password that fullfilles following Conditions: "+account.getWebsite().getPasswordCondition())
					.setTitle("Error")
					.setCancelable(true)
					.setNeutralButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).show();
		}
	}
}
