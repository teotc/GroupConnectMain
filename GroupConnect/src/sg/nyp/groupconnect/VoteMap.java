package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.data.AvailableLocationDbAdapter;
import sg.nyp.groupconnect.data.VoteLocationDbAdapter;
import sg.nyp.groupconnect.entity.AvailableLocation;
import sg.nyp.groupconnect.entity.MyItem;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class VoteMap extends FragmentActivity {

	private String currentMemberId;
	private String currentRoomId;
	private String locationId;
	private String home = "Your Home";
	private Bundle extras;
	private ArrayList<AvailableLocation> availableLocationArray = new ArrayList<AvailableLocation>();

	private Location locHome;
	private int DISTPREF = 5000; // metres

	// Common
	private LatLng myLocation;
	public static GoogleMap mMap;

	private ClusterManager<MyItem> mClusterManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vote_map);

		setTitle("Vote Location");
		
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(VoteMap.this);
		currentMemberId = sp.getString("id", null);
		Double lat = Double.parseDouble(sp.getString("homeLat", null));
		Double lon = Double.parseDouble(sp.getString("homeLng", null));

		myLocation = new LatLng(lat, lon);

		locHome = new Location("User Home");
		locHome.setLatitude(lat);
		locHome.setLongitude(lon);

		setUpMapIfNeeded();

		// Managed by Geraldine

		extras = getIntent().getExtras();
		if (extras != null) {
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

	@Override
	protected void onDestroy() {
		mMap = null;
		super.onDestroy();
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
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

			// Zoom in, animating the camera.
			mMap.animateCamera(CameraUpdateFactory.zoomIn());

			// Zoom out to zoom level 10, animating with a duration of 2
			// seconds.
			mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

			mMap.addMarker(new MarkerOptions()
					.position(myLocation)
					.title(home)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.home_mapicon)));

			CircleOptions circleOptions = new CircleOptions()
					.center(myLocation)
					// set center
					.radius(DISTPREF)
					// set radius in meters
					.strokeColor(Color.BLUE)
					.fillColor(Color.parseColor("#500084d3")).strokeWidth(5);

			mMap.addCircle(circleOptions);

			mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(final Marker marker) {
					if (!(marker.getTitle().equals(home))) {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								VoteMap.this);

						// set title
						alertDialogBuilder.setTitle("Vote for "
								+ marker.getTitle());

						// set dialog message
						alertDialogBuilder
								.setMessage("Are you okay with this location?")
								.setCancelable(false)
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										})
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
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
				}
			});

		}
	}

	class RetrieveLocation extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;

		private static final String KEY_ID = "id";
		private static final String KEY_NAME = "name";
		private static final String KEY_LOCATION = "location";
		private static final String KEY_LATITUDE = "latitude";
		private static final String KEY_LONGITUDE = "longitude";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(VoteMap.this);
			pDialog.setMessage("Setting up...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			AvailableLocationDbAdapter mDbHelper = new AvailableLocationDbAdapter(
					VoteMap.this);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchAll();
			availableLocationArray.clear();

			if (mCursor.getCount() != 0) {
				mCursor.moveToFirst();

				AvailableLocation al = new AvailableLocation(
						mCursor.getInt(mCursor.getColumnIndex(KEY_ID)),
						mCursor.getString(mCursor.getColumnIndex(KEY_NAME)),
						mCursor.getString(mCursor.getColumnIndex(KEY_LOCATION)),
						mCursor.getDouble(mCursor.getColumnIndex(KEY_LATITUDE)),
						mCursor.getDouble(mCursor.getColumnIndex(KEY_LONGITUDE)));

				availableLocationArray.add(al);

				while (mCursor.moveToNext()) {

					al = new AvailableLocation(mCursor.getInt(mCursor
							.getColumnIndex(KEY_ID)), mCursor.getString(mCursor
							.getColumnIndex(KEY_NAME)),
							mCursor.getString(mCursor
									.getColumnIndex(KEY_LOCATION)),
							mCursor.getDouble(mCursor
									.getColumnIndex(KEY_LATITUDE)),
							mCursor.getDouble(mCursor
									.getColumnIndex(KEY_LONGITUDE)));

					availableLocationArray.add(al);
				}
			}

			mCursor.close();
			mDbHelper.close();
			
			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			int count = 0;
			mClusterManager = new ClusterManager<MyItem>(VoteMap.this, mMap);
			mMap.setOnCameraChangeListener(mClusterManager);
			mClusterManager.setRenderer(new ItemRenderer());
			List<MyItem> items = new ArrayList<MyItem>();

			for (int i = 0; i < availableLocationArray.size(); i++) {
				Location locRoom = new Location("");
				locRoom.setLatitude(availableLocationArray.get(i).getLatitude());
				locRoom.setLongitude(availableLocationArray.get(i)
						.getLongitude());

				double dist = locHome.distanceTo(locRoom);

				int retval;

				// if (DISTPREF_UNIT == 0) {
				retval = Double.compare(DISTPREF, dist);
				// }
				// else {
				// retval = Double.compare(DISTPREF / 1000, dist);
				// }

				if (retval < 0) {

				} else {
					LatLng coordinate = new LatLng(availableLocationArray
							.get(i).getLatitude(), availableLocationArray
							.get(i).getLongitude());

					items.add(new MyItem(coordinate, availableLocationArray
							.get(i).getName(), BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
					count++;
				}
			}
			mClusterManager.addItems(items);

			if (count == 0) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						VoteMap.this);

				// set title
				alertDialogBuilder.setTitle("Sorry. No nearby location.");

				// set dialog message
				alertDialogBuilder
						.setMessage(
								"Load location outside 5km from your house?")
						.setCancelable(false)
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								})
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										List<MyItem> items = new ArrayList<MyItem>();

										mClusterManager.clearItems();
										for (int i = 0; i < availableLocationArray
												.size(); i++) {
											LatLng coordinate = new LatLng(
													availableLocationArray.get(
															i).getLatitude(),
													availableLocationArray.get(
															i).getLongitude());

											items.add(new MyItem(
													coordinate,
													availableLocationArray.get(
															i).getName(),
													BitmapDescriptorFactory
															.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
										}

										mClusterManager.addItems(items);
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}

			pDialog.dismiss();
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
			pDialog = new ProgressDialog(VoteMap.this);
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
					
					VoteLocationDbAdapter mDbHelper = new VoteLocationDbAdapter(
							VoteMap.this);
					mDbHelper.open();

					mDbHelper.createVoteLocation(currentMemberId, currentRoomId, locationId, "");

					mDbHelper.close();
					
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

	private class ItemRenderer extends DefaultClusterRenderer<MyItem> {

		public ItemRenderer() {
			super(VoteMap.this, mMap, mClusterManager);

		}

		@Override
		protected void onBeforeClusterItemRendered(MyItem myItem,
				MarkerOptions markerOptions) {
			markerOptions.position(myItem.getPosition())
					.title(myItem.getmTitle()).icon(myItem.getmIcon());
		}

		@Override
		protected void onBeforeClusterRendered(Cluster<MyItem> cluster,
				MarkerOptions markerOptions) {
			super.onBeforeClusterRendered(cluster, markerOptions);
		}

		@Override
		protected boolean shouldRenderAsCluster(
				@SuppressWarnings("rawtypes") Cluster cluster) {
			// Always render clusters.
			return cluster.getSize() > 1;
		}
	}

}// MainActivity
