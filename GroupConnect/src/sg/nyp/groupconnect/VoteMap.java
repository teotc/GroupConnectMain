package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.entity.AvailableLocation;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class VoteMap extends FragmentActivity {

	private String currentMemberId;
	private String currentRoomId;
	private String locationId;
	private Bundle extras;
	private ArrayList<AvailableLocation> availableLocationArray = new ArrayList<AvailableLocation>();

	// Common
	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	public static GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vote_map);
		setUpMapIfNeeded();

		// Managed by Geraldine

		extras = getIntent().getExtras();
		if (extras != null) {
			currentMemberId = extras.getString("CURRENT_MEMBER_ID");
			currentRoomId = extras.getString("CURRENT_ROOM_ID");
		}

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

		new RetrieveLocation().execute();
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
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. In this case, we just add a marker near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
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

	class RetrieveLocation extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database
		private ProgressDialog pDialog;

		JSONParser jsonParser = new JSONParser();

		private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveAvailableLocation.php";

		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MESSAGE = "message";
		private static final String TAG_ARRAY = "posts";

		private static final String TAG_ID = "id";
		private static final String TAG_NAME = "name";
		private static final String TAG_LOCATION = "location";
		private static final String TAG_LATITUDE = "latitude";
		private static final String TAG_LONGITUDE = "longitude";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.context);
			pDialog.setMessage("Setting up...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
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
					return json.getString(TAG_MESSAGE);
				} else {
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();

			mMap.clear();
			mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(final Marker marker) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							VoteMap.this);

					// set title
					alertDialogBuilder.setTitle("Vote for " + marker.getTitle());

					// set dialog message
					alertDialogBuilder
							.setMessage("Are you okay with this location?")
							.setCancelable(false)
							.setNegativeButton("No",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									})
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											for (int i = 0; i < availableLocationArray
													.size(); i++) {
												if (availableLocationArray
														.get(i)
														.getName()
														.equals(marker
																.getTitle())) {
													locationId = Integer
															.toString(availableLocationArray
																	.get(i)
																	.getId());
												}
											}
											
											new VoteLocation().execute();
										}
									});

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			});

			for (int i = 0; i < availableLocationArray.size(); i++) {

				LatLng coordinate = new LatLng(availableLocationArray.get(i)
						.getLatitude(), availableLocationArray.get(i)
						.getLongitude());

				mMap.addMarker(new MarkerOptions()
						.position(coordinate)
						.title(availableLocationArray.get(i).getName())
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
			}
		}
	}

	class VoteLocation extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database
		private ProgressDialog pDialog;

		JSONParser jsonParser = new JSONParser();

		private static final String RETRIEVE_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/createVote.php";

		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MESSAGE = "message";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.context);
			pDialog.setMessage("Voting...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("memberId", currentMemberId));
				params.add(new BasicNameValuePair("roomId", currentRoomId));
				params.add(new BasicNameValuePair("locationId", locationId));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						RETRIEVE_LOCATION_URL, "POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					return json.getString(TAG_MESSAGE);
				} else {
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (success == 1) {
				Toast.makeText(VoteMap.this, "You have voted",
						Toast.LENGTH_LONG).show();
				setResult(1);
				VoteMap.this.finish();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case android.R.id.home:
			VoteMap.this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}// MainActivity
