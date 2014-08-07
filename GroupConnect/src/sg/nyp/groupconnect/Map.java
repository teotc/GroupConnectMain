package sg.nyp.groupconnect;

import java.util.ArrayList;

import sg.nyp.groupconnect.db.RetrieveSchool;
import sg.nyp.groupconnect.db.RetrieveSubject;
import sg.nyp.groupconnect.entity.Schools;
import sg.nyp.groupconnect.entity.Categories;
import sg.nyp.groupconnect.utilities.PieChartBuilder;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SlidingDrawer;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

@SuppressWarnings("deprecation")
public class Map extends FragmentActivity {

	// Common
	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	static final LatLng somewhere = new LatLng(1.352083, 90);
	BroadcastReceiver networkStateReceiver;
	public static GoogleMap mMap;

	// Geraldine
	
	public static SlidingDrawer slide;
	
	private Intent intent = null;
	private CheckBox chkPrimary, chkSecondary, chkPolytechnic;
	private String category;
	
	public static ArrayList<Categories> arraySubject = new ArrayList<Categories>();
	public static ArrayList<Schools> arraySchools = new ArrayList<Schools>();
	public static RadioGroup rdGrp;
	public static String schoolCategory1, schoolCategory2, schoolCategory3;

	public static int subjectId;

	public static Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setTitle("View Statistics");
		
		setUpMapIfNeeded();

		// Managed by Geraldine
		
		context = this;
		
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
	
	@Override
	protected void onDestroy() {
		mMap = null;
		super.onDestroy();
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
		new RetrieveSubject().execute(); //TODO

		chkPrimary = (CheckBox) findViewById(R.id.chkPrimary);
		chkSecondary = (CheckBox) findViewById(R.id.chkSecondary);
		chkPolytechnic = (CheckBox) findViewById(R.id.chkPolytechnic);
		slide = (SlidingDrawer) findViewById(R.id.slideDrawer);
		slide.open();
	}

	public void Search(View v) {
		mMap.clear();
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				int schoolId = 0;
				for (int i = 0; i < arraySchools.size(); i++) {
					if (arraySchools.get(i).getName().equals(marker.getTitle())) {
						schoolId=arraySchools.get(i).getId();
					}
				}
				for (int i = 0; i < arraySchools.size(); i++) {
					if (arraySchools.get(i).getName().equals(marker.getTitle())) {
						schoolId=arraySchools.get(i).getId();
					}
				}
				
				intent = new Intent(getBaseContext(), PieChartBuilder.class);
				intent.putExtra("Category", category);
				intent.putExtra("School_Name", marker.getTitle());
				intent.putExtra("School_Id", schoolId);
				intent.putExtra("Subject_Id", subjectId);
				startActivity(intent);
			}
		});

		int radioButtonID = rdGrp.getCheckedRadioButtonId();
		View radioButton = rdGrp.findViewById(radioButtonID);
		int idx = rdGrp.indexOfChild(radioButton);

		if ((chkPrimary.isChecked() == true || chkSecondary.isChecked() == true || chkPolytechnic
				.isChecked() == true) && idx != -1) {

			category = arraySubject.get(idx).getName().toString();
			subjectId = arraySubject.get(idx).getId();
			
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
			
			new RetrieveSchool().execute(); //TODO
			
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
        case android.R.id.home:
            Map.this.finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}
	
}// MainActivity
