package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.Member;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MemberDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "member";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_LOCATION = "location";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_GENDER = "gender";
	private static final String KEY_SCHOOLID = "schoolId";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_TYPE = "type";
	private static final String KEY_DEVICE = "device";
	private static final String KEY_INTERESTEDSUB = "interestedSub";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public MemberDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public MemberDbAdapter open() throws SQLException {
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

	public void createMember(int id, String name, String location,
			double latitude, double longitude, String gender, int schoolId,
			String password, String type, String device, String interestedSub) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATITUDE, latitude);
		cv.put(KEY_LONGITUDE, longitude);
		cv.put(KEY_GENDER, gender);
		cv.put(KEY_SCHOOLID, schoolId);
		cv.put(KEY_PASSWORD, password);
		cv.put(KEY_TYPE, type);
		cv.put(KEY_DEVICE, device);
		cv.put(KEY_INTERESTEDSUB, interestedSub);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteMember(int id) {
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_NAME,
				KEY_LOCATION, KEY_LATITUDE, KEY_LONGITUDE, KEY_GENDER,
				KEY_SCHOOLID, KEY_PASSWORD, KEY_TYPE, KEY_DEVICE,
				KEY_INTERESTEDSUB }, null, null, null, null, null);
	}

	public Cursor fetchMemberGradeSchool(String schoolId, String subjectId) {
		String MY_QUERY = "SELECT * FROM member INNER JOIN memberGrade ON member.id = memberGrade.memberId "
				+ "WHERE schoolId = ? AND subjectId = ? AND type='Learner';";

		return mDb.rawQuery(MY_QUERY, new String[] { schoolId, subjectId });
	}

	public boolean updateMember(int id, String name, String location,
			double latitude, double longitude, String gender, int schoolId,
			String password, String type, String device, String interestedSub) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATITUDE, latitude);
		cv.put(KEY_LONGITUDE, longitude);
		cv.put(KEY_GENDER, gender);
		cv.put(KEY_SCHOOLID, schoolId);
		cv.put(KEY_PASSWORD, password);
		cv.put(KEY_TYPE, type);
		cv.put(KEY_DEVICE, device);
		cv.put(KEY_INTERESTEDSUB, interestedSub);

		return mDb.update(DATABASE_TABLE, cv, KEY_ID + "=" + id, null) > 0;
	}

	public void checkMember(ArrayList<Member> memberArray) throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < memberArray.size(); i++) {
				createMember(memberArray.get(i).getId(), memberArray.get(i)
						.getName(), memberArray.get(i).getLocation(),
						memberArray.get(i).getLatitude(), memberArray.get(i)
								.getLongitude(),
						memberArray.get(i).getGender(), memberArray.get(i)
								.getSchoolId(), memberArray.get(i)
								.getPassword(), memberArray.get(i).getType(),
						memberArray.get(i).getDevice(), memberArray.get(i)
								.getInterestedSub());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < memberArray.size(); i++) {
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == memberArray
						.get(i).getId()) {

					delete = 1;

					if (!(mCursor.getString(mCursor.getColumnIndex(KEY_NAME))
							.equals(memberArray.get(i).getName()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_LOCATION))
									.equals(memberArray.get(i).getLocation()))
							|| mCursor.getDouble(mCursor
									.getColumnIndex(KEY_LATITUDE)) != memberArray
									.get(i).getLatitude()
							|| mCursor.getDouble(mCursor
									.getColumnIndex(KEY_LONGITUDE)) != memberArray
									.get(i).getLongitude()
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_GENDER))
									.equals(memberArray.get(i).getGender()))
							|| mCursor.getDouble(mCursor
									.getColumnIndex(KEY_SCHOOLID)) != memberArray
									.get(i).getSchoolId()
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_PASSWORD))
									.equals(memberArray.get(i).getPassword()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_TYPE))
									.equals(memberArray.get(i).getType()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_DEVICE))
									.equals(memberArray.get(i).getDevice()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_INTERESTEDSUB))
									.equals(memberArray.get(i)
											.getInterestedSub()))) {
						updateMember(memberArray.get(i).getId(), memberArray
								.get(i).getName(), memberArray.get(i)
								.getLocation(), memberArray.get(i)
								.getLatitude(), memberArray.get(i)
								.getLongitude(),
								memberArray.get(i).getGender(), memberArray
										.get(i).getSchoolId(),
								memberArray.get(i).getPassword(), memberArray
										.get(i).getType(), memberArray.get(i)
										.getDevice(), memberArray.get(i)
										.getInterestedSub());
					}
				}
			}
			if (delete == 0) {
				deleteMember(mCursor.getInt(mCursor.getColumnIndex(KEY_ID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < memberArray.size(); i++) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == memberArray
							.get(i).getId()) {

						delete = 1;

						if (!(mCursor.getString(mCursor
								.getColumnIndex(KEY_NAME)).equals(memberArray
								.get(i).getName()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_LOCATION))
										.equals(memberArray.get(i)
												.getLocation()))
								|| mCursor.getDouble(mCursor
										.getColumnIndex(KEY_LATITUDE)) != memberArray
										.get(i).getLatitude()
								|| mCursor.getDouble(mCursor
										.getColumnIndex(KEY_LONGITUDE)) != memberArray
										.get(i).getLongitude()
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_GENDER))
										.equals(memberArray.get(i).getGender()))
								|| mCursor.getDouble(mCursor
										.getColumnIndex(KEY_SCHOOLID)) != memberArray
										.get(i).getSchoolId()
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_PASSWORD))
										.equals(memberArray.get(i)
												.getPassword()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_TYPE))
										.equals(memberArray.get(i).getType()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_DEVICE))
										.equals(memberArray.get(i).getDevice()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_INTERESTEDSUB))
										.equals(memberArray.get(i)
												.getInterestedSub()))) {
							updateMember(memberArray.get(i).getId(),
									memberArray.get(i).getName(), memberArray
											.get(i).getLocation(), memberArray
											.get(i).getLatitude(), memberArray
											.get(i).getLongitude(), memberArray
											.get(i).getGender(), memberArray
											.get(i).getSchoolId(), memberArray
											.get(i).getPassword(), memberArray
											.get(i).getType(),
									memberArray.get(i).getDevice(), memberArray
											.get(i).getInterestedSub());

						}
					}
				}
				if (delete == 0) {

					deleteMember(mCursor.getInt(mCursor.getColumnIndex(KEY_ID)));
				}
			}
			for (int i = 0; i < memberArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (memberArray.get(i).getId() == mCursor.getInt(mCursor
						.getColumnIndex(KEY_ID))) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (memberArray.get(i).getId() == mCursor.getInt(mCursor
							.getColumnIndex(KEY_ID))) {
						create = 1;
					}
				}
				if (create == 0) {
					createMember(memberArray.get(i).getId(), memberArray.get(i)
							.getName(), memberArray.get(i).getLocation(),
							memberArray.get(i).getLatitude(), memberArray
									.get(i).getLongitude(), memberArray.get(i)
									.getGender(), memberArray.get(i)
									.getSchoolId(), memberArray.get(i)
									.getPassword(), memberArray.get(i)
									.getType(), memberArray.get(i).getDevice(),
							memberArray.get(i).getInterestedSub());
				}
			}
		}

		mCursor.close();
		close();
	}
}
