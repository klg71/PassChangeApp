package ui.settings;

import com.passchange.passchangeapp.R;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import core.Configuration;

public class ConfigurationSettingLogout extends ConfigurationSetting implements
		OnCheckedChangeListener, OnEditorActionListener {

	private CheckBox checkBox;
	private EditText editText;

	public ConfigurationSettingLogout(Configuration configuration,
			Activity activity) {
		super(configuration, activity);
		activity.setContentView(R.layout.settings_logout_view);
		checkBox = (CheckBox) activity
				.findViewById(R.id.checkBoxLogOutWhenAppIsPaused);
		checkBox.setChecked(configuration.isLogoutWhenAppIsPaused());
		checkBox.setOnCheckedChangeListener(this);

		editText = (EditText) activity
				.findViewById(R.id.editTextTimeTillLogout);
		editText.setText(Integer.toString(configuration.getLogoutTimeMinutes()));
		if (configuration.isLogoutWhenAppIsPaused()) {
			editText.setEnabled(false);
		} else {
			editText.setEnabled(true);
		}
		editText.setOnEditorActionListener(this);

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
		}
		return false;
	}
}
