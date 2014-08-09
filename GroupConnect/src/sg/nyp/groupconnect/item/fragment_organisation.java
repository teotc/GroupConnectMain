package sg.nyp.groupconnect.item;

import java.util.ArrayList;

import sg.nyp.groupconnect.MainActivity;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.ViewRoom;
import sg.nyp.groupconnect.data.GrpRoomListAdapter;
import sg.nyp.groupconnect.data.RoomDbAdapter;
import sg.nyp.groupconnect.entity.Room;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class fragment_organisation extends Fragment {

	private SharedPreferences sp;
	private String creatorId;
	private static final String TAG = "fragment_org";

	ListView frag_org_lv_groups;
	TextView frag_org_tv_orgname;
	private BroadcastReceiver dataDoneRecv;
	String orgName = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_organisation,
				container, false);
		Log.d(TAG, "onCreateView 47");
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		creatorId = sp.getString("id", "0");
		orgName = sp.getString("username", "DadCompany");
		Log.d(TAG, "Loading OrgName: " + orgName);

		frag_org_lv_groups = (ListView) rootView
				.findViewById(R.id.frag_org_lv_groups);
		frag_org_tv_orgname = (TextView) rootView
				.findViewById(R.id.frag_org_tv_orgname);

		frag_org_tv_orgname.setText(orgName);

		frag_org_lv_groups.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(MainActivity.ct, ViewRoom.class);
				intent.putExtra("createdRoomId", details.get(position)
						.getRoom_id());
				startActivity(intent);
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		dataDoneRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "Intent Received");
				Log.d(TAG, "Executing LoadOrgRooms");
				new LoadOrgRooms().execute();
			}
		};
		IntentFilter intf = new IntentFilter("sg.nyp.groupconnect.RMDBDATADONE");
		getActivity().registerReceiver(dataDoneRecv, intf);

		new LoadOrgRooms().execute();
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(dataDoneRecv);
	}

	private RoomDbAdapter mDbHelper;

	private ArrayList<Room> details;
	private String title = null, location = null, category = null,
			status = null;
	private int room_id = 0;
	private String KEY_ROOMID = "room_id";
	private String KEY_TITLE = "title";
	private String KEY_CATEGORY = "category";
	private String KEY_LOCATION = "location";
	private String KEY_STATUS = "status";

	class LoadOrgRooms extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			mDbHelper = new RoomDbAdapter(getActivity());
			mDbHelper.open();

			details = new ArrayList<Room>();
			details.clear();
			Cursor mCursor = mDbHelper.fetchAllByCreator(creatorId);
			if (mCursor.getCount() != 0) {
				// mRMCursor.moveToFirst();
				Log.d("GrpRmPullService",
						"filldata(): count: " + mCursor.getCount());
				while (mCursor.moveToNext()) {

					room_id = mCursor
							.getInt(mCursor.getColumnIndex(KEY_ROOMID));
					title = mCursor
							.getString(mCursor.getColumnIndex(KEY_TITLE));
					category = mCursor.getString(mCursor
							.getColumnIndex(KEY_CATEGORY));
					location = mCursor.getString(mCursor
							.getColumnIndex(KEY_LOCATION));
					status = mCursor.getString(mCursor
							.getColumnIndex(KEY_STATUS));

					details.add(new Room(room_id, title, category, location,
							status));
				}
			} else {
				Log.d(TAG, "No results in cursor");
				Log.d(TAG, "Count: " + mCursor.getCount());
			}

			mCursor.close();
			mDbHelper.close();

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			frag_org_lv_groups.setAdapter(new GrpRoomListAdapter(details,
					getActivity()));
			Log.d(TAG, "LoadOrgRooms Done");
		}
	}
}