package account;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ui.MainActivity;
import android.util.Log;
import android.widget.Toast;
import core.Website;

public class Account {
	private String userName;
	private String email;
	private String actualPassword;
	private Calendar lastChangedCalendar;
	private Website website;
	private SimpleDateFormat simpleDateFormat;
	private int expire;

	public String getUserName() {
		return userName;
	}

	public void changePassword(final String newPass, final MainActivity activity) {
		website.initialize(userName, actualPassword);
		final Thread login = new Thread() {
			@Override
			public void run() {
				try {
					website.authenticate();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		login.start();
		final Thread change = new Thread() {
			@Override
			public void run() {
				try {
					while (login.isAlive())
						;
					website.changePassword(newPass);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		change.start();

		new Thread() {
			public void run() {
				while (change.isAlive())
					;
				if (website.isSuccesful()) {
					actualPassword = newPass;
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(
									activity,
									website.getName()
											+ ": Password change succesful",
									Toast.LENGTH_LONG).show();

						}
					});
				}
			}
		}.start();

	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getActualPassword() {
		return actualPassword;
	}

	public void setActualPassword(String actualPassword) {
		this.actualPassword = actualPassword;
	}

	public Website getWebsite() {
		return website;
	}

	public void setWebsite(Website website) {
		this.website = website;
	}

	@Override
	public String toString() {
		return "Account [userName=" + userName + ", email=" + email
				+ ", actualPassword=" + actualPassword
				+ ", lastChangedCalendar="
				+ simpleDateFormat.format(lastChangedCalendar.getTime())
				+ ", website=" + website + ", expire=" + expire + "]";
	}

	public boolean isExpired() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_YEAR, -10);
		if (lastChangedCalendar.get(Calendar.DAY_OF_YEAR) > now
				.get(Calendar.DAY_OF_YEAR))
			return false;
		else
			return true;
	}

	public Account(String userName, String email, String actualPassword,
			Calendar lastChangedCalendar, Website website, int expire) {
		super();
		this.userName = userName;
		this.email = email;
		this.actualPassword = actualPassword;
		this.lastChangedCalendar = lastChangedCalendar;
		this.website = website;
		this.expire = expire;
		simpleDateFormat = new SimpleDateFormat("yyyy-MM-d k:m:s");
	}

	public Calendar getLastChangedCalendar() {
		return lastChangedCalendar;
	}

	public void setLastChangedCalendar(Calendar lastChangedCalendar) {
		this.lastChangedCalendar = lastChangedCalendar;
	}

	public int getExpire() {
		return expire;
	}

	public void setExpire(int expire) {
		this.expire = expire;
	}

	public void testLogin(final MainActivity activity) {
		website.initialize(userName, actualPassword);
		final Thread login = new Thread() {
			@Override
			public void run() {
				try {
					website.authenticate();
				} catch (Exception e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}
			}
		};
		login.start();
		Log.e("test", Boolean.toString(website.isAuthenticated()));
		new Thread() {
			public void run() {

				while (login.isAlive())
					;

				if (website.isAuthenticated()) {

					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity,
									website.getName() + ": Login succesful",
									Toast.LENGTH_LONG).show();

						}
					});
				} else {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(activity,
									website.getName() + ": Login unsuccesful",
									Toast.LENGTH_LONG).show();

						}
					});
				}
			}
		}.start();
	}
}
