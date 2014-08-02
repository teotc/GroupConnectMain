package sg.nyp.groupconnect.room;

import static sg.nyp.groupconnect.notification.Util.DISPLAY_MESSAGE_ACTION;
import static sg.nyp.groupconnect.notification.Util.EXTRA_MESSAGE;
import static sg.nyp.groupconnect.notification.Util.TAG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.custom.CustomList;
import sg.nyp.groupconnect.notification.AlertDialogManager;
import sg.nyp.groupconnect.notification.AppServices;
import sg.nyp.groupconnect.notification.WakeLocker;
import sg.nyp.groupconnect.utilities.GeocodeJSONParser;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateRmStep2 extends Activity {
	// Variable
	TextView tvTitle, tvCategory, tvLocation;
	EditText etTitle, etCategory, etNoOfLearner, etLocation;
	Button btnSubmit, btnClear, btnCancel, btnSuggest, btnMap, btnBack;
	ListView suggestList;
	Spinner spRadius;

	// Getting User sign in info - onCreate
	String mem_username = "";
	String mem_type = "";
	String mem_home = "";
	String mem_homeLatLng = "";

	// For receiving intent from MainActivity - onCreate
	String location, lat, lng;

	// Dialog
	AlertDialog dialog;
	private static final int POPUPNOONE_ALERT = 1;

	// onActivityResult
	private static final int CHOOSE_LOCATION_RESULT_CODE = 100;

	// For CreateRm
	// Progress Dialog
	private ProgressDialog pDialog;
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	// testing from a real server:
	private static final String CREATE_ROOM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmCreate.php";
	// ids
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	// For Retrieving created room_id
	// testing from a real server:
	private static final String GETROOMID_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomIdRetrieve.php";
	private static final String TAG_ROOMID = "room_id";
	String createdRoomId = null;
	boolean successAll = true;

	private static final String CREATE_ROOM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemCreate.php";

	private static final String MEM_RETRIEVE_LEARNER_URL = "http://www.it3197Project.3eeweb.com/grpConnect/memRetrieveWhrType.php";

	private JSONArray mHomeInterest = null;
	private ArrayList<HashMap<String, String>> mHomeInterestList;
	// JSON IDS:
	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	private static final String TAG_ID = "userId";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_HOME = "homeLocation";
	private static final String TAG_INTEREST = "interestedSub";

	// Get all learner details to store
	String idR = "";
	String usernameR = "";
	String homeLocationR = "";
	String interestedSubR = "";

	// For getting distance between 2 points
	float[] res = new float[3];
	// Users home address lat & lng
	double homeLat, homeLng;
	// Learner home address lat & lng
	double compareLat = 0;
	double compareLng = 0;
	// Less than 1000m = add into list
	ArrayList<String> suggestNameList = new ArrayList<String>();

	// Act as the image for the customListView
	Integer[] imageId = { R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher

	};

	String username = "";
	int countForUsername = 0;
	ArrayList<String> usernameList = new ArrayList<String>();
	ArrayList<String> latLngList = new ArrayList<String>();
	ArrayList<String> distanceList = new ArrayList<String>();

	// Async Task - memRetrieveLearner
	String homeAddress;

	// For retrieving data from CreateRm.java
	String title, category, desc;

	// For Spinner
	String[] radiusList = { "1km", "2km", "3km", "4km", "5km" };
	ArrayAdapter<String> radiusAdapter;
	String selectedValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rm_step2);

		new getUsersDataForPush().execute();

		// Get the selected Location from MainActivity
		// Intent intent = new Intent();
		// location = this.getIntent().getStringExtra("location");
		// lat = this.getIntent().getStringExtra("lat");
		// lng = this.getIntent().getStringExtra("lng");

		etNoOfLearner = (EditText) findViewById(R.id.etNoOfLearner);
		etLocation = (EditText) findViewById(R.id.etLocation);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		// btnClear = (Button) findViewById(R.id.btnClear);
		btnBack = (Button) findViewById(R.id.btnBack);
		// btnCancel = (Button) findViewById(R.id.btnCancel);
		// btnSuggest = (Button) findViewById(R.id.btnSuggest);
		btnMap = (Button) findViewById(R.id.btnMap);
		spRadius = (Spinner) findViewById(R.id.spRadius);

		radiusAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, radiusList);
		spRadius.setAdapter(radiusAdapter);

		// etLocation.setText(location);

		// Get current user details
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(CreateRmStep2.this);
		mem_username = sp.getString("username", "No Username");
		mem_type = sp.getString("type", "No Type");
		mem_home = sp.getString("home", "No Home Location Found");
		// mem_homeLatLng = sp.getString("homeLatLng", "No latlng value");
		//
		// String tempArr[] = mem_homeLatLng.split(",");
		// homeLat = Double.parseDouble(tempArr[0]);
		// homeLng = Double.parseDouble(tempArr[1]);
		//
		// Log.i("Special", String.valueOf(homeLat + "/" + homeLng));
		// GetData from CreateRm.java

		// You can be pretty confident that the intent will not be null here.
		Intent intent = getIntent();

		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();
		if (extras != null) {

			title = this.getIntent().getStringExtra("title");
			category = this.getIntent().getStringExtra("category");
			desc = this.getIntent().getStringExtra("desc");

		}

		btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// String message;
				boolean success = true;

				// Check for any empty EditText

				if (etNoOfLearner.getText().toString().length() <= 0) {
					etNoOfLearner.setError("Enter No. of Learner");
					success = false;
				}

				if (etLocation.getText().toString().length() <= 0) {
					// etLocation.setError("Enter Location");
					// success = false;
					etLocation.setText("none");
				}

				if (success == true) // If all fields are filled
				{
					// put data to be return to parent in an intent
					// Intent output = new Intent();
					// output.putExtra("roomCreated", "rmCreated");
					// output.putExtra(CREATE, "Room Created");
					// output.putExtra(TITLE, etTitle.getText().toString());
					// output.putExtra(NOOFLEARNER,
					// etNoOfLearner.getText().toString());
					// output.putExtra(LOCATION,
					// etLocation.getText().toString());
					// output.putExtra(CATEGORY,
					// etCategory.getText().toString());
					// Set the results to be returned to parent
					// setResult(RESULT_OK, output);

					checkPushSameInterest();

					new createRoom().execute();
					// Once the room is created successfully in Table 'Room'
					// Find the room Id created.
					new retrieveCreatedRmId().execute();

					// Than use the roomId to create another row for RoomMember
					// to create Member data
					new createRoomMember().execute();

					if (successAll == true) {
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						finish();
					} else
						Toast.makeText(CreateRmStep2.this,
								"SuccessAll: " + successAll, Toast.LENGTH_LONG)
								.show();

				}

			}
		});

		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// put data to be return to parent in an intent
				Intent output = new Intent();
				output.putExtra("Cancel", "Canceled");
				// Set the results to be returned to parent
				setResult(RESULT_CANCELED, output);

				// Ends the sub-activity
				finish();

			}
		});

		/*
		 * btnClear.setOnClickListener(new OnClickListener(){ public void
		 * onClick(View v) { etNoOfLearner.setText(""); etLocation.setText("");
		 * 
		 * } });
		 * 
		 * btnSuggest.setOnClickListener(new OnClickListener(){ public void
		 * onClick(View v) { //Suggest Member with same interest and around the
		 * users's location //In this case 1000m new
		 * memRetrieveLearner().execute();
		 * 
		 * } });
		 */

		btnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(CreateRmStep2.this, RoomMap.class);
				myIntent.putExtra("chooseLocation", "true");
				myIntent.putStringArrayListExtra("nameList", suggestNameList);
				myIntent.putStringArrayListExtra("latLngList", latLngList);
				myIntent.putStringArrayListExtra("distanceList", distanceList);
				startActivityForResult(myIntent, CHOOSE_LOCATION_RESULT_CODE);

			}
		});

		spRadius.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				selectedValue = radiusList[position];
				Log.i("Special", selectedValue);

				new memRetrieveLearner().execute();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_LOCATION_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				location = data.getStringExtra("location");
				lat = data.getStringExtra("lat");
				lng = data.getStringExtra("lng");
				etLocation.setText(location);
			}
		}
	}

	protected Dialog onCreateDialog(int id) {
		Log.i("sg.nyp.groupconnect", "onCreateDialog");
		switch (id) {

		case POPUPNOONE_ALERT:
			Builder builder1 = new AlertDialog.Builder(this);
			builder1.setMessage("No one with the same Interest or No one with the same interested is within 1000m from your house");
			builder1.setCancelable(true);
			builder1.setPositiveButton("Okay", new OkOnClickListener());
			dialog = builder1.create();
			dialog.show();

		}

		return super.onCreateDialog(id);
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_rm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == android.R.id.home) {
			CreateRmStep2.this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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

				if (places == null)
					Log.i("Hihi", "Places is Empty");

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			// Getting a place from the places list
			// Get only the first location
			if (list.size() == 0) {
				Log.i("Hihi", "List is Empty");
			}

			HashMap<String, String> hmPlace = list.get(0);

			// Getting latitude of the place
			compareLat = Double.parseDouble(hmPlace.get("lat"));

			// Getting longitude of the place
			compareLng = Double.parseDouble(hmPlace.get("lng"));
			Log.i("Hihi",
					String.valueOf(compareLat) + String.valueOf(compareLng));
			// Compare the distance and store it into a float array, res
			Location.distanceBetween(homeLat, homeLng, compareLat, compareLng,
					res);

			// If there is result in res array
			if (res.length != 0) {
				int range = 1000;
				if (selectedValue.equals("1km")) {
					range = 1000;
				} else if (selectedValue.equals("2km")) {
					range = 2000;
				} else if (selectedValue.equals("3km")) {
					range = 3000;
				} else if (selectedValue.equals("4km")) {
					range = 4000;
				} else if (selectedValue.equals("5km")) {
					range = 5000;
				} else
					range = 1000;

				// Toast.makeText(getApplicationContext(),
				// String.valueOf(res[0]), Toast.LENGTH_LONG).show();
				// If the result, that is in meter, is less than 1000m
				// Store and display into the listview

				if (res[0] < range) {
					Log.i("CreateRm", String.valueOf(res[0]));
					// countForUsername is used to get the correct name
					suggestNameList.add(usernameList.get(countForUsername)
							+ " - " + res[0] + "m away");
					latLngList.add(compareLat + "," + compareLng);
					distanceList.add(String.valueOf(res[0]));
					CustomList adapter = new CustomList(CreateRmStep2.this,
							suggestNameList, imageId);
					suggestList = (ListView) findViewById(R.id.lv_suggestGroup);
					// Since this method will be repeated a few times,
					// Clear the listview before inserting the new
					// suggestNameList data
					suggestList.setAdapter(null);
					suggestList.setAdapter(adapter);
					countForUsername++;
				} else
					countForUsername++;
			}

		}
	}

	// For HOME Location END

	class createRoom extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// pDialog = new ProgressDialog(CreateRmStep2.this);
			// pDialog.setMessage("Posting Comment...");
			// pDialog.setIndeterminate(false);
			// pDialog.setCancelable(false);
			// pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(CreateRmStep2.this);
			String post_title = title;
			String post_category = category;
			String post_noOfLearner = etNoOfLearner.getText().toString();
			String post_location = etLocation.getText().toString();
			String post_latLng = lat + "," + lng;
			String post_creatorId = sp.getString("id", "");
			String post_desc = desc;

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("username",
				// post_username));
				params.add(new BasicNameValuePair("title", post_title));
				params.add(new BasicNameValuePair("category", post_category));
				params.add(new BasicNameValuePair("noOfLearner",
						post_noOfLearner));
				params.add(new BasicNameValuePair("location", post_location));
				params.add(new BasicNameValuePair("latLng", post_latLng));
				params.add(new BasicNameValuePair("creatorId", post_creatorId));
				params.add(new BasicNameValuePair("description", post_desc));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(CREATE_ROOM_URL,
						"POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());

					return json.getString(TAG_MESSAGE);
				} else {
					Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
					successAll = false;
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			// pDialog.dismiss();
			// if (file_url != null) {
			// Toast.makeText(CreateRmStep2.this, file_url, Toast.LENGTH_LONG)
			// .show();
			// }

		}

	}

	class retrieveCreatedRmId extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String titleR = title;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("title", titleR));

				Log.d("request!", "starting");
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(GETROOMID_URL,
						"POST", params);

				// check your log for json response
				Log.d("Login attempt", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				// createdRoomId = json.getString(TAG_ROOMID).toString() + "?";

				if (success == 1) {
					Log.d("Login Successful!", json.toString());
					createdRoomId = json.getString(TAG_ROOMID);

					return json.getString(TAG_ROOMID);
				} else {
					Log.d("Login Failure!", json.getString(TAG_MESSAGE));
					successAll = false;
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			// pDialog.dismiss();
			if (file_url != null) {
				// Toast.makeText(CreateRm.this, "Room id created: " + file_url,
				// Toast.LENGTH_LONG).show();
				// createdRoomId = file_url;
			}

		}

	}

	class createRoomMember extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(CreateRmStep2.this);
			String post_roomId = createdRoomId;
			String post_memberId = sp.getString("id", "");
			;
			String type = mem_type;

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("username",
				// post_username));
				params.add(new BasicNameValuePair("room_id", post_roomId));
				params.add(new BasicNameValuePair("memberId", post_memberId));
				params.add(new BasicNameValuePair("memberType", type));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						CREATE_ROOM_MEM_URL, "POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());

					return json.getString(TAG_MESSAGE);
				} else {
					Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
					successAll = false;
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {

		}

	}

	public void updateJSONdata() {
		Log.i("sg.nyp.groupconnect", "updateJSONdata");
		mHomeInterestList = new ArrayList<HashMap<String, String>>();

		JSONParser jParser = new JSONParser();

		JSONObject json = jParser.getJSONFromUrl(MEM_RETRIEVE_LEARNER_URL);

		try {

			mHomeInterest = json.getJSONArray(TAG_POSTS);

			// looping through all posts according to the json object returned
			for (int i = 0; i < mHomeInterest.length(); i++) {
				JSONObject c = mHomeInterest.getJSONObject(i);

				// gets the content of each tag
				usernameR = c.getString(TAG_USERNAME);
				homeLocationR = c.getString(TAG_HOME);
				interestedSubR = c.getString(TAG_INTEREST);

				Log.i("Hihi", "UsernameR: " + usernameR);
				Log.i("Hihi", "homeLocationR: " + homeLocationR);
				Log.i("Hihi", "interestedSubR: " + interestedSubR);

				// creating new HashMap and store all data
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_USERNAME, usernameR);
				map.put(TAG_HOME, homeLocationR);
				map.put(TAG_INTEREST, interestedSubR);

				// adding HashList to ArrayList
				mHomeInterestList.add(map);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateMap() {
		Log.i("sg.nyp.groupconnect", "updateMap");
		boolean sameInterest = false;
		if (mHomeInterestList != null) {
			// Clear the list of chosen name before starting to search
			suggestNameList.clear();
			distanceList.clear();
			latLngList.clear();

			for (int i = 0; i < mHomeInterestList.size(); i++) {
				// get the username, homeAddrss and interestedSub for each
				// member
				username = mHomeInterestList.get(i).get(TAG_USERNAME);
				homeAddress = mHomeInterestList.get(i).get(TAG_HOME);
				String interestedSub = mHomeInterestList.get(i).get(
						TAG_INTEREST);

				Log.i("Hihi", "Username: " + username);
				Log.i("Hihi", "homeAddress: " + homeAddress);
				Log.i("Hihi", "interestedSub: " + interestedSub);

				// Spliting the interests in order to compare with etCategory
				String[] parts = interestedSub.split(",");
				if (parts.length != 0) {
					for (int z = 0; z < parts.length; z++) {
						String sub = parts[z];
						Log.i("CreateRm", "Interest sub for " + username
								+ " = " + sub);
						if (sub.equals(category)) {

							sameInterest = true;
						}
					}
				}

				Log.i("Hihi", "SameInterest = " + sameInterest);

				// If the learner have the same interest, get convert their home
				// location to latLng to compare
				// with the creator's home address latlng
				// Within 1000m, means can be suggested in the listview
				if (sameInterest == true) {
					Log.i("Hihi", "Username with sameInterest" + username);
					Log.i("Hihi", homeAddress);
					// Insert the username into a list as a selected member to
					// be retrieve from the ParserTask
					// onPostExecute later on
					usernameList.add(username);
					String homeLocation = homeAddress;

					if (homeLocation == null || homeLocation.equals("")) {
						Toast.makeText(getBaseContext(), "No Place is entered",
								Toast.LENGTH_SHORT).show();
						return;
					}

					String url = "https://maps.googleapis.com/maps/api/geocode/json?";

					try {
						// encoding special characters like space in the user
						// input place
						homeLocation = URLEncoder.encode(homeLocation, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					String address = "address=" + homeLocation;

					String sensor = "sensor=false";

					// url , from where the geocoding data is fetched
					url = url + address + "&" + sensor;

					// Instantiating DownloadTask to get places from Google
					// Geocoding service
					// in a non-ui thread
					DownloadTask downloadTask = new DownloadTask();

					// Start downloading the geocoding places
					downloadTask.execute(url);

					// Change the interest back to false in order to change the
					// next member
					sameInterest = false;

				}

			}

			/*
			 * if (suggestList.getCount() == 0) { showDialog(POPUPNOONE_ALERT);
			 * }
			 */

			countForUsername = 0;

		}

	}

	public class memRetrieveLearner extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			updateJSONdata();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			// pDialog.dismiss();
			updateMap();
		}
	}

	// TC - GCM interested members
	String user_id;
	String user_interestedSub;
	String user_uuidR;

	String TAG_USERID = "userId";
	String TAG_INTERESTED_SUB = "interestedSub";
	String TAG_UUID = "device";

	JSONArray mRooms;

	ArrayList<HashMap<String, String>> mUserList;
	ArrayList<HashMap<String, String>> mPushToList;

	String RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/memRetrieveAll.php";

	class getUsersDataForPush extends AsyncTask<String, String, String> {

		boolean failure = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			try {
				mUserList = new ArrayList<HashMap<String, String>>();

				JSONParser jParser = new JSONParser();
				JSONObject json = jParser.getJSONFromUrl(RETRIEVE_URL);

				try {

					mRooms = json.getJSONArray(TAG_POSTS);

					for (int i = 0; i < mRooms.length(); i++) {
						JSONObject c = mRooms.getJSONObject(i);

						user_id = c.getString(TAG_USERID);
						Log.d("TC", "userid:" + user_id);
						user_interestedSub = c.getString(TAG_INTERESTED_SUB);
						Log.d("TC", "inst_sub:" + user_interestedSub);
						user_uuidR = c.getString(TAG_UUID);

						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_USERID, user_id);
						map.put(TAG_INTERESTED_SUB, user_interestedSub);
						map.put(TAG_UUID, user_uuidR);

						mUserList.add(map);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			// NA
		}
	}

	// TC - GCM interested members - push to interested members
	private void checkPushSameInterest() {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String mId = sp.getString("id", null);

		mPushToList = new ArrayList<HashMap<String, String>>();

		// Compare if the categories the same
		for (HashMap<String, String> map : mUserList) {
			user_id = map.get(TAG_USERID);
			user_interestedSub = map.get(TAG_INTERESTED_SUB);
			user_uuidR = map.get(TAG_UUID);

			if (!user_id.equals(mId) && !mId.equals(null)) {
				String[] parts = user_interestedSub.split(",");
				if (parts.length != 0) {
					for (int z = 0; z < parts.length; z++) {
						String sub = parts[z];
						if (sub.equalsIgnoreCase(category)) {
							HashMap<String, String> userMap = new HashMap<String, String>();

							userMap.put(TAG_USERID, user_id);
							userMap.put(TAG_INTERESTED_SUB, user_interestedSub);
							userMap.put(TAG_UUID, user_uuidR);

							mPushToList.add(map);
						}
					}
				}
			}
		}
		// Notify users
		try {
			for (HashMap<String, String> map : mPushToList) {
				user_id = map.get(TAG_USERID);
				user_interestedSub = map.get(TAG_INTERESTED_SUB);
				user_uuidR = map.get(TAG_UUID);

				String notifyUserUUID = user_uuidR;
				String nMessage = "A new room under " + category;
				nMessage += " was created, click to view.";

				//Thread.sleep(2500); // 2300

				pushNotification(getApplicationContext(), nMessage,
						notifyUserUUID, user_id);
				Log.d("CrRm", "Device:" + user_uuidR);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static final String CLASS_TAG = "GrpConnCommon";

	public void pushNotification(Context c, String msg, String device,
			String user_id) {

		Log.d(CLASS_TAG, user_id + " pushNotification - device: " + device);
		AppServices.sendMyselfANotification(c, msg, device);
		registerReceiver(notificationReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
	}

	/**
	 * Receives push Notifications
	 * */
	private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
		private AlertDialogManager alert = new AlertDialogManager();

		@Override
		public void onReceive(Context context, Intent intent) {

			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take some action upon receiving a push notification here!
			 **/
			String message = intent.getExtras().getString(EXTRA_MESSAGE);
			if (message == null) {
				message = "Empty Message";
			}

			Log.d(TAG, message);
			// messageTextView.append("\n" + message);

			alert.showAlertDialog(getApplicationContext(),
					getString(R.string.gcm_alert_title), message);
			Toast.makeText(getApplicationContext(),
					getString(R.string.gcm_message, message), Toast.LENGTH_LONG)
					.show();

			WakeLocker.release();
		}
	};
}
