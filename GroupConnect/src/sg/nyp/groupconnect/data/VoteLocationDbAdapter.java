package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.VoteLocation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class VoteLocationDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "voteLocation";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_MEMBERID = "memberId";
	private static final String KEY_ROOMID = "roomId";
	private static final String KEY_LOCATIONID = "locationId";
	private static final String KEY_STATUS = "status";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public VoteLocationDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public VoteLocationDbAdapter open() throws SQLException {
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

	public void createVoteLocation(String memberId, String roomId,
			String locationId, String status) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_MEMBERID, memberId);
		cv.put(KEY_ROOMID, roomId);
		cv.put(KEY_LOCATIONID, locationId);
		cv.put(KEY_STATUS, status);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteVoteLocation(String memberId, String roomId,
			String locationId) {
		return mDb.delete(DATABASE_TABLE, KEY_MEMBERID + " = " + memberId
				+ " AND " + KEY_ROOMID + " = " + roomId + " AND "
				+ KEY_LOCATIONID + " = " + locationId, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_MEMBERID,
				KEY_ROOMID, KEY_LOCATIONID, KEY_STATUS }, null, null, null,
				null, null);
	}

	public Cursor fetchAllRoomVote(String roomId) {
		String MY_QUERY = "SELECT memberId FROM votelocation WHERE roomId=?;";

		return mDb.rawQuery(MY_QUERY, new String[] { roomId });
	}

	public Cursor fetchMemberRoomVote(String memberId, String roomId) {
		String MY_QUERY = "SELECT * FROM voteLocation v INNER JOIN availableLocation a ON v.locationId = a.id WHERE memberId = ? AND roomId = ? ;";

		return mDb.rawQuery(MY_QUERY, new String[] { memberId, roomId });
	}

	public Cursor fetchVoteResult(String roomId) {
		String MY_QUERY = "SELECT locationId, COUNT(locationId) AS countValue, a.name, a.latitude, a.longitude, v.status, a.location FROM voteLocation v "
				+ "INNER JOIN availableLocation a ON v.locationId = a.id  "
				+ "WHERE roomId = ?  GROUP BY v.locationId ;";

		return mDb.rawQuery(MY_QUERY, new String[] { roomId });
	}

	public boolean updateVoteLocation(String memberId, String roomId,
			String locationId, String status) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_MEMBERID, memberId);
		cv.put(KEY_ROOMID, roomId);
		cv.put(KEY_LOCATIONID, locationId);
		cv.put(KEY_STATUS, status);

		return mDb.update(DATABASE_TABLE, cv, KEY_MEMBERID + " = " + memberId
				+ " AND " + KEY_ROOMID + " = " + roomId + " AND "
				+ KEY_LOCATIONID + " = " + locationId, null) > 0;
	}

	public boolean updateVoteLocation(String locationId, String roomId) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOMID, roomId);
		cv.put(KEY_LOCATIONID, locationId);
		cv.put(KEY_STATUS, "final");

		return mDb.update(DATABASE_TABLE, cv, KEY_ROOMID + " = " + roomId + " AND "
				+ KEY_LOCATIONID + " = " + locationId, null) > 0;
	}

	
	public void checkVoteLocation(ArrayList<VoteLocation> voteLocationArray)
			throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < voteLocationArray.size(); i++) {
				createVoteLocation(voteLocationArray.get(i).getMemberId(),
						voteLocationArray.get(i).getRoomId(), voteLocationArray
								.get(i).getLocationId(),
						voteLocationArray.get(i).getStatus());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < voteLocationArray.size(); i++) {
				if (mCursor.getString(mCursor.getColumnIndex(KEY_MEMBERID))
						.equals(voteLocationArray.get(i).getMemberId())
						&& mCursor
								.getString(mCursor.getColumnIndex(KEY_ROOMID))
								.equals(voteLocationArray.get(i).getRoomId())
						&& mCursor.getString(
								mCursor.getColumnIndex(KEY_LOCATIONID)).equals(
								voteLocationArray.get(i).getLocationId())) {

					delete = 1;

					if (!(mCursor.getString(mCursor.getColumnIndex(KEY_STATUS))
							.equals(voteLocationArray.get(i).getStatus()))) {
						updateVoteLocation(voteLocationArray.get(i)
								.getMemberId(), voteLocationArray.get(i)
								.getRoomId(), voteLocationArray.get(i)
								.getLocationId(), voteLocationArray.get(i)
								.getStatus());
					}
				}
			}
			if (delete == 0) {
				deleteVoteLocation(mCursor.getString(mCursor
						.getColumnIndex(KEY_MEMBERID)), mCursor
						.getString(mCursor.getColumnIndex(KEY_ROOMID)), mCursor
						.getString(mCursor.getColumnIndex(KEY_LOCATIONID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < voteLocationArray.size(); i++) {
					if (mCursor.getString(mCursor.getColumnIndex(KEY_MEMBERID))
							.equals(voteLocationArray.get(i).getMemberId())
							&& mCursor.getString(
									mCursor.getColumnIndex(KEY_ROOMID)).equals(
									voteLocationArray.get(i).getRoomId())
							&& mCursor.getString(
									mCursor.getColumnIndex(KEY_LOCATIONID))
									.equals(voteLocationArray.get(i)
											.getLocationId())) {

						delete = 1;

						if (!(mCursor.getString(mCursor
								.getColumnIndex(KEY_STATUS))
								.equals(voteLocationArray.get(i).getStatus()))) {
							updateVoteLocation(voteLocationArray.get(i)
									.getMemberId(), voteLocationArray.get(i)
									.getRoomId(), voteLocationArray.get(i)
									.getLocationId(), voteLocationArray.get(i)
									.getStatus());

						}
					}
				}
				if (delete == 0) {

					deleteVoteLocation(mCursor.getString(mCursor
							.getColumnIndex(KEY_MEMBERID)),
							mCursor.getString(mCursor
									.getColumnIndex(KEY_ROOMID)),
							mCursor.getString(mCursor
									.getColumnIndex(KEY_LOCATIONID)));
				}
			}
			for (int i = 0; i < voteLocationArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (mCursor.getString(mCursor.getColumnIndex(KEY_MEMBERID))
						.equals(voteLocationArray.get(i).getMemberId())
						&& mCursor
								.getString(mCursor.getColumnIndex(KEY_ROOMID))
								.equals(voteLocationArray.get(i).getRoomId())
						&& mCursor.getString(
								mCursor.getColumnIndex(KEY_LOCATIONID)).equals(
								voteLocationArray.get(i).getLocationId())) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (mCursor.getString(mCursor.getColumnIndex(KEY_MEMBERID))
							.equals(voteLocationArray.get(i).getMemberId())
							&& mCursor.getString(
									mCursor.getColumnIndex(KEY_ROOMID)).equals(
									voteLocationArray.get(i).getRoomId())
							&& mCursor.getString(
									mCursor.getColumnIndex(KEY_LOCATIONID))
									.equals(voteLocationArray.get(i)
											.getLocationId())) {
						create = 1;
					}
				}
				if (create == 0) {
					createVoteLocation(voteLocationArray.get(i).getMemberId(),
							voteLocationArray.get(i).getRoomId(),
							voteLocationArray.get(i).getLocationId(),
							voteLocationArray.get(i).getStatus());
				}
			}
		}
		mCursor.close();
		close();
	}
}
