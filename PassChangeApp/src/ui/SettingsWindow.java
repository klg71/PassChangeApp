package ui;

import com.passchange.passchangeapp.R;

import account.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import core.Configuration;

public class SettingsWindow implements OnCheckedChangeListener,
		OnEditorActionListener, OnClickListener {

	private MainActivity mainActivity;
	private Configuration configuration;
	private CheckBox checkBox;
	private Button buttonMasterPassSubmit;
	private EditText editText,passwordEditText;
	private EditText rememberEditText;
	private AccountManager accountManager;

	public SettingsWindow(MainActivity mainActivity, Configuration configuration,AccountManager accountManager) {
		this.mainActivity = mainActivity;
		this.configuration = configuration;
		this.accountManager=accountManager;
		
		buttonMasterPassSubmit=(Button)mainActivity.findViewById(R.id.buttonChangeMasterPassSubmit);
		buttonMasterPassSubmit.setOnClickListener(this);
		
		passwordEditText=(EditText)mainActivity.findViewById(R.id.editTextChangeMasterPass);
		
		
		checkBox = (CheckBox) mainActivity
				.findViewById(R.id.checkBoxLogOutWhenAppIsPaused);
		checkBox.setChecked(configuration.isLogoutWhenAppIsPaused());
		checkBox.setOnCheckedChangeListener(this);

		editText = (EditText) mainActivity
				.findViewById(R.id.editTextTimeTillLogout);
		editText.setText(Integer.toString(configuration.getLogoutTimeMinutes()));
		if (configuration.isLogoutWhenAppIsPaused()) {
			editText.setEnabled(false);
		} else {
			editText.setEnabled(true);
		}
		editText.setOnEditorActionListener(this);

		rememberEditText = (EditText) mainActivity
				.findViewById(R.id.editTextTimeTillRemember);
		rememberEditText.setText(Integer.toString(configuration
				.getRememberTimeMinmutes()));
		rememberEditText.setOnEditorActionListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		configuration.setLogoutWhenAppIsPaused(checkBox.isChecked());
		editText.setEnabled(!checkBox.isChecked());
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v.equals(editText)) {
			if (EditorInfo.IME_ACTION_DONE == actionId) {
				configuration.setLogoutTimeMinutes(Integer.parseInt(editText
						.getText().toString()));

			}
		} else if (v.equals(rememberEditText)) {
			if (EditorInfo.IME_ACTION_DONE == actionId) {
				configuration.setRememberTimeMinmutes(Integer.parseInt(rememberEditText
						.getText().toString()));

			}

		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if(v.equals(buttonMasterPassSubmit)){
			AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
			builder.setTitle("Change Masterpassword")
					.setMessage("Are you sure?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									accountManager.setMasterPass(new String(passwordEditText.getText().toString()));
								}
							}).setNegativeButton("No", null).show();
		}
		
	}

}
