package sg.nyp.groupconnect.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.R.drawable;
import sg.nyp.groupconnect.R.id;
import sg.nyp.groupconnect.R.layout;
import sg.nyp.groupconnect.R.menu;
import sg.nyp.groupconnect.custom.NotificationCustomList;
import sg.nyp.groupconnect.utilities.JSONParser;

import android.app.ActionBar;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.preference.PreferenceManager;

public class NotificationDisplay extends Activity {

	ListView notificationList;
	String userId = "";
	
	ArrayList<Integer> imageId = new ArrayList<Integer>();
	
	// Progress Dialog
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	// testing from a real server:
	private static final String NOTIFICATION_RETRIEVE_URL =
	 "http://www.it3197Project.3eeweb.com/grpConnect/notificationRetrieve.php";
	
	// JSON IDS:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_POSTS = "posts";
	// An array of all of our comments
	private JSONArray mComments = null;
	// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mCommentList;
	
	public static Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_display);

		activity = NotificationDisplay.this;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NotificationDisplay.this);
		userId = sp.getString("id", "No ID");
		
		notificationList = (ListView) findViewById(R.id.notificationList);
		
		
		
		new LoadComments().execute();
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);
		
		notificationList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(NotificationDisplay.this, RoomDetails.class);
				
				String []content = messageArray.get(arg2).split("\n");
			    String titleWithBracket = content[1].replace("(", "");
			    String titleForRoom = titleWithBracket.replace(")", "");
				
				i.putExtra("title", titleForRoom);
				startActivity(i);
			}
		});
		
	}
	
	public void updateJSONdata() {

		mCommentList = new ArrayList<HashMap<String, String>>();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", userId));

		Log.d("request!", "starting");
		JSONObject json = jsonParser.makeHttpRequest(NOTIFICATION_RETRIEVE_URL, "POST",
				params);

		try {

			mComments = json.getJSONArray(TAG_POSTS);

			for (int i = 0; i < mComments.length(); i++) {
				JSONObject c = mComments.getJSONObject(i);

				String title = c.getString("title");
				String message = c.getString("message");

				HashMap<String, String> map = new HashMap<String, String>();

				map.put("title", title);
				map.put("message", message);

				mCommentList.add(map);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the parsed data into the listview.
	 */
	ArrayList<String> titleArray = new ArrayList<String>();
	ArrayList<String> messageArray = new ArrayList<String>();
	private void updateList() {
		
		
		
		if (mCommentList != null)
		{
			for (int i = 0; i<mCommentList.size(); i++)
			{
				
				titleArray.add(mCommentList.get(i).get("title"));
				messageArray.add(mCommentList.get(i).get("message"));
				imageId.add(R.drawable.ic_launcher);
			}
			
			
			
			
			NotificationCustomList adapter = new NotificationCustomList(NotificationDisplay.this, titleArray, messageArray, imageId);
			notificationList.setAdapter(adapter);
		}
	}

	public class LoadComments extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NotificationDisplay.this);
			pDialog.setMessage("Loading notification...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			updateJSONdata();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			updateList();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}



}
