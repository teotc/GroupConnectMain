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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.GCMIntentService;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.notification.AlertDialogManager;
import sg.nyp.groupconnect.notification.AppServices;
import sg.nyp.groupconnect.notification.WakeLocker;
import sg.nyp.groupconnect.room.CreateRm.RetrieveRoomById;
import sg.nyp.groupconnect.room.db.createRmMem;
import sg.nyp.groupconnect.room.db.retrieveRmMem;
import sg.nyp.groupconnect.room.db.usernameRetrieve;
import sg.nyp.groupconnect.utilities.GeocodeJSONParser;
import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.utilities.ListAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CreateRmStep3 extends Activity{
	//Variable
	TextView tvTitle, tvCategory, tvLocation;
	EditText etTitle, etCategory, etNoOfLearner;
	Button btnSuggest, btnMap, btnBack;
	ListView suggestList;
	Spinner spRadius, spCenter;
	LinearLayout llForAutoComp;
	CheckBox chkNoLocation;

	//Getting User sign in info - onCreate
	public static String mem_id = "";
	public static String mem_username = "";
	public static String mem_type = "";
	String mem_home = "";
	String mem_homeLatLng = "";	

	//For receiving intent from RoomMap - onCreate
	String location, lat, lng;

	//Dialog
	AlertDialog dialog;
	private static final int POPUPNOONE_ALERT = 1;
	private static final int LOCATIONCHOOSE_ALERT = 2;
	private static final int CLOSEACTIVITY_ALERT = 3;
	private static final int NOSELECTLOCATION_ALERT = 4;
	private static final int ADDMEM_ALERT = 5;
	private static final int REMOVEMEM_ALERT = 6;
	private static final int MAXMEM_ALERT = 7;
	boolean noLocationConfirmation;

	//onActivityResult
	//private static final int CHOOSE_LOCATION_RESULT_CODE = 100;

	//For CreateRm
	// Progress Dialog
	private ProgressDialog pDialog;
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	//testing from a real server:
	private static final String CREATE_ROOM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmCreate.php"; 
	//ids
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";



	//For Retrieving created room_id
	// testing from a real server:
	private static final String GETROOMID_URL =
			"http://www.it3197Project.3eeweb.com/grpConnect/roomIdRetrieve.php";
	private static final String TAG_ROOMID = "room_id";
	public static String createdRoomId = null;
	boolean successAll = true;


	//AsyncTask - CreateRmMem
	private static final String CREATE_ROOM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemCreate.php"; 
	public static String crmId;
	public static String crmMemType;
	public static int count = 0;

	private JSONArray mHomeInterest = null;
	private ArrayList<HashMap<String, String>> mHomeInterestList;
	// JSON IDS:
	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	private static final String TAG_ID = "userId";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_HOME = "homeLocation";
	private static final String TAG_INTEREST = "interestedSub";

	//For getting distance between 2 points
	float[] res = new float[3];
	//Users home address lat & lng
	double homeLat, homeLng;
	//Learner home address lat & lng
	double compareLat = 0;
	double compareLng = 0;
	//Less than 1000m = add into list
	ArrayList<String> suggestNameList = new ArrayList<String>();

	//Act as the image for the customListView
	Integer[] imageId = 
		{
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher

		};

	//Get all learner details to store
	String idR = "";
	String usernameR = "";
	String homeLocationR = "";
	String interestedSubR = ""; 
	String username = "";
	int countForUsername = 0;
	ArrayList<String> allIdList = new ArrayList<String>();
	ArrayList<String> suggestIdList = new ArrayList<String>();
	ArrayList<String> usernameList = new ArrayList<String> ();
	ArrayList<String> latLngList = new ArrayList<String>();
	ArrayList<String> distanceList = new ArrayList<String>();

	//Async Task - memRetrieveLearner
	private static final String MEM_RETRIEVE_LEARNER_URL = "http://www.it3197Project.3eeweb.com/grpConnect/memRetrieveWhrType.php"; 
	String homeAddress;
	LatLng latLngForCenter = null;

	//AsyncTask - categCreate
	private static final String CATEG_CREATE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/categCreate.php"; 


	//For retrieving data from CreateRm.java
	public static String title;
	String category;
	String categoryType;
	String desc;
	String maxLearner;
	String dateFrom;
	String dateTo;
	String timeFrom;
	String timeTo;
	String categoryMethod;

	//For Spinner - spRadius & spCenter
	String [] radiusList = {"1km", "2km", "3km", "4km", "5km"};
	ArrayAdapter<String> radiusAdapter;
	String selectedValueRadius = "1km";
	String [] centerList = {"Home Location", "Current Location", "Selected Location"};
	ArrayAdapter<String> centerAdapter;
	String selectedValueCenter = "Home Location";

	//For Map
	private GoogleMap mMap;
	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	Marker temp;
	double mapLat = 0;
	double mapLng = 0;
	Geocoder geocoder;
	String addressString = "";
	Marker myLocation, homeLocation;
	//To store and clear markers
	ArrayList<Marker> markerList = new ArrayList<Marker>();
	//To create the buffer
	Circle bufferCircle;
	LatLng currentLocation = null;
	double currentLat, currentLon;
	CircleOptions circleOptions = null;
	String currentAddress = "";

	//AutoComplete
	private static final String LOG_TAG = "ExampleApp";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyBykwwQsbIZk_b4ItsD5UqRRA25ADJi2H8";
	Marker selectedLocationMarker;
	String locationSelectedAutoComp = null;
	AutoCompleteTextView autoCompView;

	//Choosing Suggested members
	ArrayList<String> chosenMemberList = new ArrayList<String>();
	public static ArrayList<String> chosenMemberIdList = new ArrayList<String>();
	String selectedMemberName = null;

	//For Update
	String roomId, latForUpdate, lngForUpdate;
	boolean update = false;
	private static final String ROOM_UPDATE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomUpdate.php";
	ArrayList<String> learnerInGrpList = new ArrayList<String>(); 

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rm_step3);

		new getUsersDataForPush().execute();

		geocoder = new Geocoder(this, Locale.ENGLISH);

		chkNoLocation = (CheckBox) findViewById(R.id.chkNoLocation);

		tvLocation = (TextView) findViewById(R.id.tvLocation);
		llForAutoComp = (LinearLayout) findViewById(R.id.llForAutoComp);

		spRadius = (Spinner) findViewById(R.id.spRadius);
		radiusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, radiusList);
		spRadius.setAdapter(radiusAdapter);

		spCenter = (Spinner) findViewById(R.id.spCenter);
		centerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, centerList);
		spCenter.setAdapter(centerAdapter);

		autoCompView = (AutoCompleteTextView) findViewById(R.id.autoComplete);
		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item_autocomplete));
		autoCompView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				locationSelectedAutoComp = autoCompView.getText().toString();
				Log.i("CreateRmStep3", "AutoCompView " + locationSelectedAutoComp);
				if(locationSelectedAutoComp!=null && !locationSelectedAutoComp.equals(""))
				{
					new GeocoderTask().execute(locationSelectedAutoComp);
					selectedValueCenter = "Selected Location";
					new memRetrieveLearner().execute();
					spRadius.setSelection(0);

				}

			}});


		//Get current user details
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateRmStep3.this);
		mem_id = sp.getString("id", "No Id");
		mem_username = sp.getString("username", "No Username");
		mem_type = sp.getString("type", "No Type");
		mem_home = sp.getString("home", "No Home Location Found");
		homeLat = Double.parseDouble(sp.getString("homeLat", null));
		homeLng = Double.parseDouble(sp.getString("homeLng", null));

		Log.i("Special", String.valueOf(homeLat + "/" + homeLng));
		//GetData from CreateRm.java

		//Setting up map
		setUpMapIfNeeded();

		// You can be pretty confident that the intent will not be null here.
		Intent intent = getIntent();

		// Get the extras (if there are any)
		Bundle extras = intent.getExtras();
		if (extras != null) {

			title = this.getIntent().getStringExtra("title");
			category = this.getIntent().getStringExtra("category");
			categoryType = this.getIntent().getStringExtra("categoryType");
			desc = this.getIntent().getStringExtra("desc");
			maxLearner = this.getIntent().getStringExtra("maxLearner");
			dateFrom = this.getIntent().getStringExtra("dateFrom");
			dateTo = this.getIntent().getStringExtra("dateTo");
			timeFrom = this.getIntent().getStringExtra("timeFrom");
			timeTo = this.getIntent().getStringExtra("timeTo");
			categoryMethod = this.getIntent().getStringExtra("categoryMethod");
			location = this.getIntent().getStringExtra("location");
			//autoCompView.setText(location);
			update = this.getIntent().getBooleanExtra("update", false);
			if (update == true)
			{
				roomId = this.getIntent().getStringExtra("roomId");
				latForUpdate = this.getIntent().getStringExtra("lat");
				lngForUpdate = this.getIntent().getStringExtra("lng");
				Log.i("UpdateTest", latForUpdate + "," + lngForUpdate);
				learnerInGrpList = this.getIntent().getStringArrayListExtra("memList");

				//Set spinner to selected location and display the autocomplete textview
				chkNoLocation.setVisibility(View.GONE); //No possible to have no location when updating




			}
			if (!location.equals(""))
			{
				autoCompView.setText(location);
				//Dont let the dropdown appear after we set the location
				autoCompView.dismissDropDown();
				spCenter.setSelection(2); //Selected Location
				tvLocation.setVisibility(View.VISIBLE);
				llForAutoComp.setVisibility(View.VISIBLE);

				locationSelectedAutoComp = autoCompView.getText().toString();
				if(locationSelectedAutoComp!=null && !locationSelectedAutoComp.equals(""))
				{
					new GeocoderTask().execute(locationSelectedAutoComp);
					selectedValueCenter = "Selected Location";
					new memRetrieveLearner().execute();

				}
			}

		}


		if (update == false)
		{
			new memRetrieveLearner().execute();

			ActionBar actionBar = getActionBar();
			actionBar.setTitle("Create Room Step 3/3");

		}
		else
		{
			ActionBar actionBar = getActionBar();
			actionBar.setTitle("Update Room Step 3/3");
		}

		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

			@Override
			public void onInfoWindowClick(Marker marker) {
				// TODO Auto-generated method stub
				boolean exist = false;
				selectedMemberName = marker.getTitle();
				if (!selectedMemberName.equals("Selected Location") && !selectedMemberName.equals("Your Location") && !selectedMemberName.equals("Home"))
				{
					//Check if the name already added before
					for(int i = 0; i<chosenMemberList.size(); i++)
					{
						if (chosenMemberList.get(i).equals(selectedMemberName))
						{

							showDialog(REMOVEMEM_ALERT);
							exist = true;
						}
					}

					if (exist == false)
					{
						int maxLearnerI = Integer.parseInt(maxLearner);
						if (chosenMemberList.size() >= maxLearnerI)
							showDialog(MAXMEM_ALERT);
						else
							showDialog(ADDMEM_ALERT);
					}

					exist = false;
				}
			}
		});


		spRadius.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				selectedValueRadius = radiusList[position];
				Log.i("Special", selectedValueRadius);
				double range = 1000;
				if (selectedValueRadius.equals("1km"))
				{
					range = 1000;
				}
				else if (selectedValueRadius.equals("2km"))
				{
					range = 2000;
				}
				else if (selectedValueRadius.equals("3km"))
				{
					range = 3000;
				}
				else if (selectedValueRadius.equals("4km"))
				{
					range = 4000;
				}
				else if (selectedValueRadius.equals("5km"))
				{
					range = 5000;
				}
				else
					range = 1000;

				userRadiusChange(range, latLngForCenter);
				//new memRetrieveLearner().execute();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

		spCenter.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

				//User want to use selected location as center point
				//Check if the EditText etLocation is empty
				if (centerList[position].equals("Selected Location"))
				{
					tvLocation.setVisibility(View.VISIBLE);
					llForAutoComp.setVisibility(View.VISIBLE);

				}
				else
				{
					tvLocation.setVisibility(View.GONE);
					llForAutoComp.setVisibility(View.GONE);
					selectedValueCenter = centerList[position];
					new memRetrieveLearner().execute();
					spRadius.setSelection(0);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			showDialog(CLOSEACTIVITY_ALERT);

		}
		return false;
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("Play", String.valueOf(suggestNameList.size()));

	}



	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
	}

	public void userRadiusChange(double range, LatLng center)
	{

		//Clear all user marker on the map
		for(int i = 0; i<markerList.size(); i++)
		{
			markerList.get(i).remove();
		}
		markerList.clear();
		boolean exist = false;

		for(int i = 0; i<suggestNameList.size(); i++)
		{
			if(Double.parseDouble(distanceList.get(i)) < range)
			{
				String[] tempArr = latLngList.get(i).split(",");
				double userLat = Double.parseDouble(tempArr[0]);
				double userLng = Double.parseDouble(tempArr[1]);
				Marker userLocation = null;
				boolean existForUpdate = false;

				//For Update. Dont display members as suggested member when 
				//they are already in the grp
				if (learnerInGrpList.size() > 0)
				{
					for(int z = 0; z<learnerInGrpList.size(); z++)
					{
						if (learnerInGrpList.get(z).equals(suggestNameList.get(i)))
						{
							existForUpdate = true;
						}
					}
				}
				if (existForUpdate == false)
				{
					if (chosenMemberList.size() > 0)
					{
						for(int z = 0; z<chosenMemberList.size(); z++)
						{
							if (chosenMemberList.get(z).equals(suggestNameList.get(i)))
							{

								userLocation = mMap.addMarker(new MarkerOptions()
								.position(new LatLng(userLat,userLng))
								.title(suggestNameList.get(i))
								.snippet(distanceList.get(i) + "m away")
								.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

								exist = true;
							}
						}
					}

					if (exist == false)
					{
						userLocation = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(userLat,userLng))
						.title(suggestNameList.get(i))
						.snippet(distanceList.get(i) + "m away")); 
					}

					markerList.add(userLocation);
					exist = false;
				}

			}

		}


		if (center != null)
		{
			// Zoom in or out, animating with a duration of 1 seconds.
			float zoom = mMap.getCameraPosition().zoom;
			Log.i("Play", String.valueOf(zoom));
			if (range == 1000)
				zoom = 14;
			else if (range == 2000)
				zoom = 13.5f;
			else if (range == 3000)
				zoom = 13;
			else if (range == 4000)
				zoom = 12.5f;
			else if (range == 5000)
				zoom = 12;

			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, zoom));            

			circleOptions = new CircleOptions()
			.center(center)   //set center
			.radius(range)   //set radius in meters
			.fillColor(Color.TRANSPARENT)  //default
			.strokeColor(Color.BLUE)
			.strokeWidth(5);

			if (bufferCircle != null)
				bufferCircle.remove();

			bufferCircle = mMap.addCircle(circleOptions);

		}

	}

	AlertDialog dialog1;


	protected Dialog onCreateDialog(int id) 
	{
		Log.i("sg.nyp.groupconnect", "onCreateDialog");

		if (id == LOCATIONCHOOSE_ALERT)
		{
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to create a room at \n" + addressString);
			builder.setCancelable(true);
			builder.setPositiveButton("Okay", new OkLocationOnClickListener());
			builder.setNegativeButton("Cancel", new CancelOnClickListener());
			dialog = builder.create();
			dialog.show();

		}
		else if(id == CLOSEACTIVITY_ALERT)
		{
			Builder builder2 = new AlertDialog.Builder(this);
			builder2.setMessage("Pressing the back button will lead you to the main page. \nAre you sure?");
			builder2.setCancelable(true);
			builder2.setPositiveButton("Okay", new OkCloseActivityOnClickListener());
			builder2.setNegativeButton("Cancel", new CancelOnClickListener());
			dialog1 = builder2.create();
			dialog1.show();
		}
		else if(id == NOSELECTLOCATION_ALERT)
		{
			Builder builder2 = new AlertDialog.Builder(this);
			builder2.setMessage("Are you sure you do not want to choose a location now?");
			builder2.setCancelable(true);
			builder2.setPositiveButton("Okay", new OkNoLocationClickListener());
			builder2.setNegativeButton("Cancel", new CancelNoLocationClickListener());
			dialog1 = builder2.create();
			dialog1.show();
		}
		else if(id == ADDMEM_ALERT)
		{
			Builder builder2 = new AlertDialog.Builder(this);
			builder2.setMessage("Are you sure you want to select this member?");
			builder2.setCancelable(true);
			builder2.setPositiveButton("Okay", new OkChooseMemClickListener());
			builder2.setNegativeButton("Cancel", new CancelOnClickListener());
			dialog1 = builder2.create();
			dialog1.show();
		}
		else if(id == REMOVEMEM_ALERT)
		{
			Builder builder2 = new AlertDialog.Builder(this);
			builder2.setMessage("Are you sure you want to remove this member?");
			builder2.setCancelable(true);
			builder2.setPositiveButton("Okay", new OkRemoveMemClickListener());
			builder2.setNegativeButton("Cancel", new CancelOnClickListener());
			dialog1 = builder2.create();
			dialog1.show();
		}
		else if(id == MAXMEM_ALERT)
		{
			Builder builder2 = new AlertDialog.Builder(this);
			builder2.setMessage("Maximum Member reached. \nMax no. of Learner choosen: "+ maxLearner);
			builder2.setCancelable(true);
			builder2.setPositiveButton("Okay", new CancelOnClickListener());
			dialog1 = builder2.create();
			dialog1.show();
		}


		return super.onCreateDialog(id);
	}


	private final class OkOnClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			selectedValueCenter = "Home Location";
			new memRetrieveLearner().execute();
			spCenter.setSelection(0);
			dialog.dismiss();
		}
	} 

	private final class OkLocationOnClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			location = addressString;
			lat = String.valueOf(mapLat);
			lng = String.valueOf(mapLng);
			autoCompView.setText(location);
			temp.remove();
		}
	}

	private final class OkCloseActivityOnClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			//Will kill all activity and go to RoomMamp Class
			Intent intent = new Intent(getApplicationContext(), RoomMap.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	} 


	private final class OkChooseMemClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			double range = 1000;

			chosenMemberList.add(selectedMemberName);


			for(int i = 0; i<suggestNameList.size(); i++)
			{
				if (suggestNameList.get(i).equals(selectedMemberName))
				{
					chosenMemberIdList.add(suggestIdList.get(i));
				}
			}

			if (selectedValueRadius.equals("1km"))
			{
				range = 1000;
			}
			else if (selectedValueRadius.equals("2km"))
			{
				range = 2000;
			}
			else if (selectedValueRadius.equals("3km"))
			{
				range = 3000;
			}
			else if (selectedValueRadius.equals("4km"))
			{
				range = 4000;
			}
			else if (selectedValueRadius.equals("5km"))
			{
				range = 5000;
			}
			else
				range = 1000;

			userRadiusChange(range,latLngForCenter);

		}
	} 

	private final class OkRemoveMemClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			double range = 1000;

			chosenMemberList.remove(selectedMemberName);

			for(int i = 0; i<suggestNameList.size(); i++)
			{
				if (suggestNameList.get(i).equals(selectedMemberName))
				{
					chosenMemberIdList.remove(suggestIdList.get(i));
				}
			}

			if (selectedValueRadius.equals("1km"))
			{
				range = 1000;
			}
			else if (selectedValueRadius.equals("2km"))
			{
				range = 2000;
			}
			else if (selectedValueRadius.equals("3km"))
			{
				range = 3000;
			}
			else if (selectedValueRadius.equals("4km"))
			{
				range = 4000;
			}
			else if (selectedValueRadius.equals("5km"))
			{
				range = 5000;
			}
			else
				range = 1000;

			userRadiusChange(range,latLngForCenter);
		}
	} 

	private final class OkNoLocationClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			noLocationConfirmation = true;
			dialog.dismiss();

		}
	} 

	private final class CancelOnClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			if (temp !=null)
				temp.remove();	
		}
	}

	private final class CancelNoLocationClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) 
		{
			noLocationConfirmation = false;	
			dialog.dismiss();
		}
	}

	@Override


	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_rm_step3, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.submit) {

			boolean success = true;

			if (chkNoLocation.isChecked() == true)
			{
				//Okay - noLocationConfirmation = true
				//Cancel - noLocationConfirmation = false //Dont want to continue creating
				showDialog(NOSELECTLOCATION_ALERT);


			}
			else
				noLocationConfirmation = true;
			//Default is true and Confirm do not want to have location also true
			if (noLocationConfirmation == true)
			{
				if (autoCompView.getText().toString().length() <= 0)
				{
					//etLocation.setError("Enter Location");
					autoCompView.setText("none");
				}

				if (success == true) //If all fields are filled
				{
					if (update == false) //For Creation
					{
						checkPushSameInterest();

						new createRoom().execute();
						//Once the room is created successfully in Table 'Room'
						//Find the room Id created.
						new retrieveCreatedRmId().execute();


						//Than use the roomId to create another row for RoomMember to create Member data
						chosenMemberIdList.add(mem_id);
						if (chosenMemberIdList.size() > 0)
						{
							new createRmMem().execute();
							new MemDetailsRetrieve().execute();

						}

						if (categoryMethod.equals("New"))
						{
							new categCreate().execute();
						}


						if (successAll == true)
						{
							Toast.makeText(CreateRmStep3.this, "Room created successfully." , Toast.LENGTH_LONG).show();
							//Will kill all activity and go to RoomMap Class
							Intent intent = new Intent(getApplicationContext(), RoomMap.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					}
					else //For Update
					{
						new RoomUpdate().execute();

						if (chosenMemberIdList.size() > 0)
						{
							new createRmMem().execute();
						}

						//Will kill all activity and go to RoomDetails Class
						Intent intent = new Intent(getApplicationContext(), RoomDetails.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("title", title);
						intent.putExtra("location", autoCompView.getText().toString());
						intent.putExtra("category", category);
						startActivity(intent);
					}


				}
			}
			return true;
		}
		else if (id == R.id.back)
		{
			//put data to be return to parent in an intent
			Intent output = new Intent();
			output.putExtra("Cancel", "Canceled");
			// Set the results to be returned to parent
			setResult(RESULT_CANCELED, output);

			// Ends the sub-activity
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpMapIfNeeded() {
		Log.i("sg.nyp.groupconnect", "setUpMapIfNeeded");
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			//mMap = (GoogleMap) findViewById(R.id.map);
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		Log.i("sg.nyp.groupconnect", "setUpMap");
		//mMap.addMarker(new MarkerOptions().position(new LatLng(1.352083, 103.819836)).title("Marker"));
		Log.i("Test", "In setUpMap");

		if (mMap!=null){

			// Move the camera instantly to Singapore with a zoom of 15.
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 15));

			// Zoom in, animating the camera.
			mMap.animateCamera(CameraUpdateFactory.zoomIn());

			// Zoom out to zoom level 10, animating with a duration of 2 seconds.
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


			//Allow user to set their own marker
			mMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) 
				{
					if (temp != null)
						temp.remove();

					temp = mMap.addMarker(new MarkerOptions().position(point));

					// you can get latitude and longitude also from 'point'
					// and using Geocoder, the address

					//To get the address of the place clicked by the user
					mapLat = point.latitude;
					mapLng = point.longitude;
					addressString = convertLatLngToAddress(mapLat, mapLng);



					showDialog(LOCATIONCHOOSE_ALERT);

				}

			});

			//Set Home Location
			homeLocation = mMap.addMarker(new MarkerOptions()
			.position(new LatLng(homeLat,homeLng))
			.title("Home")
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.home_mapicon)));


			//Get the current location
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

			locManager.requestLocationUpdates(provider, 2000, 10, locationListener);



		}
	}

	public String convertLatLngToAddress(double mapLat, double mapLng)
	{
		try 
		{

			List<Address> addresses = geocoder.getFromLocation(mapLat, mapLng, 1);

			if(addresses != null) 
			{
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");
				for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) 
				{
					strReturnedAddress.append(returnedAddress.getAddressLine(i));
				}

				addressString = strReturnedAddress.toString();
			}

			else
			{
				addressString = "No Address returned!";
			}

		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			addressString = "Cannot get Address!";
		}

		return addressString;

	}

	//Current Location START
	private void updateWithNewLocation(Location loc)
	{
		String latLonString;

		if (loc != null)
		{
			if(myLocation != null)
				myLocation.remove();
			currentLat = loc.getLatitude();
			currentLon = loc.getLongitude();

			currentLocation = new LatLng(currentLat, currentLon);
			Marker myLocation = mMap.addMarker(new MarkerOptions()
			.position(currentLocation)
			.title("Your Location")
			.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.currentlocation_mapicon)));

			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));

			latLonString = "Lat: " + currentLat + "\n" + "Lon: " + currentLon;

			getAddress(currentLat, currentLon);

		}
		else
		{
			latLonString = "No Location found";
		}

		Log.i("Special", latLonString);

	}

	private final LocationListener locationListener = new LocationListener()
	{
		public void onLocationChanged(Location arg0)
		{
			updateWithNewLocation(arg0);
		}

		public void onProviderDisabled(String arg0)
		{
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}
	};

	public List<Address> getAddress(double latitude, double longitude) {
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(CreateRmStep3.this);
			if (latitude != 0 || longitude != 0) {
				addresses = geocoder.getFromLocation(latitude ,
						longitude, 1);
				currentAddress = addresses.get(0).getAddressLine(0);

				return addresses;
			} else {
				Toast.makeText(CreateRmStep3.this, "latitude and longitude are null",
						Toast.LENGTH_LONG).show();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//Current Location END

	//For HOME Location START
	private String downloadUrl(String strUrl) throws IOException{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);
			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while( ( line = br.readLine()) != null){
				sb.append(line);
			}

			data = sb.toString();
			br.close();

		}catch(Exception e){
			Log.d("Exception while downloading url", e.toString());
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}

		return data;
	}
	/** A class, to download Places from Geocoding webservice */

	private class DownloadTask extends AsyncTask<String, Integer, String>
	{

		String data = null;

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try{
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result){

			// Instantiating ParserTask which parses the json data from Geocoding webservice
			// in a non-ui thread
			ParserTask parserTask = new ParserTask();

			// Start parsing the places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);

		}
	}

	/** A class to parse the Geocoding Places in non-ui thread */
	class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String,String>> doInBackground(String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try{
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

				if (places == null)
					Log.i("Hihi", "Places is Empty");

			}catch(Exception e){
				Log.d("Exception",e.toString());
			}
			return places;
		}



		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String,String>> list){

			// Getting a place from the places list
			//Get only the first location
			if (list.size() == 0)
			{
				Log.i("Hihi", "List is Empty");
			}

			HashMap<String, String> hmPlace = list.get(0);

			// Getting latitude of the place
			compareLat = Double.parseDouble(hmPlace.get("lat"));

			// Getting longitude of the place
			compareLng = Double.parseDouble(hmPlace.get("lng"));
			Log.i("CreateRmStep2", selectedValueCenter);
			if (selectedValueCenter.equals("Home Location"))
			{
				//Compare the distance and store it into a float array, res
				Location.distanceBetween(homeLat, homeLng, compareLat ,compareLng, res);
				latLngForCenter = new LatLng (homeLat, homeLng);
				Log.i("CreateRmStep2", homeLat + "," + homeLng);
			}
			else if (selectedValueCenter.equals("Current Location"))
			{
				//Compare the distance and store it into a float array, res
				Location.distanceBetween(currentLat, currentLon, compareLat ,compareLng, res);
				latLngForCenter = new LatLng (currentLat, currentLon);
				Log.i("CreateRmStep2", currentLat + "," + currentLon);
			}
			else if (selectedValueCenter.equals("Selected Location"))
			{
				Location.distanceBetween(mapLat, mapLng, compareLat ,compareLng, res);
				latLngForCenter = new LatLng (mapLat, mapLng);
				Log.i("CreateRmStep2", mapLat + "," + mapLng);
			}



			//If there is result in res array
			if (res.length != 0)
			{


				int range = 5000;

				Log.i("mm", "RES:" + String.valueOf(res[0]));    
				//Toast.makeText(getApplicationContext(), String.valueOf(res[0]), Toast.LENGTH_LONG).show();
				//If the result, that is in meter, is less than 1000m
				//Store and display into the listview

				if (res[0] < range)
				{
					Log.i("CreateRm", String.valueOf(res[0]));
					//countForUsername is used to get the correct name
					suggestIdList.add(allIdList.get(countForUsername));
					suggestNameList.add(usernameList.get(countForUsername));
					latLngList.add(compareLat + "," + compareLng);
					distanceList.add(String.valueOf(res[0]));
					countForUsername++;

				}
				else
					countForUsername++;
			}


			userRadiusChange(1000, latLngForCenter);




		}
	}

	//For HOME Location END

	//For AutoComplete
	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
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
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
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
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					}
					else {
						notifyDataSetInvalidated();
					}
				}};
				return filter;
		}
	}

	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

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

			if(addresses==null || addresses.size()==0){
				Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
			}

			// Clears all the existing markers on the map
			//mMap.clear();
			if (selectedLocationMarker != null)
				selectedLocationMarker.remove();

			// Adding Markers on Google Map for each matching address
			for(int i=0;i<addresses.size();i++){

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

				mapLat = address.getLatitude();
				mapLng = address.getLongitude();

				lat = String.valueOf(mapLat);
				lng = String.valueOf(mapLng);

				String addressText = String.format("%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
								address.getCountryName());

				selectedLocationMarker = mMap.addMarker(new MarkerOptions()
				.position(latLng)
				.title("Selected Location")
				.snippet(addressText)
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

			}
		}
	}


	class createRoom extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;

			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateRmStep3.this);
			String post_title = title;
			String post_category = category;
			String post_noOfLearner = maxLearner;
			String post_location = "none";
			String post_latLng = "";
			//Check for any empty EditText
			if (chkNoLocation.isChecked() == false)
			{
				if(spCenter.getSelectedItemPosition() == 0)//HomeLocation
				{
					post_location = mem_home;
					post_latLng = homeLat + "," + homeLng;
				}
				else if (spCenter.getSelectedItemPosition() == 1)//Current Location
				{
					post_location = currentAddress;
					post_latLng = currentLat + "," + currentLon; 
				}
				else if (spCenter.getSelectedItemPosition() == 2)//SelectedLocation
				{
					post_location = autoCompView.getText().toString();
					if (lat.equals(null) && lng.equals(null))
						post_latLng = null;
					else
						post_latLng = lat + "," + lng;
				}

			}

			String post_creatorId = sp.getString("id", "");
			String post_desc = desc;
			String post_dateFrom = dateFrom;
			String post_dateTo = dateTo;
			String post_timeFrom = timeFrom;
			String post_timeTo = timeTo;



			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				//params.add(new BasicNameValuePair("username", post_username));
				params.add(new BasicNameValuePair("title", post_title));
				params.add(new BasicNameValuePair("category", post_category));
				params.add(new BasicNameValuePair("noOfLearner", post_noOfLearner));
				params.add(new BasicNameValuePair("location", post_location));
				params.add(new BasicNameValuePair("latLng", post_latLng));
				params.add(new BasicNameValuePair("creatorId", post_creatorId));
				params.add(new BasicNameValuePair("description", post_desc));
				params.add(new BasicNameValuePair("status", "Not Started"));
				params.add(new BasicNameValuePair("dateFrom", post_dateFrom));
				params.add(new BasicNameValuePair("dateTo", post_dateTo));
				params.add(new BasicNameValuePair("timeFrom", post_timeFrom));
				params.add(new BasicNameValuePair("timeTo", post_timeTo));

				Log.d("request!", "starting");

				//Posting user data to script 
				JSONObject json = jsonParser.makeHttpRequest(
						CREATE_ROOM_URL, "POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());    

					return json.getString(TAG_MESSAGE);
				}else{
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
			//pDialog.dismiss();
			
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
				JSONObject json = jsonParser.makeHttpRequest(GETROOMID_URL, "POST",
						params);

				// check your log for json response
				Log.d("Login attempt", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				//createdRoomId = json.getString(TAG_ROOMID).toString() + "?";

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
			if (file_url != null) {
			}

		}

	}


	class categCreate extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String post_name = category;
			String post_typeId = "";
			if (categoryType.equals("School Subjects"))
				post_typeId = "1";
			if (categoryType.equals("Music"))
				post_typeId = "2";
			if (categoryType.equals("Computer-related"))
				post_typeId = "3";
			if (categoryType.equals("Others"))
				post_typeId = "4";

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				//params.add(new BasicNameValuePair("username", post_username));
				params.add(new BasicNameValuePair("name", post_name));
				params.add(new BasicNameValuePair("typeId", post_typeId));

				Log.d("request!", "starting");

				//Posting user data to script 
				JSONObject json = jsonParser.makeHttpRequest(
						CATEG_CREATE_URL, "POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());    

					return json.getString(TAG_MESSAGE);
				}else{
					Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
					successAll= false;
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

			for (int i = 0; i < mHomeInterest.length(); i++) {
				JSONObject c = mHomeInterest.getJSONObject(i);

				idR = c.getString(TAG_ID);
				usernameR = c.getString(TAG_USERNAME);
				homeLocationR = c.getString(TAG_HOME);
				interestedSubR = c.getString(TAG_INTEREST);

				Log.i("Hihi", "UsernameR: " + usernameR);
				Log.i("Hihi", "homeLocationR: " + homeLocationR);
				Log.i("Hihi", "interestedSubR: " + interestedSubR);

				HashMap<String, String> map = new HashMap<String, String>();
				map.put(TAG_ID, idR);
				map.put(TAG_USERNAME, usernameR);
				map.put(TAG_HOME, homeLocationR);
				map.put(TAG_INTEREST, interestedSubR);

				mHomeInterestList.add(map); 

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	private void updateMap() {
		Log.i("sg.nyp.groupconnect", "updateMap");
		boolean sameInterest = false;
		if (mHomeInterestList != null)
		{
			//Clear the list of chosen name before starting to search
			suggestNameList.clear();
			distanceList.clear();
			latLngList.clear();
			suggestIdList.clear();

			//Clear all user marker on the map
			for(int i = 0; i<markerList.size(); i++)
			{
				markerList.get(i).remove();
			}
			markerList.clear();

			for (int i = 0; i<mHomeInterestList.size(); i++)
			{
				//get the username, homeAddrss and interestedSub for each member
				idR = mHomeInterestList.get(i).get(TAG_ID);
				username = mHomeInterestList.get(i).get(TAG_USERNAME);
				homeAddress = mHomeInterestList.get(i).get(TAG_HOME);
				String interestedSub = mHomeInterestList.get(i).get(TAG_INTEREST);

				Log.i("Hihi", "Username: " + username);
				Log.i("Hihi", "homeAddress: " + homeAddress);
				Log.i("Hihi", "interestedSub: " + interestedSub);

				//Spliting the interests in order to compare with etCategory 
				String[] parts = interestedSub.split(",");
				if (parts.length != 0)
				{
					for (int z = 0; z<parts.length; z++)
					{
						String sub = parts[z];
						Log.i("CreateRm", "Interest sub for " + username + " = " + sub);
						if(sub.equals(category))
						{

							sameInterest = true;
						}
					}
				}

				Log.i("Hihi", "SameInterest = " + sameInterest);

				//If the learner have the same interest, get convert their home location to latLng to compare
				//with the creator's home address latlng
				//Within 1000m, means can be suggested in the listview
				if (sameInterest == true)
				{
					Log.i("Hihi","Username with sameInterest" + username);
					Log.i("Hihi", homeAddress);
					//Insert the username into a list as a selected member to be retrieve from the ParserTask
					//onPostExecute later on
					usernameList.add(username);
					allIdList.add(idR);

					String homeLocation = homeAddress;

					if(homeLocation==null || homeLocation.equals("")){
						Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
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

					// Instantiating DownloadTask to get places from Google Geocoding service
					// in a non-ui thread
					DownloadTask downloadTask = new DownloadTask();

					// Start downloading the geocoding places
					downloadTask.execute(url);

					//Change the interest back to false in order to change the next member
					sameInterest = false;


				}


			}

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
			//pDialog.dismiss();
			updateMap();


		}
	}

	class RoomUpdate extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String post_roomId = roomId;
			String post_title = title;
			String post_category = category;
			String post_location = "";
			String post_latLng = "";
			if(spCenter.getSelectedItemPosition() == 0)//HomeLocation
			{
				post_location = mem_home;
				post_latLng = homeLat + "," + homeLng;
			}
			else if (spCenter.getSelectedItemPosition() == 1)//Current Location
			{
				post_location = currentAddress;
				post_latLng = currentLat + "," + currentLon; 
			}
			else if (spCenter.getSelectedItemPosition() == 2)//SelectedLocation
			{
				post_location = autoCompView.getText().toString();
				if (lat.equals(null) && lng.equals(null))
					post_latLng = null;
				else
					post_latLng = lat + "," + lng;
			}
			String post_maxLearner = maxLearner;
			String post_dateFrom = dateFrom;
			String post_dateTo = dateTo;
			String post_timeFrom = timeFrom;
			String post_timeTo = timeTo;
			String post_desc = desc;



			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				//params.add(new BasicNameValuePair("username", post_username));
				params.add(new BasicNameValuePair("room_id", post_roomId));
				params.add(new BasicNameValuePair("title", post_title));
				params.add(new BasicNameValuePair("category", post_category));
				params.add(new BasicNameValuePair("location", post_location));
				params.add(new BasicNameValuePair("latLng", post_latLng));
				params.add(new BasicNameValuePair("noOfLearner", post_maxLearner));
				params.add(new BasicNameValuePair("description", post_desc));
				params.add(new BasicNameValuePair("dateFrom", post_dateFrom));
				params.add(new BasicNameValuePair("dateTo", post_dateTo));
				params.add(new BasicNameValuePair("timeFrom", post_timeFrom));
				params.add(new BasicNameValuePair("timeTo", post_timeTo));

				Log.d("request!", "starting");

				//Posting user data to script 
				JSONObject json = jsonParser.makeHttpRequest(
						ROOM_UPDATE_URL, "POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());    
					//finish();
					return json.getString(TAG_MESSAGE);
				}else{
					Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			if (file_url != null){
				Toast.makeText(CreateRmStep3.this, "Room Updated.", Toast.LENGTH_LONG).show();
			}

		}

	}


	private static final String USERNAME_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/usernameRetrieve.php";
	String uuidR;
	String memNameR = "";
	int countForInvite = 0;
	ArrayList<String> listOfMemUUID = new ArrayList<String>();
	public static int countForMemId = 0;

	class MemDetailsRetrieve extends AsyncTask<String, String, String> 
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String id = chosenMemberIdList.get(countForInvite); //Avoid running a incorrect id.
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", id));

				Log.d("request!", "starting");
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(USERNAME_RETRIEVE_URL, "POST",
						params);

				// check your log for json response
				Log.d("Login attempt", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					Log.d("Login Successful!", json.toString());

					memNameR = json.getString("username");
					uuidR = json.getString("device");
					listOfMemUUID.add(uuidR);
					return json.getString("username") + "/" + json.getString("device");
				} else {
					Log.d("Login Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			//pDialog.dismiss();
			if (file_url != null) {

				String notifyUserUUID = listOfMemUUID.get(countForInvite);
				Log.i("CreateRmStep3", notifyUserUUID);
				if (!notifyUserUUID.equals(""))	
				{
					GCMIntentService gcm = new GCMIntentService();
					gcm.setType("Invite");
					String nMessage = "Do you want to join " + title + "?";
					pushNotificationForInvite( getWindow().getDecorView().findViewById(android.R.id.content), nMessage, notifyUserUUID);

					if (countForInvite < chosenMemberIdList.size()-1)
					{
						//Storing of uuid for push notification later on
						Log.i("RoomDetails", "UserRetrieveL uuidR" + uuidR);
						countForInvite++; //Add the counter for the next time we run the method
						if (chosenMemberIdList.get(countForInvite).equals(mem_id))
						{
							countForInvite++;

						}
						//Check twice because, if the previous one is mem_id, countForInvite is added again
						if (countForInvite < chosenMemberIdList.size()-1)
							new MemDetailsRetrieve().execute();
					}
				}

			}

		}
	}
	
	public void pushNotificationForInvite(View v, String msg, String device)
	{

		//Log.i(TAG_NO, username + " pushNotification - device: " + device); 
		AppServices.sendMyselfANotification(CreateRmStep3.this, msg, device);
		registerReceiver(notificationReceiverForInvite, new IntentFilter(DISPLAY_MESSAGE_ACTION));
		//comeFromMain = true;
	}

	/**
	 * Receives push Notifications
	 * */
	int mId = 1;
	private final BroadcastReceiver notificationReceiverForInvite = new BroadcastReceiver() 
	{
		private AlertDialogManager alert = new AlertDialogManager();
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {

			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());



			//new CreateYesMember().execute();

			/**
			 * Take some action upon receiving a push notification here!
			 **/
			String message = intent.getExtras().getString(EXTRA_MESSAGE);
			if (message == null) { message = "Empty Message"; }

			Log.i(TAG, message);

			WakeLocker.release();
		}
	};





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

				GCMIntentService gcm = new GCMIntentService();
				gcm.setType("InterestedMember");
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

