package sg.nyp.groupconnect.room;

import java.util.ArrayList;
import java.util.List;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.GrpRoomDbAdapter;
import sg.nyp.groupconnect.entity.GrpRoomListExt;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearbyRooms extends FragmentActivity {

	private static final String TAG = "NearbyRooms";
	SharedPreferences sp;
	String homeLocTextAddr = null;
	private ArrayList<GrpRoomListExt> details;
	Geocoder coder;

	List<Address> address;
	LatLng homeLatLng = null;
	Location locHome;

	private GrpRoomDbAdapter mDbHelper;

	// Common
	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	public static GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nearby_rooms);
		setUpMapIfNeeded();

		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		homeLocTextAddr = sp.getString("home", null);

		mDbHelper = new GrpRoomDbAdapter(getApplicationContext());
		mDbHelper.open();

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

		coder = new Geocoder(getApplicationContext());
		getAddress();

		new CompareRoomDistance().execute();
	}

	private void getAddress() {
		homeLatLng = null;
		try {

			address = coder.getFromLocationName(homeLocTextAddr, 5);
			Log.d("Geocode", "Home Address: " + homeLocTextAddr);
			if (address == null) {
				// return null;
			}
			Address location = address.get(0);

			homeLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());

			mMap.addMarker(new MarkerOptions()
					.position(homeLatLng)
					.title(homeLocTextAddr)
					.snippet("Your home address")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

			Log.d("Geocode", "Home LatLng: " + homeLatLng.toString());
			Log.d("Geocode", "----- ");
			// return p1;
		} catch (Exception e) {
			// String s = "Unable to locate your provided address, ";
			// s+= "please check your address.";
			// Toast.makeText(getActivity(), s,
			// Toast.LENGTH_LONG).show();
			Log.d("Geocode", "Geocode Failed due to:\n" + e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated.. This will ensure that we only ever call
	 * {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 * <p>
	 * A user can return to this FragmentActivity after following the prompt and
	 * correctly installing/updating/enabling the Google Play services. Since
	 * the FragmentActivity may not have been completely destroyed during this
	 * process (it is likely that it would only be stopped or paused),
	 * {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.nbr_map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		} else {
			setUpMap();
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. This should only be called once and when we are sure that
	 * {@link #mMap} is not null.
	 */
	private void setUpMap() {
		if (mMap != null) {

			// Move the camera instantly to Singapore with a zoom of 15.
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 8));

			// Zoom in, animating the camera.
			mMap.animateCamera(CameraUpdateFactory.zoomIn());

			// Zoom out to zoom level 10, animating with a duration of 2
			// seconds.
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		}
	}

	int DISTPREF = 5000; // metres

	// fillData() variables
	private String title = null, location = null, category = null;
	private long noOfLearner = 0, room_id = 0;
	private double distance, lat, lng;
	private int icon;

	String info = null;

	class CompareRoomDistance extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			details = new ArrayList<GrpRoomListExt>();
			Cursor mCursor = mDbHelper.fetchRoomsWDistance(DISTPREF);
			if (mCursor.getCount() != 0) {
				// mRMCursor.moveToFirst();
				Log.d("GrpRmPullService",
						"filldata(): count: " + mCursor.getCount());
				while (mCursor.moveToNext()) {

					room_id = GrpRoomDbAdapter.getLong(mCursor,
							GrpRoomDbAdapter.KEY_ROOM_ID);
					title = GrpRoomDbAdapter.getString(mCursor,
							GrpRoomDbAdapter.KEY_TITLE);
					category = GrpRoomDbAdapter.getString(mCursor,
							GrpRoomDbAdapter.KEY_CATEGORY);
					noOfLearner = GrpRoomDbAdapter.getLong(mCursor,
							GrpRoomDbAdapter.KEY_NO_OF_LEARNER);
					location = GrpRoomDbAdapter.getString(mCursor,
							GrpRoomDbAdapter.KEY_LOCATION);
					distance = GrpRoomDbAdapter.getLong(mCursor,
							GrpRoomDbAdapter.KEY_DISTANCE);

					lat = GrpRoomDbAdapter.getDouble(mCursor,
							GrpRoomDbAdapter.KEY_LAT);
					lng = GrpRoomDbAdapter.getDouble(mCursor,
							GrpRoomDbAdapter.KEY_LNG);
					icon = GrpRoomDbAdapter.getInt(mCursor,
							GrpRoomDbAdapter.KEY_ICON);

					details.add(new GrpRoomListExt(room_id, title, category,
							noOfLearner, location, null, new LatLng(lat, lng),
							distance, icon));
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d(TAG, "Adding ");

			for (int i = 0; i < details.size(); i++) {
				Log.d(TAG, "oPE- Adding: " + details.get(i).getTitle());
				mMap.addMarker(new MarkerOptions()
						.position(details.get(i).getRoomLatLng())
						.title(details.get(i).getTitle())
						.snippet(details.get(i).getLocation())
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				Log.d(TAG, "LatLng:" + details.get(i).getRoomLatLng());
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case android.R.id.home:
			NearbyRooms.this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}// MainActivity
