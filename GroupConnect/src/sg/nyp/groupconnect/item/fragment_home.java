package sg.nyp.groupconnect.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sg.nyp.groupconnect.Login;
import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.GrpRoomDbAdapter;
import sg.nyp.groupconnect.data.GrpRoomListExtAdapter;
import sg.nyp.groupconnect.learner.GrpRoomListExt;
import sg.nyp.groupconnect.learner.GrpRoomListing;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

@SuppressLint("NewApi")
public class fragment_home extends Fragment {

	SharedPreferences sp;
	String homeLocation = null;
	int DISTPREF = 0; // metres
	int DISTPREF_UNIT = 0; // m

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
		// Get ListView object from xml
		ListView listView = (ListView) rootView.findViewById(R.id.listView1);

		ListView eduSuggestLV = (ListView) rootView
				.findViewById(R.id.eduSuggestLV);
		TextView homeLoc = (TextView) rootView
				.findViewById(R.id.fgh_tvHomeLocation);
		ImageButton bnSetDist = (ImageButton) rootView
				.findViewById(R.id.fgh_bnSetDist);

		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		homeLocation = sp.getString("homeLocation", null);
		homeLoc.setText(homeLocation);

		DISTPREF = sp.getInt("DISTPREF", 5000); // DISTPREF_UNIT
		DISTPREF_UNIT = sp.getInt("DISTPREF", 0);

