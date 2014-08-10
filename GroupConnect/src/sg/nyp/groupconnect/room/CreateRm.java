package sg.nyp.groupconnect.room;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.custom.CustomCategoryList;
import sg.nyp.groupconnect.custom.CustomList;
import sg.nyp.groupconnect.custom.PopupAdapter;
import sg.nyp.groupconnect.room.db.retrieveRmMem;
import sg.nyp.groupconnect.utilities.ExpandableListAdapter;
import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.*;


import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CreateRm extends Activity {
	//Variable
	EditText etTitle, etDesc;
	TextView tvCategoryChosen, tvCategoryTypeChosen;
	Button btnCategoryDialog;
	Spinner spMaxLearner;
	ArrayList<String> listOfNo = new ArrayList<String>();
	ArrayAdapter listOfNoAdapter;
	ListView lvCategory;

	//For Retrieving all the subjects for the existing expandablelistview
	private static final String RETRIEVE_ALL_SUB_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveSubjectsWithTypeAll.php";
	private JSONArray mCategory = null;
	private ArrayList<HashMap<String, String>> mCategoryList;
	private static final String TAG_POSTS = "posts";
	private static final String TAG_NAME = "name";
	private static final String TAG_TYPENAME = "typeName";
	String name, typeName;
	ArrayList<String> schSubList = new ArrayList<String>();
	ArrayList<String> musicList = new ArrayList<String>();
	ArrayList<String> computerList = new ArrayList<String>();
	ArrayList<String> otherList = new ArrayList<String>();
	
	//For ShowCustomDialog
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    RadioGroup rg_existNew;
    RadioButton rBtnExistNew;
    TextView tvCategory;
    Spinner spCategoryType;
    EditText etCategoryDialog;
    ArrayAdapter categoryTypeAdapter;
    ArrayList<String> categoryTypeList = new ArrayList<String>();
    Button btnConfirm;
    String categoryMethod;
    
    //Dialog Method
    AlertDialog dialog;
    private static final int FIELDEMPTY_ALERT = 1;
    
    //For CategoryListView
    ArrayList<String> categoryNameChosen = new ArrayList<String> ();
    ArrayList<String> categoryTypeChosen = new ArrayList<String> ();
    
    Integer[] imageId = {
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher
			
	};
    
    String location = ""; //From RoomMap By Searching
    
    //For Update
    private ProgressDialog pDialog;
    boolean update = false;
    String updateRoomId = "";
    ArrayList<String> learnerInGrpList = new ArrayList<String>();
  	private static final String RETRIEVE_ROOM_WITH_ID_URL = "http://www.it3197Project.3eeweb.com/grpConnect/roomRetrieveWithId.php";
  	private JSONArray mRoom = null;
  	private ArrayList<HashMap<String, String>> mRoomList;
	private static final String TAG_ROOMID = "room_id";
	private static final String TAG_TITLE = "title";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_NOOFLEARNER = "noOfLearner";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LATLNG = "latLng";
	private static final String TAG_STATUS = "status";
	private static final String TAG_DATEFROM = "dateFrom";
	private static final String TAG_DATETO = "dateTo";
	private static final String TAG_TIMEFROM = "timeFrom";
	private static final String TAG_TIMETO = "timeTo";
	private static final String TAG_DESC = "description";
	String roomIdR, titleR, locationR, categoryR, noOfLearnerR, latLngR, usernameR, typeNameR, statusR, dateFromR, dateToR, timeFromR, timeToR, descR;
	double latR, lngR;
    LatLng retrievedLatLng; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rm);
		
		etTitle = (EditText) findViewById(R.id.etTitle);
		btnCategoryDialog = (Button) findViewById(R.id.btnCategoryDialog);
		etDesc = (EditText) findViewById(R.id.etDesc);
	    spMaxLearner = (Spinner) findViewById(R.id.spMaxLearner);
	    tvCategoryChosen = (TextView) findViewById(R.id.tvCategoryChosen);
	    tvCategoryTypeChosen = (TextView) findViewById(R.id.tvCategoryTypeChosen);
	    
	    Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            
        	location = this.getIntent().getStringExtra("location");
            
        	update = this.getIntent().getBooleanExtra("update", false);
        	if (update == true)
        	{
        		updateRoomId = this.getIntent().getStringExtra("roomId");
        		learnerInGrpList = this.getIntent().getStringArrayListExtra("memList");
        	}
        	
        }
	    
	    categoryNameChosen.add("None");
	    categoryTypeChosen.add("None");
	    
	    btnCategoryDialog.setOnClickListener(new OnClickListener(){
	    	public void onClick(View v)
	    	{
	    		
	            showCustomDialog(tvCategoryChosen, tvCategoryTypeChosen);
	    	
	    	}
	    });
         
        
        prepareListData();
        
        if(update == true)
        {
        	new RetrieveRoomById().execute();
        	ActionBar actionBar = getActionBar();
			actionBar.setTitle("Update Room Step 1/3");
        }
        else
        {
        	ActionBar actionBar = getActionBar();
			actionBar.setTitle("Create Room Step 1/3");
        }
		 
		
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {

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
		if (id == R.id.next) {
			
			//String message;
    		boolean success = true;
    		
    		//Check for any empty EditText
    		if (etTitle.getText().toString().length() <= 0)
    		{
            	etTitle.setError("Enter Title");
    			success = false;
    		}
    		
            if (tvCategoryChosen.getText().toString().equals("None"))
            {
            	
            	success = false;
            }
            
            if (etDesc.getText().toString().length() <= 0)
            {
            	etDesc.setText("None");
            }
            
            if (success == true) //If all fields are filled
            {
            	
            	Intent myIntent = new Intent(CreateRm.this,CreateRmStep2.class);
            	myIntent.putExtra("title", etTitle.getText().toString());
            	myIntent.putExtra("category", tvCategoryChosen.getText().toString());
            	myIntent.putExtra("categoryType", tvCategoryTypeChosen.getText().toString());
            	myIntent.putExtra("desc", etDesc.getText().toString());
            	myIntent.putExtra("maxLearner", spMaxLearner.getSelectedItem().toString());
            	myIntent.putExtra("categoryMethod", categoryMethod);
            	myIntent.putExtra("update", update);
            	if (update == true)
            	{
            		myIntent.putExtra("roomId", roomIdR);
            		myIntent.putExtra("dateFrom", dateFromR);
            		myIntent.putExtra("dateTo", dateToR);
            		myIntent.putExtra("timeFrom", timeFromR);
            		myIntent.putExtra("timeTo", timeToR);
            		myIntent.putExtra("location", locationR);
            		myIntent.putExtra("lat", String.valueOf(latR));
            		myIntent.putExtra("lng", String.valueOf(lngR));
            		myIntent.putStringArrayListExtra("memList", learnerInGrpList);
            		Log.i("UpdateTest", String.valueOf(latR) + String.valueOf(lngR));
            	}
            	else
            	{
            		myIntent.putExtra("location", location);
            	}
            	startActivity(myIntent);
            	
				
            }
			return true;
		}
		else if (id == R.id.back)
		{
			//put data to be return to parent in an intent
			Intent output = new Intent();
			output.putExtra("Cancel", "Canceled");
			// Set the results to be returned to parent
			setResult(RESULT_CANCELED, output);
			
			// Ends the sub-activity
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	protected void showCustomDialog(final TextView tvCategoryChosenM, final TextView tvCategoryTypeChosenM) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(CreateRm.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_category_dialog);
        
        //Exist
        tvCategory = (TextView) dialog.findViewById(R.id.tvCategory);
        expListView = (ExpandableListView) dialog.findViewById(R.id.lvExpExist);
        //New
        etCategoryDialog = (EditText) dialog.findViewById(R.id.etCategoryDialog);
        spCategoryType = (Spinner) dialog.findViewById(R.id.spCategoryType);
        btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);

        categoryTypeAdapter = new ArrayAdapter<String>(dialog.getContext(), android.R.layout.simple_spinner_dropdown_item, categoryTypeList);
        spCategoryType.setAdapter(categoryTypeAdapter);
        
        rg_existNew = (RadioGroup) dialog.findViewById(R.id.rg_existNew);   
        rg_existNew.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				// get selected radio button from radioGroup
				int selectedId = arg1;
				// find the radiobutton by returned id
				rBtnExistNew = (RadioButton) dialog.findViewById(selectedId);
				String rbtnSelected = rBtnExistNew.getText().toString();
				
				if (rbtnSelected.equals("Exist"))
				{
					tvCategory.setText("Choose one existing category below");
					expListView.setVisibility(View.VISIBLE);
					etCategoryDialog.setVisibility(View.GONE);
					spCategoryType.setVisibility(View.GONE);
					btnConfirm.setVisibility(View.GONE);
				}
				else if (rbtnSelected.equals("New"))
				{
					tvCategory.setText("Type a new category name and choose the type");
					expListView.setVisibility(View.GONE);
					etCategoryDialog.setVisibility(View.VISIBLE);
					spCategoryType.setVisibility(View.VISIBLE);
					btnConfirm.setVisibility(View.VISIBLE);
				}
				
				
				
			}});
     
        
        
        // preparing list data
        listAdapter = new ExpandableListAdapter(dialog.getContext(), listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
        expListView.setOnChildClickListener(new OnChildClickListener() {
        	 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                
            	tvCategoryChosenM.setText(listDataChild.get
            			(listDataHeader.get(groupPosition)).get(childPosition));
            	
            	tvCategoryTypeChosenM.setText(listDataHeader.get(groupPosition));
            	
            	categoryMethod = "Exist";
            	
            	dialog.dismiss();
            	Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                        listDataHeader.get(groupPosition)).get(
                                        childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                tvCategoryChosenM.setText(etCategoryDialog.getText().toString());
                String selectedType = categoryTypeList.get((int)spCategoryType.getSelectedItemId());
                
                tvCategoryTypeChosenM.setText(selectedType);
                
                categoryMethod = "New";
                dialog.dismiss();
            }
        });
                
        dialog.show();
    }
	
	
	/*
     * Preparing the list data
     */
    private void prepareListData() {
    	listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("School Subjects");
        listDataHeader.add("Music");
        listDataHeader.add("Computer-related");
        listDataHeader.add("Others");
        
        //Adding categoryType for "New" Spinner
        categoryTypeList.add("School Subjects");
        categoryTypeList.add("Music");
        categoryTypeList.add("Computer-related");
        categoryTypeList.add("Others");
        
        //Set up the list of No. to choose max learner
        listOfNoAdapter = ArrayAdapter.createFromResource(this, R.array.listOfNo, android.R.layout.simple_list_item_1);
        spMaxLearner.setAdapter(listOfNoAdapter);
        
        new retrieveAllSubWithType().execute();
        
    }
    
    protected Dialog onCreateDialog(int id) {
    	Log.i("sg.nyp.groupconnect", "onCreateDialog");
    	
        switch (id) {
          case FIELDEMPTY_ALERT:
        	  Log.i("sg.nyp.groupconnect", "onCreateDialog - CREATERM_ALERT");
        	  
            	Builder builder = new AlertDialog.Builder(this);
	            builder.setMessage("Some or All Fields are empty. Please enter required fields.");
	            builder.setCancelable(true);
	            builder.setPositiveButton("Okay", new OkOnClickListener());
	            dialog = builder.create();
	            dialog.show();

        }
        
        return super.onCreateDialog(id);
      }
    
    private final class OkOnClickListener implements DialogInterface.OnClickListener 
    {
    	public void onClick(DialogInterface dialog, int which) 
    	{
    		dialog.dismiss();
    	}
    } 
	 
 
	/* 
	 * Retrieves recent post data from the server.
	 */
	public void updateJSONdata() {
		Log.i("sg.nyp.groupconnect", "updateJSONdata");

		mCategoryList = new ArrayList<HashMap<String, String>>();

		JSONParser jParser = new JSONParser();
		JSONObject json = jParser.getJSONFromUrl(RETRIEVE_ALL_SUB_URL);
		
		try {

			mCategory = json.getJSONArray(TAG_POSTS);

			for (int i = 0; i < mCategory.length(); i++) {
				JSONObject c = mCategory.getJSONObject(i);

				name = c.getString(TAG_NAME);
				typeName = c.getString(TAG_TYPENAME);

				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put(TAG_NAME, name);
				map.put(TAG_TYPENAME, typeName);

				mCategoryList.add(map); 

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	private void updateMap() 
	{
		Log.i("sg.nyp.groupconnect", "updateMap");
		if (mCategoryList != null)
		{
			for (int i = 0; i<mCategoryList.size(); i++)
			{

				name = mCategoryList.get(i).get(TAG_NAME);
				typeName = mCategoryList.get(i).get(TAG_TYPENAME);
				
				if (typeName.equals("School Subjects"))
				{
					schSubList.add(name);
				}
				else if (typeName.equals("Music"))
				{
					musicList.add(name);
				}
				else if (typeName.equals("Computer-related"))
				{
					computerList.add(name);
				}
				else if (typeName.equals("Others"))
				{
					otherList.add(name);
				}
				
				listDataChild.put(listDataHeader.get(0), schSubList); // Header, Child data
		        listDataChild.put(listDataHeader.get(1), musicList);
		        listDataChild.put(listDataHeader.get(2), computerList);
		        listDataChild.put(listDataHeader.get(3), otherList);
				
			}
			
			
		}
		
	}
    
	public class retrieveAllSubWithType extends AsyncTask<Void, Void, Boolean> {

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

	
	//For Updates
	public class RetrieveRoomById extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateRm.this);
			pDialog.setMessage("Loading Map...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			Log.i("sg.nyp.groupconnect", "LoadRoom - Preexecute");
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			mRoomList = new ArrayList<HashMap<String, String>>();
			String post_roomId = updateRoomId;

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("room_id", post_roomId));

			Log.d("request!", "starting");
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(
					RETRIEVE_ROOM_WITH_ID_URL, "POST", params);

			try {

				mRoom = json.getJSONArray(TAG_POSTS);

				for (int i = 0; i < mRoom.length(); i++) {
					JSONObject c = mRoom.getJSONObject(i);

					roomIdR = c.getString(TAG_ROOMID);
					titleR = c.getString(TAG_TITLE);
					locationR = c.getString(TAG_LOCATION);
					noOfLearnerR = c.getString(TAG_NOOFLEARNER);
					categoryR = c.getString(TAG_CATEGORY);
					latLngR = c.getString(TAG_LATLNG);
					statusR = c.getString(TAG_STATUS);
					dateFromR = c.getString(TAG_DATEFROM);
					dateToR = c.getString(TAG_DATETO);
					timeFromR = c.getString(TAG_TIMEFROM);
					timeToR = c.getString(TAG_TIMETO);
					typeNameR = c.getString(TAG_TYPENAME);
					descR = c.getString(TAG_DESC);
			
					HashMap<String, String> map = new HashMap<String, String>();
					
					map.put(TAG_ROOMID, roomIdR);
					map.put(TAG_TITLE, titleR);
					map.put(TAG_LOCATION, locationR);
					map.put(TAG_NOOFLEARNER, noOfLearnerR);
					map.put(TAG_LATLNG, latLngR);
					map.put(TAG_CATEGORY, categoryR);
					map.put(TAG_STATUS, statusR);
					map.put(TAG_DATEFROM, dateFromR);
					map.put(TAG_DATETO, dateToR);
					map.put(TAG_TIMEFROM, timeFromR);
					map.put(TAG_TIMETO, timeToR);
					map.put(TAG_TYPENAME, typeNameR);
					map.put(TAG_DESC, descR);

					mRoomList.add(map); 

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.i("sg.nyp.groupconnect", "LoadRoom - onPostExecute");
			super.onPostExecute(result);
			pDialog.dismiss();
			if (mRoomList != null)
			{
				for (int i = 0; i<mRoomList.size(); i++)
				{
					roomIdR = mRoomList.get(i).get(TAG_ROOMID);
					titleR = mRoomList.get(i).get(TAG_TITLE);
					etTitle.setText(titleR);
					
					locationR = mRoomList.get(i).get(TAG_LOCATION);
					
					noOfLearnerR = mRoomList.get(i).get(TAG_NOOFLEARNER);
					spMaxLearner.setSelection(Integer.parseInt(noOfLearnerR)-1);
					
					
					categoryR = mRoomList.get(i).get(TAG_CATEGORY);
					typeNameR = mRoomList.get(i).get(TAG_TYPENAME);
					tvCategoryChosen.setText(categoryR);
					tvCategoryTypeChosen.setText(typeNameR);
					
					dateFromR = mRoomList.get(i).get(TAG_DATEFROM);
					dateToR = mRoomList.get(i).get(TAG_DATETO);
					timeFromR = mRoomList.get(i).get(TAG_TIMEFROM);
					timeToR = mRoomList.get(i).get(TAG_TIMETO);
					
					descR = mRoomList.get(i).get(TAG_DESC);
					etDesc.setText(descR);
					
					String latLng = mRoomList.get(i).get(TAG_LATLNG);
					if (!latLng.equals("") && !latLng.equals("undefined") && !latLng.equals(null))
					{
						//Spliting the lat Lng 
						String[] parts = latLng.split(",");
						latR = Double.parseDouble(parts[0]); 
						lngR = Double.parseDouble(parts[1]);

					}
				}
			}
		}
	}
 
}
