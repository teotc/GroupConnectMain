package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.Categories;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CategoriesDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "categories";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	private static final String KEY_TYPEID = "typeId";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public CategoriesDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public CategoriesDbAdapter open() throws SQLException {
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

	public void createCategoryType(int id, String name, int  typeId) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_TYPEID, typeId);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteCategoryType(int id) {
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_NAME, KEY_TYPEID }, null, null, null,
				null, KEY_NAME + " ASC");
	}

	public boolean updateCategoryType(int id, String name, int typeId) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_NAME, name);
		cv.put(KEY_TYPEID, typeId);

		return mDb.update(DATABASE_TABLE, cv, KEY_ID + "=" + id, null) > 0;
	}

	public void checkCategory(
			ArrayList<Categories> categoriesArray)
			throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < categoriesArray.size(); i++) {
				createCategoryType(categoriesArray.get(i).getId(),
						categoriesArray.get(i).getName(), categoriesArray.get(i).getTypeId());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < categoriesArray.size(); i++) {
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == categoriesArray
						.get(i).getId()) {

					delete = 1;

					if (!(mCursor.getString(mCursor
							.getColumnIndex(KEY_NAME))
							.equals(categoriesArray.get(i).getName())) || mCursor.getInt(mCursor
									.getColumnIndex(KEY_TYPEID)) != categoriesArray.get(i).getTypeId()) {
						updateCategoryType(categoriesArray.get(i).getId(),
								categoriesArray.get(i).getName(), categoriesArray.get(i).getTypeId());
					}
				}
			}
			if (delete == 0) {
				deleteCategoryType(mCursor.getInt(mCursor
						.getColumnIndex(KEY_ID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < categoriesArray.size(); i++) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == categoriesArray
							.get(i).getId()) {

						delete = 1;

						if (!(mCursor.getString(mCursor
								.getColumnIndex(KEY_NAME))
								.equals(categoriesArray.get(i).getName())) || mCursor.getInt(mCursor
										.getColumnIndex(KEY_TYPEID)) != categoriesArray.get(i).getTypeId()) {
							updateCategoryType(categoriesArray.get(i).getId(),
									categoriesArray.get(i).getName(), categoriesArray.get(i).getTypeId());

						}
					}
				}
				if (delete == 0) {

					deleteCategoryType(mCursor.getInt(mCursor
							.getColumnIndex(KEY_ID)));
				}
			}
			for (int i = 0; i < categoriesArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (categoriesArray.get(i).getId() == mCursor
						.getInt(mCursor.getColumnIndex(KEY_ID))) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (categoriesArray.get(i).getId() == mCursor
							.getInt(mCursor.getColumnIndex(KEY_ID))) {
						create = 1;
					}
				}
				if (create == 0) {
					createCategoryType(categoriesArray.get(i)
							.getId(), categoriesArray.get(i).getName(), categoriesArray.get(i).getTypeId());
				}
			}
		}
		mCursor.close();
		close();
	}
	
	public static String getString(Cursor mCursor, String keyID) {
		return mCursor.getString(mCursor.getColumnIndex(keyID));
	}
}
