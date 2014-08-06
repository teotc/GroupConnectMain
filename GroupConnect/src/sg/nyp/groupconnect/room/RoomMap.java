package sg.nyp.groupconnect.room;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.custom.PopupAdapter;
import sg.nyp.groupconnect.room.CreateRm;
import sg.nyp.groupconnect.room.NotificationDisplay;
import sg.nyp.groupconnect.room.RoomDetails;
import sg.nyp.groupconnect.utilities.GeocodeJSONParser;
import sg.nyp.groupconnect.utilities.JSONParser;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.app.FragmentActivity;

public class RoomMap extends FragmentActivity implements
		OnInfoWindowClickListener {

	// Main Map
	private GoogleMap mMap;
	Button btnFind;
	EditText etSearch;
	MarkerOptions markerOptions;
	Marker searchMarker = null;
	// To know the center of singapore
	static final LatLng singapore = new LatLng(1.352083, 103.819836);

	// Dialog Method
	AlertDialog dialog;
	private static final int CREATERM_ALERT = 1;
	private static final int MAIN = 2;

	// onActivityResult
	private static final int CREATE_RM_RESULT_CODE = 100;

	// AsyncTask - LoadRoom
	// /Codes to retrieve all rooms
	// / Progress Dialog
	private ProgressDialog pDialog;
	// / testing from a real server:
	private static final String ROOM_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomRetrieve.php";

	// / JSON IDS:
	private static final String TAG_SUCCESS = "success";
	// /private static final String TAG_TITLE = "title";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	// /private static final String TAG_USERNAME = "username";
	private static final String TAG_MESSAGE = "message";

	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_NOOFLEARNER = "noOfLearner";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LATLNG = "latLng";

	// / An array of all of our comments
	private JSONArray mComments = null;
	// / manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mCommentList;

	// /LoadRoom Variables
	String roomIdR, titleR, locationR, categoryR, noOfLearnerR, latLngR,
			usernameR;
	double latR, lngR;
	LatLng retrievedLatLng;
	Marker retrievedMarker = null;

	// setupMap()
	double lat, lng;
	Marker temp;
	// / To get the location data
	Geocoder geocoder;
	String addressString = "";
	boolean getCurrentUserLocation = true;

	// For userDetailRetrieve - AsyncTask
	JSONParser jsonParser = new JSONParser();
	private static final String USER_DETAIL_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/userDetailRetrieve.php";
	int countForIdList = -1;
	String username;
	String userLocation;
	ArrayList<String> usernameList = new ArrayList<String>();
	ArrayList<String> userLocationList = new ArrayList<String>();

	// Get data from CreateRmStep2.java
	String chooseLocation = "";
	ArrayList<String> nameList = new ArrayList<String>();
	ArrayList<String> latLngList = new ArrayList<String>();
	ArrayList<String> distanceList = new ArrayList<String>();

	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyBykwwQsbIZk_b4ItsD5UqRRA25ADJi2H8";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_map);
		
		geocoder = new Geocoder(this, Locale.ENGLISH);

		// You can be pretty confident that the intent will not be null here.
		Intent intent = getIntent();

		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();

		setUpMapIfNeeded();

		// To use custom infowindow
		mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		mMap.setOnInfoWindowClickListener(this);
		Log.i("sg.nyp.groupconnect", "onCreate");

		if (extras != null) {
			if (extras.containsKey("chooseLocation")) {
				chooseLocation = this.getIntent().getStringExtra(
						"chooseLocation");
				nameList = this.getIntent().getStringArrayListExtra("nameList");
				latLngList = this.getIntent().getStringArrayListExtra(
						"latLngList");
				distanceList = this.getIntent().getStringArrayListExtra(
						"distanceList");

				for (int i = 0; i < nameList.size(); i++) {

					String[] temp = latLngList.get(i).split(",");
					Double lat = Double.parseDouble(temp[0]);
					Double lng = Double.parseDouble(temp[1]);

					Marker userLocation = mMap.addMarker(new MarkerOptions()
							.position(new LatLng(lat, lng)).title(
									nameList.get(i).toString()));

				}

			}
		}

		RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rLayout);
		if (chooseLocation.equals(""))
			rLayout.setVisibility(View.GONE);
		else
			rLayout.setVisibility(View.VISIBLE);

		btnFind = (Button) findViewById(R.id.btnFind);

		final AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoComplete);
		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this,
				R.layout.list_item_autocomplete));
		/*
		 * autoCompView.setOnItemClickListener(new OnItemClickListener(){
		 * 
		 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
		 * arg2, long arg3) { // TODO Auto-generated method stub String str =
		 * (String) arg0.getItemAtPosition(arg2); //Toast.makeText(this, str,
		 * Toast.LENGTH_SHORT).show(); Log.i("Special", "AutoComplete: " + str);
		 * }});
		 */

		// getCurrentUserLocation = false;

		btnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Getting user input location
				String location = autoCompView.getText().toString();

				if (location != null && !location.equals("")) {
					new GeocoderTask().execute(location);
				}
			}
		});

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("sg.nyp.groupconnect", "onCreateOptionMenu");
		// Inflate the menu; this adds items to the action bar if it is present.

		// Display icon only on Main Page. If come from CreateRm,

		if (chooseLocation.equals("")) {
			getMenuInflater().inflate(R.menu.main, menu);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.i("sg.nyp.groupconnect", "onOptionsItemSelected");
		int id = item.getItemId();
		if (id == R.id.refresh) {
			mMap.clear();
			new LoadRooms().execute();
		}
		if (id == R.id.notification) {
			Intent myIntent = new Intent(RoomMap.this,
					NotificationDisplay.class);
			startActivity(myIntent);
		}
		if (id == R.id.add) {
			Intent myIntent = new Intent(RoomMap.this, CreateRm.class);
			startActivityForResult(myIntent, CREATE_RM_RESULT_CODE);
		}
		if (id == android.R.id.home) {
			RoomMap.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Once the infowindow is clicked
	public void onInfoWindowClick(Marker marker) {
		Log.i("sg.nyp.groupconnect", "onInfoWindowClick");
		// Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();

		// Split the content to get the location and category
		String content = marker.getSnippet();
		String[] split = content.split("\n"); // NOTE: Null Pointer --TC
		String[] category = split[0].split(":");
		String[] location = split[1].split(":");

		Intent myIntent1 = new Intent(RoomMap.this, RoomDetails.class);
		myIntent1.putExtra("title", marker.getTitle());
		myIntent1.putExtra("location", location[1]);
		myIntent1.putExtra("category", category[1]);
		startActivity(myIntent1);
	}

	@Override
	protected void onResume() {
		Log.i("sg.nyp.groupconnect", "onResume");
		super.onResume();
		// setUpMapIfNeeded();
		// loading the rooms via AsyncTask
		if (chooseLocation.equals("")) {
			mMap.clear();
			new LoadRooms().execute();
		}
		/*
		 * else { for(int i = 0; i<idList.size(); i++) { countForIdList++; new
		 * userDetailRetrieve().execute();
		 * 
		 * } //getCurrentUserLocation = true; }
		 */

	}

	private void setUpMapIfNeeded() {
		Log.i("sg.nyp.groupconnect", "setUpMapIfNeeded");
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// mMap = (GoogleMap) findViewById(R.id.map);
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		Log.i("sg.nyp.groupconnect", "setUpMap");
		// mMap.addMarker(new MarkerOptions().position(new LatLng(1.352083,
		// 103.819836)).title("Marker"));
		Log.i("Test", "In setUpMap");

		if (mMap != null) {

			/*
			 * Marker hamburg = mMap.addMarker(new
			 * MarkerOptions().position(singapore) .title("Malay")); Marker kiel
			 * = mMap.addMarker(new MarkerOptions() .position(somewhere)
			 * .title("Kiel") .snippet("Kiel is cool")
			 * .icon(BitmapDescriptorFactory
			 * .fromResource(R.drawable.ic_launcher)));
			 */

			// Move the camera instantly to Singapore with a zoom of 15.
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 15));

			// Zoom in, animating the camera.
			mMap.animateCamera(CameraUpdateFactory.zoomIn());

			// Zoom out to zoom level 10, animating with a duration of 2
			// seconds.
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

			// Allow user to set their own marker
			mMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {
					// Only allow if is for choosing location for CreateRm.java
					if (!chooseLocation.equals("")) {
						temp = mMap.addMarker(new MarkerOptions()
								.position(point));

						// you can get latitude and longitude also from 'point'
						// and using Geocoder, the address

						// To get the address of the place clicked by the user
						try {
							lat = point.latitude;
							lng = point.longitude;
							List<Address> addresses = geocoder.getFromLocation(
									lat, lng, 1);

							if (addresses != null) {
								Address returnedAddress = addresses.get(0);
								StringBuilder strReturnedAddress = new StringBuilder(
										"");
								for (int i = 0; i < returnedAddress
										.getMaxAddressLineIndex(); i++) {
									strReturnedAddress.append(returnedAddress
											.getAddressLine(i));
								}

								addressString = strReturnedAddress.toString();
							}

							else {
								addressString = "No Address returned!";
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							addressString = "Cannot get Address!";
						}

						int num = 0;
						if (chooseLocation.equals("true")) {

							num = CREATERM_ALERT;
						} else
							num = MAIN;

						showDialog(num);
					}
				}

			});

			// Get the current location
			LocationManager locManager;
			String context = Context.LOCATION_SERVICE;
			locManager = (LocationManager) getSystemService(context);

			Criteria c = new Criteria();
			c.setAccuracy(Criteria.ACCURACY_FINE);
			c.setAltitudeRequired(false);
			c.setBearingRequired(false);
			c.setCostAllowed(true);
			c.setPowerRequirement(Criteria.POWER_LOW);

			String provider = LocationManager.NETWORK_PROVIDER;
			Location loc = locManager.getLastKnownLocation(provider);

			updateWithNewLocation(loc);

			locManager.requestLocationUpdates(provider, 2000, 10,
					locationListener);

		}
	}

	// Current Location START
	private void updateWithNewLocation(Location loc) {
		String latLonString;

		if (loc != null) {
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			LatLng currentLocation = new LatLng(lat, lon);
			Marker myLocation = mMap.addMarker(new MarkerOptions()
					.position(currentLocation)
					.title("Your Location")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.current_location)));

			if (!chooseLocation.equals(""))
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						currentLocation, 17));

			latLonString = "Lat: " + lat + "\n" + "Lon: " + lon;
		} else {
			latLonString = "No Location found";
		}

		Log.i("Special", latLonString);

	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location arg0) {
			updateWithNewLocation(arg0);
		}

		public void onProviderDisabled(String arg0) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}
	};

	// Current Location END

	protected Dialog onCreateDialog(int id) {
		Log.i("sg.nyp.groupconnect", "onCreateDialog");

		switch (id) {
		case CREATERM_ALERT:
			Log.i("sg.nyp.groupconnect", "onCreateDialog - CREATERM_ALERT");

			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to create a room at \n"
					+ addressString);
			builder.setCancelable(true);
			builder.setPositiveButton("Okay", new OkOnClickListener());
			builder.setNegativeButton("Cancel", new CancelOnClickListener());
			dialog = builder.create();
			dialog.show();

		case MAIN:
			// temp.remove();
		}

		return super.onCreateDialog(id);
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			// Toast.makeText(getApplicationContext(),
			// addressString,Toast.LENGTH_LONG).show();
			// startActivity(new Intent(getApplicationContext(),
			// CreateRm.class));
			/*
			 * Intent myIntent = new Intent(MainActivity.this,CreateRm.class);
			 * 
			 * myIntent.putExtra("location", addressString);
			 * myIntent.putExtra("lat", String.valueOf(lat));
			 * myIntent.putExtra("lng", String.valueOf(lng));
			 * startActivityForResult(myIntent,CREATE_RM_RESULT_CODE);
			 */

			Intent output = new Intent();
			output.putExtra("location", addressString);
			output.putExtra("lat", String.valueOf(lat));
			output.putExtra("lng", String.valueOf(lng));
			// Set the results to be returned to parent
			setResult(RESULT_OK, output);
			finish();
		}
	}

	private final class CancelOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG)
					.show();
			temp.remove();

		}
	}

	String result = null;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CREATE_RM_RESULT_CODE) {
			/*
			 * if(resultCode == RESULT_CANCELED){ String result = ""; result =
			 * data.getStringExtra("Cancel");
			 * 
			 * if (result.equals("Canceled")) { //temp.remove(); }
			 * Log.i("sg.nyp.groupconnect", "onActivityResult - Cancel");
			 * 
			 * } else
			 */
			/*
			 * if (resultCode == RESULT_OK) { Log.i("sg.nyp.groupconnect",
			 * "onActivityResult - Ok"); Toast.makeText(getApplicationContext(),
			 * "Okay", Toast.LENGTH_LONG).show(); }
			 */
		}
	}

	// For HOME Location START
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);
			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();
			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}

		return data;
	}

	/** A class, to download Places from Geocoding webservice */

	private class DownloadTask extends AsyncTask<String, Integer, String> {

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result) {

			// Instantiating ParserTask which parses the json data from
			// Geocoding webservice
			// in a non-ui thread
			ParserTask parserTask = new ParserTask();

			// Start parsing the places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);
		}
	}

	/** A class to parse the Geocoding Places in non-ui thread */

	class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			// Clears all the existing markers
			// mMap.clear();

			// for(int i=0;i<list.size();i++){

			// Creating a marker
			MarkerOptions markerOptions = new MarkerOptions();

			// Getting a place from the places list
			// Get only the first location
			HashMap<String, String> hmPlace = list.get(0);

			// Getting latitude of the place
			double lat = Double.parseDouble(hmPlace.get("lat"));

			// Getting longitude of the place
			double lng = Double.parseDouble(hmPlace.get("lng"));

			// Getting name
			String name = hmPlace.get("formatted_address");
			LatLng latLng = new LatLng(lat, lng);

			if (chooseLocation.equals("")) {
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(RoomMap.this);
				Editor edit = sp.edit();
				edit.putString("homeLatLng", lat + "," + lng).apply();

				// Setting the position for the marker
				markerOptions.position(latLng);

				// Set the icon for the marker
				BitmapDescriptor icon = BitmapDescriptorFactory
						.fromResource(R.drawable.home);

				markerOptions.icon(icon);

				// Placing a marker on the touched position
				mMap.addMarker(markerOptions);
			}
			/*
			 * else {
			 * 
			 * Marker userLocationMarker = mMap.addMarker(new MarkerOptions()
			 * .position(latLng) .title(usernameList.get(countForIdList))
			 * .snippet("Distance away from your home - " +
			 * distanceList.get(countForIdList)));
			 * 
			 * }
			 */

			// Locate the first location
			// if(i==0)
			// mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
			// }
		}
	}

	// For HOME Location END

	/**
	 * Retrieves recent post data from the server.
	 */
	public void updateJSONdata() {
		Log.i("sg.nyp.groupconnect", "updateJSONdata");
		// Instantiate the arraylist to contain all the JSON data.
		// we are going to use a bunch of key-value pairs, referring
		// to the json element name, and the content, for example,
		// message it the tag, and "I'm awesome" as the content..

		mCommentList = new ArrayList<HashMap<String, String>>();

		// Bro, it's time to power up the J parser
		JSONParser jParser = new JSONParser();
		// Feed the beast our comments url, and it spits us
		// back a JSON object. Boo-yeah Jerome.
		JSONObject json = jParser.getJSONFromUrl(ROOM_RETRIEVE_URL);

		// when parsing JSON stuff, we should probably
		// try to catch any exceptions:
		try {

			// I know I said we would check if "Posts were Avail." (success==1)
			// before we tried to read the individual posts, but I lied...
			// mComments will tell us how many "posts" or comments are
			// available
			mComments = json.getJSONArray(TAG_POSTS);

			// looping through all posts according to the json object returned
			for (int i = 0; i < mComments.length(); i++) {
				JSONObject c = mComments.getJSONObject(i);

				// gets the content of each tag
				roomIdR = c.getString(TAG_ROOMID);
				titleR = c.getString(TAG_TITLE);
				locationR = c.getString(TAG_LOCATION);
				noOfLearnerR = c.getString(TAG_NOOFLEARNER);
				categoryR = c.getString(TAG_CATEGORY);
				latLngR = c.getString(TAG_LATLNG);

				// creating new HashMap and store all data
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_ROOMID, roomIdR);
				map.put(TAG_TITLE, titleR);
				map.put(TAG_LOCATION, locationR);
				map.put(TAG_NOOFLEARNER, noOfLearnerR);
				map.put(TAG_LATLNG, latLngR);
				map.put(TAG_CATEGORY, categoryR);

				// adding HashList to ArrayList
				mCommentList.add(map);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateMap() {
		Log.i("sg.nyp.groupconnect", "updateMap");
		// To retrieve everything from Hashmap (mCommentList) and display all
		// rooms
		if (mCommentList != null) {
			for (int i = 0; i < mCommentList.size(); i++) {

				String latLng = mCommentList.get(i).get(TAG_LATLNG);
				if (!latLng.equals("") && !latLng.equals("undefined")) {
					// Spliting the lat Lng
					String[] parts = latLng.split(",");
					latR = Double.parseDouble(parts[0]);
					lngR = Double.parseDouble(parts[1]);

					retrievedLatLng = new LatLng(latR, lngR);

					retrievedMarker = mMap.addMarker(new MarkerOptions()
							.position(retrievedLatLng)
							.title(mCommentList.get(i).get(TAG_TITLE))
							.snippet(
									"Category: "
											+ mCommentList.get(i).get(
													TAG_CATEGORY)
											+ "\n"
											+ "Location: "
											+ mCommentList.get(i).get(
													TAG_LOCATION)));
				}
			}

		}

		// To get the home location
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(RoomMap.this);
		String mem_home = sp.getString("home", "No Home Location Found");

		String homeLocation = mem_home;

		if (homeLocation == null || homeLocation.equals("")) {
			Toast.makeText(getBaseContext(), "No Place is entered",
					Toast.LENGTH_SHORT).show();
			return;
		}

		String url = "https://maps.googleapis.com/maps/api/geocode/json?";

		try {
			// encoding special characters like space in the user input place
			homeLocation = URLEncoder.encode(homeLocation, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String address = "address=" + homeLocation;

		String sensor = "sensor=false";

		// url , from where the geocoding data is fetched
		url = url + address + "&" + sensor;

		// Instantiating DownloadTask to get places from Google Geocoding
		// service
		// in a non-ui thread
		DownloadTask downloadTask = new DownloadTask();

		// Start downloading the geocoding places
		downloadTask.execute(url);

	}

	public class LoadRooms extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RoomMap.this);
			pDialog.setMessage("Loading Map...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			Log.i("sg.nyp.groupconnect", "LoadRoom - Preexecute");
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			Log.i("sg.nyp.groupconnect", "LoadRoom - doInBackground");
			updateJSONdata();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.i("sg.nyp.groupconnect", "LoadRoom - onPostExecute");
			super.onPostExecute(result);
			pDialog.dismiss();
			updateMap();
		}
	}

	private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses) {

			if (addresses == null || addresses.size() == 0) {
				Toast.makeText(getBaseContext(), "No Location found",
						Toast.LENGTH_SHORT).show();
			}

			// Clears all the existing markers on the map
			// mMap.clear();
			if (searchMarker != null)
				searchMarker.remove();

			// Adding Markers on Google Map for each matching address
			for (int i = 0; i < addresses.size(); i++) {

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				LatLng latLng = new LatLng(address.getLatitude(),
						address.getLongitude());

				String addressText = String.format(
						"%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address
								.getCountryName());

				// markerOptions = new MarkerOptions();
				// markerOptions.position(latLng);
				// markerOptions.title(addressText);

				searchMarker = mMap
						.addMarker(new MarkerOptions()
								.position(latLng)
								.title("Search Location")
								.snippet(addressText)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

				// mMap.addMarker(markerOptions);

				// Locate the first location
				if (i == 0) {
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
							13));
					mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

				}
			}
		}
	}

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE
					+ TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&components=country:sg");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString(
						"description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String>
			implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			int count = 0;
			if (resultList != null)
				count = resultList.size();
			return count;
		}

		@Override
		public String getItem(int index) {
			String item = "";
			if (resultList != null)
				item = resultList.get(index);

			return item;
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	/*
	 * int count = 0; class userDetailRetrieve extends AsyncTask<String, String,
	 * String> {
	 * 
	 * @Override protected void onPreExecute() { super.onPreExecute();
	 * 
	 * }
	 * 
	 * @Override protected String doInBackground(String... args) { // TODO
	 * Auto-generated method stub // Check for success tag int success; String
	 * id = idList.get(countForIdList); Log.i("Special1" , id);
	 * 
	 * try { // Building Parameters List<NameValuePair> params = new
	 * ArrayList<NameValuePair>(); params.add(new BasicNameValuePair("id", id));
	 * 
	 * Log.d("request!", "starting"); // getting product details by making HTTP
	 * request JSONObject json =
	 * jsonParser.makeHttpRequest(USER_DETAIL_RETRIEVE_URL, "POST", params);
	 * 
	 * // check your log for json response Log.d("Login attempt",
	 * json.toString());
	 * 
	 * // json success tag success = json.getInt(TAG_SUCCESS); //createdRoomId =
	 * json.getString(TAG_ROOMID).toString() + "?";
	 * 
	 * if (success == 1) { Log.d("Login Successful!", json.toString());
	 * 
	 * usernameList.add(json.getString("username"));
	 * userLocationList.add(json.getString("homeLocation"));
	 * 
	 * //edit.commit(); return json.getString("username"); } else {
	 * Log.d("Login Failure!", json.getString(TAG_MESSAGE)); return
	 * json.getString(TAG_MESSAGE); } } catch (JSONException e) {
	 * e.printStackTrace(); }
	 * 
	 * return null;
	 * 
	 * }
	 * 
	 * protected void onPostExecute(String file_url) { // dismiss the dialog
	 * once product deleted //pDialog.dismiss(); if (file_url != null) {
	 * 
	 * String homeLocation = userLocationList.get(countForIdList);
	 * Log.i("Special1", homeLocation); if(homeLocation==null ||
	 * homeLocation.equals("")){ Toast.makeText(getBaseContext(),
	 * "No Place is entered", Toast.LENGTH_SHORT).show(); return; }
	 * 
	 * String url = "https://maps.googleapis.com/maps/api/geocode/json?";
	 * 
	 * try { // encoding special characters like space in the user input place
	 * homeLocation = URLEncoder.encode(homeLocation, "utf-8"); } catch
	 * (UnsupportedEncodingException e) { e.printStackTrace(); }
	 * 
	 * String address = "address=" + homeLocation;
	 * 
	 * String sensor = "sensor=false";
	 * 
	 * // url , from where the geocoding data is fetched url = url + address +
	 * "&" + sensor;
	 * 
	 * // Instantiating DownloadTask to get places from Google Geocoding service
	 * // in a non-ui thread DownloadTask downloadTask = new DownloadTask();
	 * 
	 * // Start downloading the geocoding places downloadTask.execute(url);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * }
	 */
}
