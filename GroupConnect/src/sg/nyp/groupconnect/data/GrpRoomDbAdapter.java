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
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
	public static final String KEY_LATLNG = "latlng";

	private static final String TAG = "GrpRoomDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "CREATE TABLE room (room_id INTEGER UNIQUE, "
			+ "title TEXT NOT NULL, "
			+ "category TEXT NOT NULL, "
			+ "noOfLearner TEXT NOT NULL, "
			+ "location TEXT, "
			+ "latlng TEXT NOT NULL " + ");";

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
			String noOfLearner, String location, String latlng) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROOM_ID, room_id);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_CATEGORY, category);
		cv.put(KEY_NO_OF_LEARNER, noOfLearner);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATLNG, latlng);

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
				KEY_CATEGORY, KEY_NO_OF_LEARNER, KEY_LOCATION, KEY_LATLNG },
				null, null, null, null, null);
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
				KEY_CATEGORY, KEY_NO_OF_LEARNER, KEY_LOCATION, KEY_LATLNG },
				KEY_ROOM_ID + "=" + room_id, null, null, null, null, null);
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
			String noOfLearner, String location, String latlng) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_TITLE, title);
		cv.put(KEY_CATEGORY, category);
		cv.put(KEY_NO_OF_LEARNER, noOfLearner);
		cv.put(KEY_LOCATION, location);
		cv.put(KEY_LATLNG, latlng);

		return mDb
				.update(DATABASE_TABLE, cv, KEY_ROOM_ID + "=" + room_id, null) > 0;
	}

	private ArrayList<HashMap<String, String>> roomList;

	public Cursor checkRooms(ArrayList<HashMap<String, String>> mRoomList)
			throws SQLException {
		String SVC_TAG = "GrpRmPullSvc";
		Log.d(SVC_TAG, "checkRooms()");

		String updateInfo = "";

		roomList = new ArrayList<HashMap<String, String>>();
		roomList = mRoomList;

		final String TAG_ROOMID = "room_id";
		final String TAG_TITLE = "title";
		final String TAG_CATEGORY = "category";
		final String TAG_NOOFLEARNER = "noOfLearner";
		final String TAG_LOCATION = "location";
		final String TAG_LATLNG = "latLng";
		final String TAG_USERNAME = "username";
		// final String TAG_POSTS = "posts"; // TAG_ROOMS = "rooms";

		String title = null, location = null, category = null, noOfLearner = null, latLng = null, username = null;
		long room_id = 0;

		Cursor mCursor = mDb.query(DATABASE_TABLE, new String[] { KEY_ROOM_ID,
				KEY_TITLE, KEY_CATEGORY, KEY_NO_OF_LEARNER, KEY_LOCATION,
				KEY_LATLNG }, null, null, null, null, KEY_ROOM_ID + " ASC");

		if (!(mCursor.moveToFirst()) || mCursor.getCount() == 0) {
			Log.d(SVC_TAG, "chkRm(): No results in database");

			// for (int i = 0; i < roomList.size(); i++) {
			//
			// room_id = Long.parseLong(mRoomList.get(i).get(TAG_ROOMID));
			// title = mRoomList.get(i).get(TAG_TITLE);
			// category = mRoomList.get(i).get(TAG_CATEGORY);
			// noOfLearner = mRoomList.get(i).get(TAG_NOOFLEARNER);
			// location = mRoomList.get(i).get(TAG_LOCATION);
			// latLng = mRoomList.get(i).get(TAG_LATLNG);
			// username = mRoomList.get(i).get(TAG_USERNAME);
			//
			// Log.d("GrpRmPullService",
			// "checkRooms(): Creating new records: " + room_id);
			//
			// Log.d("GrpRmPullService", "Processing checkRooms(): Username: "
			// + username);
			// // Log.d("GrpRmPullService",
			// // "Processing checkRooms(): Username: map: "
			// // + map.get(TAG_USERNAME));
			//
			// createRoom(room_id, title, category, noOfLearner, location,
			// latLng, username);
			// }

			for (HashMap<String, String> map : roomList) {
				room_id = Long.parseLong(map.get(TAG_ROOMID));
				title = map.get(TAG_TITLE);
				category = map.get(TAG_CATEGORY);
				noOfLearner = map.get(TAG_NOOFLEARNER);
				location = map.get(TAG_LOCATION);
				latLng = map.get(TAG_LATLNG);

				// Log.d("Record Update", "Created new record: " + room_id);
				updateInfo += "\nCreated new record: " + room_id;
				createRoom(room_id, title, category, noOfLearner, location,
						latLng);
			}

		} else {
			mCursor.moveToFirst();
			Log.d(SVC_TAG, "checkRooms(): Processing db results");
			while (mCursor.moveToNext()) {
				for (HashMap<String, String> map : roomList) {

					room_id = Long.parseLong(map.get(TAG_ROOMID));
					title = map.get(TAG_TITLE);
					category = map.get(TAG_CATEGORY);
					noOfLearner = map.get(TAG_NOOFLEARNER);
					location = map.get(TAG_LOCATION);
					latLng = map.get(TAG_LATLNG);

					Log.d("GrpRmPullService", "checkRooms(): Room ID: "
							+ room_id);
					Log.d("GrpRmPullService",
							"checkRooms(): Room ID (DB): "
									+ mCursor.getLong(mCursor
											.getColumnIndex(KEY_ROOM_ID)));

					boolean vExist = false;
					boolean wasUpdated = false;

					if (Long.valueOf(
							mCursor.getLong(mCursor.getColumnIndex(KEY_ROOM_ID)))
							.equals(room_id)) {
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
										mCursor.getColumnIndex(KEY_LATLNG))
										.equals(latLng)) {
							wasUpdated = false;
						}
						if (!wasUpdated && !vExist) {
							createRoom(room_id, title, category, noOfLearner,
									location, latLng);
							Log.d("Record Update", "Created new record: "
									+ room_id);
						} else if (wasUpdated && vExist) {
							updateRoom(room_id, title, category, noOfLearner,
									location, latLng);
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
		return mCursor;

	}
}
