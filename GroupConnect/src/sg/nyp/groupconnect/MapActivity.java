package sg.nyp.groupconnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.educator.CreateRm;
import sg.nyp.groupconnect.educator.NotificationDisplay;
import sg.nyp.groupconnect.educator.PopupAdapter;
import sg.nyp.groupconnect.educator.RoomDetails;
import sg.nyp.groupconnect.utilities.JSONParser;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.app.FragmentActivity;


public class MapActivity extends FragmentActivity implements OnInfoWindowClickListener {

	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	static final LatLng somewhere = new LatLng(1.352083, 90);
	AlertDialog dialog;
	
	// To get the location data
	Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
	String addressString = "";
	
	//Store data retrieved from user input in CreateRm
	String title, location, noOfLearner, category;

	
	private static final int CREATERM_ALERT = 1;
	
	private static final int CREATE_RM_RESULT_CODE = 100;
	private static final int RM_DETAIL_RESULT_CODE = 200;
	
	final static String LOCATION = "message";
	
	 private GoogleMap mMap;
	 
	 //Codes to retrieve all rooms
	 // Progress Dialog
	 private ProgressDialog pDialog;
	 // testing from a real server:
	 private static final String ROOM_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomRetrieve.php";
	 
	 // JSON IDS:
	 private static final String TAG_SUCCESS = "success";
	 //private static final String TAG_TITLE = "title";
	 private static final String TAG_POSTS = "posts";
	 private static final String TAG_POST_ID = "post_id";
	 //private static final String TAG_USERNAME = "username";
	 private static final String TAG_MESSAGE = "message";
	 
	 private static final String TAG_ROOMID = "room_id";
	 private static final String TAG_TITLE = "title";
	 private static final String TAG_CATEGORY = "category";
	 private static final String TAG_NOOFLEARNER = "noOfLearner";
	 private static final String TAG_LOCATION = "location";
	 private static final String TAG_LATLNG = "latLng";
	 private static final String TAG_USERNAME = "username";
	 // it's important to note that the message is both in the parent branch of
	 // our JSON tree that displays a "Post Available" or a "No Post Available"
	 // message,
	 // and there is also a message for each individual post, listed under the
	 // "posts"
	 // category, that displays what the user typed as their message.

	 // An array of all of our comments
	 private JSONArray mComments = null;
	 // manages all of our comments in a list.
	 private ArrayList<HashMap<String, String>> mCommentList;
	 
