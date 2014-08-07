package sg.nyp.groupconnect.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.R.drawable;
import sg.nyp.groupconnect.R.id;
import sg.nyp.groupconnect.R.layout;
import sg.nyp.groupconnect.R.menu;
import sg.nyp.groupconnect.custom.NotificationCustomList;
import sg.nyp.groupconnect.utilities.JSONParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.preference.PreferenceManager;

public class NotificationDisplay extends Activity {

	ListView notificationList;
	String userId = "";
	
	Integer[] imageId = {
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher,
			R.drawable.ic_launcher
			
	};
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_display);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NotificationDisplay.this);
		userId = sp.getString("id", "No ID");
		
		notificationList = (ListView) findViewById(R.id.notificationList);
		
		new LoadComments().execute();
		
	}
	
	public void updateJSONdata() {

		// Instantiate the arraylist to contain all the JSON data.
		// we are going to use a bunch of key-value pairs, referring
		// to the json element name, and the content, for example,
		// message it the tag, and "I'm awesome" as the content..

		mCommentList = new ArrayList<HashMap<String, String>>();

		// Bro, it's time to power up the J parser
		//JSONParser jParser = new JSONParser();
		// Feed the beast our comments url, and it spits us
		// back a JSON object. Boo-yeah Jerome.
		//JSONObject json = jParser.getJSONFromUrl(NOTIFICATION_RETRIEVE_URL);
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", userId));

		Log.d("request!", "starting");
		// getting product details by making HTTP request
		JSONObject json = jsonParser.makeHttpRequest(NOTIFICATION_RETRIEVE_URL, "POST",
				params);

		// when parsing JSON stuff, we should probably
		// try to catch any exceptions:
		try {

			// I know I said we would check if "Posts were Avail." (success==1)
			// before we tried to read the individual posts, but I lied...
			// mComments will tell us how many "posts" or comments are
			// available
			mComments = json.getJSONArray(TAG_POSTS);

			// looping through all posts according to the json object returned
			for (int i = 0; i < mComments.length(); i++) {
				JSONObject c = mComments.getJSONObject(i);

				// gets the content of each tag
				String title = c.getString("title");
				String message = c.getString("message");

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				map.put("title", title);
				map.put("message", message);

				// adding HashList to ArrayList
				mCommentList.add(map);

				// annndddd, our JSON data is up to date same with our array
				// list
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



}
