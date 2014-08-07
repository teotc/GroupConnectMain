package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.Schools;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SchoolsDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "schools";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_CATEGORY = "category";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public SchoolsDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public SchoolsDbAdapter open() throws SQLException {
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

	public void createSchools(int id, String name, String category,
			double latitude, double longitude) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_CATEGORY, category);
		cv.put(KEY_LATITUDE, latitude);
		cv.put(KEY_LONGITUDE, longitude);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteSchools(int id) {
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_NAME,
				KEY_CATEGORY, KEY_LATITUDE, KEY_LONGITUDE }, null, null, null,
				null, null);
	}

	public Cursor fetchAllSearchSchools(String category1, String category2, String category3, String subjectId) {
		String MY_QUERY = "Select s.id, s.name, s.category, s.latitude, s.longitude FROM schools s "
				+ "INNER JOIN member m ON s.id = m.schoolId "
				+ "INNER JOIN memberGrade mg ON mg.memberId = m.id "
				+ "WHERE (category = ? OR category = ? OR category = ?) AND subjectId=? "
				+ "GROUP BY s.id";

		return mDb.rawQuery(MY_QUERY, new String[] { category1, category2, category3, subjectId });
	}

	public boolean updateSchools(int id, String name, String category,
			double latitude, double longitude) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_CATEGORY, category);
		cv.put(KEY_LATITUDE, latitude);
		cv.put(KEY_LONGITUDE, longitude);

		return mDb.update(DATABASE_TABLE, cv, KEY_ID + "=" + id, null) > 0;
	}

	public void checkSchools(ArrayList<Schools> schoolsArray)
			throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < schoolsArray.size(); i++) {
				createSchools(schoolsArray.get(i).getId(), schoolsArray.get(i)
						.getName(), schoolsArray.get(i).getCategory(),
						schoolsArray.get(i).getLatitude(), schoolsArray.get(i)
								.getLongitude());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < schoolsArray.size(); i++) {
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == schoolsArray
						.get(i).getId()) {

					delete = 1;

					if (!(mCursor.getString(mCursor.getColumnIndex(KEY_NAME))
							.equals(schoolsArray.get(i).getName()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_CATEGORY))
									.equals(schoolsArray.get(i).getCategory()))
							|| mCursor.getDouble(mCursor
									.getColumnIndex(KEY_LATITUDE)) != schoolsArray
									.get(i).getLatitude()
							|| mCursor.getDouble(mCursor
									.getColumnIndex(KEY_LONGITUDE)) != schoolsArray
									.get(i).getLongitude()) {
						updateSchools(schoolsArray.get(i).getId(), schoolsArray
								.get(i).getName(), schoolsArray.get(i)
								.getCategory(), schoolsArray.get(i)
								.getLatitude(), schoolsArray.get(i)
								.getLongitude());
					}
				}
			}
			if (delete == 0) {
				deleteSchools(mCursor.getInt(mCursor.getColumnIndex(KEY_ID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < schoolsArray.size(); i++) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == schoolsArray
							.get(i).getId()) {

						delete = 1;

						if (!(mCursor.getString(mCursor
								.getColumnIndex(KEY_NAME)).equals(schoolsArray
								.get(i).getName()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_CATEGORY))
										.equals(schoolsArray.get(i)
												.getCategory()))
								|| mCursor.getDouble(mCursor
										.getColumnIndex(KEY_LATITUDE)) != schoolsArray
										.get(i).getLatitude()
								|| mCursor.getDouble(mCursor
										.getColumnIndex(KEY_LONGITUDE)) != schoolsArray
										.get(i).getLongitude()) {
							updateSchools(schoolsArray.get(i).getId(),
									schoolsArray.get(i).getName(), schoolsArray
											.get(i).getCategory(), schoolsArray
											.get(i).getLatitude(), schoolsArray
											.get(i).getLongitude());

						}
					}
				}
				if (delete == 0) {

					deleteSchools(mCursor
							.getInt(mCursor.getColumnIndex(KEY_ID)));
				}
			}
			for (int i = 0; i < schoolsArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (schoolsArray.get(i).getId() == mCursor.getInt(mCursor
						.getColumnIndex(KEY_ID))) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (schoolsArray.get(i).getId() == mCursor.getInt(mCursor
							.getColumnIndex(KEY_ID))) {
						create = 1;
					}
				}
				if (create == 0) {
					createSchools(schoolsArray.get(i).getId(), schoolsArray
							.get(i).getName(), schoolsArray.get(i)
							.getCategory(), schoolsArray.get(i).getLatitude(),
							schoolsArray.get(i).getLongitude());
				}
			}
		}
		mCursor.close();
		close();
	}
}
