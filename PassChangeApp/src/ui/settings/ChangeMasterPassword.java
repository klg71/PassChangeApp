package ui.settings;

import com.passchange.passchangeapp.R;

import account.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeMasterPassword implements OnClickListener {
	private Button buttonMasterPassSubmit;
	private EditText passwordEditText;
	private Activity mainActivity;
	private AccountManager accountManager;

	public ChangeMasterPassword(Activity activity, AccountManager accountManager) {
		this.mainActivity=activity;
		this.accountManager=accountManager;
		buttonMasterPassSubmit = (Button) activity
				.findViewById(R.id.buttonChangeMasterPassSubmit);
		buttonMasterPassSubmit.setOnClickListener(this);

		passwordEditText = (EditText) activity
				.findViewById(R.id.editTextChangeMasterPass);
	}

	@Override
	public void onClick(View v) {
		if (v.equals(buttonMasterPassSubmit)) {
			if (passwordEditText.getText().toString().length() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						mainActivity);
				builder.setTitle(
						mainActivity.getResources().getString(
								R.string.change_master_pass))
						.setMessage(
								mainActivity.getResources().getString(
										R.string.are_you_sure))
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(
								mainActivity.getResources().getString(
										R.string.yes),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										accountManager
												.setMasterPass(new String(
														passwordEditText
																.getText()
																.toString()));
									}
								})
						.setNegativeButton(
								mainActivity.getResources().getString(
										R.string.no), null).show();
			}
		}

	}
}
