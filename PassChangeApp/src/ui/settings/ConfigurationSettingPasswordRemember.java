package ui.settings;

import com.passchange.passchangeapp.R;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import core.Configuration;

public class ConfigurationSettingPasswordRemember extends ConfigurationSetting
		implements OnEditorActionListener {

	private EditText rememberEditText;

	public ConfigurationSettingPasswordRemember(Configuration configuration,
			Activity activity) {
		super(configuration, activity);
		rememberEditText = (EditText) activity
				.findViewById(R.id.editTextTimeTillRemember);
		rememberEditText.setText(Integer.toString(configuration
				.getRememberTimeMinmutes()));
		rememberEditText.setOnEditorActionListener(this);
		
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v.equals(rememberEditText)) {
			if (EditorInfo.IME_ACTION_DONE == actionId) {
				configuration.setRememberTimeMinmutes(Integer
						.parseInt(rememberEditText.getText().toString()));

			}

		}
		return false;
	}

}
