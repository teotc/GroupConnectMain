package sg.nyp.groupconnect.item;

import java.util.ArrayList;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.GrpRoomDbAdapter;
import sg.nyp.groupconnect.data.GrpRoomListAdapter;
import sg.nyp.groupconnect.data.RoomDbAdapter;
import sg.nyp.groupconnect.entity.GrpRoomList;
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
	String orgName="";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_organisation, container,
				false);
		Log.d(TAG, "onCreateView 47");
		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		creatorId = sp.getString("id", "0");
		orgName = sp.getString("username", "DadCompany");
		Log.d(TAG, "Loading OrgName: "+orgName);
		
		frag_org_lv_groups = (ListView) rootView
				.findViewById(R.id.frag_org_lv_groups);
		frag_org_tv_orgname = (TextView) rootView
				.findViewById(R.id.frag_org_tv_orgname);
		
		//frag_org_tv_orgname.setText(orgName);

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
		
//		if(mDbHelper.fetchAllByCreator(creatorId)!= null){
//		//if (details != null) {
//			Log.d(TAG, "Loading OrgRoomsLV");
		new LoadOrgRooms().execute();
//		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(dataDoneRecv);
	}

	private RoomDbAdapter mDbHelper;

	private ArrayList<GrpRoomList> details;
	private String title = null, location = null, category = null, latlng = null;
	private long noOfLearner = 0, room_id = 0;

	class LoadOrgRooms extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			
			mDbHelper = new RoomDbAdapter(getActivity());
			mDbHelper.open();

			details = new ArrayList<GrpRoomList>();
			Cursor mCursor = mDbHelper.fetchAllByCreator(creatorId);
			if (mCursor.getCount() != 0) {
				// mRMCursor.moveToFirst();
				Log.d("GrpRmPullService",
						"filldata(): count: " + mCursor.getCount());
				while (mCursor.moveToNext()) {

					room_id = GrpRoomDbAdapter.getLong(mCursor,
							RoomDbAdapter.KEY_ROOMID);
					title = GrpRoomDbAdapter.getString(mCursor,
							RoomDbAdapter.KEY_TITLE);
					category = GrpRoomDbAdapter.getString(mCursor,
							RoomDbAdapter.KEY_CATEGORY);
					noOfLearner = GrpRoomDbAdapter.getLong(mCursor,
							RoomDbAdapter.KEY_NOOFLEARNER);
					location = GrpRoomDbAdapter.getString(mCursor,
							RoomDbAdapter.KEY_LOCATION);

					latlng = GrpRoomDbAdapter.getString(mCursor,
							RoomDbAdapter.KEY_LATLNG);

					details.add(new GrpRoomList(room_id, title, category,
							noOfLearner, location, latlng));
				}
			} else {
				Log.d(TAG, "No results in cursor");
				Log.d(TAG, "Count: " + mCursor.getCount());
			}

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