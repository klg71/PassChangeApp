package ui;

import java.util.ArrayList;

import com.passchange.passchangeapp.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SettingsListAdapter extends BaseAdapter {

	private ArrayList<String> settings;

	public SettingsListAdapter(Activity activity) {
		settings = new ArrayList<String>();
		settings.add(activity.getResources().getString(R.string.setting_logout));
		settings.add(activity.getResources().getString(
				R.string.setting_password_remind));
		settings.add(activity.getResources().getString(
				R.string.setting_change_masterpass));

	}

	@Override
	public int getCount() {
		return settings.size();
	}

	@Override
	public Object getItem(int position) {
		return settings.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = new TextView(parent.getContext());
		view.setText(settings.get(position));
		return view;
	}

}
