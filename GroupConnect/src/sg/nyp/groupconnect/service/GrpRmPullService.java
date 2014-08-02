package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.*;

import sg.nyp.groupconnect.data.GrpRoomDbAdapter;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GrpRmPullService extends IntentService {

	private GrpRoomDbAdapter mDbHelper;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// testing from a real server:
	// private static final String LOGIN_URL =
	// "http://www.yourdomain.com/webservice/login.php";
	private static final String ROOM_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomRetrieve.php";

	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_NOOFLEARNER = "noOfLearner";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LATLNG = "latLng";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_POSTS = "posts";

	private static final String TAG = "GrpRmPullSvc";

	private JSONArray mRooms = null;
	private ArrayList<HashMap<String, String>> mRoomList;

	String roomIdR, titleR, locationR, categoryR, noOfLearnerR, latLngR,
			usernameR;

	// double latR, lngR;
	// LatLng retrievedLatLng;

	// ProgressDialog pDialog;

	public GrpRmPullService() {
		super("GrpRmPullService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDbHelper = new GrpRoomDbAdapter(this);
		mDbHelper.open();
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// Logging
		Log.d(TAG, "Intent handled");

		try {
			new PullRooms().execute();

		} catch (Exception e) {
			Log.d(TAG, "Unable to update database");
			e.printStackTrace();
		}
	}

	class PullRooms extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			try {
				// getting product details by making HTTP request
				Log.d(TAG, "Getting data from webservice");

				mRoomList = new ArrayList<HashMap<String, String>>();

				JSONParser jParser = new JSONParser();

				JSONObject json = jParser.getJSONFromUrl(ROOM_RETRIEVE_URL);

				try {

					mRooms = json.getJSONArray(TAG_POSTS);

					for (int i = 0; i < mRooms.length(); i++) {
						JSONObject c = mRooms.getJSONObject(i);

						// gets the content of each tag
						roomIdR = c.getString(TAG_ROOMID);
						titleR = c.getString(TAG_TITLE);
						locationR = c.getString(TAG_LOCATION);
						noOfLearnerR = c.getString(TAG_NOOFLEARNER);
						categoryR = c.getString(TAG_CATEGORY);
						latLngR = c.getString(TAG_LATLNG);
						usernameR = c.getString(TAG_USERNAME);

						Log.d(TAG, "updJSONd(): rmId:" + roomIdR);

						// creating new HashMap and store all data
						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_ROOMID, roomIdR);
						map.put(TAG_TITLE, titleR);
						map.put(TAG_LOCATION, locationR);
						map.put(TAG_NOOFLEARNER, noOfLearnerR);
						map.put(TAG_LATLNG, latLngR);
						map.put(TAG_CATEGORY, categoryR);
						map.put(TAG_USERNAME, usernameR);

						// adding HashList to ArrayList
						mRoomList.add(map);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				mDbHelper.checkRooms(mRoomList);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			// pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(GrpRmPullService.this, file_url,
						Toast.LENGTH_LONG).show();
				// fragment_home fh = new fragment_home();
				// fh.updateLV();
			}
		}

	}

}
