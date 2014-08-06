package sg.nyp.groupconnect.db;

import java.util.ArrayList;
import java.util.List;

import sg.nyp.groupconnect.Map;
import sg.nyp.groupconnect.data.SchoolsDbAdapter;
import sg.nyp.groupconnect.entity.MyItem;
import sg.nyp.groupconnect.entity.Schools;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
	public static ClusterManager<MyItem> mClusterManager;

	// Database
	public static ProgressDialog pDialog;

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_CATEGORY = "category";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(Map.context);
		pDialog.setMessage("Retreiving data...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... args) {
		// Check for success tag
		SchoolsDbAdapter mDbHelper = new SchoolsDbAdapter(Map.context);
		mDbHelper.open();

		Cursor mCursor = mDbHelper.fetchAllSearchSchools(Map.schoolCategory1,
				Map.schoolCategory2, Map.schoolCategory3,
				Integer.toString(Map.subjectId));
		Map.arraySchools.clear();

		if (mCursor.getCount() != 0) {
			mCursor.moveToFirst();
			Schools s = new Schools(
					mCursor.getInt(mCursor.getColumnIndex(KEY_ID)),
					mCursor.getString(mCursor.getColumnIndex(KEY_NAME)),
					mCursor.getString(mCursor.getColumnIndex(KEY_CATEGORY)),
					mCursor.getDouble(mCursor.getColumnIndex(KEY_LATITUDE)),
					mCursor.getDouble(mCursor.getColumnIndex(KEY_LONGITUDE)));
			Map.arraySchools.add(s);
			
			while (mCursor.moveToNext()) {

				s = new Schools(
						mCursor.getInt(mCursor.getColumnIndex(KEY_ID)),
						mCursor.getString(mCursor.getColumnIndex(KEY_NAME)),
						mCursor.getString(mCursor.getColumnIndex(KEY_CATEGORY)),
						mCursor.getDouble(mCursor.getColumnIndex(KEY_LATITUDE)),
						mCursor.getDouble(mCursor.getColumnIndex(KEY_LONGITUDE)));
				Map.arraySchools.add(s);
			}
		}

		success = mCursor.getCount();
		
		mCursor.close();
		mDbHelper.close();

		return null;

	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	@SuppressWarnings("deprecation")
	protected void onPostExecute(String file_url) {
		mClusterManager = new ClusterManager<MyItem>(Map.context, Map.mMap);
		Map.mMap.setOnCameraChangeListener(mClusterManager);
		mClusterManager.setRenderer(new ItemRenderer());
		List<MyItem> items = new ArrayList<MyItem>();

		if (success != 0) {
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
			super.onBeforeClusterRendered(cluster, markerOptions);
		}

		@Override
		protected boolean shouldRenderAsCluster(
				@SuppressWarnings("rawtypes") Cluster cluster) {
			// Always render clusters.
			return cluster.getSize() > 1;
		}
	}
}
