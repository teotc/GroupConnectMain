package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.HashMap;

import org.achartengine.*;
import org.achartengine.chart.BarChart.*;
import org.achartengine.renderer.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import utilities.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
	
	// Common
	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	static final LatLng somewhere = new LatLng(1.352083, 90);
	/* private GoogleMap map; */
	private GoogleMap mMap;
	
	//Geraldine
	private static final int SERIES_NR = 2;
	static final LatLng cwp = new LatLng(1.436518, 103.786314);
	static final LatLng nex = new LatLng(1.350541, 103.871890);
	static final LatLng nyp = new LatLng(1.379622, 103.849887);

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();

		// Managed by TC
		// Loads categories and group filters
		generateControlsOverlay(savedInstanceState);
		// Managed by Geraldine
		infoWindowSetup(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();

		// Load Category data
		new LoadCategories().execute();
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
		// mMap.addMarker(new MarkerOptions().position(new LatLng(1.352083,
		// 103.819836)).title("Marker"));

		if (mMap != null) {
			Marker cwp1 = mMap.addMarker(new MarkerOptions()
					.position(cwp)
					.title("Causeway Point")
					.snippet("Bar Graph")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

			Marker nex1 = mMap.addMarker(new MarkerOptions()
					.position(nex)
					.title("Serangoon Nex")
					.snippet("Line Graph")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

			Marker nyp1 = mMap
					.addMarker(new MarkerOptions()
							.position(nyp)
							.title("Nanyang Polytechnic")
							.snippet("Pie Chart")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
			// .icon(BitmapDescriptorFactory
			// .fromResource(R.drawable.ic_launcher)));

			// Move the camera instantly to Singapore with a zoom of 15.
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 8));

			// Zoom in, animating the camera.
			mMap.animateCamera(CameraUpdateFactory.zoomIn());

			// Zoom out to zoom level 10, animating with a duration of 2
			// seconds.
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		}
	}

	private void infoWindowSetup(Bundle savedInstanceState) {
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				AChartClasses ac = new AChartClasses();
				if (marker.getTitle().equals("Causeway Point")) {
					XYMultipleSeriesRenderer renderer = ac.getBarDemoRenderer();
					ac.setChartSettings(renderer);
					Intent intent = ChartFactory.getBarChartIntent(
							MainActivity.this, ac.getBarDemoDataset(SERIES_NR), renderer,
							Type.DEFAULT);
					startActivity(intent);
				} else if (marker.getTitle().equals("Serangoon Nex")) {
					Intent intent = ChartFactory.getLineChartIntent(
							MainActivity.this, ac.getDemoDataset(SERIES_NR),
							ac.getDemoRenderer());
					startActivity(intent);

				} else if (marker.getTitle().equals("Nanyang Polytechnic")) {
					Intent myIntent = new Intent(MainActivity.this,
							PieChartBuilder.class);
					startActivity(myIntent);

				} else
					Toast.makeText(MainActivity.this, "Wrong",
							Toast.LENGTH_SHORT).show();

			}
		});
	}

	// Below has edited code w/ some comments removed/edited
	// From http://www.mybringback.com/series/android-intermediate/

	// Progress Dialog
	private ProgressDialog pDialog;
	// testing from a real server:
	private static final String LOAD_CATEGORY_URL = "http://"
			+ "www.it3197Project.3eeweb.com/webservice/uigeneration/categories.php";

	// JSON IDS:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_CATEGORIES = "categories";
	private static final String TAG_POST_ID = "post_id";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_ID = "id";
	// it's important to note that the message is both in the parent branch of
	// our JSON tree that displays a "Post Available" or a "No Post Available"
	// message,
	// and there is also a message for each individual post, listed under the
	// "posts"
	// category, that displays what the user typed as their message.

	// An array of all of our categories
	private JSONArray mCategories = null;
	// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mCategoriesList;

	/*
	 * Managed by TC Desc: Sub-method to keep onCreate clean.
	 */
	private void generateControlsOverlay(Bundle savedInstanceState) {
		final RelativeLayout relLayFilterHolder;
		final Spinner spGrpFilter;
		final ImageButton imgbnSearch;

		relLayFilterHolder = (RelativeLayout) findViewById(R.id.relLayFilterHolder);
		spGrpFilter = (Spinner) findViewById(R.id.spGrpFilter);
		imgbnSearch = (ImageButton) findViewById(R.id.imgBnSearch);

		String[] items = { "Test - Hardcoded GF 1", "Test - Hardcoded GF 2",
				"item 3", "item 4" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				MainActivity.this,
				android.R.layout.simple_list_item_activated_1, items);
		spGrpFilter.setAdapter(adapter);

		imgbnSearch.setOnClickListener(new OnClickListener() {
			int filterView = 0;

			@Override
			public void onClick(View v) {
				if (filterView == 1) {
					relLayFilterHolder.setVisibility(View.INVISIBLE);
					filterView = 0;
				} else {
					relLayFilterHolder.setVisibility(View.VISIBLE);
					filterView = 1;
				}
			}
		});

		/*
		 * Load Categories and filters into spinner
		 */

	}

	/**
	 * Retrieves category data from the server.
	 */
	public void updateJSONdata() {

		// Instantiate the arraylist to contain all the JSON data.
		// we are going to use a bunch of key-value pairs, referring
		// to the json element name, and the content, for example,
		// message it the tag, and "I'm awesome" as the content..

		mCategoriesList = new ArrayList<HashMap<String, String>>();

		// Bro, it's time to power up the J parser
		JSONParser jParser = new JSONParser();
		// Feed the beast our comments url, and it spits us
		// back a JSON object. Boo-yeah Jerome.
		JSONObject json = jParser.getJSONFromUrl(LOAD_CATEGORY_URL);

		// when parsing JSON stuff, we should probably
		// try to catch any exceptions:
		try {

			// mCategories will tell us how many mCategories are available
			mCategories = json.getJSONArray(TAG_CATEGORIES);

			// looping through all posts according to the json object returned
			for (int i = 0; i < mCategories.length(); i++) {
				JSONObject c = mCategories.getJSONObject(i);

				// gets the content of each tag
				String content = c.getString(TAG_ID);
				String username = c.getString(TAG_CATEGORY);

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_ID, content);
				map.put(TAG_CATEGORY, username);

				// adding HashList to ArrayList
				mCategoriesList.add(map);

				// annndddd, our JSON data is up to date same with our array
				// list
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inserts the parsed data into the listview.
	 */
	private void updateList() {
		// ListActivity, List Adapter -> Spinner, ArrayAdapter

		// For a ListActivity we need to set the List Adapter, and in order to
		// do
		// that, we need to create a ListAdapter. This SimpleAdapter,
		// will utilize our updated Hashmapped ArrayList,
		// use our single_post xml template for each item in our list,
		// and place the appropriate info from the list to the
		// correct GUI id. Order is important here.
		// ListAdapter adapter = new SimpleAdapter(this, mCategoriesList,
		// R.layout.single_post, new String[] { TAG_ID, TAG_CATEGORY },
		// new int[] { R.id.title, R.id.message, R.id.username });
		// I shouldn't have to comment on this one:
		// setListAdapter(adapter);

		final Spinner spCategory;
		spCategory = (Spinner) findViewById(R.id.spCategory);

		ArrayAdapter<HashMap<String, String>> ad = new ArrayAdapter<HashMap<String, String>>(
				this, android.R.layout.simple_list_item_activated_1,
				mCategoriesList);
		spCategory.setAdapter(ad);

		// Optional: when the user clicks a list item we
		// could do something. However, we will choose
		// to do nothing...
		// ListView lv = getListView();
		// lv.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		//
		// // This method is triggered if an item is click within our
		// // list. For our example we won't be using this, but
		// // it is useful to know in real life applications.
		//
		// }
		// });
	}

	public class LoadCategories extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading Categories...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			updateJSONdata();
			return null;

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			updateList();
		}
	}

}// MainActivity
