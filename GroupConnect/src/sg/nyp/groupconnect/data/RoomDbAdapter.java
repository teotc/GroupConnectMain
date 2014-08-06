package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.Room;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class RoomDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "Room";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_ROOMID = "room_id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CATEGORY = "category";
	private static final String KEY_NOOFLEARNER = "noOfLearner";
	private static final String KEY_LOCATION = "location";
	private static final String KEY_LATLNG = "latLng";
	private static final String KEY_CREATORID = "creatorId";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_STATUS = "status";
	private static final String KEY_DATEFROM = "dateFrom";
	private static final String KEY_DATETO = "dateTo";
	private static final String KEY_TIMEFROM = "timeFrom";
	private static final String KEY_TIMETO = "timeTo";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public RoomDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public RoomDbAdapter open() throws SQLException {
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

	public void createRoom(int room_id, String title, String category,
			int noOfLearner, String location, String latLng, int creatorId,
			String description, String status, String dateFrom, String dateTo,
			String timeFrom, String timeTo) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOMID, room_id);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_CATEGORY, category);
		cv.put(KEY_NOOFLEARNER, noOfLearner);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATLNG, latLng);
		cv.put(KEY_CREATORID, creatorId);
		cv.put(KEY_DESCRIPTION, description);
		cv.put(KEY_STATUS, status);
		cv.put(KEY_DATEFROM, dateFrom);
		cv.put(KEY_DATETO, dateTo);
		cv.put(KEY_TIMEFROM, timeFrom);
		cv.put(KEY_TIMETO, timeTo);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteRoom(int room_id) {
		return mDb.delete(DATABASE_TABLE, KEY_ROOMID + " = " + room_id, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROOMID, KEY_TITLE,
				KEY_CATEGORY, KEY_NOOFLEARNER, KEY_LOCATION, KEY_LATLNG,
				KEY_CREATORID, KEY_DESCRIPTION, KEY_STATUS, KEY_DATEFROM,
				KEY_DATETO, KEY_TIMEFROM, KEY_TIMETO }, null, null, null, null,
				null);
	}

	public Cursor fetchCreatedRoom(String creatorId, String category) {
		String MY_QUERY = "SELECT * FROM Room WHERE creatorId=? AND status = 'Not Started' AND category=?;";

		return mDb.rawQuery(MY_QUERY, new String[] { creatorId, category });
	}
	
	public Cursor fetchRoom(String room_id) {
		String MY_QUERY = "SELECT * FROM Room INNER JOIN RoomMembers ON Room.room_id = RoomMembers.room_id "
				+ "INNER JOIN member ON RoomMembers.memberId = member.id WHERE Room.room_id=?;";

		return mDb.rawQuery(MY_QUERY, new String[] { room_id });
	}
	
	public boolean updateRoom(int room_id, String title, String category,
			int noOfLearner, String location, String latLng, int creatorId,
			String description, String status, String dateFrom, String dateTo,
			String timeFrom, String timeTo) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOMID, room_id);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_CATEGORY, category);
		cv.put(KEY_NOOFLEARNER, noOfLearner);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATLNG, latLng);
		cv.put(KEY_CREATORID, creatorId);
		cv.put(KEY_DESCRIPTION, description);
		cv.put(KEY_STATUS, status);
		cv.put(KEY_DATEFROM, dateFrom);
		cv.put(KEY_DATETO, dateTo);
		cv.put(KEY_TIMEFROM, timeFrom);
		cv.put(KEY_TIMETO, timeTo);

		return mDb.update(DATABASE_TABLE, cv, KEY_ROOMID + " = " + room_id,
				null) > 0;
	}

	public Cursor updateRoom(String location, String latLng, String room_id) {
		String MY_QUERY = "UPDATE Room SET location=?, latLng =? WHERE room_id=?;";

		return mDb.rawQuery(MY_QUERY, new String[] { location, latLng, room_id });
	}
	
	public boolean updateRoom(String location, String latLng, int room_id) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOMID, room_id);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATLNG, latLng);

		return mDb.update(DATABASE_TABLE, cv, KEY_ROOMID + " = " + room_id,
				null) > 0;
	}
	
	public void checkRoom(ArrayList<Room> roomArray) throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < roomArray.size(); i++) {
				createRoom(roomArray.get(i).getRoom_id(), roomArray.get(i)
						.getTitle(), roomArray.get(i).getCategory(), roomArray
						.get(i).getNoOfLearner(), roomArray.get(i)
						.getLocation(), roomArray.get(i).getLatLng(), roomArray
						.get(i).getCreatorId(), roomArray.get(i)
						.getDescription(), roomArray.get(i).getStatus(),
						roomArray.get(i).getDateFrom(), roomArray.get(i)
								.getDateTo(), roomArray.get(i).getTimeFrom(),
						roomArray.get(i).getTimeTo());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < roomArray.size(); i++) {
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)) == roomArray
						.get(i).getRoom_id()) {

					delete = 1;

					if (!(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE))
							.equals(roomArray.get(i).getTitle()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_CATEGORY))
									.equals(roomArray.get(i).getCategory()))
							|| mCursor.getInt(mCursor
									.getColumnIndex(KEY_NOOFLEARNER)) != (roomArray
									.get(i).getNoOfLearner())
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_LOCATION))
									.equals(roomArray.get(i).getLocation()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_LATLNG))
									.equals(roomArray.get(i).getLatLng()))
							|| mCursor.getInt(mCursor
									.getColumnIndex(KEY_CREATORID)) != roomArray
									.get(i).getCreatorId()
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_DESCRIPTION))
									.equals(roomArray.get(i).getDescription()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_STATUS))
									.equals(roomArray.get(i).getStatus()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_DATEFROM))
									.equals(roomArray.get(i).getDateFrom()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_DATETO))
									.equals(roomArray.get(i).getDateTo()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_TIMEFROM))
									.equals(roomArray.get(i).getTimeFrom()))
							|| !(mCursor.getString(mCursor
									.getColumnIndex(KEY_TIMETO))
									.equals(roomArray.get(i).getTimeTo()))) {
						updateRoom(roomArray.get(i).getRoom_id(), roomArray
								.get(i).getTitle(), roomArray.get(i)
								.getCategory(), roomArray.get(i)
								.getNoOfLearner(), roomArray.get(i)
								.getLocation(), roomArray.get(i).getLatLng(),
								roomArray.get(i).getCreatorId(),
								roomArray.get(i).getDescription(), roomArray
										.get(i).getStatus(), roomArray.get(i)
										.getDateFrom(), roomArray.get(i)
										.getDateTo(), roomArray.get(i)
										.getTimeFrom(), roomArray.get(i)
										.getTimeTo());
					}
				}
			}
			if (delete == 0) {
				deleteRoom(mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < roomArray.size(); i++) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)) == roomArray
							.get(i).getRoom_id()) {

						delete = 1;

						if (!(mCursor.getString(mCursor.getColumnIndex(KEY_TITLE))
								.equals(roomArray.get(i).getTitle()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_CATEGORY))
										.equals(roomArray.get(i).getCategory()))
								|| mCursor.getInt(mCursor
										.getColumnIndex(KEY_NOOFLEARNER)) != (roomArray
										.get(i).getNoOfLearner())
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_LOCATION))
										.equals(roomArray.get(i).getLocation()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_LATLNG))
										.equals(roomArray.get(i).getLatLng()))
								|| mCursor.getInt(mCursor
										.getColumnIndex(KEY_CREATORID)) != roomArray
										.get(i).getCreatorId()
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_DESCRIPTION))
										.equals(roomArray.get(i).getDescription()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_STATUS))
										.equals(roomArray.get(i).getStatus()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_DATEFROM))
										.equals(roomArray.get(i).getDateFrom()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_DATETO))
										.equals(roomArray.get(i).getDateTo()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_TIMEFROM))
										.equals(roomArray.get(i).getTimeFrom()))
								|| !(mCursor.getString(mCursor
										.getColumnIndex(KEY_TIMETO))
										.equals(roomArray.get(i).getTimeTo()))) {
							updateRoom(roomArray.get(i).getRoom_id(), roomArray
									.get(i).getTitle(), roomArray.get(i)
									.getCategory(), roomArray.get(i)
									.getNoOfLearner(), roomArray.get(i)
									.getLocation(), roomArray.get(i).getLatLng(),
									roomArray.get(i).getCreatorId(),
									roomArray.get(i).getDescription(), roomArray
											.get(i).getStatus(), roomArray.get(i)
											.getDateFrom(), roomArray.get(i)
											.getDateTo(), roomArray.get(i)
											.getTimeFrom(), roomArray.get(i)
											.getTimeTo());
						}
					}
				}
				if (delete == 0) {

					deleteRoom(mCursor.getInt(mCursor
							.getColumnIndex(KEY_ROOMID)));
				}
			}
			for (int i = 0; i < roomArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)) == roomArray
						.get(i).getRoom_id()) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ROOMID)) == roomArray
							.get(i).getRoom_id()) {
						create = 1;
					}
				}
				if (create == 0) {
					createRoom(roomArray.get(i).getRoom_id(), roomArray.get(i)
							.getTitle(), roomArray.get(i).getCategory(),
							roomArray.get(i).getNoOfLearner(), roomArray.get(i)
									.getLocation(), roomArray.get(i)
									.getLatLng(), roomArray.get(i)
									.getCreatorId(), roomArray.get(i)
									.getDescription(), roomArray.get(i)
									.getStatus(), roomArray.get(i)
									.getDateFrom(), roomArray.get(i)
									.getDateTo(), roomArray.get(i)
									.getTimeFrom(), roomArray.get(i)
									.getTimeTo());
				}
			}
		}
		mCursor.close();
		close();
	}
}
