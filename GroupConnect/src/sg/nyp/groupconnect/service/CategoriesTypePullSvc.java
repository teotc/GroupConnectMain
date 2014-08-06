package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.data.CategoriesTypeDbAdapter;
import sg.nyp.groupconnect.entity.CategoriesType;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.os.AsyncTask;

public class CategoriesTypePullSvc extends AsyncTask<String, String, String> {
		private CategoriesTypeDbAdapter mDbHelper;

		// JSON parser class
		private JSONParser jsonParser = new JSONParser();
		private int success;

		private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveCategoriesType.php";

		private static final String TAG_SUCCESS = "success";
		// private static final String TAG_MESSAGE = "message";
		private static final String TAG_ARRAY = "posts";

		private static final String TAG_ID = "id";
		private static final String TAG_TYPENAME = "typeName";

		private ArrayList<CategoriesType> categoriesTypeArray = new ArrayList<CategoriesType>();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mDbHelper = new CategoriesTypeDbAdapter(MainActivity.ct);
			mDbHelper.open();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						RETRIEVE_LOCATION_URL, "POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				categoriesTypeArray.clear();

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);
					CategoriesType category = new CategoriesType(
							c.getInt(TAG_ID), c.getString(TAG_TYPENAME));

					categoriesTypeArray.add(category);

				}

				if (success == 1) {
					mDbHelper.checkCategoryType(categoriesTypeArray);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {

		}
}
