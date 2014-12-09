package ui.settings;

import android.app.Activity;
import core.Configuration;

public abstract class ConfigurationSetting {
	protected Configuration configuration;
	protected Activity activity;

	public ConfigurationSetting(Configuration configuration, Activity activity) {
		super();
		this.configuration = configuration;
		this.activity = activity;
	}

}
