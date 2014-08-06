package sg.nyp.groupconnect.service;

import java.util.*;

import org.json.*;

import sg.nyp.groupconnect.data.GrpRoomDbAdapter;
import sg.nyp.groupconnect.learner.GrpRoomListExt;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.IntentService;
import android.content.*;
import android.location.*;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GrpRmPullService extends IntentService {

	private GrpRoomDbAdapter mDbHelper;

	// JSON parser class
	private JSONParser jParser;
	private JSONArray mRooms = null;

	private static final String TAG = "GrpRmPullSvc";

	private static final String ROOM_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomRetrieve.php";

	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_NOOFLEARNER = "noOfLearner";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_LATLNG = "latLng";

	private ArrayList<GrpRoomListExt> mRoomList;

	SharedPreferences sp;

	int DISTPREF = 5000;

	public GrpRmPullService() {
		super("GrpRmPullService");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		homeLocTextAddr = sp.getString("home", null);

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
			Log.d(TAG, "Unable to update database due to:");
			e.printStackTrace();
		}
	}

	// User's address, for comparison
	private List<Address> address;
	private Geocoder coder;
	private String homeLocTextAddr = null;
	private LatLng homeLatLng = null;

	// Room's address, if latLng is undefined
	private List<Address> tRmAddr;
	private String tRmLocTxAddr = null;

	// Temp-run variables
	private String title = null, location = null, category = null, latLng;
	private long noOfLearner = 0, room_id = 0;
	private double distance, lat, lng;
	private LatLng rmLatLng = null;

	class PullRooms extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		boolean geocdAddrErr = false;
		boolean geocdRMAddrErr = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {

			coder = new Geocoder(getApplicationContext());

			try {
				address = coder.getFromLocationName(homeLocTextAddr, 5);
				if (address == null) {
					// return null;
					geocdAddrErr = true;
				} else {
					Address location = address.get(0);

					homeLatLng = new LatLng(location.getLatitude(),
							location.getLongitude());
				}
				Log.d("Geocode", "Home Address: " + homeLocTextAddr);
				Log.d("Geocode", "Home LatLng: " + homeLatLng.toString());
				Log.d("Geocode", "----- ");
			} catch (Exception e) {
				// String s = "Unable to locate your provided address, ";
				// s+= "please check your address.";
				Log.d("Geocode", "Geocoder failed due to:\n" + e);
			}

			try {

				// getting product details by making HTTP request
				Log.d(TAG, "Getting data from webservice");

				mRoomList = new ArrayList<GrpRoomListExt>();

				jParser = new JSONParser();

				JSONObject json = jParser.getJSONFromUrl(ROOM_RETRIEVE_URL);

				try {

					mRooms = json.getJSONArray(TAG_POSTS);

					for (int i = 0; i < mRooms.length(); i++) {
						JSONObject c = mRooms.getJSONObject(i);

						// gets the content of each tag
						// roomIdR = c.getString(TAG_ROOMID);
						// titleR = c.getString(TAG_TITLE);
						// locationR = c.getString(TAG_LOCATION);
						// noOfLearnerR = c.getString(TAG_NOOFLEARNER);
						// categoryR = c.getString(TAG_CATEGORY);
						// latLngR = c.getString(TAG_LATLNG);

						room_id = Long.parseLong(c.getString(TAG_ROOMID));
						title = c.getString(TAG_TITLE);
						category = c.getString(TAG_CATEGORY);
						noOfLearner = Long.parseLong(c
								.getString(TAG_NOOFLEARNER));
						location = c.getString(TAG_LOCATION);
						latLng = c.getString(TAG_LATLNG);

						distance = 0;
						lat = 0;
						lng = 0;

						Log.d(TAG, "updJSONd(): rmId:" + room_id);

						if (!latLng.equals("") && !latLng.equals(null)
								&& !latLng.equals("undefined")) {
							String[] parts = latLng.split(",");
							lat = Double.parseDouble(parts[0]);
							lng = Double.parseDouble(parts[1]);

							rmLatLng = new LatLng(lat, lng);

							mRoomList.add(new GrpRoomListExt(room_id, title,
									category, noOfLearner, location, latLng,
									rmLatLng, distance));
						} else {
							try {
								tRmAddr = coder.getFromLocationName(
										tRmLocTxAddr, 5);
								Log.d("Geocode", "Home Address: "
										+ tRmLocTxAddr);
								if (tRmAddr == null) {
									geocdRMAddrErr = true;
								}else{
									Address loc = tRmAddr.get(0);

									rmLatLng = new LatLng(loc.getLatitude(),
											loc.getLongitude());

									mRoomList.add(new GrpRoomListExt(room_id,
											title, category, noOfLearner, location,
											latLng, rmLatLng, distance));
								}
							} catch (Exception e) {
								String err = "Could not get room " + room_id
										+ "'s coordinate due to:\n";
								Log.d("Geocode", err + e);
							}
						}

					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			new calcRmDist().execute();
			// mDbHelper.checkRooms(mRoomList);
		}

	}

	class calcRmDist extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			Location locRoom = new Location("");
			Location locHome = new Location("User Home");
			double dist;

			locHome.setLatitude(homeLatLng.latitude);
			locHome.setLongitude(homeLatLng.longitude);

			for (GrpRoomListExt r : mRoomList) {

				locRoom = new Location("Room " + r.getRoom_id());
				locRoom.setLatitude(r.getRoomLatLng().latitude);
				locRoom.setLongitude(r.getRoomLatLng().longitude);

				dist = locHome.distanceTo(locRoom);

				r.setDistance(dist);

			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d(TAG + " Geocode", "Check Rooms");
			mDbHelper.checkRooms(mRoomList);
		}

	}
}
