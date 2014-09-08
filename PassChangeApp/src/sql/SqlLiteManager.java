package sql;

import generator.Crypt;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import core.Website;
import account.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SqlLiteManager {
	private SqlLiteConnector connector;
	private SQLiteDatabase database;
	private HashMap<String, Website> websites;
	private String pass;
	private String[] allCollumns = { SqlLiteConnector.COLUMN_ID,
			SqlLiteConnector.COLUMN_NAME, SqlLiteConnector.COLUMN_EMAIL,
			SqlLiteConnector.COLUMN_PASS, SqlLiteConnector.COLUMN_WEBSITE,
			SqlLiteConnector.COLUMN_EXPIRE, SqlLiteConnector.COLUMN_LASTCHANGED };

	public SqlLiteManager(Context context, HashMap<String, Website> websites,
			String pass) {
		this.pass = pass;
		connector = new SqlLiteConnector(context);
		this.websites = websites;
	}

	public void open() throws SQLException {
		database = connector.getWritableDatabase();
	}

	public void close() {
		connector.close();
	}

	public ArrayList<Account> getAccounts() {
		ArrayList<Account> accounts = new ArrayList<Account>();

		Cursor cursor = database.query(SqlLiteConnector.TABLE_ACCOUNTS,
				allCollumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Account account = cursorToAccount(cursor);
			accounts.add(account);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		return accounts;
	}

	public void saveAccounts(ArrayList<Account> accounts) {

		for (Account account : accounts) {
			database.execSQL("TRUNCATE TABLE "
					+ SqlLiteConnector.TABLE_ACCOUNTS + ";");
			StringWriter temp = new StringWriter();
			ContentValues values = new ContentValues();
			String tempString = "";
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try {
				Crypt.encode(account.getUserName().getBytes(),
						byteArrayOutputStream, pass);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			values.put(SqlLiteConnector.COLUMN_NAME,
					byteArrayOutputStream.toString());
			try {
				Crypt.encode(account.getEmail().getBytes(),
						byteArrayOutputStream, pass);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			values.put(SqlLiteConnector.COLUMN_EMAIL,
					byteArrayOutputStream.toString());
			try {
				Crypt.encode(account.getActualPassword().getBytes(),
						byteArrayOutputStream, pass);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			values.put(SqlLiteConnector.COLUMN_PASS,
					byteArrayOutputStream.toString());
			values.put(SqlLiteConnector.COLUMN_EXPIRE, account.getExpire());
			values.put(SqlLiteConnector.COLUMN_WEBSITE, account.getWebsite()
					.getName());
			values.put(SqlLiteConnector.COLUMN_LASTCHANGED, account
					.getLastChangedCalendar().getTime().toString());

			long insertId = database.insert(SqlLiteConnector.TABLE_ACCOUNTS,
					null, values);
			Cursor cursor = database.query(SqlLiteConnector.TABLE_ACCOUNTS,
					allCollumns, SqlLiteConnector.COLUMN_ID + " = " + insertId,
					null, null, null, null);
			cursor.moveToFirst();
			Account newComment = cursorToAccount(cursor);
			cursor.close();
		}
	}

	private Account cursorToAccount(Cursor cursor) {
		Calendar tempCalendar = Calendar.getInstance();
		Date tempDate = new Date(cursor.getString(6));
		tempCalendar.setTime(tempDate);
		Account account = new Account(cursor.getString(1), cursor.getString(2),
				cursor.getString(3), tempCalendar, websites.get(cursor
						.getString(4)), cursor.getInt(5));
		return account;

	}
}
