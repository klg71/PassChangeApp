package account;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.passchange.passchangeapp.R;

import ui.MainActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
	private static final String SET_COOKIE = "Set-Cookie";
	private static final String COOKIE_VALUE_DELIMITER = ";";
	private static final String PATH = "path";
	private static final String EXPIRES = "expires";
	private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
	private static final String SET_COOKIE_SEPARATOR = "; ";
	private static final String COOKIE = "Cookie";

	private static final char NAME_VALUE_SEPARATOR = '=';
	private static final char DOT = '.';
	private DateFormat dateFormat;

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
					if (website.isAuthenticated())
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

	public void openBrowser(final WebView webView, final MainActivity activity) {

		CookieSyncManager.createInstance(webView.getContext());
		final CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();
		cookieManager.removeAllCookie();
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

		final Thread openBrowser = new Thread() {
			@Override
			public void run() {
				try {
					while (login.isAlive())
						;
					if (website.isAuthenticated()) {

						Map<String, Map<String, Map<String, String>>> cookieStore = website
								.getCookies();

						for (Map.Entry<String, Map<String, Map<String, String>>> domainStore : cookieStore
								.entrySet()) {

							if (domainStore == null)
								continue;

							StringBuffer cookieStringBuffer = new StringBuffer();

							Iterator<String> cookieNames = domainStore
									.getValue().keySet().iterator();
							while (cookieNames.hasNext()) {
								String cookieName = (String) cookieNames.next();
								Map<String, String> cookie = domainStore
										.getValue().get(cookieName);
								// check cookie to ensure path matches and
								// cookie is
								// not expired
								// if all is cool, add cookie to header string
								if (isNotExpired((String) cookie.get(EXPIRES))) {
									cookieStringBuffer.append(cookieName);
									cookieStringBuffer.append("=");
									cookieStringBuffer.append((String) cookie
											.get(cookieName));
									cookieStringBuffer
											.append(SET_COOKIE_SEPARATOR);
									cookieStringBuffer.append(" domain=" + domainStore.getKey());
								}
								Log.e("Cookie:", cookieStringBuffer.toString());
								cookieManager.setCookie(domainStore.getKey(),
										cookieStringBuffer.toString());
								cookieStringBuffer = new StringBuffer();
							}
							CookieSyncManager.getInstance().sync();
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									webView.setWebViewClient(new WebViewClient());
									webView.getSettings().setJavaScriptEnabled(true);
									webView.loadUrl(website.getWebsiteUrl());

								}
							});
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		openBrowser.start();
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
		dateFormat = new SimpleDateFormat(DATE_FORMAT);
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

	private boolean isNotExpired(String cookieExpires) {
		if (cookieExpires == null)
			return true;
		Date now = new Date();
		try {
			return (now.compareTo(dateFormat.parse(cookieExpires))) <= 0;
		} catch (java.text.ParseException pe) {
			pe.printStackTrace();
			return false;
		}
	}
}
