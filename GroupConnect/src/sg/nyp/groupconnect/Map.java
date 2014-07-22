package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.entity.fakeMember;
import sg.nyp.groupconnect.entity.fakeRoom;
import sg.nyp.groupconnect.entity.fakeSubjects;
import sg.nyp.groupconnect.entity.schools;
import sg.nyp.groupconnect.utilities.JSONParser;
import sg.nyp.groupconnect.utilities.PieChartBuilder;
import sg.nyp.groupconnect.utilities.VotingfPieChartBuilder;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends FragmentActivity {

	// Common
	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	static final LatLng somewhere = new LatLng(1.352083, 90);
	BroadcastReceiver networkStateReceiver;
	/* private GoogleMap map; */
	public static GoogleMap mMap;

	// Geraldine
	private static final int SERIES_NR = 2;
	private Button btnSearch;
	private Intent intent = null;
	private ArrayList<fakeSubjects> arrayFakeSubject = new ArrayList<fakeSubjects>();
	private ArrayList<schools> arraySchools = new ArrayList<schools>();
	private ArrayList<fakeRoom> arrayFakeRoom = new ArrayList<fakeRoom>();
	private ArrayList<fakeMember> arrayFakeMember = new ArrayList<fakeMember>();
	private CheckBox chkPrimary, chkSecondary, chkPolytechnic;
	private RadioGroup rdGrp;
	private String category, schoolCategory1, schoolCategory2, schoolCategory3;
	private float colour;

	private String[] listItemsFirstRow;
	private String[] listItemsSecondRow;
	private double[] percentage = new double[] { 40.0, 50.0, 60.0, 70.0, 80.0 };
	private String[] places;

	// Database
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String SUBJECT_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveSujects.php";
	private static final String SCHOOL_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveSchools.php";
	private static final String MEMBER_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveMemberRecord.php";
	private static final String ROOM_URL = "http://www.it3197Project.3eeweb.com/grpConnect/retrieveRoom.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_LATITUDE = "latitude";
	private static final String TAG_LONGITUDE = "longitude";

	private static final String TAG_LOCATION = "location";
	private static final String TAG_GENDER = "gender";

	private static final String TAG_MEMBERID = "memberId";
	private static final String TAG_SUBJECT = "subjects";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setUpMapIfNeeded();

		// Managed by Geraldine
		sildeDrawerSetup();
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.back);
		actionBar.setHomeButtonEnabled(true);

	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated.. This will ensure that we only ever call
	 * {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 * <p>
	 * A user can return to this FragmentActivity after following the prompt and
	 * correctly installing/updating/enabling the Google Play services. Since
	 * the FragmentActivity may not have been completely destroyed during this
	 * process (it is likely that it would only be stopped or paused),
	 * {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * This is where we can add markers or lines, add listeners or move the
	 * camera. In this case, we just add a marker near Africa.
	 * <p>
	 * This should only be called once and when we are sure that {@link #mMap}
	 * is not null.
	 */
	private void setUpMap() {
		if (mMap != null) {

			// Move the camera instantly to Singapore with a zoom of 15.
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 8));

			// Zoom in, animating the camera.
			mMap.animateCamera(CameraUpdateFactory.zoomIn());

			// Zoom out to zoom level 10, animating with a duration of 2
			// seconds.
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		}
	}

	private void sildeDrawerSetup() {
		rdGrp = (RadioGroup) findViewById(R.id.radioCategory);
		new AttemptSubject().execute();

		chkPrimary = (CheckBox) findViewById(R.id.chkPrimary);
		chkSecondary = (CheckBox) findViewById(R.id.chkSecondary);
		chkPolytechnic = (CheckBox) findViewById(R.id.chkPolytechnic);

		btnSearch = (Button) findViewById(R.id.btnSearch);

	}

	public void Search(View v) {
		mMap.clear();
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {

				intent = new Intent(getBaseContext(), PieChartBuilder.class);
				intent.putExtra("Category", category);
				intent.putExtra("School_Name", marker.getTitle());
				startActivity(intent);
			}
		});

		int radioButtonID = rdGrp.getCheckedRadioButtonId();
		View radioButton = rdGrp.findViewById(radioButtonID);
		int idx = rdGrp.indexOfChild(radioButton);

		if ((chkPrimary.isChecked() == true || chkSecondary.isChecked() == true || chkPolytechnic
				.isChecked() == true) && idx != -1) {

			category = arrayFakeSubject.get(idx).getName().toString();
			
			schoolCategory1 = "";
			schoolCategory2 = "";
			schoolCategory3 = "";
			
			arraySchools.clear();
			
			if (chkPrimary.isChecked()) {
				schoolCategory1 = "Primary";
			}
			if (chkSecondary.isChecked()) {
				schoolCategory2 = "Secondary";
			}
			if (chkPolytechnic.isChecked()) {
				schoolCategory3 = "Polytechnic";
			}
			new AttemptSchool().execute();
		} else {
			alertDialog("Error",
					"Sorry. You have to choose education level and category.");
		}
	}

	private void alertDialog(String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setNegativeButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just clos
								// the dialog box and do nothin
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void dialog(View v) {
		new AttemptMember().execute();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listItemsFirstRow.length;
		}

		@Override
		public Object getItem(int position) {
			// this isn't great
			return listItemsFirstRow[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_view,
						null);
			}

			((TextView) convertView.findViewById(R.id.text1))
					.setText(listItemsFirstRow[position]);
			((TextView) convertView.findViewById(R.id.text2))
					.setText(listItemsSecondRow[position]);

			return convertView;
		}

	}

	class AttemptSubject extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.this);
			pDialog.setMessage("Setting up...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(SUBJECT_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);

					fakeSubjects fs = new fakeSubjects(c.getInt(TAG_ID),
							c.getString(TAG_NAME));
					arrayFakeSubject.add(fs);
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
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			for (int i = 0; i < arrayFakeSubject.size(); i++) {
				RadioButton rdbtn = new RadioButton(Map.this);
				rdbtn.setId(arrayFakeSubject.get(i).getId());
				rdbtn.setText(arrayFakeSubject.get(i).getName());
				rdbtn.setTextColor(Color.WHITE);
				rdbtn.setTextSize(15f);
				rdGrp.addView(rdbtn);
			}

		}

	}

	class AttemptSchool extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.this);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("category1", schoolCategory1));
				params.add(new BasicNameValuePair("category2", schoolCategory2));
				params.add(new BasicNameValuePair("category3", schoolCategory3));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(SCHOOL_URL,
						"POST", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);

				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);

					schools s = new schools(c.getInt(TAG_ID),
							c.getString(TAG_NAME), c.getString(TAG_CATEGORY),
							c.getDouble(TAG_LATITUDE),
							c.getDouble(TAG_LONGITUDE));
					arraySchools.add(s);
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
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			for (int i = 0; i < arraySchools.size(); i++) {
				if (arraySchools.get(i).getCategory().equals("Primary")) {
					colour = BitmapDescriptorFactory.HUE_YELLOW;
				} else if (arraySchools.get(i).getCategory()
						.equals("Secondary")) {
					colour = BitmapDescriptorFactory.HUE_ORANGE;
				} else if (arraySchools.get(i).getCategory()
						.equals("Polytechnic")) {
					colour = BitmapDescriptorFactory.HUE_VIOLET;
				}

				LatLng primary = new LatLng(arraySchools.get(i).getLatitude(),
						arraySchools.get(i).getLongitude());

				Marker primaryMarker = mMap.addMarker(new MarkerOptions()
						.position(primary).title(arraySchools.get(i).getName())
						.snippet("more information")
						.icon(BitmapDescriptorFactory.defaultMarker(colour)));
			}
		}
	}

	class AttemptMember extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		boolean failure = false;
		public int success;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Map.this);
			pDialog.setMessage("Retreiving data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();

				// getting product details by making HTTP request
				JSONObject json1 = jsonParser.makeHttpRequest(MEMBER_URL,
						"POST", params1);

				// json success tag
				success = json1.getInt(TAG_SUCCESS);
				arrayFakeMember.clear();

				for (int i = 0; i < json1.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json1.getJSONArray(TAG_ARRAY).getJSONObject(
							i);

					fakeMember fm = new fakeMember(c.getInt(TAG_ID),
							c.getString(TAG_NAME), c.getString(TAG_LOCATION),
							c.getDouble(TAG_LATITUDE),
							c.getDouble(TAG_LONGITUDE), c.getString(TAG_GENDER));
					arrayFakeMember.add(fm);
				}
				if (success == 1) {
					return json1.getString(TAG_MESSAGE);
				} else {
					return json1.getString(TAG_MESSAGE);

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
			new AttemptRoom().execute();
		}
	}

	class AttemptRoom extends AsyncTask<String, String, String> {

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
			// TODO Auto-generated method stub
			// Check for success tag

			try {
				// Building Parameters
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();

				// getting product details by making HTTP
				// request
				JSONObject json2 = jsonParser.makeHttpRequest(ROOM_URL,
						"POST", params2);

				// json success tag
				success = json2.getInt(TAG_SUCCESS);
				arrayFakeRoom.clear();

				for (int i = 0; i < json2.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json2.getJSONArray(TAG_ARRAY).getJSONObject(
							i);

					fakeRoom fr = new fakeRoom(c.getInt(TAG_ID), c.getString(TAG_NAME), c.getString(TAG_SUBJECT), c.getString(TAG_LOCATION), c.getString(TAG_MEMBERID));
					arrayFakeRoom.add(fr);
				}
				if (success == 1) {
					return json2.getString(TAG_MESSAGE);
				} else {
					return json2.getString(TAG_MESSAGE);

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
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			listItemsFirstRow = new String[arrayFakeRoom.size() + 3];
			listItemsSecondRow = new String[arrayFakeRoom.size() + 3];

			listItemsFirstRow[0] = "Subject: " + arrayFakeRoom.get(0).getSubjects();
			listItemsSecondRow[0] = "Location: "
					+ arrayFakeRoom.get(0).getLocation();

			for (int i = 0; i < arrayFakeRoom.size(); i++) {
				if (arrayFakeRoom.get(i).getMemberId()
						.equals(Integer.toString(arrayFakeMember.get(i).getId()))) {
					listItemsFirstRow[i + 2] = arrayFakeMember.get(i).getName();
					listItemsSecondRow[i + 2] = arrayFakeMember.get(i)
							.getLocation();
				}
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
			builder.setAdapter(new MyAdapter(), null);
			builder.setTitle(arrayFakeRoom.get(0).getName());
			builder.setPositiveButton("Vote", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mMap.clear();
					mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
						@Override
						public void onInfoWindowClick(final Marker marker) {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									Map.this);

							// set title
							alertDialogBuilder.setTitle("Vote for "
									+ marker.getTitle());

							// set dialog message
							alertDialogBuilder
									.setMessage("Are you okay with this location?")
									.setCancelable(false)
									.setNegativeButton("No",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.cancel();
												}
											})
									.setPositiveButton("Yes",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													for (int i = 0; i < places.length; i++) {
														if (places[i].equals(marker
																.getTitle()))
															percentage[i] = percentage[i] + 100;
													}
												}
											});

							// create alert dialog
							AlertDialog alertDialog = alertDialogBuilder.create();

							// show it
							alertDialog.show();
						}
					});
					places = new String[] { "Bedok Community Library",
							"Toa Payoh Public Library",
							"Ang Mo Kio Public Library",
							"Cheng San Public Library", "Yishun Public Library" };

					LatLng coordinate1 = new LatLng(1.325424, 103.932386);

					Marker bcl = mMap.addMarker(new MarkerOptions()
							.position(coordinate1)
							.title("Bedok Community Library")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

					LatLng coordinate2 = new LatLng(1.333661, 103.854108);

					Marker tppl = mMap.addMarker(new MarkerOptions()
							.position(coordinate2)
							.title("Toa Payoh Public Library")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

					LatLng coordinate3 = new LatLng(1.378967, 103.849988);

					Marker amkpl = mMap.addMarker(new MarkerOptions()
							.position(coordinate3)
							.title("Ang Mo Kio Public Library")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

					LatLng coordinate4 = new LatLng(1.370730, 103.895307);

					Marker hgpl = mMap.addMarker(new MarkerOptions()
							.position(coordinate4)
							.title("Cheng San Public Library")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

					LatLng coordinate5 = new LatLng(1.429764, 103.840375);

					Marker ypl = mMap.addMarker(new MarkerOptions()
							.position(coordinate5)
							.title("Yishun Public Library")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

				}
			});
			builder.setNegativeButton("Voting Result", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String[] array = new String[5];
					for (int i = 0; i < array.length; i++) {
						array[i] = Double.toString(percentage[i]);
					}

					intent = new Intent(getBaseContext(),
							VotingfPieChartBuilder.class);
					intent.putExtra("PercentageArray", array);
					intent.putExtra("PlacesArray", places);
					startActivity(intent);
				}
			});
			builder.show();
		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            Map.this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}// MainActivity
