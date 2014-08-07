package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.data.RoomDbAdapter;
import sg.nyp.groupconnect.entity.Room;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.os.AsyncTask;

public class RoomPullSvc extends AsyncTask<String, String, String> {
	private RoomDbAdapter mDbHelper;

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	private int success;

	private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveRoom1.php";

	private static final String TAG_SUCCESS = "success";
	// private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_NOOFLEARNER = "noOfLearner";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LATLNG = "latLng";
	private static final String TAG_CREATORID = "creatorId";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DATEFROM = "dateFrom";
	private static final String TAG_DATETO = "dateTo";
	private static final String TAG_TIMEFROM = "timeFrom";
	private static final String TAG_TIMETO = "timeTo";

	private ArrayList<Room> roomArray = new ArrayList<Room>();

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mDbHelper = new RoomDbAdapter(MainActivity.ct);
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
			roomArray.clear();

			for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

				JSONObject c = json.getJSONArray(TAG_ARRAY).getJSONObject(i);
				
				Room r = new Room(c.getInt(TAG_ROOMID), c.getString(TAG_TITLE), c.getString(TAG_CATEGORY), c.getInt(TAG_NOOFLEARNER), 
						c.getString(TAG_LOCATION), c.getString(TAG_LATLNG), c.getInt(TAG_CREATORID), c.getString(TAG_DESCRIPTION),
						c.getString(TAG_STATUS), c.getString(TAG_DATEFROM), c.getString(TAG_DATETO), c.getString(TAG_TIMEFROM), c.getString(TAG_TIMETO));

				roomArray.add(r);

			}

			if (success == 1) {
				mDbHelper.checkRoom(roomArray);
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
