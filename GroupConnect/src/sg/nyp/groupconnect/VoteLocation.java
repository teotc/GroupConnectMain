package sg.nyp.groupconnect;

import sg.nyp.groupconnect.data.VoteLocationDbAdapter;
import sg.nyp.groupconnect.utilities.VotingfPieChartBuilder;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VoteLocation extends Activity {

	private Button btnVote, btnViewResult;
	private Intent intent = null;

	// Required info
	private String currentMemberId;
	private String currentRoomId = "159";
	private String[] roomMemberId = new String[] { "1001", "1002", "1003",
			"1004", "1005", "1006", "1007", "1117", "2095" };
	private String roomLocation = "none";
	private String creatorMemberId = "1117";
	private int stat = 0;
	private int num = 0;
	private ProgressDialog pDialog;
	private int check = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vote_location);

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

		btnVote = (Button) findViewById(R.id.vote);
		btnViewResult = (Button) findViewById(R.id.viewResult);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(VoteLocation.this);
		currentMemberId = sp.getString("id", null);

		Setup();

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Assume: \nCurrentMemberId - " + currentMemberId
				+ "\nCurrentRoomId - " + currentRoomId + "\nRoomLocation - "
				+ roomLocation + "\nCreatorMemberId - " + creatorMemberId
				+ "\nSTAT " + stat);
	}

	private void Setup() {
		check = 0;
		for (int i = 0; i < roomMemberId.length; i++) {
			if (roomMemberId[i].equals(currentMemberId)) {
				check = 1;
			}
		}
		if (check == 1 && roomLocation.equals("none")) {

		} else {
			btnVote.setVisibility(View.GONE);
			btnViewResult.setVisibility(View.GONE);
		}

		new RetrieveRoomVote().execute();

		if (currentMemberId.equals(creatorMemberId))
			stat = 1;
	}

	public void Vote(View v) {
		intent = new Intent(VoteLocation.this, VoteMap.class);
		intent.putExtra("CURRENT_ROOM_ID", currentRoomId);
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
			btnViewResult.setVisibility(View.VISIBLE);
		}
	}

	public class RetrieveRoomVote extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

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

			VoteLocationDbAdapter mDbHelper = new VoteLocationDbAdapter(
					VoteLocation.this);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchAllRoomVote(currentRoomId);

			num = mCursor.getCount();

			mCursor.close();
			mDbHelper.close();

			return null;

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			if (num == 0) {
				btnViewResult.setVisibility(View.GONE);

			} else {
				btnViewResult.setVisibility(View.VISIBLE);
				if (check == 1 && roomLocation.equals("none")) {
					new RetrieveMemberVote().execute();
				}
			}
		}
	}

	public class RetrieveMemberVote extends AsyncTask<String, String, String> {
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... args) {
			VoteLocationDbAdapter mDbHelper = new VoteLocationDbAdapter(
					VoteLocation.this);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchMemberRoomVote(currentMemberId,
					currentRoomId);

			num = mCursor.getCount();

			mCursor.close();
			mDbHelper.close();

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