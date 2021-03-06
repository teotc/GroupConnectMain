package sg.nyp.groupconnect;

import java.util.ArrayList;
import java.util.List;

import sg.nyp.groupconnect.data.MainDbAdapter;
import sg.nyp.groupconnect.data.MemberDbAdapter;
import sg.nyp.groupconnect.item.fragment_favourite;
import sg.nyp.groupconnect.item.fragment_help;
import sg.nyp.groupconnect.item.fragment_home;
import sg.nyp.groupconnect.item.fragment_organisation;
import sg.nyp.groupconnect.item.fragment_profile;
import sg.nyp.groupconnect.item.fragment_setting;
import sg.nyp.groupconnect.room.RoomDetails;
import sg.nyp.groupconnect.room.RoomMap;
import sg.nyp.groupconnect.room.RoomsRetrieve;
import sg.nyp.groupconnect.service.AvailableLocationPullSvc;
import sg.nyp.groupconnect.service.CategoriesPullSvc;
import sg.nyp.groupconnect.service.CategoriesTypePullSvc;
import sg.nyp.groupconnect.service.GrpRmPullService;
import sg.nyp.groupconnect.service.MemberGradePullSvc;
import sg.nyp.groupconnect.service.MemberPullSvc;
import sg.nyp.groupconnect.service.RoomMembersPullSvc;
import sg.nyp.groupconnect.service.RoomPullSvc;
import sg.nyp.groupconnect.service.SchoolsPullSvc;
import sg.nyp.groupconnect.service.VoteLocationPullSvc;
import sg.nyp.groupconnect.utilities.CustomAdapter;
import sg.nyp.groupconnect.utilities.RowItem;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {

	String[] menutitles;
	TypedArray menuIcons;

	// nav drawer title
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private List<RowItem> rowItems;
	private CustomAdapter adapter;

	Intent intent = null;
	ActionBar actionBar;
	// String fragmentNow = "main";
	Intent mServiceIntent;

	int item = 1;

	public static Context ct;

	private SharedPreferences sp;
	private String type;

	public static final String GCLINK = "http://www.it3197Project.3eeweb.com/grpConnect/";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sp = PreferenceManager.getDefaultSharedPreferences(this);
		type = sp.getString("type", "Learner");

		ct = this;
		new MainDbAdapter(this);

		MemberDbAdapter mDbHelper = new MemberDbAdapter(MainActivity.this);
		mDbHelper.open();

		Cursor mCursor = mDbHelper.fetchAll();

		if (mCursor.getCount() == 0) {

			new CategoriesPullSvc().execute();
			new CategoriesTypePullSvc().execute();
			new AvailableLocationPullSvc().execute();
			new MemberPullSvc().execute();
			new MemberGradePullSvc().execute();
			new RoomPullSvc().execute();
			new RoomMembersPullSvc().execute();
			new SchoolsPullSvc().execute();
			new VoteLocationPullSvc().execute();
		}

		mCursor.close();
		mDbHelper.close();

		// TC: Start service to get room data from webservice
		mServiceIntent = new Intent(this, GrpRmPullService.class);
		startService(mServiceIntent);

		mTitle = mDrawerTitle = getTitle();

		menutitles = getResources().getStringArray(R.array.titles);
		menuIcons = getResources().obtainTypedArray(R.array.icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.slider_list);

		rowItems = new ArrayList<RowItem>();

		for (int i = 0; i < menutitles.length; i++) {
			RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(
					i, -1));
			rowItems.add(items);
		}

		menuIcons.recycle();

		adapter = new CustomAdapter(getApplicationContext(), rowItems);

		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new SlideitemListener());

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		actionBar = getActionBar();

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			updateDisplay(1);
		}

	}

	class SlideitemListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			updateDisplay(position);
		}

	}

	private void updateDisplay(int position) {
		Fragment fragment = null;
		item = position;
		switch (position) {
		case 0:
			fragment = new fragment_profile();
			break;
		case 1:
			if (type.equalsIgnoreCase("Organization")) {
				fragment = new fragment_organisation();
			} else {
				fragment = new fragment_home();
			}
			break;
		case 2:
			//fragment = new fragment_favourite();
			intent = new Intent(getApplicationContext(), Login.class);
			Editor ed = sp.edit();
			ed.clear();
			ed.commit();
			startActivity(intent);
			break;
//		case 3:
//			fragment = new fragment_setting();
//			break;
//		case 4:
//			fragment = new fragment_help();
//			break;
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();
			// update selected item and title, then close the drawer

			if (item != 1)
				setTitle(menutitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.groups:
			intent = new Intent(MainActivity.this, RoomsRetrieve.class);
			startActivity(intent);
			return true;
		case R.id.map:
			intent = new Intent(MainActivity.this, Map.class);
			startActivity(intent);
			return true;
		case R.id.edu:
			intent = new Intent(MainActivity.this, RoomMap.class);
			startActivity(intent);
			return true;
		case R.id.refresh:
			new CategoriesPullSvc().execute();
			new CategoriesTypePullSvc().execute();
			new AvailableLocationPullSvc().execute();
			new MemberPullSvc().execute();
			new MemberGradePullSvc().execute();
			new RoomPullSvc().execute();
			new RoomMembersPullSvc().execute();
			new SchoolsPullSvc().execute();
			new VoteLocationPullSvc().execute();
			mServiceIntent = new Intent(this, GrpRmPullService.class);
			startService(mServiceIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

		if (drawerOpen == false && item == 1) {
			if (type.equalsIgnoreCase("Organization")) {
				menu.findItem(R.id.map).setVisible(true);
				menu.findItem(R.id.groups).setVisible(false);
				menu.findItem(R.id.edu).setVisible(false);
			} else {
				menu.findItem(R.id.groups).setVisible(true);
				menu.findItem(R.id.edu).setVisible(true);
				menu.findItem(R.id.map).setVisible(false);
			}
			menu.findItem(R.id.refresh).setVisible(true);

		} else {
			menu.findItem(R.id.map).setVisible(false);
			menu.findItem(R.id.groups).setVisible(false);
			menu.findItem(R.id.edu).setVisible(false);
			menu.findItem(R.id.refresh).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}