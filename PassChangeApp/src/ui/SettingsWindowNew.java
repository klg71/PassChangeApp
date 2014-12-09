package ui;

import ui.settings.ChangeMasterPassword;
import ui.settings.ConfigurationSettingLogout;
import ui.settings.ConfigurationSettingPasswordRemember;

import com.passchange.passchangeapp.R;

import account.AccountManager;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import core.Configuration;

public class SettingsWindowNew implements OnItemClickListener {

	private ListView listViewSettings;
	private boolean childActive;
	private Configuration configuration;
	private Activity activity;
	private AccountManager accountManager;

	public SettingsWindowNew(MainFragmentActivity mainFragmentActivity,
			Configuration configuration, AccountManager accountManager) {
		this.activity = mainFragmentActivity;
		this.configuration = configuration;
		this.accountManager = accountManager;
		activity.setContentView(R.layout.settings_new);
		listViewSettings = (ListView) mainFragmentActivity
				.findViewById(R.id.listViewSettings);
		listViewSettings.setAdapter(new SettingsListAdapter(
				mainFragmentActivity));
		listViewSettings.setOnItemClickListener(this);

	}

	public boolean goBack() {
		if (childActive) {
			activity.setContentView(R.layout.settings_new);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			childActive = true;
			new ConfigurationSettingLogout(configuration, activity);
			break;
		case 1:
			childActive = true;
			new ConfigurationSettingPasswordRemember(configuration, activity);
			break;
		case 2:
			childActive = true;
			new ChangeMasterPassword(activity, accountManager);
			break;
		}

	}

}
