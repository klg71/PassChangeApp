package sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlLiteConnector extends SQLiteOpenHelper {
	
	public SqlLiteConnector(Context context) {
		super(context, DATABASE_NAME, null,DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public static final String DATABASE_NAME = "accounts.ds";
	public static final int DATABASE_VERSION= 1;
	public static final String TABLE_ACCOUNTS = "accounts";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_PASS = "pass";
	public static final String COLUMN_WEBSITE = "website";
	public static final String COLUMN_EXPIRE = "expire";
	public static final String COLUMN_LASTCHANGED = "changed";

	private SQLiteDatabase database;
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_ACCOUNTS + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " text not null, " + COLUMN_EMAIL + " text not null, "
			+ COLUMN_PASS + " text not null, " + COLUMN_WEBSITE + " text not nulls, "
			+ COLUMN_EXPIRE + " integer, " + COLUMN_LASTCHANGED
			+ " timestamp);";

	@Override
	public void onCreate(SQLiteDatabase db) {
		database=db;
		db.execSQL(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SqlLiteConnector.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
		    onCreate(db);
		
	}
	
}
