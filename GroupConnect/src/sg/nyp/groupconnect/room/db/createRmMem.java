package sg.nyp.groupconnect.room.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.entity.Schools;
import sg.nyp.groupconnect.room.CreateRmStep3;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class createRmMem extends AsyncTask<String, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	boolean failure = false;
	public int success;
	private float colour;

	// Database
	public static ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String CREATE_ROOM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/rmMemCreate.php"; 
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";
	
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_LATITUDE = "latitude";
	private static final String TAG_LONGITUDE = "longitude";
	

	ArrayList<String> memIdList = CreateRmStep3.chosenMemberIdList;
	
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
        
    }
	
	@Override
	protected String doInBackground(String... args) {
		// TODO Auto-generated method stub
		 // Check for success tag
        int success;
        String post_roomId = CreateRmStep3.createdRoomId;
        String post_memberId = memIdList.get(CreateRmStep3.count);
        String type = "";
        if (CreateRmStep3.mem_id == memIdList.get(CreateRmStep3.count))
        	type = CreateRmStep3.mem_type;
        else
        	type = "Learner";
        	
        Log.i("createRmMem", String.valueOf(CreateRmStep3.count));
        Log.i("createRmMem", post_memberId + type);
        try {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("username", post_username));
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
       if (CreateRmStep3.count < memIdList.size()-1)
       {
    	   Log.i("createRmMem", String.valueOf(memIdList.size()));
    	   Log.i("createRmMem", "Repeat");
    	   CreateRmStep3.count = CreateRmStep3.count + 1;
    	   new createRmMem().execute();
       }
       else
       {
    	   CreateRmStep3.count = 0;
    	   Log.i("createRmMem", "Stop");
       }
       
    }
}
