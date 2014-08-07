package sg.nyp.groupconnect.room.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.custom.CustomList;
import sg.nyp.groupconnect.entity.Schools;
import sg.nyp.groupconnect.room.CreateRmStep3;
import sg.nyp.groupconnect.room.RoomDetails;
import sg.nyp.groupconnect.entity.Model;
import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.utilities.ListAdapter;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class usernameRetrieve extends AsyncTask<String, String, String> {

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
	

	//For usernameRetrieveE/L - AsyncTask
	private static final String USERNAME_RETRIEVE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/usernameRetrieve.php";
	ArrayList<String> listOfMemUUID = new ArrayList<String>();
	String uuidR;
	String memNameR = "";
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... args) {
		// TODO Auto-generated method stub
		// Check for success tag
		int success;
		Log.i("RoomDetails", "UsernameretrieveE, currentCount: " + retrieveRmMem.currentCount);
		String id = retrieveRmMem.tempListId.get(retrieveRmMem.currentCount); //Avoid running a incorrect id.
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
				return json.getString("username") + "/" + json.getString("device");
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
			
			String [] content = file_url.split("/");
			String memNameR1 = content[0];
			String device = content[1];
			
			Log.i("RoomDetails", "UsernameRetrieve currentType: " + retrieveRmMem.tempListType.get(retrieveRmMem.currentCount));
			Log.i("RoomDetails", file_url);
			if (retrieveRmMem.tempListType.get(retrieveRmMem.currentCount).equals("Educator"))
			{
				
				retrieveRmMem.tempListName.add(memNameR1);
				retrieveRmMem.educatorArray.add(retrieveRmMem.tempListName.get(retrieveRmMem.currentCount));
				Log.i("RoomDetails", "tempListName: " + retrieveRmMem.tempListName.get(retrieveRmMem.currentCount));
				Log.i("RoomDetails","EMemNameR From usernameRetrieve: " + memNameR1);
				
				
				
				//CustomList adapter = new CustomList(RoomDetails.this, educatorArray, imageId);
				
				//retrieveRmMem.educatorlist.setAdapter(null); //Since this will be run more than once, clear the listview everytime
												// before setting the new data to avoid repeated list
				//educatorlist.setAdapter(adapter);
				
				RoomDetails.listOfMemUUID.add(uuidR);
				
			}
			else if (retrieveRmMem.tempListType.get(retrieveRmMem.currentCount).equals("Learner"))
			{
				
				retrieveRmMem.tempListName.add(memNameR1);
				retrieveRmMem.memberArray.add(retrieveRmMem.tempListName.get(retrieveRmMem.currentCount));
				Log.i("RoomDetails", "tempListName: " + retrieveRmMem.tempListName.get(retrieveRmMem.currentCount));
				Log.i("RoomDetails","LMemNameR From usernameRetrieve: " + memNameR1);
				
				
				
				
				//CustomList adapter1 = new CustomList(RoomDetails.this, memberArray, imageId);
				
				//learnerlist.setAdapter(null);
				//learnerlist.setAdapter(adapter1);
	
				RoomDetails.listOfMemUUID.add(uuidR);
				//listOfMemUUID.add(uuidR);
				//Log.i("RoomDetails", "UserRetrieveL uuidR" + uuidR);
			}
			
			if (retrieveRmMem.currentCount < retrieveRmMem.tempListId.size()-1)
			{
				 //Storing of uuid for push notification later on
				Log.i("RoomDetails", "UserRetrieveL uuidR" + uuidR);
				retrieveRmMem.currentCount++; //Add the counter for the next time we run the method
				new usernameRetrieve().execute();
			}
			else
			{
				Log.i("RoomDetails", "usernameRetrieve END");
				for(int i = 0; i < retrieveRmMem.educatorArray.size(); i++)
				{
					Log.i("RoomDetails", "educatorArray: " + retrieveRmMem.educatorArray.get(i));
				}
				for(int i = 0; i < retrieveRmMem.memberArray.size(); i++)
				{
					Log.i("RoomDetails", "memberArray: " + retrieveRmMem.memberArray.get(i));
				}
				
				
				
				//CustomList adapter = new CustomList(RoomDetails.activity, retrieveRmMem.educatorArray, retrieveRmMem.imageId);
				//RoomDetails.educatorlist.setAdapter(null);
				//RoomDetails.educatorlist.setAdapter(adapter);
				//RoomDetails.tvNoOfEducator.setText(retrieveRmMem.educatorArray.size() + "/1");
				
				ListAdapter adapter3 = new ListAdapter(RoomDetails.activity, generateDataE(retrieveRmMem.educatorArray, retrieveRmMem.memberArray));
				RoomDetails.memberList.setAdapter(null);
				RoomDetails.memberList.setAdapter(adapter3);
				
				//CustomList adapter1 = new CustomList(RoomDetails.activity, retrieveRmMem.memberArray, retrieveRmMem.imageId);
				//RoomDetails.learnerlist.setAdapter(null);
				//RoomDetails.learnerlist.setAdapter(adapter1);
				//RoomDetails.tvNoOfLearner.setText(retrieveRmMem.memberArray.size() + "/" + retrieveRmMem.totalNoOfLearner);
				//ListAdapter adapter4 = new ListAdapter(RoomDetails.activity, generateDataL(retrieveRmMem.memberArray));
				//RoomDetails.learnerlist.setAdapter(null);
				//RoomDetails.learnerlist.setAdapter(adapter4);
				
				RoomDetails.pDialog.dismiss();
				
				retrieveRmMem.currentCount = 0;
				RoomDetails rmd = new RoomDetails();
				rmd.Setup();
				
				//rmd.voteMenu.setVisible(rmd.voteMenuB);
				//rmd.voteResultMenu.setVisible(rmd.voteResultMenuB);
				
			}
			
		}

	}
	
	private ArrayList<Model> generateDataE(ArrayList<String> array, ArrayList<String> array1){
		
        ArrayList<Model> models = new ArrayList<Model>();
        //For Educator
        models.add(new Model("Educator: " + retrieveRmMem.educatorArray.size() + "/1"));
        for (int i = 0; i< array.size(); i++)
        {
	        models.add(new Model(R.drawable.user, array.get(i).toString(), null));
        }
        //For learner
        models.add(new Model("Learner: " + retrieveRmMem.memberArray.size() + "/" + retrieveRmMem.totalNoOfLearner));
        for (int i = 0; i< array1.size(); i++)
        {
	        models.add(new Model(R.drawable.user,array1.get(i).toString(), null));
        }
 
        return models;
    }


}
