package sg.nyp.groupconnect.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.entity.MyItem;
import sg.nyp.groupconnect.entity.Schools;
import sg.nyp.groupconnect.utilities.JSONParser;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class RetrieveSchool extends AsyncTask<String, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	boolean failure = false;
	public int success;
	private float colour;
	private ClusterManager<MyItem> mClusterManager;

	// Database
	public static ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String SCHOOL_URL = "http://www.it3197Project.3eeweb.com/grpConnect/statics/retrieveSchools.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_ARRAY = "posts";

	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_CATEGORY = "category";
	private static final String TAG_LATITUDE = "latitude";
	private static final String TAG_LONGITUDE = "longitude";

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(Map.context);
		pDialog.setMessage("Retreiving data...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... args) {
		// Check for success tag

		try {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("category1", Map.schoolCategory1));
			params.add(new BasicNameValuePair("category2", Map.schoolCategory2));
			params.add(new BasicNameValuePair("category3", Map.schoolCategory3));
			params.add(new BasicNameValuePair("subjectId", Integer
					.toString(Map.subjectId)));

			// getting product details by making HTTP request
			JSONObject json = jsonParser.makeHttpRequest(SCHOOL_URL, "POST",
					params);

			// json success tag
			success = json.getInt(TAG_SUCCESS);
			if (success == 1) {
				for (int i = 0; i < json.getJSONArray(TAG_ARRAY).length(); i++) {

					JSONObject c = json.getJSONArray(TAG_ARRAY)
							.getJSONObject(i);

					Schools s = new Schools(c.getInt(TAG_ID),
							c.getString(TAG_NAME), c.getString(TAG_CATEGORY),
							c.getDouble(TAG_LATITUDE),
							c.getDouble(TAG_LONGITUDE));
					Map.arraySchools.add(s);
				}
			}
			if (success == 1) {
				return json.getString(TAG_MESSAGE);
			} else {
				return json.getString(TAG_MESSAGE);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return Integer.toString(success);

	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	protected void onPostExecute(String file_url) {
		mClusterManager = new ClusterManager<MyItem>(Map.context, Map.mMap);
		Map.mMap.setOnCameraChangeListener(mClusterManager);
		mClusterManager.setRenderer(new ItemRenderer());
		List<MyItem> items = new ArrayList<MyItem>();

		if (success == 1) {
			for (int i = 0; i < Map.arraySchools.size(); i++) {
				if (Map.arraySchools.get(i).getCategory().equals("Primary")) {
					colour = BitmapDescriptorFactory.HUE_YELLOW;
				} else if (Map.arraySchools.get(i).getCategory()
						.equals("Secondary")) {
					colour = BitmapDescriptorFactory.HUE_ORANGE;
				} else if (Map.arraySchools.get(i).getCategory()
						.equals("Polytechnic")) {
					colour = BitmapDescriptorFactory.HUE_VIOLET;
				}

				LatLng latLng = new LatLng(Map.arraySchools.get(i)
						.getLatitude(), Map.arraySchools.get(i).getLongitude());

				items.add(new MyItem(latLng, Map.arraySchools.get(i).getName(),
						"more information", BitmapDescriptorFactory
								.defaultMarker(colour)));
			}

			mClusterManager.addItems(items);

			pDialog.dismiss();
			Map.slide.close();
			
		} else {
			pDialog.dismiss();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					Map.context);

			// set dialog message
			alertDialogBuilder
					.setMessage("Sorry. No result found.")
					.setCancelable(false)
					.setNegativeButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
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

	}

	private class ItemRenderer extends DefaultClusterRenderer<MyItem> {

		public ItemRenderer() {
			super(Map.context, Map.mMap, mClusterManager);

		}

		@Override
		protected void onBeforeClusterItemRendered(MyItem myItem,
				MarkerOptions markerOptions) {
			markerOptions.position(myItem.getPosition())
					.title(myItem.getmTitle()).snippet(myItem.getmSnippet())
					.icon(myItem.getmIcon());
		}

		@Override
		protected void onBeforeClusterRendered(Cluster<MyItem> cluster,
				MarkerOptions markerOptions) {
			// TODO Auto-generated method stub
			super.onBeforeClusterRendered(cluster, markerOptions);
		}

		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster) {
			// Always render clusters.
			return cluster.getSize() > 1;
		}
	}
}
