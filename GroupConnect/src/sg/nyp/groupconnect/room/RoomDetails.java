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

import sg.nyp.groupconnect.GCMIntentService;
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
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class RoomDetails extends Activity {
	// Variables

	public static ListView memberList;
	public static TextView tvTitle, tvCategory, tvLocation;
	public static TextView tvStatus, tvDate, tvTime;
	public static TextView tvNoOfLearner;
	public static TextView tvNoOfEducator;
	ArrayList<String> educatorArray = new ArrayList<String>();
	ArrayList<String> memberArray = new ArrayList<String>();

	// To store the current user information
	public static String mem_id, mem_username, mem_type;

	// To see if the page come from MainActivity
	// No need to load again if come from update page
	boolean comeFromMain = true;

	// onActivityResult
	private static final int UPDATE_RM_RESULT_CODE = 100;

	Integer[] imageId = { R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher

	};

	// For Retrieving created room_id, educator limit & Member Limit

	// Progress Dialog
	public static ProgressDialog pDialog;
	public static ProgressDialog pDialog1;
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	// ids
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_NOOFLEARNER = "noOfLearner";
	private static final String TAG_CREATORID = "creatorId";
	public static String createdRoomId;
	String totalNoOfLearner;
	public static String creatorId;

	// For Retrieving Rm Members
	// testing from a real server:
	private static final String GET_RM_MEM_1URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemRetrieve1.php";

	String contentR = "";

	// For Creating a Notification
	private static final String NOTIFICATION_CREATE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/notificationCreate.php";
	public static String nTitle;
	String nMessage;
	String nType;
	public static boolean joinForNotification;

	// For retrieving UserId using a username
	String userIdR;
	String usernameForID;

	private static final String MEM_UPDATE_DEVICE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/memUpdate.php";
	static String deviceUUID = "";
	String currentUserDevice = "";

	private static final String MEM_DEVICE_R_URL = "http://www.it3197Project.3eeweb.com/grpConnect/memDeviceR.php";
	String deviceR = "";
	String notifyUserUUID = "";

	public static void setDeviceUUID(String uuid) {
		deviceUUID = uuid;

	}

	String TAG_NO = "notification";

	private static final String TAG_POSTS = "posts";
	private static final String TAG_POST_ID = "post_id";
	private static final String TAG_MEMBERID = "memberId";
	private static final String TAG_MEMBERTYPE = "memberType";

	private static final String ROOM_JOIN_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemCreate.php";

	// For CreateEduMemList1()
	String memId = "";
	String memNameR = "";
	int currentCount = 0; // Used to avoid data being overwritten
	ArrayList<String> tempListId = new ArrayList<String>();
	ArrayList<String> tempListType = new ArrayList<String>();
	ArrayList<String> tempListName = new ArrayList<String>();
	ArrayList<String> listOfMemberIdStore = new ArrayList<String>();
	public static ArrayList<String> listOfMemberIdStore1 = new ArrayList<String>();

	// For retrieveRmMem1() - AsyncTask
	// An array of all of our comments
	private JSONArray mMember = null;
	// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mMemberList;
	String memberIdR = "";
	String memberTypeR = "";
	ArrayList<String> listOfMemberId = new ArrayList<String>();
	ArrayList<String> listOfMemberType = new ArrayList<String>();
	public static ArrayList<String> learnerInGrpList = new ArrayList<String>(); // For
																				// Update
																				// Step3

	// For usernameRetrieveE/L - AsyncTask
	private static final String USERNAME_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/usernameRetrieve.php";
	public static ArrayList<String> listOfMemUUID = new ArrayList<String>();
	String uuidR;

	// For Deleting
	// testing from a real server:
	private static final String ROOM_DELETE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomDelete.php";
	private static final String ROOM_MEM_DELETE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomMemDelete.php";

	public static Activity activity;
	public static MenuItem voteMenu;
	public static MenuItem voteResultMenu;
	MenuItem mainMenu;
	public static MenuItem joinMenu;
	public static MenuItem deleteMenu;
	public static MenuItem editMenu;
	public boolean voteMenuB = true;
	public boolean voteResultMenuB = true;
	public static boolean joinMenuB = true;
	boolean mainMenuB = true;

	public static String getTvTitle() {
		return tvTitle.getText().toString();
	}

	// For customDialog - Desc
	TextView tvDescForDialog;
	public static String descForDialog = "";

	// For CreateDialog
	int LEAVETHEROOM_ALERT = 2;

	// Leaving the room
	private static final String RM_MEM_LEAVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomMemLeave.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_details);
		// For usernameRetrieve.java
		activity = RoomDetails.this;

		pDialog = new ProgressDialog(RoomDetails.this);
		pDialog.setMessage("Loading Details...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvCategory = (TextView) findViewById(R.id.tvCategory);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvTime = (TextView) findViewById(R.id.tvTime);

		memberList = (ListView) findViewById(R.id.memberList);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(RoomDetails.this);
		mem_id = sp.getString("id", "No ID");
		mem_username = sp.getString("username", "No Username");
		mem_type = sp.getString("type", "No Type");

		Intent intent = new Intent();
		// To see if the page come from MainActivity
		// No need to load again if come from update page
		if (comeFromMain == true) {
			String title = this.getIntent().getStringExtra("title");

			tvTitle.setText(title);

			// To prepare for push notification START

			// this is a hack to force AsyncTask to be initialized on main
			// thread. Without this things
			// won't work correctly on older versions of Android (2.2,
			// apilevel=8)

			try {
				Class.forName("android.os.AsyncTask");
			} catch (Exception ignored) {
			}

			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);

			AppServices.loginAndRegisterForPush(this);

			// To prepare for push notification END

			comeFromMain = false;

		}

		Intent intent1 = getIntent();
		Bundle extras = intent1.getExtras();
		if (extras != null) {

			tvTitle.setText(extras.getString("title"));
		}

		// To retrieve and display all members including educator and learners
		new retrieveRmMem().execute();

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			comeFromMain = true;
			finish();
		}
		return false;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("RoomDetails", "RESUME");

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == UPDATE_RM_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				if (data.getStringExtra("delete").equals("False")) {
					tvTitle.setText(data.getStringExtra("title"));
					tvCategory.setText(data.getStringExtra("category"));
					tvLocation.setText(data.getStringExtra("location"));
				} else if (data.getStringExtra("delete").equals("True")) {
					finish();
				}

			}
		}
		if (resultCode == 1) {
			voteMenu.setVisible(false);
			voteResultMenu.setVisible(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.room_details, menu);
		deleteMenu = menu.findItem(R.id.delete);
		editMenu = menu.findItem(R.id.edit);
		joinMenu = menu.findItem(R.id.join); // Visible directly controled in
												// retrieveRmMem
		voteMenu = menu.findItem(R.id.vote);
		voteResultMenu = menu.findItem(R.id.viewVoteResult);
		mainMenu = menu.findItem(R.id.menu);

		mainMenu.setVisible(true);

		Log.i("RoomDetails", "Menus: " + joinMenuB);
		Log.i("RoomDetails", "Menus: " + voteMenuB);
		Log.i("RoomDetails", "Menus: " + voteResultMenuB);
		Log.i("RoomDetails", "Menus: " + mainMenuB);

		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		} else if (id == R.id.edit) {
			Intent myIntent = new Intent(RoomDetails.this, CreateRm.class);
			myIntent.putExtra("roomId", createdRoomId);
			myIntent.putExtra("update", true);
			myIntent.putStringArrayListExtra("memList", learnerInGrpList);
			startActivity(myIntent);
			return true;
		} else if (id == R.id.delete) {
			showDialog(1);
			return true;
		} else if (id == R.id.join) {
			if (joinMenu.getTitle().equals("Join")) {
				pDialog1 = new ProgressDialog(this);
				pDialog1.setMessage("Updating Room...");
				pDialog1.setIndeterminate(false);
				pDialog1.setCancelable(true);
				pDialog1.show();

				for (int i = 0; i < listOfMemberIdStore.size(); i++) {
					Log.i("RoomDetails", "ListOfMemberIdStore1: "
							+ listOfMemberIdStore1.get(i));
				}

				new createRoomMember().execute(); // Add the user in the group

				// educatorlist.
				// learnerlist.clear();
				new retrieveRmMem().execute(); // To refresh the page

				// For Push Notification
				// Get all members id
				// listOfMemberIdStore1 --> Got from createRoomMember AsyncTask
				for (int i = 0; i < listOfMemberIdStore1.size(); i++) {
					// If it is different from the user's id (No reason to send
					// notification to the person
					// who join the grp)
					if (!mem_id.equals(listOfMemberIdStore1.get(i))) {
						// Different message according to user's type
						if (mem_type.equals("Educator")) {
							nTitle = "An Educator has join one of your interested group!";
							nMessage = mem_username
									+ " (Educator) has join your group \n("
									+ tvTitle.getText().toString() + ")";
							nType = "Join Group";
						} else if (mem_type.equals("Learner")) {
							nTitle = "Someone join your group!";
							nMessage = mem_username
									+ " has join your group \n("
									+ tvTitle.getText().toString() + ")";
							nType = "Join Group";
						}
						joinForNotification = true;

						userIdR = listOfMemberIdStore1.get(i);
						// For Internal notification purpose
						new notificationCreate().execute();

						notifyUserUUID = listOfMemUUID.get(i);
						if (!notifyUserUUID.equals("")) {
							GCMIntentService gcm = new GCMIntentService();
							gcm.setType("Join");
							pushNotification(getWindow().getDecorView()
									.findViewById(android.R.id.content),
									nMessage, notifyUserUUID, userIdR);
						}

					}
				}
			} else if (joinMenu.getTitle().equals("Leave the Room")) {
				showDialog(LEAVETHEROOM_ALERT);
			}

			pDialog1.dismiss();
			return true;
		} else if (id == R.id.vote) {
			Vote();
			return true;
		} else if (id == R.id.viewVoteResult) {
			VoteResult();
			return true;
		} else if (id == R.id.desc) {
			showCustomDialog(descForDialog);
		}
		return super.onOptionsItemSelected(item);
	}

	// For delete purpose. Warning Message
	AlertDialog dialog;

	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete this room?");
			builder.setCancelable(true);
			builder.setPositiveButton("Ok", new OkOnClickListener());
			builder.setNegativeButton("Cancel", new CancelOnClickListener());
			dialog = builder.create();
			dialog.show();
		}

		else if (id == LEAVETHEROOM_ALERT) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to leave this room?");
			builder.setCancelable(true);
			builder.setPositiveButton("Ok", new OkLeaveClickListener());
			builder.setNegativeButton("Cancel", new CancelOnClickListener());
			dialog = builder.create();
			dialog.show();
		}

		return super.onCreateDialog(id);
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(getApplicationContext(), "Room Deleted",
					Toast.LENGTH_LONG).show();
			new RoomDelete().execute();
			new RoomMemDelete().execute();
			Intent output = new Intent();
			output.putExtra("delete", "True");
			// Set the results to be returned to parent
			setResult(RESULT_OK, output);

			for (int i = 0; i < listOfMemUUID.size(); i++) {
				notifyUserUUID = listOfMemUUID.get(i);
				if (!notifyUserUUID.equals("")) {
					GCMIntentService gcm = new GCMIntentService();
					gcm.setType("Delete");
					String nMessage = "The Creator of the Room has close the Room";
					pushNotificationForDelete(getWindow().getDecorView()
							.findViewById(android.R.id.content), nMessage,
							notifyUserUUID);
				}
			}

			finish();
		}
	}

	private final class OkLeaveClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			new RoomMemLeave().execute();
			Log.i("UUIDTest", String.valueOf(listOfMemUUID.size()));
			for (int i = 0; i < listOfMemUUID.size(); i++) {

				notifyUserUUID = listOfMemUUID.get(i);
				if (!notifyUserUUID.equals("")) {
					Log.i("UUIDTest", notifyUserUUID);
					GCMIntentService gcm = new GCMIntentService();
					gcm.setType("Leave");
					String nMessage = mem_username + " has leave the room";
					pushNotificationForDelete(getWindow().getDecorView()
							.findViewById(android.R.id.content), nMessage,
							notifyUserUUID);
				}
			}

			new retrieveRmMem().execute();

		}
	}

	private final class CancelOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Toast.makeText(getApplicationContext(), "Canceled",
					Toast.LENGTH_LONG).show();
		}
	}

	protected void showCustomDialog(String desc) {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(RoomDetails.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_desc_dialog);

		// Exist
		tvDescForDialog = (TextView) dialog.findViewById(R.id.tvDescForDialog);
		tvDescForDialog.setText(desc);

		dialog.show();
	}

	public void pushNotification(View v, String msg, String device,
			String username) {

		Log.i(TAG_NO, username + " pushNotification - device: " + device);
		AppServices.sendMyselfANotification(this, msg, device);
		registerReceiver(notificationReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		comeFromMain = true;
	}

	public void pushNotificationForDelete(View v, String msg, String device) {

		AppServices.sendMyselfANotification(this, msg, device);
		registerReceiver(notificationReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
	}

	/**
	 * Receives push Notifications
	 * */
	int mId = 1;
	private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
		private AlertDialogManager alert = new AlertDialogManager();

		@SuppressLint("NewApi")
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

			Log.i(TAG, message);

			WakeLocker.release();
		}
	};

	// this will be called when the screen rotates instead of onCreate()
	// due to manifest setting, see: android:configChanges
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// initUI();
	}

	// Geraldine's part START

	private Intent intent = null;

	// Required info
	private String currentMemberId = "";
	// private String[] roomMemberId = new String[] { "1001", "1002", "1003",
	// "1004", "1005", "1006", "1007" };
	private ArrayList<String> roomMemberId = new ArrayList<String>();
	private String roomLocation = "";
	private String creatorMemberId = "";
	private int stat = 0;
	private int num = 0;
	private int check = 0;

	public void Setup() {

		currentMemberId = mem_id;
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
		Intent intent = new Intent(RoomDetails.this, VoteMap.class);
		intent.putExtra("CURRENT_ROOM_ID", createdRoomId);
		startActivityForResult(intent, 0);
	}

	public void VoteResult() {
		Intent intent = new Intent(RoomDetails.this,
				VotingfPieChartBuilder.class);
		intent.putExtra("CURRENT_ROOM_ID", createdRoomId);
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

			Cursor mCursor = mDbHelper.fetchAllRoomVote(createdRoomId);

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
				// btnViewResult.setVisibility(View.GONE);
				voteResultMenu.setVisible(false);

			} else {
				// btnViewResult.setVisibility(View.VISIBLE);
				voteResultMenu.setVisible(true);
				if (check == 1 && roomLocation.equals("none")) {
					new RetrieveMemberVote().execute();
				}
				// else
				// pDialog1.dismiss(); //This means pDialog1 wont be dismiss if
				// num==0 -- Alfred
			}

			pDialog1.dismiss();
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
					activity);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchMemberRoomVote(currentMemberId,
					createdRoomId);

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
				voteMenu.setVisible(false);
			}
		}
	}

	// Geraldine's part END

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

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("title", post_title));
				params.add(new BasicNameValuePair("message", post_message));
				params.add(new BasicNameValuePair("type", post_type));
				params.add(new BasicNameValuePair("userId", post_userId));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						NOTIFICATION_CREATE_URL, "POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());

					return json.getString(TAG_MESSAGE);
				} else {
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

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(RoomDetails.this);
			Editor edit = sp.edit();
			String post_memId = sp.getString("id", "No Id");
			String post_device = deviceUUID;

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("username",
				// post_username));
				params.add(new BasicNameValuePair("id", post_memId));
				params.add(new BasicNameValuePair("device", post_device));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						MEM_UPDATE_DEVICE_URL, "POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());

					// Get the updated device uuid
					currentUserDevice = post_device;
					return json.getString(TAG_MESSAGE);
				} else {
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
			if (file_url != null) {
			}

		}

	}

	class MemDeviceR extends AsyncTask<String, String, String> {

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
					.getDefaultSharedPreferences(RoomDetails.this);
			String post_memId = sp.getString("id", "No Id");
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", post_memId));

				Log.d("request!", "starting");
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(MEM_DEVICE_R_URL,
						"POST", params);

				// check your log for json response
				Log.d("Login attempt", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);

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
			if (file_url != null) {

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
			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(RoomDetails.this);

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

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(ROOM_JOIN_URL,
						"POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());
					// finish();
					return json.getString(TAG_MESSAGE);
				} else {
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
			if (file_url != null) {
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
					.getDefaultSharedPreferences(RoomDetails.this);
			String post_roomId = createdRoomId;
			String post_memberId = sp.getString("id", "No ID");
			String post_memberType = sp.getString("type", "No Type");
			Log.i("RoomDetails", "Post_roomId" + post_roomId);
			Log.i("RoomDetails", "Post_memberId" + post_memberId);
			Log.i("RoomDetails", "Post_memberType" + post_memberType);

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("room_id", post_roomId));
				params.add(new BasicNameValuePair("memberId", post_memberId));
				params.add(new BasicNameValuePair("memberType", post_memberType));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(ROOM_JOIN_URL,
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

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(ROOM_DELETE_URL,
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

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						ROOM_MEM_DELETE_URL, "POST", params);

				// full json response
				Log.d("Post Comment attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Comment Added!", json.toString());
					return json.getString(TAG_MESSAGE);
				} else {
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

	class RoomMemLeave extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String post_roomId = createdRoomId;
			String post_memberId = mem_id;

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("room_id", post_roomId));
				params.add(new BasicNameValuePair("memberId", post_memberId));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(RM_MEM_LEAVE_URL,
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