	//For Retrieving Rm Members
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	// testing from a real server:
	private static final String GET_RM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemRetrieve.php";
	private static final String TAG_EDUCATOR = "educator";
	private static final String TAG_MEMBER = "member";
	String createdRoomId;
	String educatorR, memberR;
	String contentR = "";
	String[] educatorS;
	String[] memberS;
	ArrayList<String> educatorArray = new ArrayList<String>();
	ArrayList<String> memberArray = new ArrayList<String>();
	 
	 
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_mapedu);
	        setUpMapIfNeeded();
	        
	        //To use custom infowindow
	        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
	        mMap.setOnInfoWindowClickListener(this);
	        Log.i("sg.nyp.groupconnect", "onCreate");
	       
	    }
	    
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			Log.i("sg.nyp.groupconnect", "onCreateOptionMenu");
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.map_activity, menu);
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
			if (id == R.id.notification)
			{
				Intent myIntent = new Intent(MapActivity.this,NotificationDisplay.class);
		      	startActivity(myIntent);
			}
			return super.onOptionsItemSelected(item);
		}
	    
	    //Once the infowindow is clicked
	    public void onInfoWindowClick(Marker marker) {
	    	Log.i("sg.nyp.groupconnect", "onInfoWindowClick");
	        //Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
	    	
	    	//Split the content to get the location and category
	    	String content = marker.getSnippet();
	    	String [] split = content.split("\n");
	    	String[] category = split[0].split(":");
	    	String[] location  = split[1].split(":");
	    	
	        Intent myIntent1 = new Intent(MapActivity.this,RoomDetails.class);
	      	myIntent1.putExtra("title", marker.getTitle());
	      	myIntent1.putExtra("location", location[1]);
	      	myIntent1.putExtra("category", category[1]);
	      	startActivity(myIntent1);
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

		/**
	     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	     * installed) and the map has not already been instantiated.. This will ensure that we only ever
	     * call {@link #setUpMap()} once when {@link #mMap} is not null.
	     * <p>
	     * If it isn't installed {@link SupportMapFragment} (and
	     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
	     * install/update the Google Play services APK on their device.
	     * <p>
	     * A user can return to this FragmentActivity after following the prompt and correctly
	     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	     * have been completely destroyed during this process (it is likely that it would only be
	     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	     * method in {@link #onResume()} to guarantee that it will be called.
	     */
	    private void setUpMapIfNeeded() {
	    	Log.i("sg.nyp.groupconnect", "setUpMapIfNeeded");
	        // Do a null check to confirm that we have not already instantiated the map.
	        if (mMap == null) {
	            // Try to obtain the map from the SupportMapFragment.
	            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.edumap))
	                    .getMap();
	            // Check if we were successful in obtaining the map.
	            if (mMap != null) {
	                setUpMap();
	            }
	        }
	    }

	    /**
	     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
	     * just add a marker near Africa.
	     * <p>
	     * This should only be called once and when we are sure that {@link #mMap} is not null.
	     */
	    
	    double lat, lng; 
	    Marker temp;
	    
	    private void setUpMap() {
	    	Log.i("sg.nyp.groupconnect", "setUpMap");
	        //mMap.addMarker(new MarkerOptions().position(new LatLng(1.352083, 103.819836)).title("Marker"));
	    	Log.i("Test", "In setUpMap");
	            
	            if (mMap!=null){
	             /* Marker hamburg = mMap.addMarker(new MarkerOptions().position(singapore)
	                  .title("Malay"));
	              Marker kiel = mMap.addMarker(new MarkerOptions()
	                  .position(somewhere)
	                  .title("Kiel")
	                  .snippet("Kiel is cool")
	                  .icon(BitmapDescriptorFactory
	                      .fromResource(R.drawable.ic_launcher))); */
	              
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
	            	          temp = mMap.addMarker(new MarkerOptions().position(point));
	            	          
	            	          // you can get latitude and longitude also from 'point'
	            	          // and using Geocoder, the address
	            	          
	            	          
	            	          //To get the address of the place clicked by the user
	            		       try 
	            		       {
	            		    	  lat = point.latitude;
	            		    	  lng = point.longitude;
	            		    	  List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
	            		 
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
			            		  addressString = "Canont get Address!";
	            		       }
	            	          
	            	          
	            	          showDialog(CREATERM_ALERT);
	            	          
	            	    }
	            	});
	              
	            }
	    }
	    
	    protected Dialog onCreateDialog(int id) {
	    	Log.i("sg.nyp.groupconnect", "onCreateDialog");
	        switch (id) {
	          case CREATERM_ALERT:
	        	  Log.i("sg.nyp.groupconnect", "onCreateDialog - CREATERM_ALERT");
	            Builder builder = new AlertDialog.Builder(this);
	            builder.setMessage("Are you sure you want to create a room at \n" + addressString);
	            builder.setCancelable(true);
	            builder.setPositiveButton("Okay", new OkOnClickListener());
	            builder.setNegativeButton("Cancel", new CancelOnClickListener());
	            dialog = builder.create();
	            dialog.show();
	            
	        }
	        
	        return super.onCreateDialog(id);
	      }

	    private final class OkOnClickListener implements DialogInterface.OnClickListener 
	    {
	    	public void onClick(DialogInterface dialog, int which) 
	    	{
		      	//Toast.makeText(getApplicationContext(), addressString,Toast.LENGTH_LONG).show();
		      	//startActivity(new Intent(getApplicationContext(), CreateRm.class));
		      	Intent myIntent = new Intent(MapActivity.this,CreateRm.class);
		      	
		      	myIntent.putExtra("location", addressString);
		      	myIntent.putExtra("lat", String.valueOf(lat));
		      	myIntent.putExtra("lng", String.valueOf(lng));
		      	startActivityForResult(myIntent,CREATE_RM_RESULT_CODE);
	    	}
	    } 
	    
	    private final class CancelOnClickListener implements DialogInterface.OnClickListener 
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	        	Toast.makeText(getApplicationContext(), "Cancel",
		                  Toast.LENGTH_LONG).show();
	        	temp.remove();
	        	
	        }
	    }
	    
	    String result = null;
	    protected void onActivityResult (int requestCode, int resultCode, Intent data)
	    {
		    if (requestCode == CREATE_RM_RESULT_CODE ) {
			    if(resultCode == RESULT_CANCELED){
			    	String result = "";
			    	result = data.getStringExtra("Cancel");
			    	
			    	if (result.equals("Canceled"))
			    	{
			    		temp.remove();
			    	}
			    	Log.i("sg.nyp.groupconnect", "onActivityResult - Cancel");
			    		
			    	//Toast.makeText(getApplicationContext(), "Cancel-Nono", Toast.LENGTH_LONG).show();
			    	
			    }
			    else if (resultCode == RESULT_OK)
			    {
			    	Log.i("sg.nyp.groupconnect", "onActivityResult - Ok");
			    	Toast.makeText(getApplicationContext(), "Okay", Toast.LENGTH_LONG).show();
			    }
		    }
	    }

	    
	    String roomIdR, titleR, locationR, categoryR, noOfLearnerR, latLngR, usernameR;
	    double latR, lngR;
	    LatLng retrievedLatLng;
	    Marker retrievedMarker = null;
	    Marker retrievedMarker1;
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
			// To retrieve everything from Hashmap (mCommentList) and display all rooms
			if (mCommentList != null)
			{
				for (int i = 0; i<mCommentList.size(); i++)
				{
					
					String latLng = mCommentList.get(i).get(TAG_LATLNG);
					
					//Spliting the lat Lng 
					String[] parts = latLng.split(",");
					latR = Double.parseDouble(parts[0]); 
					lngR = Double.parseDouble(parts[1]);
					
					retrievedLatLng = new LatLng(latR , lngR);
					
					retrievedMarker = mMap.addMarker(new MarkerOptions()
		            .position(retrievedLatLng)
		            .title(mCommentList.get(i).get(TAG_TITLE))
		            .snippet("Category: " + mCommentList.get(i).get(TAG_CATEGORY) + "\n" + "Location: " + mCommentList.get(i).get(TAG_LOCATION)));
				}
			}
			
			
		}
	    
		public class LoadRooms extends AsyncTask<Void, Void, Boolean> {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(MapActivity.this);
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
		
}
