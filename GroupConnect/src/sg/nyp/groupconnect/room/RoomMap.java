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
import java.sql.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.custom.PopupAdapter;
import sg.nyp.groupconnect.utilities.GeocodeJSONParser;
import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.*;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.app.FragmentActivity;


public class RoomMap extends FragmentActivity implements OnInfoWindowClickListener {

	//Main Map
	private GoogleMap mMap;
	ImageView btnFind;
	EditText etSearch;
	MarkerOptions markerOptions;
	Marker searchMarker = null;
	Marker homeMarker = null;
	//To know the center of singapore
	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	AutoCompleteTextView autoCompView;

	//Dialog Method
	AlertDialog dialog;
	private static final int CREATERM_ALERT = 1;
	private static final int MAIN = 2;
	private static final int CREATESEARCH_ALERT = 3;
	private static final int ROOMJOIN_ALERT = 4;
	
	//onActivityResult
	private static final int CREATE_RM_RESULT_CODE = 100;
	

	//AsyncTask - LoadRoom
	///Codes to retrieve all rooms
	/// Progress Dialog
	private ProgressDialog pDialog;
	/// testing from a real server:
	private static final String ROOM_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomRetrieve.php";
	 
	/// JSON IDS:
	private static final String TAG_SUCCESS = "success";
	///private static final String TAG_TITLE = "title";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	///private static final String TAG_USERNAME = "username";
	private static final String TAG_MESSAGE = "message";
	
	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_NOOFLEARNER = "noOfLearner";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LATLNG = "latLng";
	private static final String TAG_TYPENAME = "typeName";
	private static final String TAG_STATUS = "status";

	/// An array of all of our comments
	private JSONArray mComments = null;
	/// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mCommentList;
	 
	///LoadRoom Variables
    String roomIdR, titleR, locationR, categoryR, noOfLearnerR, latLngR, usernameR, typeNameR, statusR;
    double latR, lngR;
    LatLng retrievedLatLng;
    Marker retrievedMarker = null;
    List<Marker> listOfMarker = new ArrayList<Marker>(); //to save all the marker
	 

	//setupMap()
	double lat, lng; 
    Marker temp;
	/// To get the location data
 	
 	String addressString = "";
 	boolean getCurrentUserLocation = true;
    
    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBykwwQsbIZk_b4ItsD5UqRRA25ADJi2H8";
    
    public static final String MyPREFERENCES = "MyPrefs" ;
	SharedPreferences sharedpreferences;
	
	//For Slide Drawer
	public static SlidingDrawer slide;
	public static RadioGroup rgCategory;
	public static ArrayList <String> schSubList = new ArrayList<String>();
	public static ArrayList <String> musicList = new ArrayList<String>();
	public static ArrayList <String> computerList = new ArrayList<String>();
	public static ArrayList <String> othersList = new ArrayList<String>();
	public static ArrayList <String> categoryList = new ArrayList<String>();
	int countForRbtnId = 0;
	String searchCategory = "";

	//Retrieve All Sub
	private static final String RETRIEVE_ALL_SUB_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveSubjectsWithTypeAll.php";
	private JSONArray mCategory = null;
	private ArrayList<HashMap<String, String>> mCategoryList;
	private static final String TAG_NAME = "name";
	String name, typeName;
	
	//Retrieve Rooms according to category
	private static final String RETRIEVE_ROOM_WITH_CATEGORY_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomRetrieveWithCategory.php";
	private JSONArray mSearch = null;
	private ArrayList<HashMap<String, String>> mSearchList;
    
	public static ArrayList<Integer> resourceForIconArr = new ArrayList<Integer>();
	public static int count = 0;
	
	//Current Location Marker
	Marker myLocation;
	
	public void setImageIcon(int resource)
	{
		resourceForIconArr.add(resource);
	}

	public void resetCount()
	{
		count = 0;
	}
	
	//For when notification arrive
	boolean notificationArrive = false;
	String titleToFocus = "";
	
