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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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

public class CreateRmStep2 extends Activity {
	
	//Get data from CreateRm
	String title, category, categoryType, desc, maxLearner, categoryMethod;
    
    //Dialog Method
    AlertDialog dialog;
    private static final int FIELDEMPTY_ALERT = 1;
    
    //For Date & Time Picker
    public Button btnDateFrom;
	Button btnDateTo;
	Button btnTimeFrom;
	Button btnTimeTo;

    static final int DATEFROM_DIALOG_ID = 2;
    static final int DATETO_DIALOG_ID = 3;
    static final int TIMEFROM_DIALOG_ID= 4;
    static final int TIMETO_DIALOG_ID= 5;

    // variables to save user selected date and time
    public  int year,month,day,hour,minute;  
    // declare  the variables to Show/Set the date and time when Time and  Date Picker Dialog first appears
    private int mYear, mMonth, mDay,mHour,mMinute; 
    
    
    
    public CreateRmStep2()
    {
    	// Assign current Date and Time Values to Variables
    	final Calendar c = Calendar.getInstance();
    	mYear = c.get(Calendar.YEAR);
    	mMonth = c.get(Calendar.MONTH);
    	mDay = c.get(Calendar.DAY_OF_MONTH);
    	mHour = c.get(Calendar.HOUR_OF_DAY);
    	mMinute = c.get(Calendar.MINUTE);
    }
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rm_step2);
	    
        btnDateFrom =(Button)findViewById(R.id.btnDateFrom);
        btnDateTo =(Button)findViewById(R.id.btnDateTo);
        btnTimeFrom =(Button)findViewById(R.id.btnTimeFrom);
        btnTimeTo =(Button)findViewById(R.id.btnTimeTo);
        
        
        // Get the extras (if there are any)
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            
        	title = this.getIntent().getStringExtra("title");
        	category = this.getIntent().getStringExtra("category");
        	categoryType = this.getIntent().getStringExtra("categoryType");
        	desc = this.getIntent().getStringExtra("desc");
        	maxLearner = this.getIntent().getStringExtra("maxLearner");
        	categoryMethod = this.getIntent().getStringExtra("categoryMethod");
            
        }
        
        //Set the suggested date and time according to current date and time
        btnDateFrom.setText(mDay + "/" + mMonth + "/" + mYear);
        btnDateTo.setText((mDay+1) + "/" + (mMonth) + "/" + (mYear));
        btnTimeFrom.setText(convertTime(mHour, mMinute));
        btnTimeTo.setText(convertTime(mHour+1, mMinute));
       
        btnDateFrom.setOnClickListener(new View.OnClickListener() {
           
            public void onClick(View v) {
                // Show the DatePickerDialog
                 showDialog(DATEFROM_DIALOG_ID);
            }
        });
        
        btnDateTo.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                // Show the DatePickerDialog
                 showDialog(DATETO_DIALOG_ID);
            }
        });
       
        btnTimeFrom.setOnClickListener(new View.OnClickListener() {
           
            public void onClick(View v) {
                // Show the TimePickerDialog
                 showDialog(TIMEFROM_DIALOG_ID);
            }
        });
        
        btnTimeTo.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                // Show the TimePickerDialog
                 showDialog(TIMETO_DIALOG_ID);
            }
        });
	    
		 
		
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {

    }
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_rm_step2, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.next) {
			
    		boolean success = true;
            
            if (success == true) //If all fields are filled
            {
            	
            	Intent myIntent = new Intent(CreateRmStep2.this,CreateRmStep3.class);
            	myIntent.putExtra("title", title);
            	myIntent.putExtra("category", category);
            	myIntent.putExtra("categoryType", categoryType);
            	myIntent.putExtra("desc", desc);
            	myIntent.putExtra("maxLearner", maxLearner);
            	myIntent.putExtra("dateFrom", btnDateFrom.getText().toString());
            	myIntent.putExtra("dateTo", btnDateTo.getText().toString());
            	myIntent.putExtra("timeFrom", btnTimeFrom.getText().toString());
            	myIntent.putExtra("timeTo", btnTimeTo.getText().toString());
            	myIntent.putExtra("categoryMethod", categoryMethod);
            	startActivity(myIntent);
            	//finish();
            	
				
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
	            
          case DATEFROM_DIALOG_ID:
              // create a new DatePickerDialog with values you want to show
             return new DatePickerDialog(this,
                         mDateSetListenerFrom,
                         mYear, mMonth, mDay);
             
          case DATETO_DIALOG_ID:
              // create a new DatePickerDialog with values you want to show
             return new DatePickerDialog(this,
                         mDateSetListenerTo,
                         mYear, mMonth, mDay+1);
             // create a new TimePickerDialog with values you want to show
          case TIMEFROM_DIALOG_ID:
             return new TimePickerDialog(this,
                     mTimeSetListenerFrom, mHour, mMinute, false);
             
          case TIMETO_DIALOG_ID:
              return new TimePickerDialog(this,
                      mTimeSetListenerTo, mHour+1, mMinute, false);

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
    
    
    // Register  DatePickerDialog listener
    private DatePickerDialog.OnDateSetListener mDateSetListenerFrom =
    		new DatePickerDialog.OnDateSetListener() {
    	// the callback received when the user "sets" the Date in the DatePickerDialog
    	public void onDateSet(DatePicker view, int yearSelected,
    			int monthOfYear, int dayOfMonth) {
    		
    		year = yearSelected;
    		month = monthOfYear;
    		day = dayOfMonth;
    		// Set the Selected Date in Select date Button
    		btnDateFrom.setText(day +"/" +month + "/" +year);
    	}
    };
    private DatePickerDialog.OnDateSetListener mDateSetListenerTo =
    		new DatePickerDialog.OnDateSetListener() {
    	// the callback received when the user "sets" the Date in the DatePickerDialog
    	public void onDateSet(DatePicker view, int yearSelected,
    			int monthOfYear, int dayOfMonth) {
    		
    		year = yearSelected;
    		month = monthOfYear;
    		day = dayOfMonth;
    		// Set the Selected Date in Select date Button
    		btnDateTo.setText(day +"/" +month + "/" +year);
    	}
    };

    // Register  TimePickerDialog listener                
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerFrom =
    		new TimePickerDialog.OnTimeSetListener() {
    	// the callback received when the user "sets" the TimePickerDialog in the dialog
    	public void onTimeSet(TimePicker view, int hourOfDay, int min) {
    		hour = hourOfDay;
    		minute = min;
    		
    		String result = convertTime(hour, minute);
    		
    		// Set the Selected Date in Select date Button
    		btnTimeFrom.setText(result);
    	}



    };
    private TimePickerDialog.OnTimeSetListener mTimeSetListenerTo =
    		new TimePickerDialog.OnTimeSetListener() {
    	// the callback received when the user "sets" the TimePickerDialog in the dialog
    	public void onTimeSet(TimePicker view, int hourOfDay, int min) {
    		hour = hourOfDay;
    		minute = min;
    		
    		String result = convertTime(hour, minute);
    		
    		// Set the Selected Date in Select date Button
    		btnTimeTo.setText(result);
    	}


    };
    
    public String convertTime(int hour, int min)
    {
    	String am_pm = "";
    	String result = "";

	    Calendar datetime = Calendar.getInstance();
	    datetime.set(Calendar.HOUR_OF_DAY, hour);
	    datetime.set(Calendar.MINUTE, min);

	    if (datetime.get(Calendar.AM_PM) == Calendar.AM)
	        am_pm = "AM";
	    else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
	        am_pm = "PM";

	    String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+""; 

	    String minS = String.valueOf(datetime.get(Calendar.MINUTE));
	    if (minS.length() == 1)
	    	minS = "0" + minS;
	    
	    result = strHrsToShow+":"+minS+" "+am_pm;
	    
	    return result;
    }
    
 
}
