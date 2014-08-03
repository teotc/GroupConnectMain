package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.utilities.VotingfPieChartBuilder;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VoteLocation extends Activity {

	private Button btnVote, btnViewResult;
	private Intent intent = null;

	// Required info
	private String currentMemberId = "1007";
	private String currentRoomId = "159";
	private String[] roomMemberId = new String[] { "1001", "1002", "1003",
			"1004", "1005", "1006", "1007" };
	private String roomLocation = "none";
	private String creatorMemberId = "1007";
	private int stat = 0;
	private int num = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vote_location);

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

		btnVote = (Button) findViewById(R.id.vote);
		btnViewResult = (Button) findViewById(R.id.viewResult);

		Setup();

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Assume: \nCurrentMemberId - " + currentMemberId
				+ "\nCurrentRoomId - " + currentRoomId + "\nRoomLocation - "
				+ roomLocation + "\nCreatorMemberId - " + creatorMemberId);
	}

	private void Setup() {
		int check = 0;
		for (int i = 0; i < roomMemberId.length; i++) {
			if (roomMemberId[i].equals(currentMemberId)) {
				check = 1;
			}
		}
		if (check == 1 && roomLocation.equals("none")) {
			new RetrieveMemberVote().execute();

		} else {
			btnVote.setVisibility(View.GONE);
			btnViewResult.setVisibility(View.GONE);
		}

		if (currentMemberId == creatorMemberId)
			stat = 1;
	}

	public void Vote(View v) {
		intent = new Intent(VoteLocation.this, VoteMap.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
		intent.putExtra("CURRENT_MEMBER_ID", currentMemberId);
		startActivityForResult(intent, 0);
	}

	public void VoteResult(View v) {
		intent = new Intent(VoteLocation.this, VotingfPieChartBuilder.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
		intent.putExtra("CREATOR_STATUS", stat);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			btnVote.setVisibility(View.GONE);
		}
	}

	public class RetrieveMemberVote extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		// Database
		private ProgressDialog pDialog;

		JSONParser jsonParser = new JSONParser();

		private static final String RETRIEVE_VOTE_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveVote.php";

		private static final String TAG_SUCCESS = "success";
		private static final String TAG_MESSAGE = "message";
		private static final String TAG_ARRAY = "posts";

		private static final String TAG_NUM = "num";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.context);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("memberId", currentMemberId));
				params.add(new BasicNameValuePair("roomId", currentRoomId));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(RETRIEVE_VOTE_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);

					num = c.getInt(TAG_NUM);
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

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if (num != 0) {
				btnVote.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			VoteLocation.this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}