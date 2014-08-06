package sg.nyp.groupconnect.db;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.data.CategoriesDbAdapter;
import sg.nyp.groupconnect.entity.Categories;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RadioButton;

public class RetrieveSubject extends AsyncTask<String, String, String> {

	private ProgressDialog pDialog;

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_TYPEID = "typeId";
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(Map.context);
		pDialog.setMessage("Setting up...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... args) {
		// Check for success tag

		CategoriesDbAdapter mDbHelper = new CategoriesDbAdapter(Map.context);
		mDbHelper.open();
		
		Cursor mCursor = mDbHelper.fetchAll();
		Map.arraySubject.clear();
		
		if (mCursor.getCount() != 0) {
			mCursor.moveToFirst();

			Categories c = new Categories(mCursor.getInt(mCursor
					.getColumnIndex(KEY_ID)), mCursor.getString(mCursor
					.getColumnIndex(KEY_NAME)),  mCursor.getInt(mCursor
							.getColumnIndex(KEY_TYPEID)));

			Map.arraySubject.add(c);

			while (mCursor.moveToNext()) {

				c = new Categories(mCursor.getInt(mCursor
						.getColumnIndex(KEY_ID)), mCursor.getString(mCursor
								.getColumnIndex(KEY_NAME)),  mCursor.getInt(mCursor
										.getColumnIndex(KEY_TYPEID)));

				Map.arraySubject.add(c);
			}
		}
		
		mCursor.close();
		mDbHelper.close();

		return null;

	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	protected void onPostExecute(String file_url) {
		for (int i = 0; i < Map.arraySubject.size(); i++) {
			RadioButton rdbtn = new RadioButton(Map.context);
			rdbtn.setId(Map.arraySubject.get(i).getId());
			rdbtn.setText(Map.arraySubject.get(i).getName());
			rdbtn.setTextColor(Color.WHITE);
			rdbtn.setTextSize(20f);
			Map.rdGrp.addView(rdbtn);
		}
		pDialog.dismiss();

	}

}