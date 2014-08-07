package sg.nyp.groupconnect.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.data.RoomMembersDbAdapter;
import sg.nyp.groupconnect.entity.RoomMembers;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.os.AsyncTask;

public class RoomMembersPullSvc extends AsyncTask<String, String, String> {
	private RoomMembersDbAdapter mDbHelper;

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	private int success;

	private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveRoomMember.php";

	private static final String TAG_SUCCESS = "success";
	// private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_MEMBERID = "memberId";
	private static final String TAG_MEMBERTYPE = "memberType";

	private ArrayList<RoomMembers> roomMembersArray = new ArrayList<RoomMembers>();

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mDbHelper = new RoomMembersDbAdapter(MainActivity.ct);
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
			roomMembersArray.clear();

			for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

				JSONObject c = json.getJSONArray(TAG_ARRAY).getJSONObject(i);
				
				RoomMembers m = new RoomMembers(c.getInt(TAG_ROOMID), c.getInt(TAG_MEMBERID), c.getString(TAG_MEMBERTYPE));

				roomMembersArray.add(m);

			}

			if (success == 1) {
				mDbHelper.checkRoomMember(roomMembersArray);
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
