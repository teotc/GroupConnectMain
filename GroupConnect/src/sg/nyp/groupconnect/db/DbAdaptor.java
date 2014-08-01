package sg.nyp.groupconnect.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdaptor {
	public static final String DATABASE_NAME = "test.db";
	public static final String DATABASE_TABLE = "groupConnectDb";
	public static final int DATABASE_VERSION = 1;
	private SQLiteDatabase _db;
	private final Context context;

	public static final String KEY_ID = "_id";
	public static final int COLUMN_KEY_ID = 0;

	public static final String ENTRY_LONGITUDE = "longitude";
	public static final int COLUMN_LONGITUDE_ID = 1;

	public static final String ENTRY_LATITUDE = "latitude";
	public static final int COLUMN_LATITUDE_ID = 2;

	public static final String ENTRY_AREA_NAME = "area_name";
	public static final int COLUMN_AREA_NAME_ID = 3;

	protected static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ "integer primary key autoincrement, " + ENTRY_LONGITUDE
			+ " Text, " + ENTRY_LATITUDE + " Text, " + ENTRY_AREA_NAME
			+ "Text);";

	private String DBADAPTOR_LOG_CAT = "MY_LOG";

	public class DBOpenHelper extends SQLiteOpenHelper {

		public DBOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			Log.w(DBADAPTOR_LOG_CAT, "Helper: DB " + DATABASE_TABLE
					+ " Created");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}// DBOpenHelper END

	private DBOpenHelper dbHelper;

	public DbAdaptor(Context _Context) {
		this.context = _Context;
		dbHelper = new DBOpenHelper(_Context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public void close() {
		_db.close();
		Log.w(DBADAPTOR_LOG_CAT, "DB Closed");
	}

	public void open() throws SQLiteException {
		try {
			_db = dbHelper.getWritableDatabase();
			Log.w(DBADAPTOR_LOG_CAT, "DB opened as writable database");
		} catch (SQLiteException e) {
			_db = dbHelper.getReadableDatabase();
			Log.w(DBADAPTOR_LOG_CAT, "DB opened as writable database");
		}
	}

	public long insertEntry(String longitude, String latitude, String areaName) {
		// Create a new record
		ContentValues newEntryValues = new ContentValues();

		// Assign values for each row
		newEntryValues.put(ENTRY_LONGITUDE, longitude);
		newEntryValues.put(ENTRY_LATITUDE, latitude);
		newEntryValues.put(ENTRY_AREA_NAME, areaName);

		// Insert the row
		Log.w(DBADAPTOR_LOG_CAT, "Inserted LONGITUDE = " + longitude
				+ " LATITUDE = " + latitude + " AREA_NAME = " + areaName
				+ "into table" + DATABASE_TABLE);

		return _db.insert(DATABASE_TABLE, null, newEntryValues);
	}

	public boolean removeEntry(long _rowIndex) {
		if (_db.delete(DATABASE_TABLE, KEY_ID + " = " + _rowIndex, null) <= 0) {
			Log.w(DBADAPTOR_LOG_CAT, "Removing entry where id = " + _rowIndex
					+ "Failed");
			return false;
		}

		Log.w(DBADAPTOR_LOG_CAT, "Removing entry where id = " + _rowIndex
				+ "Success");
		return true;
	}

	public Cursor retrieveAllEntriesCursor() {
		Cursor c = null;

		try {
			c = _db.query(DATABASE_TABLE, new String[] { KEY_ID,
					ENTRY_LONGITUDE, ENTRY_LATITUDE, ENTRY_AREA_NAME }, null,
					null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(DBADAPTOR_LOG_CAT, "Retrieve fail");
		}
		
		return c;
	}

}
