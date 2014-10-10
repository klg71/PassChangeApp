package ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import account.AccountManager;
import android.database.DataSetObserver;
import android.graphics.LinearGradient;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccountListAdapter extends BaseAdapter {

	private AccountManager accountManager;
	private SimpleDateFormat format;

	public AccountListAdapter(AccountManager accountManager) {
		this.accountManager = accountManager;
		format=(SimpleDateFormat) new SimpleDateFormat().getDateInstance(SimpleDateFormat.MEDIUM);
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
		LinearLayout layout=new LinearLayout(parent.getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		Calendar tempCal=(Calendar) accountManager.getAccount(position).getLastChangedCalendar().clone();
		tempCal.add(Calendar.DAY_OF_YEAR,accountManager.getAccount(position).getExpire());
		TextView view = new TextView(parent.getContext());
		view.setText(accountManager.getAccount(position).getUserName() + " - "
				+ accountManager.getAccount(position).getWebsite()+System.getProperty("line.separator")+"Expires: "+format.format(tempCal.getTime()));
		
		ImageView imageView=new ImageView(parent.getContext());
		imageView.setImageResource( accountManager.getAccount(position).getWebsite().getImageSource());
		imageView.setLayoutParams(new LayoutParams(128, 128));
		layout.addView(imageView);
		layout.addView(view);
		return layout;
	}

}
