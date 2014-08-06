package sg.nyp.groupconnect;

//import static sg.nyp.groupconnect.notification.Util.*;
//import static sg.nyp.groupconnect.notification.Util.TAG;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.entity.Member;
import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.utilities.PieChartBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ViewRoom extends Activity {

	ListView learnerlist;
	TextView tvTitle, tvCategory;
	Button btnDone;
	public static ArrayList<Member> arrayMember = new ArrayList<Member>();

	private String title = "", category = "";
	private ArrayList<String> member = new ArrayList<String>();

	public ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_view);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvCategory = (TextView) findViewById(R.id.tvCategory);

		btnDone = (Button) findViewById(R.id.btnOkay);

		btnDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		new retrieveRoomDetail().execute();
	}

	class retrieveRoomDetail extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database

		JSONParser jsonParser = new JSONParser();

		private static final String ROOM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveRoom.php"; //TODO

		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MESSAGE = "message";
		private static final String TAG_ARRAY = "posts";

		private static final String TAG_TITLE = "title";
		private static final String TAG_CATEGORY = "category";
		// private static final String TAG_MEMBERID = "memberId";
		private static final String TAG_MEMBERNAME = "name";
		private static final String TAG_MEMBERTYPE = "memberType";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(ViewRoom.this);
			pDialog.setMessage("Retrieving Data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag
			int success;
			String room_id = Integer.toString(PieChartBuilder.createdRoomId);
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("room_id", room_id));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(ROOM_URL, "POST",
						params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				JSONObject c = json.getJSONArray(TAG_ARRAY).getJSONObject(0);
				title = c.getString(TAG_TITLE);
				category = c.getString(TAG_CATEGORY);

				int size = json.getJSONArray(TAG_ARRAY).length();

				member.clear();

				for (int j = 0; j < size; j++) {
					JSONObject c1 = json.getJSONArray(TAG_ARRAY).getJSONObject(
							j);

					if (c1.getString(TAG_MEMBERTYPE).equals("Learner")) {
						member.add(c1.getString(TAG_MEMBERNAME));
					}
				}

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
			tvTitle.setText(title);
			tvCategory.setText(category);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					ViewRoom.this, android.R.layout.simple_list_item_1,
					android.R.id.text1, member);

			learnerlist = (ListView) findViewById(R.id.learnerList);
			learnerlist.setAdapter(adapter);
			pDialog.dismiss();
		}
	}
}
