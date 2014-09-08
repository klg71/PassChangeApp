package account;

import java.util.Calendar;
import java.util.Date;

import core.Website;

public class Account {
	private String userName;
	private String email;
	private String actualPassword;
	private Calendar lastChangedCalendar;
	private Website website;
	private int expire;

	public String getUserName() {
		return userName;
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
				+ ", actualPassword=" + actualPassword + ", website=" + website
				+ "]";
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

}
