package sg.nyp.groupconnect.item;

import java.util.ArrayList;
import java.util.Collections;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.*;
import sg.nyp.groupconnect.entity.DistanceSorter;
import sg.nyp.groupconnect.entity.GrpRoomListExt;
import sg.nyp.groupconnect.room.NearbyRooms;
import sg.nyp.groupconnect.room.RoomDetails;
import sg.nyp.groupconnect.room.RoomsRetrieve;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.maps.model.LatLng;

@SuppressLint("NewApi")
public class fragment_home extends Fragment {

	SharedPreferences sp;
	String homeLocTextAddr = null;
	int DISTPREF = 5000; // metres
	int DISTPREF_UNIT = 0; // m

	private static final String TAG = "GrpConnFragHome";

	ListView eduSuggestLV;
	Button frag_bnnearbyrooms;

	BroadcastReceiver dataDone;
	
	private Intent intent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		eduSuggestLV = (ListView) rootView.findViewById(R.id.eduSuggestLV);
		frag_bnnearbyrooms = (Button) rootView
				.findViewById(R.id.frag_bnnearbyrooms);

		TextView homeLoc = (TextView) rootView
				.findViewById(R.id.fgh_tvHomeLocation);

		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		homeLocTextAddr = sp.getString("home", "Error in retrieving location");
		Log.i("Geocode", "Home:" + homeLocTextAddr);
		homeLoc.setText(homeLocTextAddr);

		mDbHelper = new GrpRoomDbAdapter(getActivity());
		mDbHelper.open();

		frag_bnnearbyrooms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), NearbyRooms.class);
				startActivity(i);
			}
		});
		eduSuggestLV.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				intent = new Intent(getActivity(), RoomDetails.class);
				intent.putExtra("title", details.get(position).getTitle());
				intent.putExtra("category", details.get(position).getCategory());
				intent.putExtra("location", details.get(position).getLocation());
				startActivity(intent);
				// details.get(position).getTitle()
				// String s = (String) ((TextView) v.findViewById(R.id.lmTitle))
				// .getText();
				// Toast.makeText(Search_Group.this, s,
				// Toast.LENGTH_LONG).show();

				// intent = new Intent(Search_Group.this, RoomInfo.class);
				// intent.putExtra(KEY_ROOM_ID,
				// details.get(position).getRoom_id());
				// intent.putExtra(KEY_NO_OF_LEARNER,
				// details.get(position).getNoOfLearner());
				// intent.putExtra(KEY_LATLNG,
				// details.get(position).getLatlng());
				// intent.putExtra(KEY_USERNAME,
				// details.get(position).getUsername());
				//
				// startActivity(intent);
			}
		});
		return rootView;
	}

	@Override
	public void onPause() {
		super.onPause();
		// getActivity().unregisterReceiver(dataDone);
	}

	@Override
	public void onResume() {
		super.onResume();

		dataDone = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "Intent Received");
				Log.d(TAG, "Executing CompareRoomDistance");
				new CompareRoomDistance().execute();
			}
		};
		IntentFilter intf = new IntentFilter("sg.nyp.groupconnect.DATADONE");
		getActivity().registerReceiver(dataDone, intf);

		if (details != null) {
			Log.d(TAG, "Loading LV");
			new CompareRoomDistance().execute();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new CompareRoomDistance().execute();
	}

	public void updateLV() {
		eduSuggestLV.setAdapter(new GrpRoomListExtAdapter(details,
				getActivity()));
	}

	private GrpRoomDbAdapter mDbHelper;

	// fillData() variables
	String title = null, location = null, category = null;
	long noOfLearner = 0, room_id = 0;
	double distance, lat, lng;
	int icon;

	private ArrayList<GrpRoomListExt> details;

	class CompareRoomDistance extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {

			details = new ArrayList<GrpRoomListExt>();
			Cursor mCursor = mDbHelper.fetchRoomsWDistance(DISTPREF);
			if (mCursor.getCount() != 0) {
				// mRMCursor.moveToFirst();
				Log.d("GrpRmPullService",
						"filldata(): count: " + mCursor.getCount());
				while (mCursor.moveToNext()) {

					room_id = GrpRoomDbAdapter.getLong(mCursor,
							GrpRoomDbAdapter.KEY_ROOM_ID);
					title = GrpRoomDbAdapter.getString(mCursor,
							GrpRoomDbAdapter.KEY_TITLE);
					category = GrpRoomDbAdapter.getString(mCursor,
							GrpRoomDbAdapter.KEY_CATEGORY);
					noOfLearner = GrpRoomDbAdapter.getLong(mCursor,
							GrpRoomDbAdapter.KEY_NO_OF_LEARNER);
					location = GrpRoomDbAdapter.getString(mCursor,
							GrpRoomDbAdapter.KEY_LOCATION);
					distance = GrpRoomDbAdapter.getLong(mCursor,
							GrpRoomDbAdapter.KEY_DISTANCE);

					lat = GrpRoomDbAdapter.getLong(mCursor,
							GrpRoomDbAdapter.KEY_LAT);
					lng = GrpRoomDbAdapter.getLong(mCursor,
							GrpRoomDbAdapter.KEY_LNG);
					icon = GrpRoomDbAdapter.getInt(mCursor,
							GrpRoomDbAdapter.KEY_ICON);

					details.add(new GrpRoomListExt(room_id, title, category,
							noOfLearner, location, null, new LatLng(lat, lng),
							distance, icon));
					Collections.sort(details, new DistanceSorter());
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
			updateLV();
			Log.d(TAG + " Geocode", "Done");
		}
	}
}