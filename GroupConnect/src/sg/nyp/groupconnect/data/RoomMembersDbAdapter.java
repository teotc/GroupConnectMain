package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.RoomMembers;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class RoomMembersDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "RoomMembers";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_ROOMID = "room_id";
	private static final String KEY_MEMBERID = "memberId";
	private static final String KEY_MEMBERTYPE = "memberType";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public RoomMembersDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public RoomMembersDbAdapter open() throws SQLException {
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

	public void createRoomMembers(int room_id, int memberId, String memberType) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOMID, room_id);
		cv.put(KEY_MEMBERID, memberId);
		cv.put(KEY_MEMBERTYPE, memberType);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteRoomMembers(int room_id, int memberId) {
		return mDb.delete(DATABASE_TABLE, KEY_ROOMID + " = " + room_id
				+ " AND " + KEY_MEMBERID + " = " + memberId, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROOMID,
				KEY_MEMBERID, KEY_MEMBERTYPE }, null, null, null, null, null);
	}

	public boolean updateRoomMembers(int room_id, int memberId,
			String memberType) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOMID, room_id);
		cv.put(KEY_MEMBERID, memberId);
		cv.put(KEY_MEMBERTYPE, memberType);

		return mDb.update(DATABASE_TABLE, cv, KEY_ROOMID + " = " + room_id
				+ " AND " + KEY_MEMBERID + " = " + memberId, null) > 0;
	}

	public void checkRoomMember(ArrayList<RoomMembers> roomMembersArray)
			throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < roomMembersArray.size(); i++) {
				createRoomMembers(roomMembersArray.get(i).getRoom_id(),
						roomMembersArray.get(i).getMemberId(), roomMembersArray
								.get(i).getMemberType());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < roomMembersArray.size(); i++) {
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)) == roomMembersArray
						.get(i).getRoom_id()
						&& mCursor.getInt(mCursor.getColumnIndex(KEY_MEMBERID)) == roomMembersArray
								.get(i).getMemberId()) {

					delete = 1;

					if (!(mCursor.getString(mCursor
							.getColumnIndex(KEY_MEMBERTYPE))
							.equals(roomMembersArray.get(i).getMemberType()))) {
						updateRoomMembers(roomMembersArray.get(i).getRoom_id(),
								roomMembersArray.get(i).getMemberId(),
								roomMembersArray.get(i).getMemberType());
					}
				}
			}
			if (delete == 0) {
				deleteRoomMembers(
						mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)),
						mCursor.getInt(mCursor.getColumnIndex(KEY_MEMBERID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < roomMembersArray.size(); i++) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)) == roomMembersArray
							.get(i).getRoom_id()
							&& mCursor.getInt(mCursor
									.getColumnIndex(KEY_MEMBERID)) == roomMembersArray
									.get(i).getMemberId()) {

						delete = 1;

						if (!(mCursor.getString(mCursor
								.getColumnIndex(KEY_MEMBERTYPE))
								.equals(roomMembersArray.get(i).getMemberType()))) {
							updateRoomMembers(roomMembersArray.get(i)
									.getRoom_id(), roomMembersArray.get(i)
									.getMemberId(), roomMembersArray.get(i)
									.getMemberType());
						}
					}
				}
				if (delete == 0) {

					deleteRoomMembers(
							mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)),
							mCursor.getInt(mCursor.getColumnIndex(KEY_MEMBERID)));
				}
			}
			for (int i = 0; i < roomMembersArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)) == roomMembersArray
						.get(i).getRoom_id()
						&& mCursor.getInt(mCursor.getColumnIndex(KEY_MEMBERID)) == roomMembersArray
								.get(i).getMemberId()) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)) == roomMembersArray
							.get(i).getRoom_id()
							&& mCursor.getInt(mCursor
									.getColumnIndex(KEY_MEMBERID)) == roomMembersArray
									.get(i).getMemberId()) {
						create = 1;
					}
				}
				if (create == 0) {
					createRoomMembers(roomMembersArray.get(i).getRoom_id(),
							roomMembersArray.get(i).getMemberId(),
							roomMembersArray.get(i).getMemberType());
				}
			}
		}
		mCursor.close();
		close();
	}
}
