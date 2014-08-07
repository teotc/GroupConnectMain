package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.data.MemberDbAdapter;
import sg.nyp.groupconnect.entity.Member;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.os.AsyncTask;

public class MemberPullSvc extends AsyncTask<String, String, String> {
		private MemberDbAdapter mDbHelper;

		// JSON parser class
		private JSONParser jsonParser = new JSONParser();
		private int success;

		private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveMembers.php";

		private static final String TAG_SUCCESS = "success";
		// private static final String TAG_MESSAGE = "message";
		private static final String TAG_ARRAY = "posts";

		private static final String TAG_ID = "id";
		private static final String TAG_NAME = "name";
		private static final String TAG_LOCATION = "location";
		private static final String TAG_LATITUDE = "latitude";
		private static final String TAG_LONGITUDE = "longitude";
		private static final String TAG_GENDER = "gender";
		private static final String TAG_SCHOOLID = "schoolId";
		private static final String TAG_PASSWORD = "password";
		private static final String TAG_TYPE = "type";
		private static final String TAG_DEVICE = "device";
		private static final String TAG_INTERESTEDSUB = "interestedSub";

		private ArrayList<Member> memberArray = new ArrayList<Member>();
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			mDbHelper = new MemberDbAdapter(MainActivity.ct);
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
				memberArray.clear();

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);
					Member m = new Member(
							c.getInt(TAG_ID), c.getString(TAG_NAME),
							c.getString(TAG_LOCATION),
							c.getDouble(TAG_LATITUDE),
							c.getDouble(TAG_LONGITUDE),
							c.getString(TAG_GENDER),
							c.getInt(TAG_SCHOOLID),
							c.getString(TAG_PASSWORD),
							c.getString(TAG_TYPE),
							c.getString(TAG_DEVICE),
							c.getString(TAG_INTERESTEDSUB)
							);

					memberArray.add(m);

				}

				if (success == 1) {
					mDbHelper.checkMember(memberArray);
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
