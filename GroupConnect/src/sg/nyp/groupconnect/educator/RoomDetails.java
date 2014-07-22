package sg.nyp.groupconnect.educator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.room.*;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class RoomDetails extends Activity {

	ListView educatorlist, learnerlist;
	TextView tvTitle, tvCategory, tvNoOfLearner, tvLocation, tvNoOfEducator;
	Button btnDone, btnUpdate, btnJoin;
	ArrayList<String> educatorArray = new ArrayList<String>();
	ArrayList<String> memberArray = new ArrayList<String>();
	
	private static final int VISIBLE = 0;
	private static final int GONE = 2;
	String learnerExist = "";
	String educatorExist = "";
	String mem_id, mem_username, mem_type;
	
	boolean comeFromMain = true;
	private static final int UPDATE_RM_RESULT_CODE = 100;
	
	/*String[] educator = {
			"You (Alfred)"
	};
	
	String[] learner = {
			"Aloysius",
			"Ben",
			"Chris",
			"Kelly",
			
	};*/
	
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
    private static final String GETROOMDETAILS_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomDetailRetrieve.php";
    private static final String TAG_ROOMID = "room_id";
    private static final String TAG_NOOFLEARNER = "noOfLearner";
    String createdRoomId, totalNoOfLearner;
	
	//For Retrieving Rm Members
	// testing from a real server:
	private static final String GET_RM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemRetrieve.php";
	private static final String TAG_EDUCATOR = "educator";
	private static final String TAG_MEMBER = "member";
	
	String educatorR, memberR;
	String contentR = "";
	String[] educatorS;
	String[] memberS;
	
	//For updating the Room
	private static final String ROOM_JOIN_LEARNER_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomJoinAsLearner.php";
	private static final String ROOM_JOIN_EDUCATOR_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomJoinAsEducator.php";
	
	//For Creating a Notification
	private static final String NOTIFICATION_CREATE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/notificationCreate.php";
	String nTitle, nMessage, nType;
	
	//For retrieving UserId using a username
	private static final String USERID_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/userIdRetrieve.php";
	String userIdR;
	String usernameForID;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_details);

		pDialog = new ProgressDialog(RoomDetails.this);
		pDialog.setMessage("Loading Details...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
		
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvCategory = (TextView) findViewById(R.id.tvCategory);
		tvNoOfLearner = (TextView) findViewById(R.id.tvNoOfLearner);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvNoOfEducator = (TextView) findViewById(R.id.tvNoOfEducator);
		
		btnDone = (Button) findViewById(R.id.btnDone);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnJoin = (Button) findViewById(R.id.btnJoin);
		
		
		
		
		Intent intent = new Intent();
		if (comeFromMain == true)
		{
			String title = this.getIntent().getStringExtra("title");
			String location = this.getIntent().getStringExtra("location");
			String category = this.getIntent().getStringExtra("category");
			
			
			tvTitle.setText(title);
			tvLocation.setText(location);
			tvCategory.setText(category);
			
			comeFromMain = false;
		}
		
		//Toast.makeText(RoomDetails.this, tvTitle.getText().toString(), Toast.LENGTH_LONG).show();
		//Find the room Id created.
    	new retrieveCreatedRmId().execute();
    	new retrieveRmMem().execute();    	
    
		
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
        		if (mem_type.equals("Educator"))
        		{
        			educatorExist += mem_username + ",";
        			new roomJoinByEducator().execute(); //To add in the educator
        			new retrieveRmMem().execute(); //To refresh the page
        			Toast.makeText(RoomDetails.this, "Educator JOIN", Toast.LENGTH_LONG).show();
        			
        			if (!learnerExist.equals(""))
        			{
        				String[] learnerNames = learnerExist.split(",");
        				for (int i = 0; i < learnerNames.length; i++)
        				{
        					usernameForID = learnerNames[i];
        					Log.i("Test1", usernameForID);
        					new userIdRetrieve().execute();
        					nTitle = "An Educator has join one of your interested group!";
    	        			nMessage = mem_username + " (Educator) has join your group (" + tvTitle.getText().toString() + ")";
    	        			nType = "Join Group";
    	        			new notificationCreate().execute();
    	        			try {
								Thread.sleep(1500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

        				}
        			}
        			
        		}
        		
        		if (mem_type.equals("Learner"))
        		{
        			
	        		//To Join the room.
	        		learnerExist += mem_username +",";
	        		new roomJoinByLearner().execute(); //To add in the learner
	        		new retrieveRmMem().execute(); //To refresh the page
	        		
	        		Toast.makeText(RoomDetails.this, "Learner JOIN", Toast.LENGTH_LONG).show();
	        		
	        		//To send Notification to all educator
	        		if (!educatorExist.equals(""))
	        		{
	        					
	        			String eduName = educatorExist.replaceAll(",", ""); //Since educator is only limit to one
	        			usernameForID = eduName;
	        			new userIdRetrieve().execute();
	        			nTitle = "Someone join your group!";
	        			nMessage = mem_username + " has join your group (" + tvTitle.getText().toString() + ")";
	        			nType = "Join Group";
	        			new notificationCreate().execute();
	        			
	        		}
	        		
	        		
        			
        		}
        	}
        });
	}
	
	//To check whether the Join button should appear or not
	public void joinRequirement(String educatorJoined, String learnerJoined)
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
		mem_id = sp.getString("id", "No ID");
		mem_username = sp.getString("username", "No Username");
		mem_type = sp.getString("type", "No Type");
		if (mem_type.equals("Educator"))
		{
			// If there is even one educator exist, educator not allow to join again
			if (educatorJoined.equals(""))
				btnJoin.setVisibility(View.VISIBLE);
			else
				btnJoin.setVisibility(View.GONE);
			
		}
		else if (mem_type.equals("Learner"))
		{
			Log.i("Test", "Learner");
			boolean join = false;
			Log.i("Test", "LearnerExist" + learnerJoined);
			if (!learnerJoined.equals(""))
			{
				Log.i("Test", "Learner - Not Empty");
				//To check if you have already joined
				String[] checkList = learnerJoined.split(",");
				
				for (int i = 0; i < checkList.length; i++)
				{
					String existName = checkList[i];
					if (mem_username.equals(existName))
					{
						join = true;
					}
				}
			
			
				String[] noOfExistLearner = learnerJoined.split(",");
				int totalNo = Integer.parseInt(totalNoOfLearner);
				// If the room is full OR you have already join the room, not allow to join anymore.
				if (noOfExistLearner.length >= totalNo || join == true)
					btnJoin.setVisibility(View.GONE);
				else
					btnJoin.setVisibility(View.VISIBLE);
			}
			else
			{
				Log.i("Test", "Learner - Empty");
				btnJoin.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void createEduMemList(String contentR)
	{
		String[] split = contentR.split("/");
		String educatorL = "";
		String memberL = "";
		educatorArray.clear();
		memberArray.clear();
		if (split.length == 1)
		{
			educatorL = split[0];
			joinRequirement(educatorL, "");
		}
		else if (split.length == 2)
		{
			educatorL = split[0];
			memberL = split[1];
			joinRequirement(educatorL, memberL);
		}
				
		if (!educatorL.equals(""))
		{
			educatorS = educatorL.split(",");
			for (int i = 0; i<educatorS.length; i++)
			{
				educatorArray.add(educatorS[i].toString() + ",");
			}
			
			tvNoOfEducator.setText(educatorArray.size() + "/1");
			
			CustomList adapter = new CustomList(RoomDetails.this, educatorArray, imageId);
			educatorlist = (ListView) findViewById(R.id.educatorList);
			educatorlist.setAdapter(adapter);
		}
		else
		{
			tvNoOfEducator.setText("0/1");
		}
		if (!memberL.equals(""))
		{
			memberS = memberL.split(",");
			for (int i = 0; i<memberS.length; i++)
			{
				memberArray.add(memberS[i].toString() + ",");
			}
			
			tvNoOfLearner.setText(memberArray.size() + "/" + totalNoOfLearner);
			
			CustomList adapter1 = new CustomList(RoomDetails.this, memberArray, imageId);
			learnerlist = (ListView) findViewById(R.id.learnerList);
			learnerlist.setAdapter(adapter1);
		}
		else
		{
			tvNoOfLearner.setText("0/" + totalNoOfLearner);
		}
		
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

	
	class retrieveCreatedRmId extends AsyncTask<String, String, String> {

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
			String title = tvTitle.getText().toString();
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("title", title));

				Log.d("request!", "starting");
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(GETROOMDETAILS_URL, "POST",
						params);

				// check your log for json response
				Log.d("Login attempt", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				//createdRoomId = json.getString(TAG_ROOMID).toString() + "?";
				
				if (success == 1) {
					Log.d("Login Successful!", json.toString());
					//The one that cause the roomId to be stored.
					//Not accessable in onCreate method
					createdRoomId = json.getString(TAG_ROOMID);
					totalNoOfLearner = json.getString(TAG_NOOFLEARNER);
					return json.getString(TAG_ROOMID);
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
				//Toast.makeText(RoomDetails.this, "File_url: " + file_url, Toast.LENGTH_LONG).show();
				createdRoomId = file_url;
				//Toast.makeText(RoomDetails.this, "CreatedRoomId: " + createdRoomId, Toast.LENGTH_LONG).show();
			}

		}

	}


	class retrieveRmMem extends AsyncTask<String, String, String> {

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
			String room_id = createdRoomId;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("room_id", room_id));

				Log.d("request!", "starting");
				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(GET_RM_MEM_URL, "POST",
						params);

				// check your log for json response
				Log.d("Login attempt", json.toString());

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				//createdRoomId = json.getString(TAG_ROOMID).toString() + "?";
				
				if (success == 1) {
					Log.d("Login Successful!", json.toString());
					
					//Store content with "educator/member"
					String tempEducator = json.getString(TAG_EDUCATOR);
					String tempMem = json.getString(TAG_MEMBER);
					String tempContent = tempEducator + "/" + tempMem;
					/*
					// Store into somewhere.
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RoomDetails.this);
					Editor edit = sp.edit();
					edit.putString("educator_Mem", tempContent);
					edit.commit(); */
					
					learnerExist = tempMem;
					educatorExist = tempEducator;

					//Return the educator & member to be displayed
					return tempContent;
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
				createEduMemList(file_url);

			}

		}

	}
	
