package sg.nyp.groupconnect;

//import static sg.nyp.groupconnect.notification.Util.*;
//import static sg.nyp.groupconnect.notification.Util.TAG;

import java.util.ArrayList;

import sg.nyp.groupconnect.data.RoomDbAdapter;
import sg.nyp.groupconnect.entity.Model;
import sg.nyp.groupconnect.utilities.ListAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ViewRoom extends Activity {
	// Variables
	public static ListView memberList;
	public static TextView tvTitle, tvCategory, tvLocation;
	public static TextView tvStatus, tvDate, tvTime;
	public static TextView tvNoOfLearner;
	public static TextView tvNoOfEducator;
	public static Button btnJoin, btnVote, btnViewResult;
	ArrayList<String> memberArray = new ArrayList<String>();
	private Bundle extras;
	public static Activity activity;
	
	private int createdRoomId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room_details);
		// For usernameRetrieve.java
		activity = ViewRoom.this;

		extras = getIntent().getExtras();
		if (extras != null) {
			createdRoomId = extras.getInt("createdRoomId");
		}
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvCategory = (TextView) findViewById(R.id.tvCategory);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvTime = (TextView) findViewById(R.id.tvTime);

		btnJoin = (Button) findViewById(R.id.btnJoin);
		btnVote = (Button) findViewById(R.id.vote);
		btnViewResult = (Button) findViewById(R.id.viewResult);

		btnJoin.setVisibility(View.GONE);
		btnVote.setVisibility(View.GONE);
		btnViewResult.setVisibility(View.GONE);

		memberList = (ListView) findViewById(R.id.memberList);

		new retrieveRmMemDetail().execute();

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("RoomDetails", "RESUME");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case android.R.id.home:
			ViewRoom.this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class retrieveRmMemDetail extends AsyncTask<Void, Void, Boolean> {

		// retrieveRmMem doinbackground & postexecute
		// Progress Dialog
		private ProgressDialog pDialog;

		// private static final String KEY_ROOMID = "room_id";
		private static final String KEY_TITLE = "title";
		private static final String KEY_CATEGORY = "category";
		private static final String KEY_NOOFLEARNER = "noOfLearner";
		private static final String KEY_LOCATION = "location";
		// private static final String KEY_LATLNG = "latLng";
		// private static final String KEY_CREATORID = "creatorId";
		// private static final String KEY_DESCRIPTION = "description";
		private static final String KEY_STATUS = "status";
		private static final String KEY_DATEFROM = "dateFrom";
		private static final String KEY_DATETO = "dateTo";
		private static final String KEY_TIMEFROM = "timeFrom";
		private static final String KEY_TIMETO = "timeTo";

		private static final String TAG_MEMBERNAME = "name";

		String totalNoOfLearner;
		String status = "";
		String dateFrom = "";
		String dateTo = "";
		String timeFrom = "";
		String timeTo = "";
		String title = "";
		String location = "";
		String category = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			String room_id = Integer.toString(createdRoomId);

			RoomDbAdapter mDbHelper = new RoomDbAdapter(activity);
			mDbHelper.open();

			Cursor mCursor = mDbHelper.fetchRoomDetail(room_id);

			if (mCursor.getCount() != 0) {
				mCursor.moveToFirst();

				title = mCursor.getString(mCursor.getColumnIndex(KEY_TITLE));
				category = mCursor.getString(mCursor
						.getColumnIndex(KEY_CATEGORY));
				totalNoOfLearner = Integer.toString(mCursor.getInt(mCursor
						.getColumnIndex(KEY_NOOFLEARNER)));
				location = mCursor.getString(mCursor
						.getColumnIndex(KEY_LOCATION));
				status = mCursor.getString(mCursor.getColumnIndex(KEY_STATUS));
				dateFrom = mCursor.getString(mCursor
						.getColumnIndex(KEY_DATEFROM));
				dateTo = mCursor.getString(mCursor.getColumnIndex(KEY_DATETO));
				timeFrom = mCursor.getString(mCursor
						.getColumnIndex(KEY_TIMEFROM));
				timeTo = mCursor.getString(mCursor.getColumnIndex(KEY_TIMETO));
			}

			Cursor mCursor1 = mDbHelper.fetchMemberDetail(room_id);

			memberArray.clear();
			
			if (mCursor1.getCount() != 0) {

				mCursor1.moveToFirst();
				memberArray.add(mCursor1.getString(mCursor1
						.getColumnIndex(TAG_MEMBERNAME)));

				while (mCursor1.moveToNext()) {

					memberArray.add(mCursor1.getString(mCursor1
							.getColumnIndex(TAG_MEMBERNAME)));
				}
			}

			mCursor1.close();
			mCursor.close();
			mDbHelper.close();

			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			tvTitle.setText(title);
			tvLocation.setText(location);
			tvCategory.setText(category);
			tvStatus.setText(status);
			
			if (status.equalsIgnoreCase("Ended")) {
				tvStatus.setBackgroundResource(R.drawable.rectangle_red);
			} else if (status.equalsIgnoreCase("Ongoing")) {
				tvStatus.setBackgroundResource(R.drawable.rectangle_green);
			}
			
			tvDate.setText(dateFrom + " to " + dateTo);
			tvTime.setText(timeFrom + " to " + timeTo);
			
			ListAdapter adapter = new ListAdapter(activity,
					generateDataE(memberArray, totalNoOfLearner));
			memberList.setAdapter(null);
			memberList.setAdapter(adapter);

			pDialog.dismiss();
		}
	}

	private ArrayList<Model> generateDataE(ArrayList<String> array,
			String totalNoOfLearner) {

		ArrayList<Model> models = new ArrayList<Model>();
		models.add(new Model("Learner: " + memberArray.size()
				+ "/" + totalNoOfLearner));
		for (int i = 0; i < array.size(); i++) {
			models.add(new Model(R.drawable.user, array.get(i).toString(), null));
		}

		return models;
	}
}