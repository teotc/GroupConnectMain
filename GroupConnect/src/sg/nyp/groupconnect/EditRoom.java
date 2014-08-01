package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditRoom extends Activity {

	EditText etTitle, etCategory;
	Button btnSubmit, btnCancel;

	String location = "180 Ang Mo Kio Avenue 8 Singapore 569830",
			lat = "1.379961", lng = "103.848772";
	
	String roomName;
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// For Retrieving created room_id
	// testing from a real server:
	int createdRoomId = 0;
	String post_memberId = "1001";
	String post_memberType = "Educator";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_edit);

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

		etTitle = (EditText) findViewById(R.id.etTitle);
		etCategory = (EditText) findViewById(R.id.etCategory);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// String message;

				boolean success = true;

				// Check for any empty EditText
				if (etTitle.getText().toString().length() <= 0) {
					etTitle.setError("Enter Title");
					success = false;
				}

				if (etCategory.getText().toString().length() <= 0) {
					etCategory.setError("Enter Category");
					success = false;
				}

				if (success == true) // If all fields are filled
				{
					new createRoom().execute();
				}

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();

			}
		});
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
			pDialog = new ProgressDialog(EditRoom.this);
			pDialog.setMessage("Creating Room...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String post_title = etTitle.getText().toString();
			String post_category = etCategory.getText().toString();

			roomName = post_title + " - " + post_category;
			
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("title", post_title));
				params.add(new BasicNameValuePair("category", post_category));
				params.add(new BasicNameValuePair("noOfLearner", "0"));
				params.add(new BasicNameValuePair("location", location));
				params.add(new BasicNameValuePair("latLng", lat + "," + lng));
				params.add(new BasicNameValuePair("creatorId", post_memberId));

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(CREATE_ROOM_URL,
						"POST", params);

				// json success element
				success = json.getInt(TAG_SUCCESS);

				JSONObject c = json.getJSONArray(TAG_ARRAY).getJSONObject(0);
				createdRoomId = c.getInt("room_id");

				if (success == 1) {
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
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String post_roomId = Integer.toString(createdRoomId);

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// params.add(new BasicNameValuePair("username",
				// post_username));
				params.add(new BasicNameValuePair("room_id", post_roomId));
				params.add(new BasicNameValuePair("memberId", post_memberId));
				params.add(new BasicNameValuePair("memberType", post_memberType));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						CREATE_ROOM_MEM_URL, "POST", params);

				// json success element
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
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
			Toast.makeText(EditRoom.this, "Room created successfully",
					Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			EditRoom.this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
