package ui;

import com.passchange.passchangeapp.R;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import core.Configuration;

public class SettingsWindow implements OnCheckedChangeListener,
		OnEditorActionListener {

	private MainActivity mainActivity;
	private Configuration configuration;
	private CheckBox checkBox;
	private EditText editText;

	public SettingsWindow(MainActivity mainActivity, Configuration configuration) {
		this.mainActivity = mainActivity;
		this.configuration = configuration;
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
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		configuration.setLogoutWhenAppIsPaused(checkBox.isChecked());
		editText.setEnabled(!checkBox.isChecked());
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			configuration.setLogoutTimeMinutes(Integer.parseInt(editText
					.getText().toString()));;
		}
		return false;
	}

}
