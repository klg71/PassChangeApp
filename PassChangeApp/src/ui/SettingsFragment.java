package ui;

import com.passchange.passchangeapp.R;

import core.Configuration;
import account.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SettingsFragment extends CustomFragment implements OnClickListener, OnCheckedChangeListener, OnEditorActionListener {
	
	private MainFragmentActivity mainActivity;
	private Configuration configuration;
	private CheckBox checkBox;
	private Button buttonMasterPassSubmit;
	private EditText editText, passwordEditText;
	private EditText rememberEditText;
	private AccountManager accountManager;

	public SettingsFragment(MainFragmentActivity mainActivity,
			Configuration configuration, AccountManager accountManager) {
		this.mainActivity = mainActivity;
		this.configuration = configuration;
		this.accountManager = accountManager;

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
				configuration.setRememberTimeMinmutes(Integer
						.parseInt(rememberEditText.getText().toString()));

			}

		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(buttonMasterPassSubmit)) {
			if (passwordEditText.getText().toString().length() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						mainActivity);
				builder.setTitle(mainActivity.getResources().getString(R.string.change_master_pass))
						.setMessage(mainActivity.getResources().getString(R.string.are_you_sure))
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(mainActivity.getResources().getString(R.string.yes),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										accountManager
												.setMasterPass(new String(
														passwordEditText
																.getText()
																.toString()));
									}
								}).setNegativeButton(mainActivity.getResources().getString(R.string.no), null).show();
			}
		}

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return getActivity().findViewById(R.layout.settings);
	}

	@Override
	public CharSequence getTitle() {
		return getActivity().getResources().getString(R.string.settings);
	}

	@Override
	public void onStart() {
		buttonMasterPassSubmit = (Button) mainActivity
				.findViewById(R.id.buttonChangeMasterPassSubmit);
		buttonMasterPassSubmit.setOnClickListener(this);

		passwordEditText = (EditText) mainActivity
				.findViewById(R.id.editTextChangeMasterPass);

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
		super.onStart();
	}

}
