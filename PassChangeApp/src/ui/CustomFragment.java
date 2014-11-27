package ui;

import android.support.v4.app.Fragment;

public abstract class CustomFragment extends Fragment {
	public abstract void onBackPressed();

	public abstract CharSequence getTitle();
}