		bnSetDist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				show();
			}
		});

		suggestEducatorRoom();

		// Defined Array values to show in ListView
		String[] values = new String[] { "Joined Group 1", "Joined Group 2",
				"Joined Group 3", "Joined Group 4", "Joined Group 5",
				"Joined Group 6", "Joined Group 7", "Joined Group 8",
				"Joined Group 9" };

		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				rootView.getContext(), android.R.layout.simple_list_item_1,
				android.R.id.text1, values);

		eduSuggestLV.setAdapter(new GrpRoomListExtAdapter(roomLocationList,
				getActivity()));

		// Assign adapter to ListView
		listView.setAdapter(adapter);
		// eduSuggestLV.setAdapter(adap2);
		return rootView;
	}

	Spinner spUnit;

	public void show() {

		final Dialog d = new Dialog(getActivity());
		d.setTitle("Choose a boundary");
		d.setContentView(R.layout.dialog_distpicker);
		Button dist_set = (Button) d.findViewById(R.id.diag_dist_set);
		Button dist_cancel = (Button) d.findViewById(R.id.diag_dist_cancel);
		//spUnit = (Spinner) d.findViewById(R.id.diag_dist_spUnit);

		String[] unitArray = new String[] { "M", "KM" };

		// Selection of the spinner
		spUnit = (Spinner) d.findViewById(R.id.diag_dist_spUnit);

		// Application of the Array to the Spinner
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				d.getContext(), android.R.layout.simple_spinner_item,
				getResources().getStringArray(R.array.spDistUnits));
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
																							// drop
																							// down
																							// view
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
		spUnit.setSelection(DISTPREF_UNIT);

		// Set up Numberpicker
		final NumberPicker np = (NumberPicker) d
				.findViewById(R.id.diag_dist_npDist);
		np.setMaxValue(9999);
		np.setMinValue(1); // min value 1
		np.setWrapSelectorWheel(false);
		np.setValue(DISTPREF);
		// np.setOnValueChangedListener(this);

		dist_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// tv.setText(String.valueOf(np.getValue())); //set the value to
				// textview
				SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				Editor edit = sp.edit();
				edit.putInt("DISTPREF", np.getValue());
				edit.putInt("DISTPREF_UNIT", spUnit.getSelectedItemPosition());
				edit.commit();
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

	// Database access
	public static final String KEY_ROOM_ID = "room_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_CATEGORY = "category";
	public static final String KEY_NO_OF_LEARNER = "noOfLearner";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_LATLNG = "latlng";
	public static final String KEY_USERNAME = "username";
	private GrpRoomDbAdapter mDbHelper;

	private ArrayList<GrpRoomListing> details;
	ArrayList<GrpRoomListExt> roomLocationList;

	double latR, lngR;
	LatLng roomLatLng;

	private void suggestEducatorRoom() {

		mDbHelper = new GrpRoomDbAdapter(getActivity());
		mDbHelper.open();

		GrpRoomListing grpRmList;
		Cursor mCursor = mDbHelper.fetchAllRooms();
		details = new ArrayList<GrpRoomListing>();
		while (mCursor.moveToNext()) {
			grpRmList = new GrpRoomListing(
					mCursor.getLong(mCursor
							.getColumnIndex(GrpRoomDbAdapter.KEY_ROOM_ID)),
					mCursor.getString(mCursor
							.getColumnIndex(GrpRoomDbAdapter.KEY_TITLE)),
					mCursor.getString(mCursor
							.getColumnIndex(GrpRoomDbAdapter.KEY_CATEGORY)),
					mCursor.getLong(mCursor
							.getColumnIndex(GrpRoomDbAdapter.KEY_NO_OF_LEARNER)),
					mCursor.getString(mCursor
							.getColumnIndex(GrpRoomDbAdapter.KEY_LOCATION)),
					mCursor.getString(mCursor
							.getColumnIndex(GrpRoomDbAdapter.KEY_LATLNG)));
			details.add(grpRmList);
		}

		roomLocationList = new ArrayList<GrpRoomListExt>();

		for (int i = 0; i < details.size(); i++) {
			String latLng = details.get(i).getLatlng();
			long room_id = details.get(i).getRoom_id();

			Log.d("Geocode", "db LatLng: " + latLng);

			// Split lat Lng
			String[] parts = latLng.split(",");
			latR = Double.parseDouble(parts[0]);
			lngR = Double.parseDouble(parts[1]);

			roomLatLng = new LatLng(latR, lngR);

			roomLocationList.add(new GrpRoomListExt(room_id, details.get(i)
					.getTitle(), details.get(i).getCategory(), details.get(i)
					.getNoOfLearner(), details.get(i).getLocation(), details
					.get(i).getLatlng(), roomLatLng));
		}

		Geocoder coder = new Geocoder(getActivity());
		List<Address> address;

		String homeAddress = homeLocation;
		LatLng homeLatLng = null;

		try {

			address = coder.getFromLocationName(homeAddress, 5);
			if (address == null) {
				// return null;
			}
			Address location = address.get(0);

			homeLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());

			// GeoPoint p1 = new GeoPoint((int) (location.getLatitude() * 1E6),
			// (int) (location.getLongitude() * 1E6));
			Log.d("Geocode", "LatLng: " + homeLatLng.toString());
			// return p1;
		} catch (Exception e) {
			// TODO: handle exception
		}

		Location locRoom = new Location("");
		Location locHome;

		ArrayList<Integer> toRemove = new ArrayList<Integer>();

		for (GrpRoomListExt r : roomLocationList) {
			int curr = 0;

			locRoom = new Location("Room " + r.getRoom_id());
			locRoom.setLatitude(r.getRoomLatLng().latitude);
			locRoom.setLongitude(r.getRoomLatLng().longitude);

			locHome = new Location("User Home");
			locHome.setLatitude(homeLatLng.latitude);
			locHome.setLongitude(homeLatLng.longitude);

			double dist = locHome.distanceTo(locRoom);

			Log.d("Geocode", "Process Room ID: " + r.getRoom_id());
			Log.d("Geocode", "Dist: " + dist);

			// if (dist < DISTPREF) {
			// Log.d("Geocode", "Removed Room ID: " + r.getRoom_id());
			// Log.d("Geocode", "Dist: " + dist);
			// toRemove.add(curr);
			// }

			int retval;

			if (DISTPREF_UNIT == 0) {
				retval = Double.compare(DISTPREF, dist);
			} else {
				retval = Double.compare(DISTPREF / 1000, dist);
			}

			if (retval < 0) {
				Log.d("Geocode", "Removed Room ID: " + r.getRoom_id()
						+ ", Dist: " + dist);
				toRemove.add(curr);
			}

			curr++;
		}
		if (!toRemove.isEmpty()) {
			for (int x : toRemove) {
				roomLocationList.remove(x);
			}
		}
	}

}