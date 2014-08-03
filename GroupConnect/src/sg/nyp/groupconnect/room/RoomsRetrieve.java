package sg.nyp.groupconnect.room;

import java.util.ArrayList;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.*;
import sg.nyp.groupconnect.learner.GrpRoomListing;
import sg.nyp.groupconnect.room.*;
import android.app.*;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class RoomsRetrieve extends Activity {

	// Database access
	public static final String KEY_ROOM_ID = "room_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_NO_OF_LEARNER = "noOfLearner";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_LATLNG = "latlng";
	public static final String KEY_USERNAME = "username";
	private GrpRoomDbAdapter mDbHelper;

	// UI
	private ListView roomList;
	private ArrayList<GrpRoomListing> details;
	private AdapterView.AdapterContextMenuInfo info;

	// Others
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_group);

		mDbHelper = new GrpRoomDbAdapter(this);
		mDbHelper.open();

		roomList = (ListView) findViewById(R.id.GroupList);
		fillData();

		registerForContextMenu(roomList);

		roomList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// details.get(position).getTitle()
				// String s = (String) ((TextView) v.findViewById(R.id.lmTitle))
				// .getText();
				// Toast.makeText(Search_Group.this, s,
				// Toast.LENGTH_LONG).show();

				// intent = new Intent(Search_Group.this, RoomInfo.class);
				//
				// intent.putExtra(KEY_ROOM_ID,
				// details.get(position).getRoom_id());
				// intent.putExtra(KEY_TITLE, details.get(position).getTitle());
				// intent.putExtra(KEY_CATEGORY,
				// details.get(position).getCategory());
				// intent.putExtra(KEY_NO_OF_LEARNER,
				// details.get(position).getNoOfLearner());
				// intent.putExtra(KEY_LOCATION,
				// details.get(position).getLocation());
				// intent.putExtra(KEY_LATLNG,
				// details.get(position).getLatlng());
				// intent.putExtra(KEY_USERNAME,
				// details.get(position).getUsername());
				//
				// startActivity(intent);
			}
		});

		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			RoomsRetrieve.this.finish();
			return true;
		case R.id.srgrp_createroom:
			intent = new Intent(RoomsRetrieve.this, CreateRm.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		info = (AdapterContextMenuInfo) menuInfo;
		System.out.println("Reached");

		int id = (int) roomList.getAdapter().getItemId(info.position);
		System.out.println("id " + roomList.getAdapter().getItem(id));

		System.out.println("Msg" + details.get(info.position).getTitle());
		System.out.println("Msg" + details.get(info.position).getRoom_id());

		menu.setHeaderTitle(details.get(info.position).getTitle());
		menu.add(Menu.NONE, v.getId(), 0, "Reply");
		menu.add(Menu.NONE, v.getId(), 0, "Reply All");
		menu.add(Menu.NONE, v.getId(), 0, "Forward");

		System.out.println("ID " + info.id);
		System.out.println("Pos " + info.position);
		System.out.println("Info " + info.toString());
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Reply") {
			System.out.println("Id " + info.id);
			System.out.println("Pos " + info.position);
			System.out.println("info " + info.toString());
		} else if (item.getTitle() == "Reply All") {
			System.out.println("Id " + info.id);
			System.out.println("Pos " + info.position);
			System.out.println("info " + info.toString());
		} else if (item.getTitle() == "Reply All") {
			System.out.println("Id " + info.id);
			System.out.println("Pos " + info.position);
			System.out.println("info " + info.toString());
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.search_group, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillData();
	}

	private void fillData() {

		GrpRoomListing grpRmList;
		details = new ArrayList<GrpRoomListing>();
		Cursor mRMCursor = mDbHelper.fetchAllRooms();
		if (mRMCursor.getCount() != 0) {
			// mRMCursor.moveToFirst();
			Log.d("GrpRmPullService",
					"filldata(): count: " + mRMCursor.getCount());
			while (mRMCursor.moveToNext()) {
				// Log.d("GrpRmPullService",
				// "filldata(): title: "
				// + mRMCursor.getString(mRMCursor
				// .getColumnIndex(GrpRoomDbAdapter.KEY_TITLE)));
				grpRmList = new GrpRoomListing(
						mRMCursor.getLong(mRMCursor
								.getColumnIndex(GrpRoomDbAdapter.KEY_ROOM_ID)),
						mRMCursor.getString(mRMCursor
								.getColumnIndex(GrpRoomDbAdapter.KEY_TITLE)),
						mRMCursor.getString(mRMCursor
								.getColumnIndex(GrpRoomDbAdapter.KEY_CATEGORY)),
						mRMCursor.getLong(mRMCursor
								.getColumnIndex(GrpRoomDbAdapter.KEY_NO_OF_LEARNER)),
						mRMCursor.getString(mRMCursor
								.getColumnIndex(GrpRoomDbAdapter.KEY_LOCATION)),
						mRMCursor.getString(mRMCursor
								.getColumnIndex(GrpRoomDbAdapter.KEY_LATLNG)));
				details.add(grpRmList);
			}
			roomList.setAdapter(new GrpRoomListAdapter(details,
					RoomsRetrieve.this));
		} else {

		}
	}
}
