package sg.nyp.groupconnect.data;

import java.util.ArrayList;

import sg.nyp.groupconnect.entity.CategoriesType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CategoriesTypeDbAdapter {

	private final Context mCtx;

	private static final String DATABASE_TABLE = "categoriesType";
	private static final int DATABASE_VERSION = 2;

	private static final String KEY_ID = "id";
	private static final String KEY_TYPENAME = "typeName";

	private MainDbAdapter mDbHelper;
	private SQLiteDatabase mDb;

	public CategoriesTypeDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public CategoriesTypeDbAdapter open() throws SQLException {
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

	public void createCategory(int id, String typeName) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_TYPENAME, typeName);

		mDb.insert(DATABASE_TABLE, null, cv);
	}

	public boolean deleteCategory(int id) {
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
	}

	public boolean deleteAll() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAll() {
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_TYPENAME }, null, null, null,
				null, null);
	}

	public boolean updateCategory(int id, String typeName) {
		ContentValues cv = new ContentValues();
		cv.put(KEY_ID, id);
		cv.put(KEY_TYPENAME, typeName);

		return mDb.update(DATABASE_TABLE, cv, KEY_ID + "=" + id, null) > 0;
	}

	public void checkCategoryType(
			ArrayList<CategoriesType> categoriesTypeArray)
			throws SQLException {

		Cursor mCursor = fetchAll();

		if (mCursor.getCount() == 0) {

			for (int i = 0; i < categoriesTypeArray.size(); i++) {
				createCategory(categoriesTypeArray.get(i).getId(),
						categoriesTypeArray.get(i).getTypeName());
			}

		} else {
			mCursor.moveToFirst();
			int delete = 0;
			for (int i = 0; i < categoriesTypeArray.size(); i++) {
				if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == categoriesTypeArray
						.get(i).getId()) {

					delete = 1;

					if (!(mCursor.getString(mCursor
							.getColumnIndex(KEY_TYPENAME))
							.equals(categoriesTypeArray.get(i).getTypeName()))) {
						updateCategory(categoriesTypeArray.get(i).getId(),
								categoriesTypeArray.get(i).getTypeName());
					}
				}
			}
			if (delete == 0) {
				deleteCategory(mCursor.getInt(mCursor
						.getColumnIndex(KEY_ID)));
			}
			while (mCursor.moveToNext()) {
				delete = 0;

				for (int i = 0; i < categoriesTypeArray.size(); i++) {
					if (mCursor.getInt(mCursor.getColumnIndex(KEY_ID)) == categoriesTypeArray
							.get(i).getId()) {

						delete = 1;

						if (!(mCursor.getString(mCursor
								.getColumnIndex(KEY_TYPENAME))
								.equals(categoriesTypeArray.get(i).getTypeName()))) {
							updateCategory(categoriesTypeArray.get(i).getId(),
									categoriesTypeArray.get(i).getTypeName());

						}
					}
				}
				if (delete == 0) {

					deleteCategory(mCursor.getInt(mCursor
							.getColumnIndex(KEY_ID)));
				}
			}
			for (int i = 0; i < categoriesTypeArray.size(); i++) {
				int create = 0;
				mCursor = fetchAll();
				mCursor.moveToFirst();
				if (categoriesTypeArray.get(i).getId() == mCursor
						.getInt(mCursor.getColumnIndex(KEY_ID))) {
					create = 1;
				}
				while (mCursor.moveToNext()) {
					if (categoriesTypeArray.get(i).getId() == mCursor
							.getInt(mCursor.getColumnIndex(KEY_ID))) {
						create = 1;
					}
				}
				if (create == 0) {
					createCategory(categoriesTypeArray.get(i)
							.getId(), categoriesTypeArray.get(i).getTypeName());
				}
			}
		}
		mCursor.close();
		close();
	}
}
