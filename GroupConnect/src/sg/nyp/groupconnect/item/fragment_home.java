package sg.nyp.groupconnect.item;

import java.util.ArrayList;
import java.util.List;

import sg.nyp.groupconnect.R;
import sg.nyp.groupconnect.data.GrpRoomDbAdapter;
import sg.nyp.groupconnect.data.GrpRoomListExtAdapter;
import sg.nyp.groupconnect.learner.GrpRoomListExt;
import sg.nyp.groupconnect.learner.GrpRoomListing;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;

@SuppressLint("NewApi")
public class fragment_home extends Fragment {

	SharedPreferences sp;
	String homeLocation = null;
	int DISTPREF = 5000; // metres
	int DISTPREF_UNIT = 0; // m

	ListView eduSuggestLV;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);
		// Get ListView object from xml
		ListView listView = (ListView) rootView.findViewById(R.id.listView1);

		eduSuggestLV = (ListView) rootView.findViewById(R.id.eduSuggestLV);
		TextView homeLoc = (TextView) rootView
				.findViewById(R.id.fgh_tvHomeLocation);
		// ImageButton bnSetDist = (ImageButton) rootView
		// .findViewById(R.id.fgh_bnSetDist);

		sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		homeLocation = sp.getString("homeLocation", null);
		Log.i("Geocode","Home:"+homeLocation);
		homeLoc.setText(homeLocation);

		// DISTPREF = sp.getInt("DISTPREF", 5000); // DISTPREF_UNIT
		// DISTPREF_UNIT = sp.getInt("DISTPREF", 0);

		// bnSetDist.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // show();
		// }
		// });
		CompareRoomDistance crd = new CompareRoomDistance();
		crd.execute();

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

		/*
		 * eduSuggestLV.setAdapter(new GrpRoomListExtAdapter(roomLocationList,
		 * getActivity()));
		 */
		// for (GrpRoomListExt x : roomLocationList) {
		// Log.d("TEST", "x: " + x.getLocation() + "\n" + x.getTitle());
		// }

		// Assign adapter to ListView
		listView.setAdapter(adapter);
		return rootView;
	}

	Spinner spUnit;

	public void show() {

		final Dialog d = new Dialog(getActivity());
		d.setTitle("Choose a boundary");
		d.setContentView(R.layout.dialog_distpicker);
		Button dist_set = (Button) d.findViewById(R.id.diag_dist_set);
		Button dist_cancel = (Button) d.findViewById(R.id.diag_dist_cancel);
		spUnit = (Spinner) d.findViewById(R.id.diag_dist_spUnit);

		String[] unitArray = new String[] { "M", "KM" };

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
		eduSuggestLV.setAdapter(new GrpRoomListExtAdapter(roomLocationList,
				getActivity()));
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
	ArrayList<GrpRoomListExt> newRoomLocList;

	double latR, lngR;
	LatLng roomLatLng;

	class CompareRoomDistance extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			
			mDbHelper = new GrpRoomDbAdapter(getActivity());
			mDbHelper.open();
			
			Geocoder coder = new Geocoder(getActivity());
			List<Address> address;

			String homeAddress = homeLocation;
			LatLng homeLatLng = null;
			
			Location locRoom = new Location("");
			Location locHome;
			
			newRoomLocList = new ArrayList<GrpRoomListExt>();
			roomLocationList = new ArrayList<GrpRoomListExt>();

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
			
			for (int i = 0; i < details.size(); i++) {
				if (!details.get(i).getLatlng().equals("")
						&& !details.get(i).getLatlng().equals(null)
						&& !details.get(i).getLatlng().equals("undefined")) {
					String latLng = details.get(i).getLatlng();
					long room_id = details.get(i).getRoom_id();

					Log.d("Geocode", "db LatLng: " + latLng);

					// Split lat Lng
					String[] parts = latLng.split(",");
					latR = Double.parseDouble(parts[0]);
					lngR = Double.parseDouble(parts[1]);

					roomLatLng = new LatLng(latR, lngR);

					roomLocationList.add(new GrpRoomListExt(room_id, details
							.get(i).getTitle(), details.get(i).getCategory(),
							details.get(i).getNoOfLearner(), details.get(i)
									.getLocation(), details.get(i).getLatlng(),
							roomLatLng, 0));
				}
			}
			
			try {

				address = coder.getFromLocationName(homeAddress, 5);
				Log.d("Geocode", "Home Address: " + homeAddress);
				if (address == null) {
					// return null;
				}
				Address location = address.get(0);

				homeLatLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				
				Log.d("Geocode", "Home LatLng: " + homeLatLng.toString());
				Log.d("Geocode", "----- ");
				// return p1;
			} catch (Exception e) {
				// String s = "Unable to locate your provided address, ";
				// s+= "please check your address.";
				// Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
				Log.d("Geocode", "Geocode Failed");
			}
			
			for (GrpRoomListExt r : roomLocationList) {

				locRoom = new Location("Room " + r.getRoom_id());
				locRoom.setLatitude(r.getRoomLatLng().latitude);
				locRoom.setLongitude(r.getRoomLatLng().longitude);

				locHome = new Location("User Home");
				locHome.setLatitude(homeLatLng.latitude);
				locHome.setLongitude(homeLatLng.longitude);
				Log.d("Geocode", "Removed: " + r.getRoom_id());

				double dist = locHome.distanceTo(locRoom);

				r.setDistance(dist);

				int retval;

				// if (DISTPREF_UNIT == 0) {
				retval = Double.compare(DISTPREF, dist);
				// }
				// else {
				// retval = Double.compare(DISTPREF / 1000, dist);
				// }
				
				Log.d("Geocode", "Process Room ID: " + r.getRoom_id());
				Log.d("Geocode", "Dist: " + dist);
				
				if (retval < 0) {
					Log.d("Geocode", "Removed: " + r.getRoom_id());
				} else{
					Log.d("Geocode", "Added: " + r.getRoom_id());
					newRoomLocList.add(r);
				}
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			// pDialog.dismiss();
			eduSuggestLV.setAdapter(new GrpRoomListExtAdapter(newRoomLocList,
					getActivity()));
		}

	}

}