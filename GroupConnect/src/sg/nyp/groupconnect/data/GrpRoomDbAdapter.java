/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.learner.GrpRoomListExt;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

public class GrpRoomDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "room";
	private static final int DATABASE_VERSION = 2;

	public static final String KEY_ROOM_ID = "room_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_NO_OF_LEARNER = "noOfLearner";
	public static final String KEY_LOCATION = "location";

	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";
	public static final String KEY_DISTANCE = "distance";

	private static final String TAG = "GrpRoomDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "CREATE TABLE room (room_id INTEGER UNIQUE, "
			+ "title TEXT NOT NULL, "
			+ "category TEXT NOT NULL, "
			+ "noOfLearner INTEGER NOT NULL, "
			+ "location TEXT, "
			+ "lat DOUBLE, " + "lng DOUBLE, " + "distance DOUBLE " + ");";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS room");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public GrpRoomDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public GrpRoomDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void updateTable() {
		mDbHelper.onUpgrade(mDb, DATABASE_VERSION, DATABASE_VERSION);
	}

	public void createTable() {
		mDbHelper.onCreate(mDb);
	}

	/**
	 * Create a new note using the title and body provided. If the note is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 * 
	 * @param title
	 *            the title of the note
	 * @param body
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */

	public long createRoom(long room_id, String title, String category,
			long noOfLearner, String location, double lat, double lng,
			double distance) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOM_ID, room_id);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_CATEGORY, category);
		cv.put(KEY_NO_OF_LEARNER, noOfLearner);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_DISTANCE, distance);
		cv.put(KEY_LAT, lat);
		cv.put(KEY_LNG, lng);

		return mDb.insert(DATABASE_TABLE, null, cv);
	}

	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId
	 *            id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteRoom(long room_id) {
		return mDb.delete(DATABASE_TABLE, KEY_ROOM_ID + "=" + room_id, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all notes in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllRooms() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROOM_ID, KEY_TITLE,
				KEY_CATEGORY, KEY_NO_OF_LEARNER, KEY_LOCATION, KEY_DISTANCE,
				KEY_LAT, KEY_LNG }, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchRoom(long room_id) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROOM_ID, KEY_TITLE,
				KEY_CATEGORY, KEY_NO_OF_LEARNER, KEY_LOCATION, KEY_DISTANCE,
				KEY_LAT, KEY_LNG }, KEY_ROOM_ID + "=" + room_id, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchRoomsWDistance(long distance) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROOM_ID, KEY_TITLE,
				KEY_CATEGORY, KEY_NO_OF_LEARNER, KEY_LOCATION, KEY_DISTANCE,
				KEY_LAT, KEY_LNG }, KEY_DISTANCE + "<" + distance, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Update the room using the details provided. Room to be updated is
	 * specified using the room_id, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param room_id
	 *            - id of note to update
	 * @param title
	 *            - value to set note title to
	 * @param body
	 *            - value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateRoom(long room_id, String title, String category,
			long noOfLearner, String location, double lat, double lng,
			double distance) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOM_ID, room_id);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_CATEGORY, category);
		cv.put(KEY_NO_OF_LEARNER, noOfLearner);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_DISTANCE, distance);
		cv.put(KEY_LAT, lat);
		cv.put(KEY_LNG, lng);

		return mDb
				.update(DATABASE_TABLE, cv, KEY_ROOM_ID + "=" + room_id, null) > 0;
	}

	public void checkRooms(ArrayList<GrpRoomListExt> mRoomList)
			throws SQLException {
		String SVC_TAG = "GrpRmPullSvc";
		Log.d(SVC_TAG, "checkRooms()");

		String updateInfo = "";

		String title = null, location = null, category = null;
		long noOfLearner = 0, room_id = 0;
		double distance, lat, lng;

		Cursor mCursor = mDb.query(DATABASE_TABLE, new String[] { KEY_ROOM_ID,
				KEY_TITLE, KEY_CATEGORY, KEY_NO_OF_LEARNER, KEY_LOCATION,
				KEY_DISTANCE, KEY_LAT, KEY_LNG }, null, null, null, null,
				KEY_ROOM_ID + " ASC");

		if (!(mCursor.moveToFirst()) || mCursor.getCount() == 0) {
			Log.d(SVC_TAG, "chkRm(): No results in database");

			for (GrpRoomListExt r : mRoomList) {
				room_id = r.getRoom_id();
				title = r.getTitle();
				category = r.getCategory();
				noOfLearner = r.getNoOfLearner();
				location = r.getLocation();
				distance = r.getDistance();
				lat = r.getRoomLatLng().latitude;
				lng = r.getRoomLatLng().longitude;

				updateInfo += "\nCreated new record: " + room_id;
				createRoom(room_id, title, category, noOfLearner, location,
						lat, lng, distance);
			}

		} else {
			mCursor.moveToFirst();
			Log.d(SVC_TAG, "checkRooms(): Processing db results");
			while (mCursor.moveToNext()) {
				for (GrpRoomListExt r : mRoomList) {

					room_id = r.getRoom_id();
					title = r.getTitle();
					category = r.getCategory();
					noOfLearner = r.getNoOfLearner();
					location = r.getLocation();
					distance = r.getDistance();
					lat = r.getRoomLatLng().latitude;
					lng = r.getRoomLatLng().longitude;

					Log.d("GrpRmPullService",
							"chkRm(): RmID: "
									+ room_id
									+ ", DB:"
									+ Long.toString(getLong(mCursor,
											KEY_ROOM_ID)));

					boolean vExist = false;
					boolean wasUpdated = false;

					if (Long.valueOf(getLong(mCursor, KEY_ROOM_ID)).equals(
							room_id)) {
						vExist = true;
						if (mCursor
								.getString(mCursor.getColumnIndex(KEY_TITLE))
								.equals(title)
								&& mCursor.getString(
										mCursor.getColumnIndex(KEY_CATEGORY))
										.equals(category)
								&& mCursor
										.getString(
												mCursor.getColumnIndex(KEY_NO_OF_LEARNER))
										.equals(noOfLearner)
								&& mCursor.getString(
										mCursor.getColumnIndex(KEY_LOCATION))
										.equals(location)
								&& mCursor.getString(
										mCursor.getColumnIndex(KEY_LAT))
										.equals(lat)
								&& mCursor.getString(
										mCursor.getColumnIndex(KEY_LNG))
										.equals(lng)
								&& mCursor.getString(
										mCursor.getColumnIndex(KEY_DISTANCE))
										.equals(distance)) {
							wasUpdated = false;
						}
						if (!wasUpdated && !vExist) {
							createRoom(room_id, title, category, noOfLearner,
									location, lat, lng, distance);
							Log.d("Record Update", "Created new record: "
									+ room_id);
						} else if (wasUpdated && vExist) {
							updateRoom(room_id, title, category, noOfLearner,
									location, lat, lng, distance);
							Log.d("Record Update", "Updated record: " + room_id);
						} else {
							Log.d("Record Update",
									"No change to db for: "
											+ room_id
											+ " / "
											+ mCursor.getLong(mCursor
													.getColumnIndex(KEY_ROOM_ID)));
						}
					}
				}
			}
		}
		Log.d(SVC_TAG, "Updated record:\n" + updateInfo);

		 broadcastIntent();
	}

	public void broadcastIntent() {
		Log.d(TAG, "Intent Broadcasted");
		Intent intent = new Intent();
		intent.setAction("sg.nyp.groupconnect.DATADONE");
		mCtx.sendBroadcast(intent);
	}

	public static long getLong(Cursor mCursor, String keyID) {
		return Long.valueOf(mCursor.getLong(mCursor.getColumnIndex(keyID)));
	}
	
	public static Double getDouble(Cursor mCursor, String keyID) {
		return Double.valueOf(mCursor.getDouble(mCursor.getColumnIndex(keyID)));
	}

	public static String getString(Cursor mCursor, String keyID) {
		return mCursor.getString(mCursor.getColumnIndex(keyID));
	}
}
