package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.AvailableLocation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AvailableLocationDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "availableLocation";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_LOCATION = "location";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public AvailableLocationDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public AvailableLocationDbAdapter open() throws SQLException {
		mDbHelper = new MainDbAdapter(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDb.close();
		mDbHelper.close();
	}

	public void updateTable() {
		mDbHelper.onUpgrade(mDb, DATABASE_VERSION, DATABASE_VERSION);
	}

	public void createTable() {
		mDbHelper.onCreate(mDb);
	}

	public void createAvailableLocation(int id, String name, String location,
			double latitude, double longitude) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATITUDE, latitude);
		cv.put(KEY_LONGITUDE, longitude);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteAvailableLocation(int id) {
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_NAME,
				KEY_LOCATION, KEY_LATITUDE, KEY_LONGITUDE }, null, null, null,
				null, null);
	}

	public boolean updateAvailableLocation(int id, String name, String location,
			double latitude, double longitude) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATITUDE, latitude);
		cv.put(KEY_LONGITUDE, longitude);

		return mDb.update(DATABASE_TABLE, cv, KEY_ID + "=" + id, null) > 0;
	}

	public void checkAvailableLocation(
			ArrayList<AvailableLocation> availableLocationArray)
			throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < availableLocationArray.size(); i++) {
				createAvailableLocation(availableLocationArray.get(i).getId(),
						availableLocationArray.get(i).getName(),
						availableLocationArray.get(i).getlocation(),
						availableLocationArray.get(i).getLatitude(),
						availableLocationArray.get(i).getLongitude());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < availableLocationArray.size(); i++) {
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == availableLocationArray
						.get(i).getId()) {

					delete = 1;

					if (!(mCursor.getString(mCursor
							.getColumnIndex(KEY_NAME))
							.equals(availableLocationArray.get(i).getName()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_LOCATION))
									.equals(availableLocationArray.get(i)
											.getlocation()))
							|| mCursor.getDouble(mCursor
									.getColumnIndex(KEY_LATITUDE)) != availableLocationArray
									.get(i).getLatitude()
							|| mCursor.getDouble(mCursor
									.getColumnIndex(KEY_LONGITUDE)) != availableLocationArray
									.get(i).getLongitude()) {
						updateAvailableLocation(availableLocationArray.get(i).getId(),
								availableLocationArray.get(i).getName(),
								availableLocationArray.get(i).getlocation(),
								availableLocationArray.get(i).getLatitude(),
								availableLocationArray.get(i).getLongitude());
					}
				}
			}
			if (delete == 0) {
				deleteAvailableLocation(mCursor.getInt(mCursor
						.getColumnIndex(KEY_ID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < availableLocationArray.size(); i++) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == availableLocationArray
							.get(i).getId()) {

						delete = 1;

						if (!(mCursor.getString(mCursor
								.getColumnIndex(KEY_NAME))
								.equals(availableLocationArray.get(i).getName()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_LOCATION))
										.equals(availableLocationArray.get(i)
												.getlocation()))
								|| mCursor.getDouble(mCursor
										.getColumnIndex(KEY_LATITUDE)) != availableLocationArray
										.get(i).getLatitude()
								|| mCursor.getDouble(mCursor
										.getColumnIndex(KEY_LONGITUDE)) != availableLocationArray
										.get(i).getLongitude()) {
							updateAvailableLocation(
									availableLocationArray.get(i).getId(),
									availableLocationArray.get(i).getName(),
									availableLocationArray.get(i).getlocation(),
									availableLocationArray.get(i).getLatitude(),
									availableLocationArray.get(i)
											.getLongitude());

						}
					}
				}
				if (delete == 0) {

					deleteAvailableLocation(mCursor.getInt(mCursor
							.getColumnIndex(KEY_ID)));
				}
			}
			for (int i = 0; i < availableLocationArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (availableLocationArray.get(i).getId() == mCursor
						.getInt(mCursor.getColumnIndex(KEY_ID))) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (availableLocationArray.get(i).getId() == mCursor
							.getInt(mCursor.getColumnIndex(KEY_ID))) {
						create = 1;
					}
				}
				if (create == 0) {
					createAvailableLocation(availableLocationArray.get(i)
							.getId(), availableLocationArray.get(i).getName(),
							availableLocationArray.get(i).getlocation(),
							availableLocationArray.get(i).getLatitude(),
							availableLocationArray.get(i).getLongitude());
				}
			}
		}
		mCursor.close();
		close();
	}
}
