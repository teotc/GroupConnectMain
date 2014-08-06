package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.data.SchoolsDbAdapter;
import sg.nyp.groupconnect.entity.Schools;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.os.AsyncTask;

public class SchoolsPullSvc extends AsyncTask<String, String, String> {
		private SchoolsDbAdapter mDbHelper;

		// JSON parser class
		private JSONParser jsonParser = new JSONParser();
		private int success;

		private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveSchool.php";

		private static final String TAG_SUCCESS = "success";
		// private static final String TAG_MESSAGE = "message";
		private static final String TAG_ARRAY = "posts";

		private static final String TAG_ID = "id";
		private static final String TAG_NAME = "name";
		private static final String TAG_CATEGORY = "category";
		private static final String TAG_LATITUDE = "latitude";
		private static final String TAG_LONGITUDE = "longitude";

		private ArrayList<Schools> schoolsArray = new ArrayList<Schools>();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mDbHelper = new SchoolsDbAdapter(MainActivity.ct);
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
				schoolsArray.clear();

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);
					Schools s = new Schools(
							c.getInt(TAG_ID), c.getString(TAG_NAME),
							c.getString(TAG_CATEGORY),
							c.getDouble(TAG_LATITUDE),
							c.getDouble(TAG_LONGITUDE));

					schoolsArray.add(s);

				}

				if (success == 1) {
					mDbHelper.checkSchools(schoolsArray);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			new CategoriesPullSvc().execute();
		}
}
