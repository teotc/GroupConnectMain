package sg.nyp.groupconnect.room;

//import static sg.nyp.groupconnect.notification.Util.*;
//import static sg.nyp.groupconnect.notification.Util.TAG;

import static sg.nyp.groupconnect.notification.Util.DISPLAY_MESSAGE_ACTION;
import static sg.nyp.groupconnect.notification.Util.EXTRA_MESSAGE;
import static sg.nyp.groupconnect.notification.Util.TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.VoteMap;
import sg.nyp.groupconnect.data.VoteLocationDbAdapter;
import sg.nyp.groupconnect.notification.AlertDialogManager;
import sg.nyp.groupconnect.notification.AppServices;
import sg.nyp.groupconnect.notification.WakeLocker;
import sg.nyp.groupconnect.room.db.retrieveRmMem;
import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.utilities.VotingfPieChartBuilder;
import android.annotation.SuppressLint;
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
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class RoomDetails extends Activity {
	//Variables
	//public static ListView educatorlist;
	//public static ListView learnerlist;
	public static ListView memberList;
	public static TextView tvTitle, tvCategory, tvLocation;
	public static TextView tvStatus, tvDate, tvTime;
	public static TextView tvNoOfLearner;
	public static TextView tvNoOfEducator;
	public static Button btnJoin;
	Button btnVote;
	Button btnViewResult;
	ArrayList<String> educatorArray = new ArrayList<String>();
	ArrayList<String> memberArray = new ArrayList<String>();
	
	
	//To store the current user information
	public static String mem_id, mem_username, mem_type;
	
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
    public static ProgressDialog pDialog;
    public static ProgressDialog pDialog1;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
   
    private static final String TAG_ROOMID = "room_id";
    private static final String TAG_NOOFLEARNER = "noOfLearner";
    private static final String TAG_CREATORID = "creatorId";
    public static String createdRoomId;
	String totalNoOfLearner;
	public static String creatorId;
	
	//For Retrieving Rm Members
	// testing from a real server:
	private static final String GET_RM_MEM_1URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemRetrieve1.php";
	

	String contentR = "";

	
	
	//For Creating a Notification
	private static final String NOTIFICATION_CREATE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/notificationCreate.php";
	public static String nTitle;
	String nMessage;
	String nType;
	
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
	 
		
	private static final String ROOM_JOIN_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemCreate.php";
		
	//For CreateEduMemList1()
	String memId = "";
	String memNameR = "";
	int currentCount = 0; //Used to avoid data being overwritten
	ArrayList<String> tempListId = new ArrayList<String>();
	ArrayList<String> tempListType = new ArrayList<String>();
	ArrayList<String> tempListName = new ArrayList<String>();
	ArrayList<String> listOfMemberIdStore = new ArrayList<String>();
	public static ArrayList<String> listOfMemberIdStore1 = new ArrayList<String>();
	
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
	private static final String USERNAME_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/usernameRetrieve.php";
	public static ArrayList<String> listOfMemUUID = new ArrayList<String>();
	String uuidR;
	
	//For Deleting
    //testing from a real server:
    private static final String ROOM_DELETE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomDelete.php";
    private static final String ROOM_MEM_DELETE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomMemDelete.php";
	
	public static Activity activity;
	public static MenuItem voteMenu;
	public static MenuItem voteResultMenu; 
	MenuItem mainMenu;
	public static MenuItem joinMenu;
	public boolean voteMenuB = true;
	public boolean voteResultMenuB = true;
	public static boolean joinMenuB = true;
	boolean mainMenuB = true;
	
	public static String getTvTitle()
	{
		return tvTitle.getText().toString();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_details);
		//For usernameRetrieve.java
		activity = RoomDetails.this;
		
		pDialog = new ProgressDialog(RoomDetails.this);
		pDialog.setMessage("Loading Details...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
		
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvCategory = (TextView) findViewById(R.id.tvCategory);
		//tvNoOfLearner = (TextView) findViewById(R.id.tvNoOfLearner);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		//tvNoOfEducator = (TextView) findViewById(R.id.tvNoOfEducator);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvTime = (TextView) findViewById(R.id.tvTime);
		
		btnJoin = (Button) findViewById(R.id.btnJoin);
		btnVote = (Button) findViewById(R.id.vote);
		btnViewResult = (Button) findViewById(R.id.viewResult);
		
		//educatorlist = (ListView) findViewById(R.id.educatorList);
		//learnerlist = (ListView) findViewById(R.id.learnerList);
		memberList = (ListView) findViewById(R.id.memberList);
		
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
		new retrieveRmMem().execute();
		/*pDialog.dismiss();
		CustomList adapter = new CustomList(RoomDetails.this, retrieveRmMem.educatorArray, imageId);
		educatorlist.setAdapter(null);
		educatorlist.setAdapter(adapter);
		
		CustomList adapter1 = new CustomList(RoomDetails.this, retrieveRmMem.memberArray, imageId);
		
		learnerlist.setAdapter(null);
		learnerlist.setAdapter(adapter1);*/

		//For Geraldine's part - Vote
		//Setup();

		//ListAdapter adapter = new ListAdapter(this, generateData());
		 
        // if extending Activity 2. Get ListView from activity_main.xml
        //ListView listView = (ListView) findViewById(R.id.listview);
 
        // 3. setListAdapter
        //learnerlist.setAdapter(adapter);
		
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
    	        new retrieveRmMem().execute(); //To refresh the page
    	        
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
							if (!notifyUserUUID.equals(""))	
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
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
        	comeFromMain = true;
        	finish();
        }
        return false;
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("RoomDetails" , "RESUME");
		//For Geraldine's part - Vote
		//Setup();
		
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
			//btnVote.setVisibility(View.GONE);
			voteMenu.setVisible(false);
	    	//voteMenuB = false;
	    }
	    if (resultCode == 1) {
	    	voteMenu.setVisible(false);
	    	voteMenu.setVisible(true);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.room_details, menu);
		
		joinMenu = menu.findItem(R.id.join); //Visible directly controled in retrieveRmMem               
		voteMenu = menu.findItem(R.id.vote);  
		voteResultMenu = menu.findItem(R.id.viewVoteResult);  
		mainMenu = menu.findItem(R.id.menu);
		
		/*if (joinMenuB == false && voteMenuB == false && voteResultMenuB == false)
		{
			mainMenuB = false;
		}
		else
		{
			mainMenuB = true;
		}*/
		
		//joinMenu.setVisible(joinMenuB);
		//voteMenu.setVisible(voteMenuB);
		//voteResultMenu.setVisible(voteResultMenuB);
		mainMenu.setVisible(true);
		
		Log.i("RoomDetails" , "Menus: " + joinMenuB);
		Log.i("RoomDetails" , "Menus: " + voteMenuB);
		Log.i("RoomDetails" , "Menus: " + voteResultMenuB);
		Log.i("RoomDetails" , "Menus: " + mainMenuB);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.edit) {
			Intent myIntent = new Intent(RoomDetails.this,ManageRoom.class);
    		myIntent.putExtra("roomId", createdRoomId);
	      	myIntent.putExtra("title", tvTitle.getText().toString());
	      	myIntent.putExtra("category", tvCategory.getText().toString());
	      	myIntent.putExtra("location", tvLocation.getText().toString());
	      	startActivityForResult(myIntent,UPDATE_RM_RESULT_CODE);
			return true;
		}
		else if (id == R.id.delete) {
			showDialog(1);
			return true;
		}
		else if (id == R.id.join)
		{
			 for (int i = 0; i <listOfMemberIdStore.size(); i++)
 	        {
 	        	Log.i("RoomDetails", "ListOfMemberIdStore1: " + listOfMemberIdStore1.get(i));
 	        }
 	        

 	        
 	        new createRoomMember().execute(); //Add the user in the group
 	        
 	        //educatorlist.
 	        //learnerlist.clear();
 	        new retrieveRmMem().execute(); //To refresh the page
 	        
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
							if (!notifyUserUUID.equals(""))	
								pushNotification( getWindow().getDecorView().findViewById(android.R.id.content), nMessage, notifyUserUUID, userIdR);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
 	        	}
 	        }
			return true;
		}
		else if (id == R.id.vote)
		{
			Vote();
			return true;
		}
		else if (id == R.id.viewVoteResult)
		{
			VoteResult();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//For delete purpose. Warning Message
	AlertDialog dialog;
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete this room?");
			builder.setCancelable(true);
			builder.setPositiveButton("Ok", new OkOnClickListener());
			builder.setNegativeButton("Cancel", new CancelOnClickListener());
			dialog = builder.create();
			dialog.show();

		}

		return super.onCreateDialog(id);
	}

	private final class OkOnClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(getApplicationContext(), "Room Delete",
					Toast.LENGTH_LONG).show();
			new RoomDelete().execute();
			new RoomMemDelete().execute();
			Intent output = new Intent();
			output.putExtra("delete", "True");
			// Set the results to be returned to parent
			setResult(RESULT_OK, output);
			finish();
		}
	}

	private final class CancelOnClickListener implements DialogInterface.OnClickListener 
	{
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(getApplicationContext(), "Canceled",
					Toast.LENGTH_LONG).show();
		}
	}
	
	public void pushNotification(View v, String msg, String device, String username)
	{
		
	    Log.i(TAG_NO, username + " pushNotification - device: " + device); 
	    AppServices.sendMyselfANotification(v.getContext(), msg, device);
	    registerReceiver(notificationReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
	    comeFromMain = true;
	}
	
	 /**
	   * Receives push Notifications
	   * */
	int mId = 1;
	private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() 
	{
		private AlertDialogManager alert = new AlertDialogManager();
	    @SuppressLint("NewApi")
		@Override
	    public void onReceive(Context context, Intent intent) {

	      // Waking up mobile if it is sleeping
	      WakeLocker.acquire(getApplicationContext());

	      

	     /* NotificationCompat.Builder mBuilder =
	    		  new NotificationCompat.Builder(RoomDetails.this)
	      .setSmallIcon(R.drawable.ic_launcher)
	      .setContentTitle("My notification")
	      .setContentText("Hello World!");
	      // Creates an explicit intent for an Activity in your app
	      Intent resultIntent = new Intent(RoomDetails.this, RoomMap.class);

	      // The stack builder object will contain an artificial back stack for the
	      // started Activity.
	      // This ensures that navigating backward from the Activity leads out of
	      // your application to the Home screen.
	      TaskStackBuilder stackBuilder = TaskStackBuilder.create(RoomDetails.this);
	      // Adds the back stack for the Intent (but not the Intent itself)
	      stackBuilder.addParentStack(RoomMap.class);
	      // Adds the Intent that starts the Activity to the top of the stack
	      stackBuilder.addNextIntent(resultIntent);
	      PendingIntent resultPendingIntent =
	    		  stackBuilder.getPendingIntent(
	    				  0,
	    				  PendingIntent.FLAG_UPDATE_CURRENT
	    				  );
	      mBuilder.setContentIntent(resultPendingIntent);
	      NotificationManager mNotificationManager =
	    		  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	      // mId allows you to update the notification later on.
	      
	      mNotificationManager.notify(mId, mBuilder.build());
	      */
	      
	      
	      /**
	       * Take some action upon receiving a push notification here!
	       **/
	      String message = intent.getExtras().getString(EXTRA_MESSAGE);
	      if (message == null) { message = "Empty Message"; }
	      
	      Log.i(TAG, message);
	      //messageTextView.append("\n" + message);

	      //alert.showAlertDialog(context, getString(R.string.gcm_alert_title), message);
	      //Toast.makeText(getApplicationContext(), getString(R.string.gcm_message, message), Toast.LENGTH_LONG).show();

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
	private int check = 0;
	
	
	
	public void Setup() {
		
		currentMemberId = mem_id;
		currentRoomId = createdRoomId;
		roomMemberId = listOfMemberIdStore1;
		roomLocation = tvLocation.getText().toString();
		creatorMemberId = creatorId;
		Log.i("Geraldine", "mem_id" + mem_id);
		Log.i("Geraldine", "createdRoomId" + createdRoomId);
		Log.i("Geraldine", "listOfMemberIdStore1" + listOfMemberIdStore1);
		Log.i("Geraldine", "tvLocation" + tvLocation.getText().toString());
		Log.i("Geraldine", "creatorId" + creatorId);
		
		
		check = 0;
		for (int i = 0; i < roomMemberId.size(); i++) {
			if (roomMemberId.get(i).equals(currentMemberId)) {
				check = 1;
			}
		}
		if (check == 1 && roomLocation.equals("none")) {

		} else {
			voteMenu.setVisible(false);
			voteResultMenu.setVisible(false);
		}

		new RetrieveRoomVote().execute();

		if (currentMemberId.equals(creatorMemberId))
			stat = 1;
		
		
	}

	public void Vote() {
		intent = new Intent(RoomDetails.this, VoteMap.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
		startActivityForResult(intent, 0);
	}

	public void VoteResult() {
		intent = new Intent(RoomDetails.this, VotingfPieChartBuilder.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
		intent.putExtra("CREATOR_STATUS", stat);
		startActivity(intent);
	}

	public class RetrieveRoomVote extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog1 = new ProgressDialog(activity);
			pDialog1.setMessage("Retreiving data...");
			pDialog1.setIndeterminate(false);
			pDialog1.setCancelable(true);
			pDialog1.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag

			VoteLocationDbAdapter mDbHelper = new VoteLocationDbAdapter(
					activity);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchAllRoomVote(currentRoomId);

			num = mCursor.getCount();

			mCursor.close();
			mDbHelper.close();

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			if (num == 0) {
				//btnViewResult.setVisibility(View.GONE);
				voteResultMenu.setVisible(false);

			} else {
				//btnViewResult.setVisibility(View.VISIBLE);
				voteResultMenu.setVisible(true);
				if (check == 1 && roomLocation.equals("none")) {
					new RetrieveMemberVote().execute();
				}
				else
					pDialog1.dismiss();
			}
		}
	}
	
	public class RetrieveMemberVote extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... args) {
			VoteLocationDbAdapter mDbHelper = new VoteLocationDbAdapter(
					RoomDetails.this);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchMemberRoomVote(currentMemberId,
					currentRoomId);

			num = mCursor.getCount();

			mCursor.close();
			mDbHelper.close();

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			pDialog1.dismiss();
			if (num != 0) {
				//btnVote.setVisibility(View.GONE);
				voteMenu.setVisible(false);
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

	/*

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
	
	}*/
	
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

	class RoomDelete extends AsyncTask<String, String, String> {
		
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
	        String post_roomId = createdRoomId;
	              
	        try {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("room_id", post_roomId));

	            Log.d("request!", "starting");
	            
	            //Posting user data to script 
	            JSONObject json = jsonParser.makeHttpRequest(
	            		ROOM_DELETE_URL, "POST", params);

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

	class RoomMemDelete extends AsyncTask<String, String, String> {
		
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
	        String post_roomId = createdRoomId;
	              
	        try {
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("room_id", post_roomId));

	            Log.d("request!", "starting");
	            
	            //Posting user data to script 
	            JSONObject json = jsonParser.makeHttpRequest(
	            		ROOM_MEM_DELETE_URL, "POST", params);

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
	
	public void Vote(View v) {
		intent = new Intent(activity, VoteMap.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
		startActivityForResult(intent, 0);
	}

	public void VoteResult(View v) {
		intent = new Intent(activity, VotingfPieChartBuilder.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
		intent.putExtra("CREATOR_STATUS", stat);
		startActivity(intent);
	}

}