class roomJoinByLearner extends AsyncTask<String, String, String> {
		
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
            String post_member = learnerExist;
            
            
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("member", post_member));
                params.add(new BasicNameValuePair("room_id", post_roomId));
 
                Log.d("request!", "starting");
                
                //Posting user data to script 
                JSONObject json = jsonParser.makeHttpRequest(
                		ROOM_JOIN_LEARNER_URL, "POST", params);
 
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

class roomJoinByEducator extends AsyncTask<String, String, String> {
	
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
        String post_educator = educatorExist;
        
        
        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("username", post_username));
            params.add(new BasicNameValuePair("educator", post_educator));
            params.add(new BasicNameValuePair("room_id", post_roomId));

            Log.d("request!", "starting");
            
            //Posting user data to script 
            JSONObject json = jsonParser.makeHttpRequest(
            		ROOM_JOIN_EDUCATOR_URL, "POST", params);

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

class userIdRetrieve extends AsyncTask<String, String, String> {

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
		String username = usernameForID;
		Log.i("Test2", username);
		try {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));

			Log.d("request!", "starting");
			// getting product details by making HTTP request
			JSONObject json = jsonParser.makeHttpRequest(USERID_RETRIEVE_URL, "POST",
					params);

			// check your log for json response
			Log.d("Login attempt", json.toString());

			// json success tag
			success = json.getInt(TAG_SUCCESS);
			//createdRoomId = json.getString(TAG_ROOMID).toString() + "?";
			
			if (success == 1) {
				Log.d("Login Successful!", json.toString());
				
				userIdR = json.getString("userId");
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



}
