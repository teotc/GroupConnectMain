package sg.nyp.groupconnect.room;

//import static sg.nyp.groupconnect.notification.Util.*;
//import static sg.nyp.groupconnect.notification.Util.TAG;

import static sg.nyp.groupconnect.notification.Util.EXTRA_MESSAGE;
import static sg.nyp.groupconnect.notification.Util.TAG;
import static sg.nyp.groupconnect.notification.Util.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.room.RoomMap.LoadRooms;
import sg.nyp.groupconnect.R.drawable;
import sg.nyp.groupconnect.R.id;
import sg.nyp.groupconnect.R.layout;
import sg.nyp.groupconnect.R.menu;
import sg.nyp.groupconnect.R.string;
import sg.nyp.groupconnect.VoteMap;
import sg.nyp.groupconnect.custom.CustomList;
import sg.nyp.groupconnect.notification.AlertDialogManager;
import sg.nyp.groupconnect.notification.AppServices;
import sg.nyp.groupconnect.notification.WakeLocker;
import sg.nyp.groupconnect.VoteLocation.RetrieveMemberVote;
import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.utilities.VotingfPieChartBuilder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;

public class RoomDetails extends Activity {
	//Variables
	ListView educatorlist, learnerlist;
	TextView tvTitle, tvCategory, tvNoOfLearner, tvLocation, tvNoOfEducator;
	Button btnDone, btnUpdate, btnJoin, btnVote, btnViewResult;;
	ArrayList<String> educatorArray = new ArrayList<String>();
	ArrayList<String> memberArray = new ArrayList<String>();
	
	//To store the current user information
	String mem_id, mem_username, mem_type;
	
	//To see if the page come from MainActivity
	//No need to load again if come from update page
	boolean comeFromMain = true;
	
	//onActivityResult
	private static final int UPDATE_RM_RESULT_CODE = 100;
	
	
	Integer[] imageId = {
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher
			
	};
	
	//For Retrieving created room_id, educator limit & Member Limit
	
	// Progress Dialog
    private ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
   
    private static final String TAG_ROOMID = "room_id";
    private static final String TAG_NOOFLEARNER = "noOfLearner";
    private static final String TAG_CREATORID = "creatorId";
    String createdRoomId, totalNoOfLearner, creatorId;
	
	//For Retrieving Rm Members
	// testing from a real server:
	private static final String GET_RM_MEM_1URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemRetrieve1.php";
	

	String contentR = "";

	
	
	//For Creating a Notification
	private static final String NOTIFICATION_CREATE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/notificationCreate.php";
	String nTitle, nMessage, nType;
	
	//For retrieving UserId using a username
	String userIdR;
	String usernameForID;
	
	private static final String MEM_UPDATE_DEVICE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/memUpdate.php";
	static String deviceUUID = "";
	String currentUserDevice = "";
	
	private static final String MEM_DEVICE_R_URL = "http://www.it3197Project.3eeweb.com/grpConnect/memDeviceR.php";
	String deviceR = "";
	String notifyUserUUID = "";
	public static void setDeviceUUID(String uuid)
	{
		deviceUUID = uuid; 
		
	}
	
	String TAG_NO = "notification";
	
	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	private static final String TAG_MEMBERID = "memberId";
	private static final String TAG_MEMBERTYPE = "memberType";
	 
	private static final String USERNAME_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/usernameRetrieve.php";
		
	private static final String ROOM_JOIN_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemCreate.php";
		
	//For CreateEduMemList1()
	String memId = "";
	String memNameR = "";
	int currentCount = 0; //Used to avoid data being overwritten
	ArrayList<String> tempListId = new ArrayList<String>();
	ArrayList<String> tempListType = new ArrayList<String>();
	ArrayList<String> tempListName = new ArrayList<String>();
	ArrayList<String> listOfMemberIdStore = new ArrayList<String>();
	ArrayList<String> listOfMemberIdStore1 = new ArrayList<String>();
	
	//For retrieveRmMem1() - AsyncTask
	//An array of all of our comments
	private JSONArray mMember = null;
	// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mMemberList;
	String memberIdR = "";
	String memberTypeR = "";
	ArrayList<String> listOfMemberId = new ArrayList<String>();
	ArrayList<String> listOfMemberType = new ArrayList<String>();
	
