package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.MemberGrades;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MemberGradeDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "memberGrade";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_MEMBERID = "memberId";
	private static final String KEY_SUBJECTID = "subjectId";
	private static final String KEY_OLDGRADE = "oldGrade";
	private static final String KEY_NEWGRADE = "newGrade";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public MemberGradeDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public MemberGradeDbAdapter open() throws SQLException {
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

	public void createMemberGrade(String memberId, String subjectId,
			double oldGrade, double newGrade) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_MEMBERID, memberId);
		cv.put(KEY_SUBJECTID, subjectId);
		cv.put(KEY_OLDGRADE, oldGrade);
		cv.put(KEY_NEWGRADE, newGrade);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteMemberGrade(String memberId, String subjectId) {
		return mDb.delete(DATABASE_TABLE, KEY_MEMBERID + " = " + memberId
				+ " AND " + KEY_SUBJECTID + " = " + subjectId, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_MEMBERID,
				KEY_SUBJECTID, KEY_OLDGRADE, KEY_NEWGRADE }, null, null, null,
				null, null);
	}

	public boolean updateMemberGrade(String memberId, String subjectId,
			double oldGrade, double newGrade) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_MEMBERID, memberId);
		cv.put(KEY_SUBJECTID, subjectId);
		cv.put(KEY_OLDGRADE, oldGrade);
		cv.put(KEY_NEWGRADE, newGrade);

		return mDb.update(DATABASE_TABLE, cv, KEY_MEMBERID + " = " + memberId
				+ " AND " + KEY_SUBJECTID + " = " + subjectId, null) > 0;
	}

	public void checkMemberGrade(ArrayList<MemberGrades> memberGradeArray)
			throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < memberGradeArray.size(); i++) {
				createMemberGrade(memberGradeArray.get(i).getMemberId(),
						memberGradeArray.get(i).getSubjectId(),
						memberGradeArray.get(i).getOldGrade(), memberGradeArray
								.get(i).getNewGrade());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < memberGradeArray.size(); i++) {
				if (mCursor.getString(mCursor.getColumnIndex(KEY_MEMBERID))
						.equals(memberGradeArray.get(i).getMemberId())
						&& mCursor.getString(
								mCursor.getColumnIndex(KEY_SUBJECTID)).equals(
								memberGradeArray.get(i).getSubjectId())) {

					delete = 1;

					if (mCursor.getDouble(mCursor.getColumnIndex(KEY_NEWGRADE)) != memberGradeArray
							.get(i).getNewGrade()
							|| mCursor.getDouble(mCursor
									.getColumnIndex(KEY_OLDGRADE)) != memberGradeArray
									.get(i).getOldGrade()) {
						updateMemberGrade(
								memberGradeArray.get(i).getMemberId(),
								memberGradeArray.get(i).getSubjectId(),
								memberGradeArray.get(i).getOldGrade(),
								memberGradeArray.get(i).getNewGrade());
					}
				}
			}
			if (delete == 0) {
				deleteMemberGrade(mCursor.getString(mCursor
						.getColumnIndex(KEY_MEMBERID)), mCursor
						.getString(mCursor.getColumnIndex(KEY_SUBJECTID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < memberGradeArray.size(); i++) {
					if (mCursor.getString(mCursor.getColumnIndex(KEY_MEMBERID))
							.equals(memberGradeArray.get(i).getMemberId())
							&& mCursor.getString(
									mCursor.getColumnIndex(KEY_SUBJECTID))
									.equals(memberGradeArray.get(i)
											.getSubjectId())) {

						delete = 1;

						if (mCursor.getDouble(mCursor
								.getColumnIndex(KEY_NEWGRADE)) != memberGradeArray
								.get(i).getNewGrade()
								|| mCursor.getDouble(mCursor
										.getColumnIndex(KEY_OLDGRADE)) != memberGradeArray
										.get(i).getOldGrade()) {
							updateMemberGrade(memberGradeArray.get(i)
									.getMemberId(), memberGradeArray.get(i)
									.getSubjectId(), memberGradeArray.get(i)
									.getOldGrade(), memberGradeArray.get(i)
									.getNewGrade());
						}
					}
				}
				if (delete == 0) {

					deleteMemberGrade(mCursor.getString(mCursor
							.getColumnIndex(KEY_MEMBERID)),
							mCursor.getString(mCursor
									.getColumnIndex(KEY_SUBJECTID)));
				}
			}
			for (int i = 0; i < memberGradeArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (mCursor.getString(mCursor.getColumnIndex(KEY_MEMBERID))
						.equals(memberGradeArray.get(i).getMemberId())
						&& mCursor.getString(
								mCursor.getColumnIndex(KEY_SUBJECTID)).equals(
								memberGradeArray.get(i).getSubjectId())) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (mCursor.getString(mCursor.getColumnIndex(KEY_MEMBERID))
							.equals(memberGradeArray.get(i).getMemberId())
							&& mCursor.getString(
									mCursor.getColumnIndex(KEY_SUBJECTID))
									.equals(memberGradeArray.get(i)
											.getSubjectId())) {
						create = 1;
					}
				}
				if (create == 0) {
					createMemberGrade(memberGradeArray.get(i).getMemberId(),
							memberGradeArray.get(i).getSubjectId(),
							memberGradeArray.get(i).getOldGrade(),
							memberGradeArray.get(i).getNewGrade());
				}
			}
		}
		mCursor.close();
		close();
	}
}
