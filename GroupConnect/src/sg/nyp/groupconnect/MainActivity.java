package sg.nyp.groupconnect;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {

	static final LatLng singapore = new LatLng(1.352083, 103.819836);
	static final LatLng somewhere = new LatLng(1.352083, 90);
	/* private GoogleMap map; */

	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();
		
		/* Managed by TC
		 * Desc: Sub-method to keep onCreate clean.
		 */
		generateControlsOverlay(savedInstanceState);
	}

	private void generateControlsOverlay(Bundle savedInstanceState) {
		final LinearLayout linlaySearchMenu;
		final Spinner spCategory;
		final ImageButton imgbnSearch;
		
		linlaySearchMenu = (LinearLayout) findViewById(R.id.linlaySearchMenu);
		spCategory = (Spinner) findViewById(R.id.spCategory);
		imgbnSearch = (ImageButton) findViewById(R.id.imgBnSearch);
		
		String[] items = { "Item 1", "item 2", "item 3", "item 4" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_list_item_activated_1, items);
		spCategory.setAdapter(adapter);
		
		imgbnSearch.setOnClickListener(new OnClickListener() {
			int searchMenuState = 0;
			@Override
			public void onClick(View v) {
				if(searchMenuState == 1){
					linlaySearchMenu.setVisibility(View.GONE);
					searchMenuState = 0;
				}
				else{
					linlaySearchMenu.setVisibility(View.VISIBLE);
					searchMenuState = 1;
				}
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
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
			Marker sgp = mMap.addMarker(new MarkerOptions().position(singapore)
					.title("Singapore"));
			Marker kiel = mMap.addMarker(new MarkerOptions()
					.position(somewhere)
					.title("Kiel")
					.snippet("Kiel is cool")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_launcher)));

			// Move the camera instantly to Singapore with a zoom of 15.
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 15));

			// Zoom in, animating the camera.
			mMap.animateCamera(CameraUpdateFactory.zoomIn());

			// Zoom out to zoom level 10, animating with a duration of 2
			// seconds.
			mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		}
	}

}
