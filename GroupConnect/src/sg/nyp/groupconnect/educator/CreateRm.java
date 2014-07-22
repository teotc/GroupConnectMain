package sg.nyp.groupconnect.educator;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.utilities.JSONParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;

public class CreateRm extends Activity {

	TextView tvTitle, tvCategory, tvLocation;
	EditText etTitle, etCategory, etNoOfLearner, etLocation;
	Button btnSubmit, btnClear, btnCancel;
	
	//Getting User sign in info
	String mem_username = "";
	String mem_type = "";
	
	final static String CREATE = "message";
	final static String CANCEL = "message";
	
	final static String TITLE = "title";
	final static String LOCATION = "location";
	final static String NOOFLEARNER = "noOfLearner";
	final static String CATEGORY = "category";
	
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

    //For receiving intent from MainActivity
	String location, lat, lng;
	
	//For Retrieving created room_id
	// testing from a real server:
	 private static final String GETROOMID_URL =
	 "http://www.it3197Project.3eeweb.com/grpConnect/roomIdRetrieve.php";
	 private static final String TAG_ROOMID = "room_id";
	 String createdRoomId = null;
	 int createdRoomIdI = 0;
	 boolean successAll = true;
	 
	 private static final String CREATE_ROOM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemCreate.php"; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rm);
		
		Intent intent = new Intent();
		location = this.getIntent().getStringExtra("location");
		lat = this.getIntent().getStringExtra("lat");
		lng = this.getIntent().getStringExtra("lng");
		/*if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		
		 etTitle = (EditText) findViewById(R.id.etTitle);
		 etCategory = (EditText) findViewById(R.id.etCategory);
		 etNoOfLearner = (EditText) findViewById(R.id.etNoOfLearner);
		 etLocation = (EditText) findViewById(R.id.etLocation);
	     btnSubmit = (Button) findViewById(R.id.btnSubmit);
	     btnClear = (Button) findViewById(R.id.btnClear);
	     btnCancel = (Button) findViewById(R.id.btnCancel);
	     
	     etLocation.setText(location);
	     
	     SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateRm.this);
         mem_username = sp.getString("username", "No Username");
         mem_type = sp.getString("type", "No Type");
	     //Toast.makeText(getApplicationContext(), post_username + "\n" + post_type, Toast.LENGTH_LONG).show();
	     
	     btnSubmit.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		//String message;
        		boolean success = true;
        		
        		//Check for any empty EditText
        		if (etTitle.getText().toString().length() <= 0)
        		{
                	etTitle.setError("Enter Title");
        			success = false;
        		}
        		
                if (etCategory.getText().toString().length() <= 0)
                {
                	etCategory.setError("Enter Category");
                	success = false;
                }

                if (etNoOfLearner.getText().toString().length() <= 0)
                {
                	etNoOfLearner.setError("Enter No. of Learner");
        			success = false;
                }
                
                if (etLocation.getText().toString().length() <= 0)
                {
                	etLocation.setError("Enter Location");
        			success = false;
                }
                
                if (success == true) //If all fields are filled
                {
	                //put data to be return to parent in an intent
					Intent output = new Intent();
					output.putExtra("roomCreated", "rmCreated");
					//output.putExtra(CREATE, "Room Created");
					//output.putExtra(TITLE, etTitle.getText().toString());
					//output.putExtra(NOOFLEARNER, etNoOfLearner.getText().toString());
					//output.putExtra(LOCATION, etLocation.getText().toString());
					//output.putExtra(CATEGORY, etCategory.getText().toString());
					// Set the results to be returned to parent
					setResult(RESULT_OK, output);
					
					
					new createRoom().execute();
					//Once the room is created successfully in Table 'Room'
                	//Find the room Id created.
                	new retrieveCreatedRmId().execute();
                	
            		//Than use the roomId to create another row for RoomMember to create Member data
                	new createRoomMember().execute();
                	
                	if (successAll == true)
                	{
                		try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                		finish();
                	}
                	else
                		Toast.makeText(CreateRm.this, "SuccessAll: " + successAll, Toast.LENGTH_LONG).show();
                	
                	
					
                }

        	}
        });
	     
	     btnCancel.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v)
	        	{
	        		
	                //put data to be return to parent in an intent
					Intent output = new Intent();
					output.putExtra("Cancel", "Canceled");
					// Set the results to be returned to parent
					setResult(RESULT_CANCELED, output);
					
					// Ends the sub-activity
					finish();
	        	
	        	}
	        });
	     
	     btnClear.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v)
	        	{
	        		etTitle.setText("");
		       		etCategory.setText("");
		       		etNoOfLearner.setText("");
		       		etLocation.setText("");
	                
	        	
	        	}
	        });
	     
		
		
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
		return super.onOptionsItemSelected(item);
	}

class createRoom extends AsyncTask<String, String, String> {
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateRm.this);
            pDialog.setMessage("Posting Comment...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
            int success;
            String post_title = etTitle.getText().toString();
            String post_category = etCategory.getText().toString();
            String post_noOfLearner = etNoOfLearner.getText().toString();
            String post_location = etLocation.getText().toString();
            String post_latLng = lat + "," + lng;
            String post_username = mem_username;
            
            
            
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("title", post_title));
                params.add(new BasicNameValuePair("category", post_category));
                params.add(new BasicNameValuePair("noOfLearner", post_noOfLearner));
                params.add(new BasicNameValuePair("location", post_location));
                params.add(new BasicNameValuePair("latLng", post_latLng));
                params.add(new BasicNameValuePair("username", post_username));
 
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
            pDialog.dismiss();
            if (file_url != null){
            	Toast.makeText(CreateRm.this, file_url, Toast.LENGTH_LONG).show();
            }
 
        }
		
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
		String title = etTitle.getText().toString();
		try {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("title", title));

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
		//pDialog.dismiss();
		if (file_url != null) {
			//Toast.makeText(CreateRm.this, "Room id created: " + file_url, Toast.LENGTH_LONG).show();
			//createdRoomId = file_url;
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
        String post_roomId = createdRoomId;
        
        String post_educator = "";
        String post_member = "";
        String type = mem_type;
        
        if (type.equals("Educator"))
        {
        	post_educator = mem_username + ",";
        }
        else if (type.equals("Learner"))
        {
        	post_member = mem_username + ",";
        }
        	
        
        
        //We need to change this:
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateRm.this);
        //String post_username = sp.getString("username", "anon");
        
        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("username", post_username));
            params.add(new BasicNameValuePair("room_id", post_roomId));
            params.add(new BasicNameValuePair("educator", post_educator));
            params.add(new BasicNameValuePair("member", post_member));

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

}
