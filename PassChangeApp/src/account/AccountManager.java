package account;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.passchange.passchangeapp.R;

import ui.MainActivity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import core.Configuration;
import core.Website;
import file.XmlParser;

public class AccountManager {
	private ArrayList<Account> accounts;
	private String accountFile;
	private String masterPass;
	private XmlParser xmlParser;
	private HashMap<String, Website> websites;
	private Configuration configuration;
	private AccountExpiredListener accountExpiredListener;
	private Timer expirationTimer;

	public void setMasterPass(String masterPass) {
		this.masterPass = masterPass;
	}

	// private MysqlManager mysqlManager;

	public AccountManager(String accountFile, String masterPass,
			HashMap<String, Website> websites,
			AccountExpiredListener accountExpiredListener) {
		accounts = new ArrayList<Account>();
		this.accountExpiredListener = accountExpiredListener;
		this.accountFile = accountFile;
		this.masterPass = masterPass;
		this.websites = websites;
		// mysqlManager=new MysqlManager("", "", websites);
		xmlParser = new XmlParser(websites);
		configuration = new Configuration(true, 0, 10);

	}
	
	public void exportAccount(final AccountExportListener exportListener,final Account account){
		final AccountExporter accountExporter=new AccountExporter(masterPass);
		final Thread export = new Thread() {
			@Override
			public void run() {
				try {
					String hash=accountExporter.exportAccount(account);
					exportListener.exportSuccessful(hash);
				} catch (Exception e) {
					exportListener.exportFailed();
					e.printStackTrace();
				}
			}
		};
		export.start();
		
	}

	public AccountManager(String masterPass, HashMap<String, Website> websites) {
		accounts = new ArrayList<Account>();
		this.masterPass = masterPass;
		this.websites = websites;
		// mysqlManager=new MysqlManager("", "", websites);
		xmlParser = new XmlParser(websites);

	}

	public HashMap<String, Website> getWebsites() {
		return websites;
	}

	public void loadFromFile() throws Exception {
		accounts = xmlParser.loadAccountsFromFile(accountFile, masterPass);
		configuration = xmlParser.loadConfigurationFromFile(accountFile,
				masterPass);
		startExpirationTimer();
	}

	public void writeToFile() throws FileNotFoundException, Exception {
		xmlParser.saveAccountsToFile(accountFile, masterPass, this,
				configuration);
	}

	public void writeToFile(String pass) throws FileNotFoundException,
			Exception {
		xmlParser.saveAccountsToFile(accountFile, pass, this, configuration);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void loadFromDatabase() {
		// accounts=mysqlManager.loadFromDatabase(getId(), masterPass);
	}

	public void saveToDabase() {
		// mysqlManager.saveToDatabase(this,masterPass);
	}

	public void saveToDabase(String pass) {
		// mysqlManager.saveToDatabase(this,pass);
	}

	public void exportAccounts(String filename) {

	}

	public Account addAccount(Account newAccount) {
		accounts.add(newAccount);
		return newAccount;
	}

	public void removeAccount(String website, String accountName) {
		int i = -1;
		for (Account account : accounts) {
			if (account.getWebsite().toString().equals(website)
					&& account.getUserName().equals(accountName)) {
				i = accounts.indexOf(account);
			}
		}
		accounts.remove(i);
	}

	public void removeAccount(Account account) {
		accounts.remove(account);
	}

	public Account findAccount(String website, String accountName) {
		int i = -1;
		for (Account account : accounts) {
			if (account.getWebsite().toString().equals(website)
					&& account.getUserName().equals(accountName)) {
				i = accounts.indexOf(account);
			}
		}
		return accounts.get(i);
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public Account getAccount(int index) {
		return accounts.get(index);
	}

	public HashMap<String, ArrayList<Account>> getAccountMap() {
		HashMap<String, ArrayList<Account>> accountMap = new HashMap<String, ArrayList<Account>>();
		for (Account account : accounts) {
			if (accountMap.containsKey(account.getWebsite().toString())) {
				accountMap.get(account.getWebsite().toString()).add(account);
			} else {
				ArrayList<Account> temp = new ArrayList<Account>();
				temp.add(account);
				accountMap.put(account.getWebsite().toString(), temp);

			}
		}
		return accountMap;
	}

	public int getId() {
		return 1;
	}

	public void startExpirationTimer() {
		if (expirationTimer != null)
			expirationTimer.cancel();
		expirationTimer = new Timer();
		expirationTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (MainActivity.DEBUG_ACTIVATED) {
					Log.e("debug", "run task");
				}
				checkExpired();
				if (MainActivity.DEBUG_ACTIVATED) {
					Log.e("debug", "run task: check expire");
				}

			}
		}, 60000 * getConfiguration().getRememberTimeMinmutes(),
				60000 * getConfiguration().getRememberTimeMinmutes());
	}

	private void checkExpired() {
		ArrayList<Account> expiredAccounts = new ArrayList<Account>();
		for (final Account account : getAccounts()) {
			if (account.isExpired()) {
				expiredAccounts.add(account);
			}

		}
		if (expiredAccounts.size() > 0) {
			accountExpiredListener.accountsExpired(expiredAccounts);
		}

	}

}
