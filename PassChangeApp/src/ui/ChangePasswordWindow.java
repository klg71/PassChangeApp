package ui;

import com.passchange.passchangeapp.R;

import account.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class ChangePasswordWindow implements OnClickListener {

	private Account account;
	private MainActivity mainActivity;

	public ChangePasswordWindow(Account account, MainActivity mainActivity) {
		this.account = account;
		this.mainActivity = mainActivity;
		ImageView imageView=(ImageView)mainActivity.findViewById(R.id.imageViewIcon);
		imageView.setImageResource(account.getWebsite().getImageSource());
		TextView textView=(TextView)mainActivity.findViewById(R.id.textViewInformation);
		String source="You are about to change the password<br>"+System.getProperty("line.separator")+"of account: <br>"+System.getProperty("line.separator")+"<b>"+account.getUserName()+"</b> at <b>"+account.getWebsite().getName()+"</b>.";
		textView.setText(Html.fromHtml(source));
		Button submit = (Button) mainActivity
				.findViewById(R.id.buttonChangeSubmit);
		submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		EditText pass;
		pass = (EditText) mainActivity.findViewById(R.id.editNewPass);
		if (account.getWebsite().validatePassword(pass.getText().toString())) {
			account.changePassword(pass.getText().toString(),mainActivity);

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