	//CreateYesMember - AsyncTask
	String roomIdForYes;
	private static final String CREATE_ROOM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemCreate.php"; 
	String yesMemId = "";
	JSONParser jsonParser = new JSONParser();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_map);

        
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
 
		setUpMapIfNeeded();
        
        
        //To use custom infowindow
        Log.i("sg.nyp.groupconnect", "onCreate");
        
       //When user agree to join the room (Invite from CreateRmStep3)
		Intent intent1 = getIntent();
        Bundle extras = intent1.getExtras();
        if (extras != null) {
        	
        	roomIdForYes = extras.getString("roomId");
        	yesMemId = extras.getString("memId");
    		Log.i("CreateRmStep3", "YesMemId" + yesMemId + roomIdForYes);
    		new CreateYesMember().execute();
        }
        
        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rLayout);
        rLayout.setVisibility(View.VISIBLE);
        
        btnFind = (ImageView) findViewById(R.id.btnFind);
        
        
        autoCompView = (AutoCompleteTextView) findViewById(R.id.autoComplete);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item_autocomplete));
        autoCompView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//Log.i("Special", "AutoComplete: " + str);
				// Getting user input location
				String location = autoCompView.getText().toString();

				if(location!=null && !location.equals("")){
					new GeocoderTask().execute(location);
				}
			}});
        
        ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);
        
		//ForSlideDrawer
		sildeDrawerSetup();
        
		btnFind.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Getting user input location
				String location = autoCompView.getText().toString();

				if(location!=null && !location.equals("")){
					new GeocoderTask().execute(location);
				}
		}});
        
        
    }
	    
		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("sg.nyp.groupconnect", "onCreateOptionMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		
		//Display icon only on Main Page. If come from CreateRm,
		getMenuInflater().inflate(R.menu.room_map, menu);

		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.i("sg.nyp.groupconnect", "onOptionsItemSelected");
		int id = item.getItemId();
		if (id == android.R.id.home)
		{
			if (notificationArrive == false)
				RoomMap.this.finish();
			else
			{
				notificationArrive = false;
				RoomMap.this.finish();
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
			}
		}
		if (id == R.id.refresh) {
			mMap.clear();
			new LoadRooms().execute();
		}
		if (id == R.id.notification)
		{
			Intent myIntent = new Intent(RoomMap.this,NotificationDisplay.class);
	      	startActivity(myIntent);
		}
		if (id == R.id.add)
		{
			Intent myIntent = new Intent(RoomMap.this,CreateRm.class);
	      	startActivityForResult(myIntent,CREATE_RM_RESULT_CODE);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
    
    //Once the infowindow is clicked
    public void onInfoWindowClick(Marker marker) {
    	Log.i("sg.nyp.groupconnect", "onInfoWindowClick");
    	if (!marker.getTitle().equals("Search Location") && !marker.getTitle().equals("Home") && !marker.getTitle().equals("Your Location"))
    	{
    		//Split the content to get the location and category
    		String content = marker.getSnippet();
    		String [] split = content.split("\n"); // NOTE: Null Pointer --TC
    		String[] category = split[0].split(":");
    		String[] location  = split[1].split(":");

    		//There is a extra space in front.
    		String locationDetails = location[1].substring(1);
    		String categoryDetails = category[1].substring(1);

    		Intent myIntent1 = new Intent(RoomMap.this,RoomDetails.class);
    		myIntent1.putExtra("title", marker.getTitle());
    		myIntent1.putExtra("location", locationDetails);
    		myIntent1.putExtra("category", categoryDetails);
    		startActivity(myIntent1);
    	}
    	else if (marker.getTitle().equals("Search Location"))
    	{
    		showDialog(CREATESEARCH_ALERT);
    	}
      }


    
    @Override
	protected void onResume() {
		Log.i("sg.nyp.groupconnect", "onResume");
        super.onResume();
        //setUpMapIfNeeded();
        // loading the rooms via AsyncTask
        mMap.clear();
        new LoadRooms().execute();

        
        	
    }
    
    

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}

	private void setUpMapIfNeeded() {
    	Log.i("sg.nyp.groupconnect", "setUpMapIfNeeded");
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
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
 
            	          
            	    }
            	    
            	});
              
              
  	    	
            }
    }
    
    //Slide Drawer START
    
       private void sildeDrawerSetup() {
    	rgCategory = (RadioGroup) findViewById(R.id.rgCategory);
    	
    	new retrieveAllSubWithType().execute();

    	slide = (SlidingDrawer) findViewById(R.id.slideDrawer);
    	
    }

    public void RoomWithNoLocation(View v)
    {
    	Intent i = new Intent(RoomMap.this, RoomWithoutLocation.class);
    	startActivity(i);
    }
    
    
    public void Search(View v) {

    	int radioButtonID = rgCategory.getCheckedRadioButtonId();
    	
    	RadioButton rbtn = (RadioButton) findViewById(radioButtonID);
    	Log.i("RoomMap", rbtn.getText().toString());
    	searchCategory = rbtn.getText().toString();
    	
    	//View radioButton = rgSchSub.findViewById(radioButtonID);
    	//int idx = rgSchSub.indexOfChild(radioButton);
    	
    	//Log.i("RoomMap", String.valueOf(radioButtonID)); //0123
    	//Log.i("RoomMap", String.valueOf(idx));// 1234
    	
    	//Clear the map and close the slidedrawer
    	mMap.clear();
    	slide.close();
    	new LoadRoomsAccToCategory().execute();

    }
    
    //Slide Drawer END
    
    //Current Location START
	private void updateWithNewLocation(Location loc)
	{
		String latLonString;
		
		if (loc != null)
		{
			if (myLocation != null)
			{
				myLocation.remove();
			}
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			LatLng currentLocation = new LatLng(lat, lon);
			myLocation = mMap.addMarker(new MarkerOptions()
            .position(currentLocation)
            .title("Your Location")
            .icon(BitmapDescriptorFactory
                .fromResource(R.drawable.currentlocation_mapicon)));
			
			latLonString = "Lat: " + lat + "\n" + "Lon: " + lon;
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
    
	//Current Location END
	
    protected Dialog onCreateDialog(int id) {
    	Log.i("sg.nyp.groupconnect", "onCreateDialog");
    	
        if (id == CREATERM_ALERT)
        {
        	  Log.i("sg.nyp.groupconnect", "onCreateDialog - CREATERM_ALERT");
        	  
            	Builder builder = new AlertDialog.Builder(this);
	            builder.setMessage("Are you sure you want to create a room at \n" + addressString);
	            builder.setCancelable(true);
	            builder.setPositiveButton("Okay", new OkOnClickListener());
	            builder.setNegativeButton("Cancel", new CancelOnClickListener());
	            dialog = builder.create();
	            dialog.show();
        }    
        	  
        else if (id == CREATESEARCH_ALERT)
        {
        	  Log.i("sg.nyp.groupconnect", "onCreateDialog - CREATERM_ALERT");
        	  
            	Builder builder1 = new AlertDialog.Builder(this);
	            builder1.setMessage("Do you want to create a room at \n" + addressString + "?");
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("Okay", new OkSearchOnClickListener());
	            builder1.setNegativeButton("Cancel", new CancelOnClickListener());
	            dialog = builder1.create();
	            dialog.show();
        }
        
        else if (id == ROOMJOIN_ALERT)
        {
        	  Log.i("sg.nyp.groupconnect", "onCreateDialog - CREATERM_ALERT");
        	  
            	Builder builder2 = new AlertDialog.Builder(this);
	            builder2.setMessage("You have successfully joined the room");
	            builder2.setCancelable(true);
	            builder2.setPositiveButton("Okay", new CancelOnClickListener());
	            dialog = builder2.create();
	            dialog.show();
        }
        
        
        return super.onCreateDialog(id);
      }

    
    private final class OkOnClickListener implements DialogInterface.OnClickListener 
    {
    	public void onClick(DialogInterface dialog, int which) 
    	{
    		
    		Intent output = new Intent();
			output.putExtra("location", addressString);
			output.putExtra("lat", String.valueOf(lat));
			output.putExtra("lng", String.valueOf(lng));
			// Set the results to be returned to parent
			setResult(RESULT_OK, output);
			finish();
    	}
    } 
    
    
    private final class CancelOnClickListener implements DialogInterface.OnClickListener 
    {
        public void onClick(DialogInterface dialog, int which) 
        {
        	if (temp != null)
        		temp.remove();
        	
        }
    }
    
    private final class OkSearchOnClickListener implements DialogInterface.OnClickListener 
    {
    	public void onClick(DialogInterface dialog, int which) 
    	{
    		
    		Intent intent = new Intent(RoomMap.this, CreateRm.class);
			intent.putExtra("location", autoCompView.getText().toString());
			Log.i("CreateRmStep3", autoCompView.getText().toString());
			startActivityForResult(intent,CREATE_RM_RESULT_CODE);
    	}
    } 
    
    
    String result = null;
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
	    if (requestCode == CREATE_RM_RESULT_CODE ) {

	    }
    }
    
    
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
    
    
    private class DownloadTask extends AsyncTask<String, Integer, String>{
 
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
	 
	            }catch(Exception e){
	                Log.d("Exception",e.toString());
	            }
	            return places;
	        }
	 
	        // Executed after the complete execution of doInBackground() method
	        @Override
	        protected void onPostExecute(List<HashMap<String,String>> list){
	 
	            // Clears all the existing markers
	            //mMap.clear();
	 
	            //for(int i=0;i<list.size();i++){
	 
	                // Creating a marker
	                MarkerOptions markerOptions = new MarkerOptions();
	 
	                // Getting a place from the places list
	                //Get only the first location
	                HashMap<String, String> hmPlace = list.get(0);
	 
	                // Getting latitude of the place
	                double lat = Double.parseDouble(hmPlace.get("lat"));
	 
	                // Getting longitude of the place
	                double lng = Double.parseDouble(hmPlace.get("lng"));
	                
	                // Getting name
	                String name = hmPlace.get("formatted_address");
	                LatLng latLng = new LatLng(lat, lng);
	                
	                
		                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomMap.this);
				        Editor edit = sp.edit();
				        edit.putString("homeLatLng", lat + "," + lng).apply();
	 
		                // Setting the position for the marker
		                markerOptions.position(latLng);
		                
		                //Set the icon for the marker
		                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.home_mapicon);
		                
		                markerOptions.icon(icon);
		 
		                // Placing a marker on the touched position
		                mMap.addMarker(markerOptions);
  
	        }
	    }
	    
	 //For HOME Location END
	    
	    
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
				statusR = c.getString(TAG_STATUS);
				typeNameR = c.getString(TAG_TYPENAME);
		
				// creating new HashMap and store all data 
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put(TAG_ROOMID, roomIdR);
				map.put(TAG_TITLE, titleR);
				map.put(TAG_LOCATION, locationR);
				map.put(TAG_NOOFLEARNER, noOfLearnerR);
				map.put(TAG_LATLNG, latLngR);
				map.put(TAG_CATEGORY, categoryR);
				map.put(TAG_STATUS, statusR);
				map.put(TAG_TYPENAME, typeNameR);

				// adding HashList to ArrayList
				mCommentList.add(map); 

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void updateMap() 
	{
		Log.i("sg.nyp.groupconnect", "updateMap");
		PopupAdapter pop = new PopupAdapter();
		// To retrieve everything from Hashmap (mCommentList) and display all rooms
		if (mCommentList != null)
		{
			for (int i = 0; i<mCommentList.size(); i++)
			{
				
				String latLng = mCommentList.get(i).get(TAG_LATLNG);
				String typeName = mCommentList.get(i).get(TAG_TYPENAME);
				if (!latLng.equals("") && !latLng.equals("undefined") && !latLng.equals(null))
				{
					//Spliting the lat Lng 
					String[] parts = latLng.split(",");
					latR = Double.parseDouble(parts[0]); 
					lngR = Double.parseDouble(parts[1]);
					
					retrievedLatLng = new LatLng(latR , lngR);
					BitmapDescriptor icon = null;
					Log.i("RoomMap", "TypeName:" + typeName);
					if (typeName.equals("School Subjects"))
					{
						icon = BitmapDescriptorFactory.fromResource(R.drawable.school_mapicon);
						setImageIcon(R.drawable.school_mapicon);
					}
					else if (typeName.equals("Music"))
					{
						icon = BitmapDescriptorFactory.fromResource(R.drawable.music_mapicon);
						setImageIcon(R.drawable.music_mapicon);
					}
					else if (typeName.equals("Computer-related"))
						icon = BitmapDescriptorFactory.fromResource(R.drawable.computer_mapicon);
					else if (typeName.equals("Others"))
						icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

					retrievedMarker = mMap.addMarker(new MarkerOptions()
						.position(retrievedLatLng)
						.title(mCommentList.get(i).get(TAG_TITLE))
						.snippet("Category: " + mCommentList.get(i).get(TAG_CATEGORY) + "\n" + "Location: " + mCommentList.get(i).get(TAG_LOCATION) + "\n" + "Status: " + mCommentList.get(i).get(TAG_STATUS))
						.icon(icon));
					
					
					listOfMarker.add(retrievedMarker);
					
				}
			}
			
		
	        resetCount();
		}
		
		//To get the home location
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomMap.this);
        Double mem_home_lat = Double.parseDouble(sp.getString("homeLat", ""));
        Double mem_home_lng = Double.parseDouble(sp.getString("homeLng", ""));
        
        homeMarker = mMap.addMarker(new MarkerOptions()
        .position(new LatLng(mem_home_lat,mem_home_lng))
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
        
		Intent intent1 = getIntent();
        Bundle extras = intent1.getExtras();
        if (extras != null) {
            notificationArrive = extras.getBoolean("arrive");
            titleToFocus = extras.getString("titleToFocus");
        	Log.i("NotiticationTest", notificationArrive + titleToFocus);
            
            for(Marker m : listOfMarker) {
                if(m.getTitle().equals(titleToFocus)) {
                    // do something with the marker
                	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 12)); 
                	m.showInfoWindow();
                    break; // stop the loop
                }
            }
        }

	}
    
	public class LoadRooms extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RoomMap.this);
			pDialog.setMessage("Loading Map...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
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
	
		public class retrieveAllSubWithType extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.i("sg.nyp.groupconnect", "LoadRoom - Preexecute");
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			mCategoryList = new ArrayList<HashMap<String, String>>();

			JSONParser jParser = new JSONParser();
			JSONObject json = jParser.getJSONFromUrl(RETRIEVE_ALL_SUB_URL);

			try {

				mCategory = json.getJSONArray(TAG_POSTS);

				for (int i = 0; i < mCategory.length(); i++) {
					JSONObject c = mCategory.getJSONObject(i);

					name = c.getString(TAG_NAME);
					typeName = c.getString(TAG_TYPENAME);
			
					HashMap<String, String> map = new HashMap<String, String>();
					
					map.put(TAG_NAME, name);
					map.put(TAG_TYPENAME, typeName);

					mCategoryList.add(map); 
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.i("sg.nyp.groupconnect", "LoadRoom - onPostExecute");
			super.onPostExecute(result);
			Log.i("sg.nyp.groupconnect", "updateMap");
			
			schSubList.clear();
			musicList.clear();
			computerList.clear();
			othersList.clear();
			categoryList.clear();

			if (mCategoryList != null)
			{
				for (int i = 0; i<mCategoryList.size(); i++)
				{

					name = mCategoryList.get(i).get(TAG_NAME);
					typeName = mCategoryList.get(i).get(TAG_TYPENAME);
					
					categoryList.add(name); 
					
					//For PopupAdapter
					if (typeName.equals("School Subjects"))
					{
						schSubList.add(name);
					}
					else if (typeName.equals("Music"))
					{
						musicList.add(name);
					}
					else if (typeName.equals("Computer-related"))
					{
						computerList.add(name);
					}
					else if (typeName.equals("Others"))
					{
						othersList.add(name);
					}
					
				}
			

				for (int i = 0; i < categoryList.size(); i++) {
					RadioButton rdbtn = new RadioButton(RoomMap.this);
					rdbtn.setId(countForRbtnId);
					rdbtn.setText(categoryList.get(i));
					rdbtn.setTextColor(Color.WHITE);
					rdbtn.setTextSize(20f);
					rgCategory.addView(rdbtn);
					
					if (countForRbtnId == 0) //For some reason the id 1,2,3 does not work
						countForRbtnId = 4;
					else
						countForRbtnId++;
					
				}
				
				
			}
			countForRbtnId = 0;
			mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
	        mMap.setOnInfoWindowClickListener(RoomMap.this);
		}
	}
	
	
	public class LoadRoomsAccToCategory extends AsyncTask<Void, Void, Boolean> {

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

			mSearchList = new ArrayList<HashMap<String, String>>();
			String post_category = searchCategory;

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("category", post_category));

			Log.d("request!", "starting");
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(
					RETRIEVE_ROOM_WITH_CATEGORY_URL, "POST", params);

			try {

				mSearch = json.getJSONArray(TAG_POSTS);

				for (int i = 0; i < mSearch.length(); i++) {
					JSONObject c = mSearch.getJSONObject(i);

					roomIdR = c.getString(TAG_ROOMID);
					titleR = c.getString(TAG_TITLE);
					locationR = c.getString(TAG_LOCATION);
					noOfLearnerR = c.getString(TAG_NOOFLEARNER);
					categoryR = c.getString(TAG_CATEGORY);
					latLngR = c.getString(TAG_LATLNG);
					statusR = c.getString(TAG_STATUS);
					typeNameR = c.getString(TAG_TYPENAME);
			
					HashMap<String, String> map = new HashMap<String, String>();
					
					map.put(TAG_ROOMID, roomIdR);
					map.put(TAG_TITLE, titleR);
					map.put(TAG_LOCATION, locationR);
					map.put(TAG_NOOFLEARNER, noOfLearnerR);
					map.put(TAG_LATLNG, latLngR);
					map.put(TAG_CATEGORY, categoryR);
					map.put(TAG_STATUS, statusR);
					map.put(TAG_TYPENAME, typeNameR);

					mSearchList.add(map); 

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.i("sg.nyp.groupconnect", "LoadRoom - onPostExecute");
			super.onPostExecute(result);
			pDialog.dismiss();
			Log.i("sg.nyp.groupconnect", "updateMap");
			PopupAdapter pop = new PopupAdapter();
			// To retrieve everything from Hashmap (mCommentList) and display all rooms
			if (mSearchList != null)
			{
				for (int i = 0; i<mSearchList.size(); i++)
				{
					
					String latLng = mSearchList.get(i).get(TAG_LATLNG);
					String typeName = mSearchList.get(i).get(TAG_TYPENAME);
					if (!latLng.equals("") && !latLng.equals("undefined") && !latLng.equals(null))
					{
						//Spliting the lat Lng 
						String[] parts = latLng.split(",");
						latR = Double.parseDouble(parts[0]); 
						lngR = Double.parseDouble(parts[1]);
						
						retrievedLatLng = new LatLng(latR , lngR);
						BitmapDescriptor icon = null;
						Log.i("RoomMap", "TypeName:" + typeName);
						if (typeName.equals("School Subjects"))
						{
							icon = BitmapDescriptorFactory.fromResource(R.drawable.school_mapicon);
							setImageIcon(R.drawable.school_mapicon);
						}
						else if (typeName.equals("Music"))
						{
							icon = BitmapDescriptorFactory.fromResource(R.drawable.music_mapicon);
							setImageIcon(R.drawable.music_mapicon);
						}
						else if (typeName.equals("Computer-related"))
							icon = BitmapDescriptorFactory.fromResource(R.drawable.computer_mapicon);
						else if (typeName.equals("Others"))
							icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

						retrievedMarker = mMap.addMarker(new MarkerOptions()
							.position(retrievedLatLng)
							.title(mSearchList.get(i).get(TAG_TITLE))
							.snippet("Category: " + mSearchList.get(i).get(TAG_CATEGORY) + "\n" + "Location: " + mSearchList.get(i).get(TAG_LOCATION) + "\n" + "Status: " + mSearchList.get(i).get(TAG_STATUS))
							.icon(icon));
						
						
						
						
					}
				}
				
				mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		        resetCount();
			}
			
			//To get the home location
	    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomMap.this);
	        Double mem_home_lat = Double.parseDouble(sp.getString("homeLat", ""));
	        Double mem_home_lng = Double.parseDouble(sp.getString("homeLng", ""));
	        
	        homeMarker = mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(mem_home_lat,mem_home_lng))
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
	
	
	class CreateYesMember extends AsyncTask<String, String, String> {

		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	    }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
	        int success;
	        String post_roomId = roomIdForYes;
	        String post_memberId = yesMemId;
	        String type = "Learner";

	        try {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("room_id", post_roomId));
	            params.add(new BasicNameValuePair("memberId", post_memberId));
	            params.add(new BasicNameValuePair("memberType", type));

	            Log.d("request!", "starting");
	            
	            //Posting user data to script 
	            JSONObject json = jsonParser.makeHttpRequest(
	            		CREATE_ROOM_MEM_URL, "POST", params);

	            // full json response
	            Log.d("Post Comment attempt", json.toString());

	            // json success element
	            success = json.getInt(TAG_SUCCESS);
	            if (success == 1) {
	            	Log.d("Comment Added!", json.toString());    
	            	
	            	return json.getString(TAG_MESSAGE);
	            }else{
	            	Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
	            	//successAll= false;
	            	return json.getString(TAG_MESSAGE);
	            	
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }

	        
	        return null;
			
		}
		
	    protected void onPostExecute(String file_url) {

	       Log.i("CreateRmStep3", file_url);
	       
	       NotificationManager manager = (NotificationManager) RoomMap.this.getSystemService(Context.NOTIFICATION_SERVICE);
	        manager.cancel(1);
	       
	       showDialog(ROOMJOIN_ALERT);
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
            if (searchMarker != null)
            	searchMarker.remove();
 
            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){
 
                Address address = (Address) addresses.get(i);
 
                // Creating an instance of GeoPoint, to display in Google Map
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
 
                String addressText = String.format("%s, %s",
                		address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                				address.getCountryName());
                
                searchMarker = mMap.addMarker(new MarkerOptions()
	                .position(latLng)
	                .title("Search Location")
	                .snippet(addressText + " \nClick Here to create a room with this address")
	                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                
                searchMarker.showInfoWindow();
                
                // Locate the first location
                if(i==0)
                {
                	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
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
}
