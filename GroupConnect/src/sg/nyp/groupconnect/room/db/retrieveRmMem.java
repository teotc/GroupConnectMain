package sg.nyp.groupconnect.room.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.custom.CustomList;
import sg.nyp.groupconnect.entity.Schools;
import sg.nyp.groupconnect.room.CreateRmStep3;
import sg.nyp.groupconnect.room.RoomDetails;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class retrieveRmMem extends AsyncTask<Void, Void, Boolean> {

	//retrieveRmMem doinbackground & postexecute
	// Progress Dialog
	private ProgressDialog pDialog;
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	//ids
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_NOOFLEARNER = "noOfLearner";
	private static final String TAG_CREATORID = "creatorId";
	private static final String TAG_MEMBERID = "memberId";
	private static final String TAG_MEMBERTYPE = "memberType";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DATEFROM = "dateFrom";
	private static final String TAG_DATETO = "dateTo";
	private static final String TAG_TIMEFROM = "timeFrom";
	private static final String TAG_TIMETO = "timeTo";
	String createdRoomId;
	static String totalNoOfLearner;
	String creatorId;

	//For Retrieving Rm Members
	// testing from a real server:
	private static final String GET_RM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemRetrieve1.php";

	private ArrayList<HashMap<String, String>> mMemberList;
	private JSONArray mMember = null;
	String memberIdR = "";
	String memberTypeR = "";
	ArrayList<String> listOfMemberId = new ArrayList<String>();
	ArrayList<String> listOfMemberType = new ArrayList<String>();
	String statusR = "";
	String dateFromR = "";
	String dateToR = "";
	String timeFromR = "";
	String timeToR = "";
	
	//For CreateEduMemList()
	String memId = "";
	public static String memType = "";
	
	public static int currentCount = 0; //Used to avoid data being overwritten
	public static ArrayList<String> tempListId = new ArrayList<String>();
	public static ArrayList<String> tempListType = new ArrayList<String>();
	public static ArrayList<String> tempListName = new ArrayList<String>();
	ArrayList<String> listOfMemberIdStore = new ArrayList<String>();
	ArrayList<String> listOfMemberIdStore1 = new ArrayList<String>();
	public static ArrayList<String> educatorArray = new ArrayList<String>();
	public static ArrayList<String> memberArray = new ArrayList<String>();
	
	//JoinRequirement
	String mem_id, mem_username, mem_type;
	
	RoomDetails rmd = new RoomDetails();
	
	public static Integer[] imageId = {
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher
			
	};

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


	public void updateJSONdata() {
		Log.i("sg.nyp.groupconnect", "updateJSONdata");
		mMemberList = new ArrayList<HashMap<String, String>>();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("title", RoomDetails.getTvTitle()));

		Log.d("request!", "starting");
		JSONObject json = jsonParser.makeHttpRequest(GET_RM_MEM_URL, "POST",
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
				RoomDetails.createdRoomId = c.getString(TAG_ROOMID);
				totalNoOfLearner = c.getString(TAG_NOOFLEARNER);
				memberIdR = c.getString(TAG_MEMBERID);
				memberTypeR = c.getString(TAG_MEMBERTYPE);
				creatorId = c.getString(TAG_CREATORID);
				RoomDetails.creatorId = c.getString(TAG_CREATORID);
				
				statusR = c.getString(TAG_STATUS);
				dateFromR = c.getString(TAG_DATEFROM);
				dateToR = c.getString(TAG_DATETO);
				timeFromR = c.getString(TAG_TIMEFROM);
				timeToR = c.getString(TAG_TIMETO);
				
				// creating new HashMap and store all data 
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_ROOMID, createdRoomId);
				map.put(TAG_NOOFLEARNER, totalNoOfLearner);
				map.put(TAG_MEMBERID, memberIdR);
				map.put(TAG_MEMBERTYPE, memberTypeR);
				map.put(TAG_CREATORID, creatorId);
				map.put(TAG_STATUS, statusR);
				map.put(TAG_DATEFROM, dateFromR);
				map.put(TAG_DATETO, dateToR);
				map.put(TAG_TIMEFROM, timeFromR);
				map.put(TAG_TIMETO, timeToR);
				

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
			
			RoomDetails.tvStatus.setText( mMemberList.get(0).get(TAG_STATUS));
			RoomDetails.tvDate.setText(
					 mMemberList.get(0).get(TAG_DATEFROM) + " to " +
							 mMemberList.get(0).get(TAG_DATETO));
			RoomDetails.tvTime.setText(
					 mMemberList.get(0).get(TAG_TIMEFROM) + " to " +
							 mMemberList.get(0).get(TAG_TIMETO));

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
			createEduMemList(listOfMemberId, listOfMemberType);

		}



	}
	
	public void createEduMemList(ArrayList listOfMemberId , ArrayList listOfMemberType)
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
		RoomDetails.listOfMemberIdStore1 = listOfMemberId;
		joinRequirement(listOfMemberIdStore);
		Log.i("RoomDetails", "ListOfMemberId SIZE: " + listOfMemberId.size() );
		
		
		for(int i = 0; i <listOfMemberId.size(); i ++)
		{
			
			//AsyncTask method runs together with the main codes. Thus, storing data in array first
			//To avoid data being overwritten while the AsyncTask method is still running the previous method
			Log.i("RoomDetails", "In CreateEduMemList1   2");
			memId = listOfMemberId.get(i).toString();
			tempListId.add(memId);
			Log.i("RoomDetails",memId );
			memType = listOfMemberType.get(i).toString();
			tempListType.add(memType);
			Log.i("RoomDetails",memType );
			/*if (memType.equals("Educator"))
			{
				Log.i("RoomDetails", "UsernameRetrieve E");
				//Retrieve username + storing of ListView
				new usernameRetrieve().execute();
				
				
			}
			else
			{
				RoomDetails.tvNoOfEducator.setText("0/1");
			}
			
			if (memType.equals("Learner"))
			{
				Log.i("RoomDetails", "UsernameRetrieve L");
				new usernameRetrieve().execute();
			}
			else
			{
				RoomDetails.tvNoOfLearner.setText("0/" + totalNoOfLearner);
			}*/
			
		}
		
		new usernameRetrieve().execute();
		

		
		
		
		
		
	}
	
	//To check whether the Join button should appear or not
		boolean join = false;
		public void joinRequirement(ArrayList listOfMemberId)
		{
			mem_id = RoomDetails.mem_id;
			mem_username = RoomDetails.mem_username;
			mem_type = RoomDetails.mem_type;
			
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
			{
				RoomDetails.btnJoin.setVisibility(View.GONE);
				rmd.joinMenu.setVisible(false);
			}
			else if (join == false)
			{
				RoomDetails.btnJoin.setVisibility(View.VISIBLE);
				rmd.joinMenu.setVisible(true);
			}
		}
}