	//For usernameRetrieveE/L - AsyncTask
	ArrayList<String> listOfMemUUID = new ArrayList<String>();
	String uuidR;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_details);

		pDialog = new ProgressDialog(RoomDetails.this);
		pDialog.setMessage("Loading Details...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
		
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvCategory = (TextView) findViewById(R.id.tvCategory);
		tvNoOfLearner = (TextView) findViewById(R.id.tvNoOfLearner);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvNoOfEducator = (TextView) findViewById(R.id.tvNoOfEducator);
		
		btnDone = (Button) findViewById(R.id.btnDone);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnJoin = (Button) findViewById(R.id.btnJoin);
		btnVote = (Button) findViewById(R.id.vote);
		btnViewResult = (Button) findViewById(R.id.viewResult);
		
		educatorlist = (ListView) findViewById(R.id.educatorList);
		learnerlist = (ListView) findViewById(R.id.learnerList);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
		mem_id = sp.getString("id", "No ID");
		mem_username = sp.getString("username", "No Username");
		mem_type = sp.getString("type", "No Type");

		Intent intent = new Intent();
		//To see if the page come from MainActivity
		//No need to load again if come from update page
		if (comeFromMain == true)
		{
			String title = this.getIntent().getStringExtra("title");
			String location = this.getIntent().getStringExtra("location");
			String category = this.getIntent().getStringExtra("category");
			
			
			tvTitle.setText(title);
			tvLocation.setText(location);
			tvCategory.setText(category);
			
			//To prepare for push notification START
			
			// this is a hack to force AsyncTask to be initialized on main thread. Without this things
		    // won't work correctly on older versions of Android (2.2, apilevel=8)
			
		    try {
		    	Class.forName("android.os.AsyncTask");
		    } catch (Exception ignored) {}
		    
		    GCMRegistrar.checkDevice(this);
		    GCMRegistrar.checkManifest(this);

		    AppServices.loginAndRegisterForPush(this);
		    
		    //To prepare for push notification END
			
			comeFromMain = false;
			
		}
		
		
		
		//To retrieve and display all members including educator and learners
		new retrieveRmMem1().execute();

		//For Geraldine's part - Vote
		//Setup();
		
		
		btnDone.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		comeFromMain = true;
				// Ends the sub-activity
				finish();
        	}
        });
		
		btnUpdate.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		Intent myIntent = new Intent(RoomDetails.this,ManageRoom.class);
        		myIntent.putExtra("roomId", createdRoomId);
    	      	myIntent.putExtra("title", tvTitle.getText().toString());
    	      	myIntent.putExtra("category", tvCategory.getText().toString());
    	      	myIntent.putExtra("location", tvLocation.getText().toString());
    	      	startActivityForResult(myIntent,UPDATE_RM_RESULT_CODE);
        	}
        });
		
		btnJoin.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		//Update user's device UUID
        		/*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
        		currentUserDevice = sp.getString("device", "");
    	        if (currentUserDevice.equals(""))
    	        {
    	        	//Update using current device uuid
    	        	new MemDeviceUpdate().execute();
    	        	//currentUserDevice = deviceUUID;
    	        	
    	        }
    	        else
    	        {
    	        	//Do nothing
    	        	Log.i("Test1", "Do Nothing");
    	        }*/
    	        
    	        for (int i = 0; i <listOfMemberIdStore.size(); i++)
    	        {
    	        	Log.i("RoomDetails", "ListOfMemberIdStore1: " + listOfMemberIdStore1.get(i));
    	        }
    	        

    	        
    	        new createRoomMember().execute(); //Add the user in the group
    	        
    	        //educatorlist.
    	        //learnerlist.clear();
    	        new retrieveRmMem1().execute(); //To refresh the page
    	        
    	        //For Push Notification
    	        //Get all members id
    	        //listOfMemberIdStore1 --> Got from createRoomMember AsyncTask
    	        for (int i = 0; i < listOfMemberIdStore1.size(); i++)
    	        {
    	        	//If it is different from the user's id (No reason to send notification to the person
    	        	//who join the grp)
    	        	if(!mem_id.equals(listOfMemberIdStore1.get(i)))
    	        	{
    	        		//Different message according to user's type
    	        		if (mem_type.equals("Educator"))
    	        		{
    	        			nTitle = "An Educator has join one of your interested group!";
    	        			nMessage = mem_username + " (Educator) has join your group (" + tvTitle.getText().toString() + ")";
    	        			nType = "Join Group";
    	        		}
    	        		else if (mem_type.equals("Learner"))
    	        		{
    	        			nTitle = "Someone join your group!";
    	        			nMessage = mem_username + " has join your group (" + tvTitle.getText().toString() + ")";
    	        			nType = "Join Group";
    	        		}
    	        		
    	        		userIdR = listOfMemberIdStore1.get(i);
    	        		//For Internal notification purpose
    	        		new notificationCreate().execute();
    	        		
    	        		try {
							Thread.sleep(2500); //2300
							// Notify the learners through push notification
							//SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
							//notifyUserUUID = "";
							//notifyUserUUID = sp1.getString("deviceR", "No Device");
							//Get the uuid and send it out
							notifyUserUUID = listOfMemUUID.get(i);
							pushNotification(v, nMessage, notifyUserUUID, userIdR);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	        	}
    	        }
        	}
        });
		
		
	}
	

	//To check whether the Join button should appear or not
	boolean join = false;
	public void joinRequirement1(ArrayList listOfMemberId)
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
		mem_id = sp.getString("id", "No ID");
		mem_username = sp.getString("username", "No Username");
		mem_type = sp.getString("type", "No Type");
		
		//listOfMemberId -> The id of the people who are in the group educator or learner
		//If the user id is found in the listOfMemberId = Already join. Thus, join button should not appear
		for (int i = 0; i<listOfMemberId.size(); i++)
		{
			if (mem_id.equals(listOfMemberId.get(i)))
			{
				join = true;
			}
		}
		
		if (join == true)
			btnJoin.setVisibility(View.GONE);
		else if (join == false)
			btnJoin.setVisibility(View.VISIBLE);
		
	}
	

	
	public void createEduMemList1(ArrayList listOfMemberId , ArrayList listOfMemberType)
	{
		Log.i("RoomDetails", "In CreateEduMemList1     1");
		//Prevent repeated list
		tempListId.clear();
		tempListType.clear();
		tempListName.clear();
		educatorArray.clear();
		memberArray.clear();
		
		
		//Data in the array dissapear after joinRequirement1 for unknown reason thus, listOfMemberId is
		//Store into two seperate array to retrieve the id later when the user wants to join the room
		listOfMemberIdStore = listOfMemberId;
		listOfMemberIdStore1 = listOfMemberId;
		joinRequirement1(listOfMemberIdStore);
		Log.i("RoomDetails", "ListOfMemberId SIZE: " + listOfMemberId.size() );
		
		
		for(int i = 0; i <listOfMemberId.size(); i ++)
		{
			
			//AsyncTask method runs together with the main codes. Thus, storing data in array first
			//To avoid data being overwritten while the AsyncTask method is still running the previous method
			Log.i("RoomDetails", "In CreateEduMemList1   2");
			memId = listOfMemberId.get(i).toString();
			tempListId.add(memId);
			Log.i("RoomDetails",memId );
			String memType = listOfMemberType.get(i).toString();
			tempListType.add(memType);
			Log.i("RoomDetails",memType );
			if (memType.equals("Educator"))
			{
				Log.i("RoomDetails", "UsernameRetrieve E");
				//Retrieve username + storing of ListView
				new usernameRetrieveE().execute();

				
			}
			else
			{
				tvNoOfEducator.setText("0/1");
			}
			
			if (memType.equals("Learner"))
			{
				Log.i("RoomDetails", "UsernameRetrieve L");
				new usernameRetrieveL().execute();
			}
			else
			{
				tvNoOfLearner.setText("0/" + totalNoOfLearner);
			}
			
		}

		currentCount = 0;
		pDialog.dismiss();
	}
	
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
	    if (requestCode == UPDATE_RM_RESULT_CODE ) {
		    if(resultCode == RESULT_OK){
		    	if (data.getStringExtra("delete").equals("False"))
		    	{
			    	tvTitle.setText(data.getStringExtra("title"));
			    	tvCategory.setText(data.getStringExtra("category"));
			    	tvLocation.setText(data.getStringExtra("location"));
		    	}
		    	else if (data.getStringExtra("delete").equals("True"))
		    	{
		    		finish();
		    	}
		    	
		    }
	    }
	    if (resultCode == 1) {
			btnVote.setVisibility(View.GONE);
	    }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.room_details, menu);
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
		return super.onOptionsItemSelected(item);
	}
	
	public void pushNotification(View v, String msg, String device, String username)
	{
		
	    Log.i(TAG_NO, username + " pushNotification - device: " + device); 
	    AppServices.sendMyselfANotification(v.getContext(), msg, device);
	    registerReceiver(notificationReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
	}
	
	 /**
	   * Receives push Notifications
	   * */
	private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() 
	{
		private AlertDialogManager alert = new AlertDialogManager();
	    @Override
	    public void onReceive(Context context, Intent intent) {

	      // Waking up mobile if it is sleeping
	      WakeLocker.acquire(getApplicationContext());

	      /**
	       * Take some action upon receiving a push notification here!
	       **/
	      String message = intent.getExtras().getString(EXTRA_MESSAGE);
	      if (message == null) { message = "Empty Message"; }
	      
	      Log.i(TAG, message);
	      //messageTextView.append("\n" + message);

	      alert.showAlertDialog(context, getString(R.string.gcm_alert_title), message);
	      Toast.makeText(getApplicationContext(), getString(R.string.gcm_message, message), Toast.LENGTH_LONG).show();

	      WakeLocker.release();
	    }
	};

	// this will be called when the screen rotates instead of onCreate()
	// due to manifest setting, see: android:configChanges
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);
	    //initUI();
	}

	//Geraldine's part START
	
	private Intent intent = null;

	// Required info
	private String currentMemberId = "";
	private String currentRoomId = "";
	//private String[] roomMemberId = new String[] { "1001", "1002", "1003",
	//		"1004", "1005", "1006", "1007" };
	private ArrayList<String> roomMemberId = new ArrayList<String>();
	private String roomLocation = "";
	private String creatorMemberId = "";
	private int stat = 0;
	private int num = 0;
	
	
	
	
	private void Setup() {
		
		currentMemberId = mem_id;
		currentRoomId = createdRoomId;
		roomMemberId = listOfMemberIdStore1;
		roomLocation = tvLocation.getText().toString();
		creatorMemberId = creatorId;
		
		
		int check = 0;
		for (int i = 0; i < roomMemberId.size(); i++) {
			if (roomMemberId.get(i).equals(currentMemberId)) {
				check = 1;
			}
		}
		if (check == 1 && roomLocation.equals("none")) {
			new RetrieveMemberVote().execute();
			
		} else {
			btnVote.setVisibility(View.GONE);
			btnViewResult.setVisibility(View.GONE);
		}

		if (currentMemberId == creatorMemberId)
			stat = 1;
	}

	public void Vote(View v) {
		Setup();
		/*Log.i("Geraldine", "CurrentMemberId: " + currentMemberId);
		Log.i("Geraldine", "CurrentRoomId: " + currentRoomId);
		for (int i = 0; i<roomMemberId.size(); i ++)
			Log.i("Geraldine", "roomMemberIdArr: " + roomMemberId.get(i));
		Log.i("Geraldine", "RoomLocation: " + roomLocation);
		Log.i("Geraldine", "creatorMemberId: " + creatorMemberId);*/
		intent = new Intent(RoomDetails.this, VoteMap.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
		intent.putExtra("CURRENT_MEMBER_ID", currentMemberId);
		startActivityForResult(intent, 0);
	}

	public void VoteResult(View v) {
		intent = new Intent(RoomDetails.this, VotingfPieChartBuilder.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
		intent.putExtra("CREATOR_STATUS", stat);
		startActivity(intent);
	}

	class RetrieveMemberVote extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database
		private ProgressDialog pDialog;

		JSONParser jsonParser = new JSONParser();

		private static final String RETRIEVE_VOTE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveVote.php";

		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MESSAGE = "message";
		private static final String TAG_ARRAY = "posts";

		private static final String TAG_NUM = "num";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RoomDetails.this);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
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

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(RETRIEVE_VOTE_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);

					num = c.getInt(TAG_NUM);
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
			if(num != 0){
				 btnVote.setVisibility(View.GONE);
			 }
		}
	}
	
	
	//Geraldine's part END
	  
	


	class notificationCreate extends AsyncTask<String, String, String> {
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        
	    }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
	        int success;
	        String post_title = nTitle;
	        String post_message = nMessage;
	        String post_type = nType;
	        String post_userId = userIdR;
	        Log.i("Test1", post_userId + "-");
	        
	        //We need to change this:
	        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateRm.this);
	        //String post_username = sp.getString("username", "anon");
	        
	        try {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("title", post_title));
	            params.add(new BasicNameValuePair("message", post_message));
	            params.add(new BasicNameValuePair("type", post_type));
	            params.add(new BasicNameValuePair("userId", post_userId));
	
	            Log.d("request!", "starting");
	            
	            //Posting user data to script 
	            JSONObject json = jsonParser.makeHttpRequest(
	            		NOTIFICATION_CREATE_URL, "POST", params);
	
	            // full json response
	            Log.d("Post Comment attempt", json.toString());
	
	            // json success element
	            success = json.getInt(TAG_SUCCESS);
	            if (success == 1) {
	            	Log.d("Comment Added!", json.toString());    
	            	
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
	       
	
	    }
		
	}
	
	class MemDeviceUpdate extends AsyncTask<String, String, String> {
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	       /* pDialog = new ProgressDialog(ManageRoom.this);
	        pDialog.setMessage("Updating...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(true);
	        pDialog.show();*/
	    }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
	        int success;
	        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
	        Editor edit = sp.edit();
	        String post_memId = sp.getString("id", "No Id");
	        String post_device = deviceUUID;
	       
	        
	        try {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            //params.add(new BasicNameValuePair("username", post_username));
	            params.add(new BasicNameValuePair("id", post_memId));
	            params.add(new BasicNameValuePair("device", post_device));
	
	            Log.d("request!", "starting");
	            
	            //Posting user data to script 
	            JSONObject json = jsonParser.makeHttpRequest(
	            		MEM_UPDATE_DEVICE_URL, "POST", params);
	
	            // full json response
	            Log.d("Post Comment attempt", json.toString());
	
	            // json success element
	            success = json.getInt(TAG_SUCCESS);
	            if (success == 1) {
	            	Log.d("Comment Added!", json.toString());
	            	
	     
	            	//Get the updated device uuid
		        	currentUserDevice = post_device;
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
	        //pDialog.dismiss();
	        if (file_url != null){
	        	//Toast.makeText(ManageRoom.this, file_url, Toast.LENGTH_LONG).show();
	        }
	
	    }
		
	}
	
	class MemDeviceR extends AsyncTask<String, String, String> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		/*pDialog = new ProgressDialog(CreateRm.this);
		pDialog.setMessage("Attempting login...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();*/
	}

	@Override
	protected String doInBackground(String... args) {
		// TODO Auto-generated method stub
		// Check for success tag
		int success;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
        String post_memId = sp.getString("id", "No Id");
		try {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", post_memId));

			Log.d("request!", "starting");
			// getting product details by making HTTP request
			JSONObject json = jsonParser.makeHttpRequest(MEM_DEVICE_R_URL, "POST",
					params);

			// check your log for json response
			Log.d("Login attempt", json.toString());

			// json success tag
			success = json.getInt(TAG_SUCCESS);
			//createdRoomId = json.getString(TAG_ROOMID).toString() + "?";
			
			if (success == 1) {
				Log.d("Login Successful!", json.toString());
				
				currentUserDevice = json.getString("device");
				Log.i("Test1", userIdR);
				return json.getString(TAG_MESSAGE);
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
			//Toast.makeText(CreateRm.this, "Room id created: " + file_url, Toast.LENGTH_LONG).show();
			//Store content with "educator/member"
			//Split them up
			//contentR = file_url;
			//Toast.makeText(RoomDetails.this, "ContentR: " + contentR, Toast.LENGTH_LONG).show();
			//createEduMemList(file_url);

		}

	}

}


	public void updateJSONdata() {
		Log.i("sg.nyp.groupconnect", "updateJSONdata");
		mMemberList = new ArrayList<HashMap<String, String>>();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("title", tvTitle.getText().toString()));
	
		Log.d("request!", "starting");
		JSONObject json = jsonParser.makeHttpRequest(GET_RM_MEM_1URL, "POST",
				params);
	
	
		// when parsing JSON stuff, we should probably
		// try to catch any exceptions:
		try {
			mMember = json.getJSONArray(TAG_POSTS);
			
	
			// looping through all posts according to the json object returned
			for (int i = 0; i < mMember.length(); i++) {
				JSONObject c = mMember.getJSONObject(i);
	
				// gets the content of each tag
				createdRoomId = c.getString(TAG_ROOMID); //Get the createdRoomId for future purpose
				totalNoOfLearner = c.getString(TAG_NOOFLEARNER);
				memberIdR = c.getString(TAG_MEMBERID);
				memberTypeR = c.getString(TAG_MEMBERTYPE);
				creatorId = c.getString(TAG_CREATORID);

				// creating new HashMap and store all data 
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put(TAG_ROOMID, createdRoomId);
				map.put(TAG_NOOFLEARNER, totalNoOfLearner);
				map.put(TAG_MEMBERID, memberIdR);
				map.put(TAG_MEMBERTYPE, memberTypeR);
				map.put(TAG_CREATORID, creatorId);
	
				// adding HashList to ArrayList
				mMemberList.add(map); 
	
			}
	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void updateMap() 
	{
		Log.i("sg.nyp.groupconnect", "updateMap");
		if (mMemberList != null)
		{
			//Clear the list to avoid repeated list
			//Since it is possible to run the method more than once.
			//1. Retrieve all member to display
			//2. Join grp required refresh, thus this method will be run again with new members join in.
			listOfMemberId.clear();
			listOfMemberType.clear();
			//Only need to retrieve once, all will be the same due to INNER JOIN.
			createdRoomId = mMemberList.get(0).get(TAG_ROOMID);
			totalNoOfLearner = mMemberList.get(0).get(TAG_NOOFLEARNER);
			creatorId = mMemberList.get(0).get(TAG_CREATORID);

			for (int i = 0; i<mMemberList.size(); i++)
			{
				
				//Get all members id and their type and store into array
				String memberId = mMemberList.get(i).get(TAG_MEMBERID);
				String memberType = mMemberList.get(i).get(TAG_MEMBERTYPE);
				
				listOfMemberId.add(memberId);
				listOfMemberType.add(memberType);
				
				Log.i("RoomDetails", memberId);
				Log.i("RoomDetails", memberType);
			}
			
			//Create the list in the activity
			createEduMemList1(listOfMemberId, listOfMemberType);
			
		}
		
		
	}
	
	public class retrieveRmMem1 extends AsyncTask<Void, Void, Boolean> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
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
		
		updateMap();
	}
}


	class usernameRetrieveE extends AsyncTask<String, String, String> {
		
	
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			Log.i("RoomDetails", "UsernameretrieveE, currentCount: " + currentCount);
			String id = tempListId.get(currentCount); //Avoid running a incorrect id.
			Log.i("RoomDetails", "UsernameretrieveE, STring id: " + id);
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
				//createdRoomId = json.getString(TAG_ROOMID).toString() + "?";
				
				if (success == 1) {
					Log.d("Login Successful!", json.toString());
					
					memNameR = json.getString("username");
					uuidR = json.getString("device");
					
			        //edit.commit();
					return json.getString("username");
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
	
				String memNameR1 = file_url;
				tempListName.add(memNameR1);
				educatorArray.add(tempListName.get(currentCount));
				Log.i("RoomDetails","MemNameR From usernameRetrieve: " + memNameR1);
				tvNoOfEducator.setText(educatorArray.size() + "/1");
				
				CustomList adapter = new CustomList(RoomDetails.this, educatorArray, imageId);
				
				educatorlist.setAdapter(null); //Since this will be run more than once, clear the listview everytime
												// before setting the new data to avoid repeated list
				educatorlist.setAdapter(adapter);
				
				currentCount++; //Add the counter for the next time we run the method
				listOfMemUUID.add(uuidR); //Storing of uuid for push notification later on
				Log.i("RoomDetails", "UserRetrieveL uuidR" + uuidR);
			}
	
		}
	
	}
	
	class usernameRetrieveL extends AsyncTask<String, String, String> {
		
	
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
	
		}
	
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			Log.i("RoomDetails", "UsernameretrieveL, currentCount: " + currentCount);
			String id = tempListId.get(currentCount);
			Log.i("RoomDetails", "UsernameretrieveL, STring id: " + id);
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
				//createdRoomId = json.getString(TAG_ROOMID).toString() + "?";
				
				if (success == 1) {
					Log.d("Login Successful!", json.toString());
					
					memNameR = json.getString("username");
					uuidR = json.getString("device");
					
			        //edit.commit();
					return json.getString("username");
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
	
				String memNameR1 = file_url;
				tempListName.add(memNameR1);
				memberArray.add(tempListName.get(currentCount));
				//Log.i("RoomDetails","MemNameR From usernameRetrieve: " + memNameR1);
				tvNoOfLearner.setText(memberArray.size() + "/" + totalNoOfLearner);
				
				
				
				CustomList adapter1 = new CustomList(RoomDetails.this, memberArray, imageId);
				
				learnerlist.setAdapter(null);
				learnerlist.setAdapter(adapter1);
	
				currentCount++;
				listOfMemUUID.add(uuidR);
				Log.i("RoomDetails", "UserRetrieveL uuidR" + uuidR);
			}
	
		}
	
	}
	
	class roomJoin extends AsyncTask<String, String, String> {
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
	        int success;
	        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
			
	        String post_roomId = createdRoomId;
	        String post_memberId = sp.getString("id", "No ID");
	        String post_memberType = sp.getString("type", "No Type");
	        Log.i("RoomDetails", "Post_roomId" + post_roomId);
	        Log.i("RoomDetails", "Post_memberId" + post_memberId);
	        Log.i("RoomDetails", "Post_memberType" + post_memberType);
	        
	        try {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("memberId", post_memberId));
	            params.add(new BasicNameValuePair("memberType", post_memberType));
	            params.add(new BasicNameValuePair("room_id", post_roomId));
	
	            Log.d("request!", "starting");
	            
	            //Posting user data to script 
	            JSONObject json = jsonParser.makeHttpRequest(
	            		ROOM_JOIN_URL, "POST", params);
	
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
	        //pDialog.dismiss();
	        if (file_url != null){
	        	//Toast.makeText(ManageRoom.this, file_url, Toast.LENGTH_LONG).show();
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
        String post_roomId = createdRoomId;
        String post_memberId = sp.getString("id", "No ID");
        String post_memberType = sp.getString("type", "No Type");
        Log.i("RoomDetails", "Post_roomId" + post_roomId);
        Log.i("RoomDetails", "Post_memberId" + post_memberId);
        Log.i("RoomDetails", "Post_memberType" + post_memberType);
        	
        
        
        //We need to change this:
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateRm.this);
        //String post_username = sp.getString("username", "anon");
        
        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("username", post_username));
            params.add(new BasicNameValuePair("room_id", post_roomId));
            params.add(new BasicNameValuePair("memberId", post_memberId));
            params.add(new BasicNameValuePair("memberType", post_memberType));

            Log.d("request!", "starting");
            
            //Posting user data to script 
            JSONObject json = jsonParser.makeHttpRequest(
            		ROOM_JOIN_URL, "POST", params);

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
       

    }
	
}
}
