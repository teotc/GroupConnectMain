package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.data.AvailableLocationDbAdapter;
import sg.nyp.groupconnect.entity.AvailableLocation;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.os.AsyncTask;

public class AvailableLocationPullSvc extends AsyncTask<String, String, String> {
		private AvailableLocationDbAdapter mDbHelper;

		// JSON parser class
		private JSONParser jsonParser = new JSONParser();
		private int success;

		private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveAvailableLocation.php";

		private static final String TAG_SUCCESS = "success";
		// private static final String TAG_MESSAGE = "message";
		private static final String TAG_ARRAY = "posts";

		private static final String TAG_ID = "id";
		private static final String TAG_NAME = "name";
		private static final String TAG_LOCATION = "location";
		private static final String TAG_LATITUDE = "latitude";
		private static final String TAG_LONGITUDE = "longitude";

		private ArrayList<AvailableLocation> availableLocationArray = new ArrayList<AvailableLocation>();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mDbHelper = new AvailableLocationDbAdapter(MainActivity.ct);
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
				availableLocationArray.clear();

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);
					AvailableLocation al = new AvailableLocation(
							c.getInt(TAG_ID), c.getString(TAG_NAME),
							c.getString(TAG_LOCATION),
							c.getDouble(TAG_LATITUDE),
							c.getDouble(TAG_LONGITUDE));

					availableLocationArray.add(al);

				}

				if (success == 1) {
					mDbHelper.checkAvailableLocation(availableLocationArray);
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
