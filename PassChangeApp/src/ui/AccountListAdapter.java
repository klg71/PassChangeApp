package ui;

import account.AccountManager;
import android.database.DataSetObserver;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

public class AccountListAdapter extends BaseAdapter {

	private AccountManager accountManager;

	public AccountListAdapter(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	@Override
	public int getCount() {
		return accountManager.getAccounts().size();
	}

	@Override
	public Object getItem(int position) {
		return accountManager.getAccount(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = new TextView(parent.getContext());
		view.setText(accountManager.getAccount(position).getUserName() + " - "
				+ accountManager.getAccount(position).getWebsite());

		return view;
	}

}
