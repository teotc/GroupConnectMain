package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.data.RoomDbAdapter;
import sg.nyp.groupconnect.data.RoomMembersDbAdapter;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditRoom2 extends Activity {

	// Get data from CreateRm
	String title, category, categoryType, desc, maxLearner, categoryMethod;

	// Dialog Method
	AlertDialog dialog;
	private static final int FIELDEMPTY_ALERT = 1;

	// For Date & Time Picker
	public Button btnDateFrom;
	Button btnDateTo;
	Button btnTimeFrom;
	Button btnTimeTo;

	static final int DATEFROM_DIALOG_ID = 2;
	static final int DATETO_DIALOG_ID = 3;
	static final int TIMEFROM_DIALOG_ID = 4;
	static final int TIMETO_DIALOG_ID = 5;

	// variables to save user selected date and time
	public int year, month, day, hour, minute;
	// declare the variables to Show/Set the date and time when Time and Date
	// Picker Dialog first appears
	private int mYear, mMonth, mDay, mHour, mMinute;

	private int createdRoomId = 0;
	private String roomName = "";

	private ProgressDialog pDialog;

	public EditRoom2() {
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

		setTitle("Create Room Step 2");

		btnDateFrom = (Button) findViewById(R.id.btnDateFrom);
		btnDateTo = (Button) findViewById(R.id.btnDateTo);
		btnTimeFrom = (Button) findViewById(R.id.btnTimeFrom);
		btnTimeTo = (Button) findViewById(R.id.btnTimeTo);

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

		// Set the suggested date and time according to current date and time
		btnDateFrom.setText(mDay + "/" + mMonth + "/" + mYear);
		btnDateTo.setText((mDay + 1) + "/" + (mMonth) + "/" + (mYear));
		btnTimeFrom.setText(convertTime(mHour, mMinute));
		btnTimeTo.setText(convertTime(mHour + 1, mMinute));

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_rm_step3, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.submit) {

			boolean success = true;

			if (success == true) // If all fields are filled
			{
				new createRoom().execute();

			}
			return true;
		} else if (id == R.id.back) {
			setResult(RESULT_CANCELED);
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
			return new DatePickerDialog(this, mDateSetListenerFrom, mYear,
					mMonth, mDay);

		case DATETO_DIALOG_ID:
			// create a new DatePickerDialog with values you want to show
			return new DatePickerDialog(this, mDateSetListenerTo, mYear,
					mMonth, mDay + 1);
			// create a new TimePickerDialog with values you want to show
		case TIMEFROM_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListenerFrom, mHour,
					mMinute, false);

		case TIMETO_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListenerTo, mHour + 1,
					mMinute, false);

		}

		return super.onCreateDialog(id);
	}

	private final class OkOnClickListener implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}

	// Register DatePickerDialog listener
	private DatePickerDialog.OnDateSetListener mDateSetListenerFrom = new DatePickerDialog.OnDateSetListener() {
		// the callback received when the user "sets" the Date in the
		// DatePickerDialog
		public void onDateSet(DatePicker view, int yearSelected,
				int monthOfYear, int dayOfMonth) {

			year = yearSelected;
			month = monthOfYear;
			day = dayOfMonth;
			// Set the Selected Date in Select date Button
			btnDateFrom.setText(day + "/" + month + "/" + year);
		}
	};
	private DatePickerDialog.OnDateSetListener mDateSetListenerTo = new DatePickerDialog.OnDateSetListener() {
		// the callback received when the user "sets" the Date in the
		// DatePickerDialog
		public void onDateSet(DatePicker view, int yearSelected,
				int monthOfYear, int dayOfMonth) {

			year = yearSelected;
			month = monthOfYear;
			day = dayOfMonth;
			// Set the Selected Date in Select date Button
			btnDateTo.setText(day + "/" + month + "/" + year);
		}
	};

	// Register TimePickerDialog listener
	private TimePickerDialog.OnTimeSetListener mTimeSetListenerFrom = new TimePickerDialog.OnTimeSetListener() {
		// the callback received when the user "sets" the TimePickerDialog in
		// the dialog
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			hour = hourOfDay;
			minute = min;

			String result = convertTime(hour, minute);

			// Set the Selected Date in Select date Button
			btnTimeFrom.setText(result);
		}

	};
	private TimePickerDialog.OnTimeSetListener mTimeSetListenerTo = new TimePickerDialog.OnTimeSetListener() {
		// the callback received when the user "sets" the TimePickerDialog in
		// the dialog
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			hour = hourOfDay;
			minute = min;

			String result = convertTime(hour, minute);

			// Set the Selected Date in Select date Button
			btnTimeTo.setText(result);
		}

	};

	public String convertTime(int hour, int min) {
		String am_pm = "";
		String result = "";

		Calendar datetime = Calendar.getInstance();
		datetime.set(Calendar.HOUR_OF_DAY, hour);
		datetime.set(Calendar.MINUTE, min);

		if (datetime.get(Calendar.AM_PM) == Calendar.AM)
			am_pm = "AM";
		else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
			am_pm = "PM";

		String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12"
				: datetime.get(Calendar.HOUR) + "";

		String minS = String.valueOf(datetime.get(Calendar.MINUTE));
		if (minS.length() == 1)
			minS = "0" + minS;

		result = strHrsToShow + ":" + minS + " " + am_pm;

		return result;
	}

	class createRoom extends AsyncTask<String, String, String> {
		String CREATE_ROOM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/createRoom.php";

		String TAG_SUCCESS = "success";
		String TAG_MESSAGE = "message";
		String TAG_ARRAY = "posts";

		// JSON parser class
		JSONParser jsonParser = new JSONParser();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(EditRoom2.this);
			pDialog.setMessage("Creating Room...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			int success;

			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(EditRoom2.this);

			roomName = title + " - " + category;

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("title", title));
				params.add(new BasicNameValuePair("category", category));
				params.add(new BasicNameValuePair("noOfLearner", maxLearner));
				params.add(new BasicNameValuePair("location", sp.getString(
						"home", null)));
				params.add(new BasicNameValuePair("latLng", sp.getString(
						"homeLat", null) + "," + sp.getString("homeLng", null)));
				params.add(new BasicNameValuePair("creatorId", sp.getString(
						"id", null)));
				params.add(new BasicNameValuePair("description", desc));
				params.add(new BasicNameValuePair("dateFrom", btnDateFrom
						.getText().toString()));
				params.add(new BasicNameValuePair("dateTo", btnDateTo.getText()
						.toString()));
				params.add(new BasicNameValuePair("timeFrom", btnTimeFrom
						.getText().toString()));
				params.add(new BasicNameValuePair("timeTo", btnTimeTo.getText()
						.toString()));
				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(CREATE_ROOM_URL,
						"POST", params);

				Log.e("SQL", json.toString());
				
				// json success element
				success = json.getInt(TAG_SUCCESS);

				JSONObject c = json.getJSONArray(TAG_ARRAY).getJSONObject(0);
				createdRoomId = c.getInt("room_id");

				if (success == 1) {
					RoomDbAdapter mDbHelper = new RoomDbAdapter(EditRoom2.this);
					mDbHelper.open();

					mDbHelper.createRoom(createdRoomId, title, category,
							Integer.parseInt(maxLearner), sp.getString("home",
									null), sp.getString("homeLat", null) + ","
									+ sp.getString("homeLng", null), Integer
									.parseInt(sp.getString("id", null)), desc,
							"Not Started", btnDateFrom.getText().toString(),
							btnDateTo.getText().toString(), btnTimeFrom
									.getText().toString(), btnTimeTo.getText()
									.toString());

					mDbHelper.close();

					new createRoomMember().execute();
					return json.getString(TAG_MESSAGE);
				} else {
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

	class createRoomMember extends AsyncTask<String, String, String> {
		String CREATE_ROOM_MEM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/createRoomMember.php";

		String TAG_SUCCESS = "success";
		String TAG_MESSAGE = "message";

		// JSON parser class
		JSONParser jsonParser = new JSONParser();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag
			int success;
			String post_roomId = Integer.toString(createdRoomId);

			SharedPreferences sp = PreferenceManager
					.getDefaultSharedPreferences(EditRoom2.this);
			int memberId = Integer
					.parseInt(sp.getString("id", null));
			
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("username",
				// post_username));
				params.add(new BasicNameValuePair("room_id", post_roomId));
				params.add(new BasicNameValuePair("memberId", Integer.toString(memberId)));
				params.add(new BasicNameValuePair("memberType", "Educator"));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						CREATE_ROOM_MEM_URL, "POST", params);

				// json success element
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					
					RoomMembersDbAdapter mDbHelper = new RoomMembersDbAdapter(EditRoom2.this);
					mDbHelper.open();

					mDbHelper.createRoomMembers(Integer.parseInt(post_roomId), memberId, "Educator");

					mDbHelper.close();
					
					return json.getString(TAG_MESSAGE);
				} else {
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();

			Intent i = new Intent();
			i.putExtra("RoomId", createdRoomId);
			i.putExtra("RoomName", roomName);
			setResult(RESULT_OK, i);
			Toast.makeText(EditRoom2.this, "Room created successfully",
					Toast.LENGTH_SHORT).show();
			finish();
		}

	}

}
