package sg.nyp.groupconnect.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.R.drawable;
import sg.nyp.groupconnect.R.id;
import sg.nyp.groupconnect.R.layout;
import sg.nyp.groupconnect.R.menu;
import sg.nyp.groupconnect.custom.NotificationCustomList;
import sg.nyp.groupconnect.custom.PopupAdapter;
import sg.nyp.groupconnect.custom.RoomWithoutLocationCustomList;
import sg.nyp.groupconnect.utilities.JSONParser;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.preference.PreferenceManager;

public class RoomWithoutLocation extends Activity {

	ListView roomList;
	
	
	public static Activity activity;
	
	//AsyncTask - LoadRoom
	///Codes to retrieve all rooms
	/// Progress Dialog
	private ProgressDialog pDialog;
	/// testing from a real server:
	private static final String ROOM_RETRIEVE_WITHOUT_LOCATION_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomRetrieveWithoutLocation.php";
	 
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
	private JSONArray mRoom = null;
	/// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mRoomList;
	 
	///LoadRoom Variables
    String roomIdR, titleR, locationR, categoryR, noOfLearnerR, latLngR, usernameR, typeNameR, statusR;
    double latR, lngR;
    
    ArrayList<String> titleArray = new ArrayList<String>();
	ArrayList<String> messageArray = new ArrayList<String>();
	ArrayList<Integer> imageId = new ArrayList<Integer>();
	ArrayList<String> status = new ArrayList<String>();
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_without_location);

		activity = RoomWithoutLocation.this;
		
		roomList = (ListView) findViewById(R.id.roomList);
		
		
		
		new LoadRooms().execute();
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);
		
		roomList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RoomWithoutLocation.this, RoomDetails.class);
				
				String title = titleArray.get(arg2);
				
				i.putExtra("title", title);
				startActivity(i);
			}
		});
		
	}
	
	public void updateJSONdata() {
		Log.i("sg.nyp.groupconnect", "updateJSONdata");

		mRoomList = new ArrayList<HashMap<String, String>>();

		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(ROOM_RETRIEVE_WITHOUT_LOCATION_URL);

		try {
			mRoom = json.getJSONArray(TAG_POSTS);
			

			// looping through all posts according to the json object returned
			for (int i = 0; i < mRoom.length(); i++) {
				JSONObject c = mRoom.getJSONObject(i);

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
				mRoomList.add(map); 

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
		if (mRoomList != null)
		{
			for (int i = 0; i<mRoomList.size(); i++)
			{
					
				titleArray.add(mRoomList.get(i).get(TAG_TITLE));
				
				String msg = "Category: " + mRoomList.get(i).get(TAG_CATEGORY) +
						"\nLocation: " + mRoomList.get(i).get(TAG_LOCATION);
				
				messageArray.add(msg);
				
				int resource = 0;
				if (mRoomList.get(i).get(TAG_TYPENAME).equals("School Subjects"))
				{
					resource = R.drawable.schsub_roomicon;
				}
				else if (mRoomList.get(i).get(TAG_TYPENAME).equals("Music"))
				{
					resource = R.drawable.music_roomicon;
				}
				else if (mRoomList.get(i).get(TAG_TYPENAME).equals("Computer-related"))
				{
					resource = R.drawable.computer_roomicon;
				}
				else if (mRoomList.get(i).get(TAG_TYPENAME).equals("Others"))
				{
					resource = R.drawable.ic_launcher;
				}

				
				imageId.add(resource);
				
				status.add(mRoomList.get(i).get(TAG_STATUS));
				
				
				
				
			}
			
			RoomWithoutLocationCustomList adapter = new RoomWithoutLocationCustomList(RoomWithoutLocation.this, titleArray, messageArray, imageId, status);
			roomList.setAdapter(adapter);
		}

	
	}
    
	public class LoadRooms extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RoomWithoutLocation.this);
			pDialog.setMessage("Loading Rooms...");
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}



}
