package sg.nyp.groupconnect.item;

import java.util.ArrayList;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.*;
import sg.nyp.groupconnect.learner.GrpRoomListExt;
import sg.nyp.groupconnect.room.NearbyRooms;
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

		// DISTPREF = sp.getInt("DISTPREF", 5000); // DISTPREF_UNIT
		// DISTPREF_UNIT = sp.getInt("DISTPREF", 0);

		// bnSetDist.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // show();
		// }
		// });
		// new CompareRoomDistance().execute();

		return rootView;
	}

	@Override
	public void onPause() {
		super.onPause();
		//getActivity().unregisterReceiver(dataDone);
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
		
		if(details != null){
			updateLV();
		}
		
		//new CompareRoomDistance().execute();
	}

	Spinner spUnit;

	public void show() {

		final Dialog d = new Dialog(getActivity());
		d.setTitle("Choose a boundary");
		d.setContentView(R.layout.dialog_distpicker);
		Button dist_set = (Button) d.findViewById(R.id.diag_dist_set);
		Button dist_cancel = (Button) d.findViewById(R.id.diag_dist_cancel);
		spUnit = (Spinner) d.findViewById(R.id.diag_dist_spUnit);

		// String[] unitArray = new String[] { "M", "KM" };

		// Selection of the spinner
		spUnit = (Spinner) d.findViewById(R.id.diag_dist_spUnit);

		// Application of the Array to the Spinner
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				d.getContext(), android.R.layout.simple_spinner_item,
				getResources().getStringArray(R.array.spDistUnits));
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spUnit.setAdapter(spinnerArrayAdapter);

		// String[] unitArray = getActivity().getResources().getStringArray(
		// R.array.spDistUnits);
		// ArrayList<String> units = new ArrayList<String>(
		// Arrays.asList(unitArray));
		//
		// spUnit = new Spinner(getActivity());
		// ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
		// getActivity(), android.R.layout.simple_spinner_item,
		// units);
		// spinnerArrayAdapter
		// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spUnit.setAdapter(spinnerArrayAdapter);

		// spUnit.setSelection(DISTPREF_UNIT);

		// Set up Numberpicker
		final NumberPicker np = (NumberPicker) d
				.findViewById(R.id.diag_dist_npDist);
		np.setMaxValue(9999);
		np.setMinValue(1); // min value 1
		np.setWrapSelectorWheel(false);
		// np.setValue(DISTPREF);
		// np.setOnValueChangedListener(this);

		dist_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// tv.setText(String.valueOf(np.getValue())); //set the value to
				// textview

				// SharedPreferences sp = PreferenceManager
				// .getDefaultSharedPreferences(getActivity());
				// Editor edit = sp.edit();
				// edit.putInt("DISTPREF", np.getValue());
				// edit.putInt("DISTPREF_UNIT",
				// spUnit.getSelectedItemPosition());
				// edit.commit();
				d.dismiss();
			}
		});
		dist_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				d.dismiss(); // dismiss the dialog
			}
		});
		d.show();

	}

	public void updateLV() {
		eduSuggestLV.setAdapter(null);
		eduSuggestLV.setAdapter(new GrpRoomListExtAdapter(details,
				getActivity()));
	}

	private GrpRoomDbAdapter mDbHelper;

	// fillData() variables
	String title = null, location = null, category = null;
	long noOfLearner = 0, room_id = 0;
	double distance, lat, lng;

	private ArrayList<GrpRoomListExt> details;
	private ArrayList<GrpRoomListExt> roomList2;

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

					details.add(new GrpRoomListExt(room_id, title, category,
							noOfLearner, location, null, new LatLng(lat, lng),
							distance));
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
			eduSuggestLV.setAdapter(new GrpRoomListExtAdapter(details,
					getActivity()));
			Log.d(TAG + " Geocode", "Done");
		}
	}
}