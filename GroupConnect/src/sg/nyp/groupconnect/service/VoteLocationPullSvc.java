package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.data.VoteLocationDbAdapter;
import sg.nyp.groupconnect.entity.VoteLocation;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.os.AsyncTask;

public class VoteLocationPullSvc extends AsyncTask<String, String, String> {
	private VoteLocationDbAdapter mDbHelper;

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	private int success;

	private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveVoteLocation.php";

	private static final String TAG_SUCCESS = "success";
	// private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_MEMBERID = "memberId";
	private static final String TAG_ROOMID = "roomId";
	private static final String TAG_LOCATIONID = "locationId";
	private static final String TAG_STATUS = "status";

	private ArrayList<VoteLocation> voteLocationArray = new ArrayList<VoteLocation>();

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mDbHelper = new VoteLocationDbAdapter(MainActivity.ct);
		mDbHelper.open();
	}

	@Override
	protected String doInBackground(String... args) {
		// Check for success tag

		try {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			// getting product details by making HTTP request
			JSONObject json = jsonParser.makeHttpRequest(RETRIEVE_LOCATION_URL,
					"POST", params);

			// json success tag
			success = json.getInt(TAG_SUCCESS);
			voteLocationArray.clear();

			for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

				JSONObject c = json.getJSONArray(TAG_ARRAY).getJSONObject(i);
				VoteLocation vl = new VoteLocation(c.getString(TAG_MEMBERID),
						c.getString(TAG_ROOMID), c.getString(TAG_LOCATIONID),
						c.getString(TAG_STATUS));

				voteLocationArray.add(vl);

			}

			if (success == 1) {
				mDbHelper.checkVoteLocation(voteLocationArray);
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
