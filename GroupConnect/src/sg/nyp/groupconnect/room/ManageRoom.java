package sg.nyp.groupconnect.room;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.R;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class ManageRoom extends Activity {
	
	EditText etTitle, etCategory, etLocation;
	Button btnUpdate, btnDelete;
	String roomId = null;
	String title = null;
	String category = null; 
	String location = null;

	//For Updating
	 // Progress Dialog
    private ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    //testing from a real server:
    private static final String ROOM_UPDATE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomUpdate.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    
    //For Deleting
    //testing from a real server:
    private static final String ROOM_DELETE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomDelete.php";
    private static final String ROOM_MEM_DELETE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomMemDelete.php";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_room);
		
		etTitle = (EditText) findViewById(R.id.etTitle);
		etCategory = (EditText) findViewById(R.id.etCategory);
		etLocation = (EditText) findViewById(R.id.etLocation);
		
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		
		Intent intent = new Intent();
		roomId = this.getIntent().getStringExtra("roomId");
		title = this.getIntent().getStringExtra("title");
		category = this.getIntent().getStringExtra("category");
		location = this.getIntent().getStringExtra("location");
		
		etTitle.setText(title);
		etCategory.setText(category);
		etLocation.setText(location);
		
		
		btnUpdate.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		Toast.makeText(ManageRoom.this, roomId + "\n" + etTitle.getText().toString() + "\n" + etCategory.getText().toString() + "\n" + etLocation.getText().toString(), Toast.LENGTH_LONG).show();
        		new RoomUpdate().execute();
        		//put data to be return to parent in an intent
				Intent output = new Intent();
				output.putExtra("title", etTitle.getText().toString());
				output.putExtra("category", etCategory.getText().toString());
				output.putExtra("location", etLocation.getText().toString());
				output.putExtra("delete", "False");
				// Set the results to be returned to parent
				setResult(RESULT_OK, output);
				// Ends the sub-activity
				finish();  	
        	}
        });
		
		btnDelete.setOnClickListener(new OnClickListener(){
        	public void onClick(View v)
        	{
        		
        		showDialog(1);
				  	
        	}
        });
		
		

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
	
class RoomUpdate extends AsyncTask<String, String, String> {
		
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
            String post_roomId = roomId;
            String post_title = etTitle.getText().toString();
            String post_category = etCategory.getText().toString();
            String post_location = etLocation.getText().toString();
            
            
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("title", post_title));
                params.add(new BasicNameValuePair("category", post_category));
                params.add(new BasicNameValuePair("location", post_location));
                params.add(new BasicNameValuePair("room_id", post_roomId));
 
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
            //pDialog.dismiss();
            if (file_url != null){
            	Toast.makeText(ManageRoom.this, file_url, Toast.LENGTH_LONG).show();
            }
 
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
        String post_roomId = roomId;
              
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
        String post_roomId = roomId;
              
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


 



}
